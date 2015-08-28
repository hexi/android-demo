package com.example.hexi.canvastest.model;

/**
 * Created by hexi on 15/8/26.
 */
public class Candle extends Entry {
    private float open;
    private float close;

    public Candle(int xIndex, float open, float close) {
        super(xIndex, (open + close) / 2);
        this.open = open;
        this.close = close;
    }

    public float getOpen() {
        return open;
    }

    public float getClose() {
        return close;
    }
}
