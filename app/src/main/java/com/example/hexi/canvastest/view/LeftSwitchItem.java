package com.example.hexi.canvastest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.hexi.canvastest.R;


/**
 * Created by hexi on 16/5/31.
 */
public class LeftSwitchItem extends TextView {
    private final String TAG = this.getClass().getSimpleName() + "@" + this.hashCode();
    private static final int UN_SELECTED_BG_COLOR = Color.parseColor("#ffffffff");
    private int selectedBgColor;
    Paint bgPaint;
    int radius;
    RectF content;
    Xfermode xfermode;
    private int border = 2;

    public void setSelectedBgColor(int selectedBgColor) {
        this.selectedBgColor = selectedBgColor;
    }

    public LeftSwitchItem(Context context) {
        this(context, null);
    }

    public LeftSwitchItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftSwitchItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        radius = context.getResources().getDimensionPixelSize(R.dimen.switcher_radius);
        content = new RectF();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            content.set(border, border, w, h - border);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.d(TAG, "===dispatchDraw===");
        int color;
        if (isSelected()) {
            color = selectedBgColor;
        } else {
            color = UN_SELECTED_BG_COLOR;
        }
        bgPaint.setColor(color);

        bgPaint.setXfermode(xfermode);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        c.drawBitmap(createRound(color), 0, 0, null);
        c.drawBitmap(createRect(color), 0, 0, bgPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);

    }

    private Bitmap createRect(int color) {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        c.drawRect(content.left + radius, content.top, content.right, content.bottom, p);
        return bm;
    }

    private Bitmap createRound(int color) {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);

        RectF rectF = new RectF();
        rectF.set(content.left, content.top, content.right - radius, content.bottom);
        c.drawRoundRect(rectF, radius, radius, p);
        return bm;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}
