package com.example.programming.term;

import android.graphics.Path;

public class Pathset {
    Path path;
    boolean isEraser;
    int size;

    Pathset(Path path, boolean isEraser, int size) {
        this.path = path;
        this.isEraser = isEraser;
        this.size = size;
    }
}
