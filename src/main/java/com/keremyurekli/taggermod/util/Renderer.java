package com.keremyurekli.taggermod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Renderer extends RenderLayer {


    public Renderer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static void renderBlockBounding(MatrixStack matrices, BufferBuilder builder, BlockPos b, float red, float green, float blue, float alpha) {
        if (b == null) {
            return;
        }

        final float size = 1.0f;
        final float x = b.getX(), y = b.getY(), z = b.getZ();



        WorldRenderer.drawBox(
                matrices, builder, x, y, z, x + size, y + size, z + size, red, green, blue, alpha);
    }

    public static void renderBox(MatrixStack stack, BufferBuilder builder, Box b, float red, float green, float blue, float alpha) {
        if (b == null) {
            return;
        }

        WorldRenderer.drawBox(stack, builder, b, red, green, blue, alpha);
    }

    public static void renderSingleLine(MatrixStack stack, VertexConsumer buffer, float x1, float y1, float z1,
                                        float x2, float y2,
                                        float z2, float r, float g, float b, float a) {
        Vec3f normal = new Vec3f(x2 - x1, y2 - y1, z2 - z1);
        normal.normalize();
        renderSingleLine(stack, buffer, x1, y1, z1, x2, y2, z2, r, g, b, a, normal.getX(), normal.getY(),
                normal.getZ());
    }

    public static void renderSingleLine(MatrixStack stack, VertexConsumer buffer, float x1, float y1, float z1,
                                        float x2, float y2,
                                        float z2, float r, float g, float b, float a, float normalX, float normalY, float normalZ) {
        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        Matrix3f matrix3f = stack.peek().getNormalMatrix();
        buffer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a)
                .normal(matrix3f, normalX, normalY, normalZ).next();
        buffer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a)
                .normal(matrix3f, normalX, normalY, normalZ).next();
    }


}



