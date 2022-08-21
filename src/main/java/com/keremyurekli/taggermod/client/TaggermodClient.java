package com.keremyurekli.taggermod.client;

import com.keremyurekli.taggermod.util.Renderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static com.keremyurekli.taggermod.util.ConfigManager.*;
import static com.keremyurekli.taggermod.util.Renderer.renderBlockBounding;

@Environment(EnvType.CLIENT)
public class TaggermodClient implements ClientModInitializer {

    private static KeyBinding rayKey;
    private static KeyBinding clearKey;

    private static KeyBinding toggleKey;

    public static final Logger LOGGER = LoggerFactory.getLogger("TAGGERMOD");

    public static final Color COLOR = new Color(0, 255, 0);
    public static final float[] COLOR_FLOAT = new float[]{COLOR.getRed() / 255f, COLOR.getGreen() / 255f, COLOR.getBlue() / 255f, COLOR.getAlpha() / 255f};

    @Override
    public void onInitializeClient() {


        MinecraftClient client = MinecraftClient.getInstance();

        LOGGER.info("Initializing Taggermod Client");

        WorldRenderEvents.AFTER_ENTITIES.register(this::afterEntities);
        PlayerBlockBreakEvents.AFTER.register(this::afterBlockBreak);

        rayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ray.fire", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z, // The keycode of the key
                "category.tagger.main" // The translation key of the keybinding's category.
        ));
        clearKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ray.clear", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_G, // The keycode of the key
                "category.tagger.main" // The translation key of the keybinding's category.
        ));
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tracer.toggle", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_X, // The keycode of the key
                "category.tagger.main" // The translation key of the keybinding's category.
        ));


        ClientTickEvents.END_CLIENT_TICK.register(run -> {
            while (rayKey.wasPressed()) {
                if (client.world != null) {
                    //get tickdelta
                    float tickDelta = client.getTickDelta();
                    if (client.player != null) {
                        HitResult hit = client.player.raycast(200.0, tickDelta, false);
                        switch (hit.getType()) {
                            case MISS:
                                client.player.sendMessage(new LiteralText("§4MISS"), true);
                                break;
                            case BLOCK:
                                BlockHitResult blockHit = (BlockHitResult) hit;
                                BlockPos blockPos = blockHit.getBlockPos();
                                BlockState blockState = client.world.getBlockState(blockPos);
                                Block block = blockState.getBlock();
                                String blockName = block.getTranslationKey();

                                client.player.sendMessage(new LiteralText("§aHIT"), true);
                                if(blockPosList.contains(blockPos)) {
                                    blockPosList.remove(blockPos);
                                } else {
                                    blockPosList.add(blockPos);
                                }


                                break;
                        }

                    }
                }
            }
            while (clearKey.wasPressed()) {
                blockPosList.clear();
            }

            while (toggleKey.wasPressed()) {
                if(isTracerEnabled) {
                    isTracerEnabled = false;
                } else {
                    isTracerEnabled = true;
                }
            }
        });
    }

    private void afterBlockBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(playerEntity.equals(player)){
            if(blockPosList.contains(blockPos)) {
                blockPosList.remove(blockPos);
            }
        }
    }

    private void afterEntities(WorldRenderContext context) {
        if(!blockPosList.isEmpty()){
            MinecraftClient minecraft = MinecraftClient.getInstance();
            ClientPlayerEntity player = minecraft.player;
            for(BlockPos blockPos : blockPosList){
                Box aabb = new Box(blockPos);

                MatrixStack stack = context.matrixStack();
                float delta = context.tickDelta();
                Camera mainCamera = minecraft.gameRenderer.getCamera();
                Vec3d camera = mainCamera.getPos();
                GL11.glDisable(GL11.GL_DEPTH_TEST);

                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableTexture();

                stack.push();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                buffer.color(COLOR_FLOAT[0], COLOR_FLOAT[1], COLOR_FLOAT[2], COLOR_FLOAT[3]);

                VertexConsumerProvider vertexConsumerProvider = context.consumers();

                assert vertexConsumerProvider != null;


                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.LINES);


                RenderSystem.applyModelViewMatrix();
                stack.translate(-camera.x, -camera.y, -camera.z);
                Vec3f look = mainCamera.getHorizontalPlane();
                float px = (float) (player.prevX + (player.getX() - player.prevX) * delta) + look.getX();
                float py = (float) (player.prevY + (player.getY() - player.prevY) * delta) + look.getY()
                        + player.getStandingEyeHeight();
                float pz = (float) (player.prevZ + (player.getZ() - player.prevZ) * delta) + look.getZ();


                renderBlockBounding(stack,buffer,blockPos);

                if(isTracerEnabled){
                    Vec3d center = aabb.getCenter();
                    Renderer.renderSingleLine(stack, buffer, px, py, pz, (float) center.x,
                            (float) center.y, (float) center.z, COLOR_FLOAT[0], COLOR_FLOAT[1], COLOR_FLOAT[2], 1.0f);
                }


                tessellator.draw();
                stack.pop();
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setShaderColor(1, 1, 1, 1);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);

            }
        }

    }

}