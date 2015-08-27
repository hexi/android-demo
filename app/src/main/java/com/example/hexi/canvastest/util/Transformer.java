package com.example.hexi.canvastest.util;

import android.graphics.Matrix;
import android.util.Log;

/**
 * Created by hexi on 15/8/27.
 */
public class Transformer {

    private static final String TAG = "Transformer";
    private ViewPortHandler viewPortHandler;
    private Matrix valueMatrix = new Matrix();
    private Matrix offsetMatrix = new Matrix();

    public Transformer(ViewPortHandler viewPortHandler) {
        this.viewPortHandler = viewPortHandler;
    }

    public void prepareValueMatrix(float xMin, float deltaX, float yMin, float deltaY) {
        float scaleX = this.viewPortHandler.contentWidth() /  deltaX;
        float scaleY = this.viewPortHandler.contentHeight() / deltaY;
        Log.d(TAG, "===prepareMatrix, xMin: " + xMin + ", deltaX: " + deltaX
                + ", yMin: " + yMin + ", deltaY: " + deltaY
                + ", scaleX:" + scaleX + ", scaleY:" + scaleY);

        valueMatrix.reset();
        valueMatrix.postTranslate(-xMin, -yMin);

        valueMatrix.postScale(scaleX, -scaleY);
//            valueMatrix.postScale(scaleX, scaleY);
    }

    public void prepareOffsetMatrix() {
        offsetMatrix.reset();

        offsetMatrix.postTranslate(this.viewPortHandler.offsetLeft(),
                this.viewPortHandler.getCanvasHeight() - viewPortHandler.offsetBottom());
//            offsetMatrix.postTranslate(this.viewPortHandler.offsetLeft(),
//                    this.viewPortHandler.offsetTop());
    }

    public void pointValuesToPixel(float[] pts) {
        this.valueMatrix.mapPoints(pts);
        this.offsetMatrix.mapPoints(pts);
    }
}
