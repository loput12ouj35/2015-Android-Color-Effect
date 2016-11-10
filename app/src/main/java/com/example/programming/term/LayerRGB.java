package com.example.programming.term;

import android.graphics.Color;

public class LayerRGB {
    float R = 1;
    float G = 1;
    float B = 1;

    public int toColor() {
        return  Color.argb(0xff, (int) (R * 255), (int) (G * 255), (int) (B * 255));
    }

}
