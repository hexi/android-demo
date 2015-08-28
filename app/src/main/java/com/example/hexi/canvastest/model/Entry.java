package com.example.hexi.canvastest.model;

/**
 * Created by hexi on 15/8/28.
 */
public abstract class Entry {
    private int xIndex;
    private float value;

    public Entry(int xIndex, float value) {
        this.xIndex = xIndex;
        this.value = value;
    }

    public int getXIndex() {
        return xIndex;
    }

    public float getValue() {
        return value;
    }
}
