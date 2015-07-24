package com.example.hexi.canvastest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class FillPathView extends View {

    private static final String TAG = "MyView";

    public FillPathView(Context context) {
        super(context);
        init(null, 0);
    }

    public FillPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FillPathView(Context context, AttributeSet attrs, int defStyle) {
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
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        PointF[] firstLine = new PointF[]{new PointF(20, 5),
                new PointF(50, 10),
                new PointF(100, 15),
                new PointF(200, 20),
                new PointF(300, 15),
                new PointF(400, 10),
                new PointF(500, 5),
        };
        PointF[] secondLine = new PointF[]{new PointF(20, 20),
                new PointF(50, 30),
                new PointF(100, 40),
                new PointF(200, 50),
                new PointF(300, 40),
                new PointF(400, 30),
                new PointF(500, 20)
        };

        Path path = new Path();
        path.moveTo(firstLine[0].x, firstLine[0].y);
        for (int i = 1; i < firstLine.length; i++) {
            PointF p = firstLine[i];
            path.lineTo(p.x, p.y);
        }

        for (int i = secondLine.length - 1; i >= 0; i--) {
            PointF p = secondLine[i];
            path.lineTo(p.x, p.y);
        }
        path.close();
        canvas.drawPath(path, paint);

    }

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            Log.e(TAG, "===LongPress detected===");
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
