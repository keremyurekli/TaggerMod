package com.keremyurekli.taggermod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

import static com.keremyurekli.taggermod.client.TaggermodClient.COLOR_FLOAT;

public class Renderer extends RenderLayer {


    public Renderer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static void renderBlockBounding(MatrixStack matrices, VertexConsumer builder, BlockPos b) {

        if (b == null) {
            return;
        }

        final float size = 1.0f;
        final float x = b.getX(), y = b.getY(), z = b.getZ(), opacity = 1.0f;

        float red = COLOR_FLOAT[0];
        float green = COLOR_FLOAT[1];
        float blue = COLOR_FLOAT[2];

        RenderSystem.disableDepthTest();
        WorldRenderer.drawBox(
                matrices, builder, x, y, z, x + size, y + size, z + size, red, green, blue, opacity);
        RenderSystem.enableDepthTest();
    }



}



