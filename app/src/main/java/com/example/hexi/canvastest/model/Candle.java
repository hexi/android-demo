package com.example.hexi.canvastest.model;

/**
 * Created by hexi on 15/8/26.
 */
public class Candle {
    public int xIndex;
    public float open;
    public float close;

    public Candle(int xIndex, float open, float close) {
        this.xIndex = xIndex;
        this.open = open;
        this.close = close;
    }
}
