package com.example.hexi.canvastest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.hexi.canvastest.R;


public class DrawLinesView extends View {

    private static final String TAG = "DrawLinesView";

    public DrawLinesView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawLinesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawLinesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MyView, defStyle, 0);

        a.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        float xPos0 = 5;
        float xPos1 = 20;
        float height0 = 100;
        float height1 = 200;
        int height = getHeight();
        float[] pts = new float[8];
        pts[0] = xPos0;
        pts[1] = height;
        pts[2] = xPos0;
        pts[3] = height - height0;

        pts[4] = xPos1;
        pts[5] = height;
        pts[6] = xPos1;
        pts[7] = height - height1;
        canvas.drawLines(pts, paint);

    }
}
