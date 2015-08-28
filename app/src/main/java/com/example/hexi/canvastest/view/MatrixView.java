package com.example.hexi.canvastest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.hexi.canvastest.model.Candle;
import com.example.hexi.canvastest.model.Line;
import com.example.hexi.canvastest.model.LineEntry;
import com.example.hexi.canvastest.util.Transformer;
import com.example.hexi.canvastest.util.ViewPortHandler;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

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
        Paint backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.parseColor("#ffcccccc"));
        canvas.drawRect(viewPortHandler.getContentRect(), backgroundPaint);

        List<Candle> candles = Lists.newArrayList(
                new Candle(0, 100f, 200f),
                new Candle(1, 150f, 300f),
                new Candle(2, 80f, 20f),
                new Candle(4, 155f, 255f),
                new Candle(6, 108f, 260f)
        );

        List<LineEntry> points = Lists.newArrayList(
                new LineEntry(0, 105),
                new LineEntry(1, 220),
                new LineEntry(2, 99),
                new LineEntry(3, 400),
                new LineEntry(4, 199),
                new LineEntry(5, 299),
                new LineEntry(6, 199)
        );

        Line line = new Line(points);

        prepareMatrix(candles, line);

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

        LineBuffer lineBuffer = new LineBuffer(line.size() * 4);
        lineBuffer.feed(line.getPoints());
        transformer.pointValuesToPixel(lineBuffer.getBuffer());

        canvas.drawLines(lineBuffer.getBuffer(), paint);
    }

    private void prepareMatrix(List<Candle> candles, Line line) {
        float xMin = Float.MAX_VALUE, xMax = Float.MIN_VALUE, deltaX,
                yMin = Float.MAX_VALUE, yMax = Float.MIN_VALUE, deltaY;
        for (Candle candle : candles) {
            if (candle.getXIndex() < xMin) {
                xMin = candle.getXIndex();
            }
            if (candle.getXIndex() > xMax) {
                xMax = candle.getXIndex();
            }
            float minValue = Math.min(candle.getOpen(), candle.getClose());
            float maxValue = Math.max(candle.getOpen(), candle.getClose());

            if (minValue < yMin) {
                yMin = minValue;
            }

            if (maxValue > yMax) {
                yMax = maxValue;
            }
        }

        for (LineEntry point : line.getPoints()) {
            if (point.getXIndex() < xMin) {
                xMin = point.getXIndex();
            }
            if (point.getXIndex() > xMax) {
                xMax = point.getXIndex();
            }
            if (point.getValue() < yMin) {
                yMin = point.getValue();
            }
            if (point.getValue() > yMax) {
                yMax = point.getValue();
            }
        }

        xMin -= 0.5;
        xMax -= 0.5;
        deltaX = xMax - xMin + 1;
        deltaY = yMax - yMin;

        transformer.prepareValueMatrix(xMin, deltaX, yMin, deltaY);
        transformer.prepareOffsetMatrix();
    }

    static abstract class AbstractBuffer<T> {
        protected float[] buffer;
        protected int index = 0;
        public AbstractBuffer(int size) {
            index = 0;
            this.buffer = new float[size];
        }

        protected void reset() {
            index = 0;
        }

        public abstract void feed(List<T> entries);

        public float[] getBuffer() {
            return buffer;
        }
    }

    static class CandleBuffer extends AbstractBuffer<Candle> {
        private float bodySpace = 0.1f;

        public CandleBuffer(int size) {
            super(size);
        }

        @Override
        public void feed(List<Candle> candles) {
            for (Candle candle : candles) {
                addBody(candle.getXIndex() - 0.5f + bodySpace, candle.getOpen(), candle.getXIndex() + 0.5f - bodySpace, candle.getClose());
            }
            reset();
        }

        private void addBody(float left, float top, float right, float bottom) {
            buffer[index++] = left;
            buffer[index++] = top;
            buffer[index++] = right;
            buffer[index++] = bottom;
        }
    }

    static class LineBuffer extends AbstractBuffer<LineEntry> {
        public LineBuffer(int size) {
            super(size);
        }

        public void feed(List<LineEntry> entries) {
            moveTo(entries.get(0).getXIndex(), entries.get(0).getValue());
            for (int i = 1; i < entries.size(); i++) {
                LineEntry point = entries.get(i);
                lineTo(point.getXIndex(),  point.getValue());
            }

            reset();
        }

        private void lineTo(float x, float y) {
            if (index == 2) {
                buffer[index++] = x;
                buffer[index++] = y;
            } else {
                float prevX = buffer[index - 2];
                float prevY = buffer[index - 1];
                buffer[index++] = prevX;
                buffer[index++] = prevY;
                buffer[index++] = x;
                buffer[index++] = y;
            }
        }

        private void moveTo(int x, float y) {
            if (index != 0) {
                return;
            }
            buffer[index++] = x;
            buffer[index++] = y;

            //in case just one entry, this is overwritten when lineTo is called
            buffer[index] = x;
            buffer[index + 1] = y;
        }
    }
}
