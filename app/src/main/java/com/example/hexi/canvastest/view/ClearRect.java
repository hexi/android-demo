package com.example.hexi.canvastest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.example.hexi.canvastest.R;


public class ClearRect extends View {

    private static final String TAG = "DrawLinesView";

    public ClearRect(Context context) {
        super(context);
        init(null, 0);
    }

    public ClearRect(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ClearRect(Context context, AttributeSet attrs, int defStyle) {
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
        paint.setStyle(Paint.Style.STROKE);
        float py = getHeight() / 2;
        float px = 0;
        float xStep = 15;
        float yFixed = 20;
        Path path = new Path();
        path.moveTo(px, py);
        for (int i = 0; i < 20; i++) {
            px += xStep;
            py = py + (i % 2 == 0 ? yFixed : (-yFixed));
            if (i == 10) {
                path.moveTo(px, py);
            } else {
                path.lineTo(px, py);
            }
        }
        canvas.drawPath(path, paint);

    }
}
