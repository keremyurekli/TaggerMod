package com.keremyurekli.taggermod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
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

    public static final Logger LOGGER = LoggerFactory.getLogger("TAGGERMOD");

    public static final Color COLOR = new Color(0, 255, 0);
    public static final float[] COLOR_FLOAT = new float[]{COLOR.getRed() / 255f, COLOR.getGreen() / 255f, COLOR.getBlue() / 255f, COLOR.getAlpha() / 255f};

    @Override
    public void onInitializeClient() {

        //init();
        //createEmptyFile();
//        WorldRenderEvents.AFTER_ENTITIES.register(this::displayBoundingBox);
        MinecraftClient client = MinecraftClient.getInstance();

        LOGGER.info("Initializing Taggermod Client");

        WorldRenderEvents.AFTER_ENTITIES.register(this::afterEntities);

        rayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ray.fire", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z, // The keycode of the key
                "category.tagger.main" // The translation key of the keybinding's category.
        ));


        ClientTickEvents.END_CLIENT_TICK.register(run -> {
            while (rayKey.wasPressed()) {
                if (client.world != null) {
                    run.player.sendMessage(new LiteralText("input"), false);
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

                                client.player.sendMessage(new LiteralText("§aBLOCK: " + blockName), true);
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
        });
    }

    private void afterEntities(WorldRenderContext context) {
        if(!blockPosList.isEmpty()){
            MinecraftClient minecraft = MinecraftClient.getInstance();
            for(BlockPos blockPos : blockPosList){
                MatrixStack stack = context.matrixStack();
                Camera mainCamera = minecraft.gameRenderer.getCamera();
                Vec3d camera = mainCamera.getPos();


                stack.push();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

                VertexConsumerProvider vertexConsumerProvider = context.consumers();
                
                assert vertexConsumerProvider != null;

                
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.LINES);


                RenderSystem.applyModelViewMatrix();
                stack.translate(-camera.x, -camera.y, -camera.z);
                
                renderBlockBounding(stack,vertexConsumer,blockPos);


                tessellator.draw();
                stack.pop();
                RenderSystem.applyModelViewMatrix();

            }
        }

    }

}