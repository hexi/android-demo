package com.example.hexi.canvastest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.hexi.canvastest.R;


public class BadgeView extends View {

    private static final String TAG = "DrawCircleView";

    public BadgeView(Context context) {
        super(context);
        init(null, 0);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
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

        float xPos = 100;
        float yPos = 100;
        float radius = 15;
        float textSize = 18;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(xPos, yPos, radius, paint);

        paint.setTextSize(textSize);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setTextAlign(Paint.Align.CENTER);
        String text = "3";
        yPos += calcTextHeight(paint, text) / 2;
        canvas.drawText(text, xPos, yPos, paint);
    }

    private static int calcTextHeight(Paint paint, String demoText) {
        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }
}
