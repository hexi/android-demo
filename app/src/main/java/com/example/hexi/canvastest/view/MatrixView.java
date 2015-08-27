package com.example.hexi.canvastest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.hexi.canvastest.model.Candle;
import com.example.hexi.canvastest.util.Transformer;
import com.example.hexi.canvastest.util.ViewPortHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class MatrixView extends View {
    private static final String TAG = "MatrixView";

    private Paint paint;
    private ViewPortHandler viewPortHandler;
    private Transformer transformer;


    public MatrixView(Context context) {
        super(context);
        init(null, 0);
    }

    public MatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MatrixView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paint.setColor(Color.RED);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(1);

        viewPortHandler = new ViewPortHandler();
        viewPortHandler.restrainViewPort(100f, 100f, 100f, 100f);
        transformer = new Transformer(viewPortHandler);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && h > 0 && w < 10000 && h < 10000) {
            viewPortHandler.setCanvasDimens(w, h);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.d(TAG, "=== width: " + getWidth() + ", height: " + getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        List<Candle> candles = new ArrayList<>();
        candles.add(new Candle(0, 100f, 200f));
        candles.add(new Candle(1, 150f, 300f));
        candles.add(new Candle(2, 80f, 20f));
        candles.add(new Candle(4, 100f, 30f));

        prepareMatrix(candles);

        CandleBuffer candleBuffer = new CandleBuffer(candles.size() * 4);
        candleBuffer.feed(candles);
        float[] buffer = candleBuffer.getBuffer();

        Log.d(TAG, "=== buffer before map: " + new Gson().toJson(buffer));
        transformer.pointValuesToPixel(buffer);
        Log.d(TAG, "=== buffer after map: " + new Gson().toJson(buffer));
        for (int i = 0; i < buffer.length; i += 4) {
            float left = buffer[i];
            float open = buffer[i + 1];
            float right = buffer[i + 2];
            float close = buffer[i + 3];

            if (open < close) {
                canvas.drawRect(left, open, right, close, paint);
            } else {
                canvas.drawRect(left, close, right, open, paint);
            }
        }
    }

    private void prepareMatrix(List<Candle> candles) {
        float xMin = Float.MAX_VALUE, xMax = Float.MIN_VALUE, deltaX,
                yMin = Float.MAX_VALUE, yMax = Float.MIN_VALUE, deltaY;
        for (Candle candle : candles) {
            if (candle.xIndex < xMin) {
                xMin = candle.xIndex;
            }
            if (candle.xIndex > xMax) {
                xMax = candle.xIndex;
            }
            float minValue = Math.min(candle.open, candle.close);
            float maxValue = Math.max(candle.open, candle.close);

            if (minValue < yMin) {
                yMin = minValue;
            }

            if (maxValue > yMax) {
                yMax = maxValue;
            }
        }

        deltaX = xMax - xMin + 1;
        deltaY = yMax - yMin;

        transformer.prepareValueMatrix(xMin, deltaX, yMin, deltaY);
        transformer.prepareOffsetMatrix();
    }

    static class CandleBuffer {
        private float[] buffer;
        private int index = 0;
        private float bodySpace = 0.1f;

        public CandleBuffer(int size) {
            index = 0;
            this.buffer = new float[size];
        }

        public void feed(List<Candle> candles) {
            for (Candle candle : candles) {
                addBody(candle.xIndex + bodySpace, candle.open, candle.xIndex + 1f - bodySpace, candle.close);
            }
            reset();
        }

        private void addBody(float left, float top, float right, float bottom) {
            buffer[index++] = left;
            buffer[index++] = top;
            buffer[index++] = right;
            buffer[index++] = bottom;
        }

        private void reset() {
            index = 0;
        }

        public float[] getBuffer() {
            return buffer;
        }
    }
}
