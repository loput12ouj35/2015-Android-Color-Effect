package com.example.programming.term;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class GradientGenerator {
    public PaintDrawable hBar, sBar, vBar, rBar, gBar, bBar;

    GradientGenerator(){
        hBar = new PaintDrawable();
        hBar.setShape(new RectShape());
        hBar.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width / 2, height,
                        new int[] {Color.RED, Color.GREEN, Color.BLUE }, null, Shader.TileMode.MIRROR);
            }
        });

        sBar = new PaintDrawable();
        sBar.setShape(new RectShape());
        sBar.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, height,
                        Color.GRAY, Color.RED, Shader.TileMode.CLAMP);
            }
        });

        vBar = new PaintDrawable();
        vBar.setShape(new RectShape());
        vBar.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, height,
                        Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP);
            }
        });

        rBar = new PaintDrawable();
        rBar.setShape(new RectShape());
        rBar.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, height,
                        Color.BLACK, Color.RED, Shader.TileMode.CLAMP);
            }
        });

        gBar = new PaintDrawable();
        gBar.setShape(new RectShape());
        gBar.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, height,
                        Color.BLACK, Color.GREEN, Shader.TileMode.CLAMP);
            }
        });

        bBar = new PaintDrawable();
        bBar.setShape(new RectShape());
        bBar.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, height,
                        Color.BLACK, Color.BLUE, Shader.TileMode.CLAMP);
            }
        });
    }
}
