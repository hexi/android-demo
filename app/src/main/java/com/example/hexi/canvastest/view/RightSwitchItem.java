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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.hexi.canvastest.R;


/**
 * Created by hexi on 16/5/31.
 */
public class RightSwitchItem extends TextView {
    private final String TAG = this.getClass().getSimpleName() + "@" + this.hashCode();
    private int selectedBgColor;
    private int unSelectedBgColor = Color.parseColor("#ffffffff");
    private int selectedTextColor = Color.parseColor("#ffffffff");
    private int unSelectedTextColor = Color.parseColor("#ff585d66");
    Paint bgPaint;
    Paint textPaint;
    int radius;
    RectF content;
    Xfermode xfermode;
    private int border = 2;
    Bitmap bm;
    private String text;

    public void setSelectedBgColor(int selectedBgColor) {
        this.selectedBgColor = selectedBgColor;
    }

    public void setUnSelectedBgColor(int unSelectedBgColor) {
        this.unSelectedBgColor = unSelectedBgColor;
    }

    public RightSwitchItem(Context context) {
        this(context, null);
    }

    public RightSwitchItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightSwitchItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        radius = context.getResources().getDimensionPixelSize(R.dimen.switcher_radius);
        content = new RectF();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(getTextSize());
        text = getText().toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            content.set(0, border, w - border, h - border);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.d(TAG, "===dispatchDraw===");
        if (isSelected()) {
            bgPaint.setColor(selectedBgColor);
            textPaint.setColor(selectedTextColor);
        } else {
            bgPaint.setColor(unSelectedBgColor);
            textPaint.setColor(unSelectedTextColor);
        }

        //创建一个图层，在图层上演示图形混合后的效果
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG |
                Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        bgPaint.setXfermode(xfermode);
        Canvas c = new Canvas(bm);

        c.drawBitmap(drawBackground(), 0, 0, null);
//        c.drawBitmap(drawText(), 0, 0, bgPaint);

        canvas.drawBitmap(bm, 0, 0, null);


        // 还原画布
        canvas.restoreToCount(sc);
    }

    private Bitmap drawText() {
        Canvas c = new Canvas(bm);
        float x = getWidth()/2;
        float y = getHeight()/2;
        c.drawText(text, x, y, textPaint);
        return bm;
    }

    @NonNull
    private Bitmap drawBackground() {
        Canvas c = new Canvas(bm);

        c.drawBitmap(createRound(), 0, 0, null);
        c.drawBitmap(createRect(), 0, 0, bgPaint);
        return bm;
    }

    private Bitmap createRect() {
        Canvas c = new Canvas(bm);
        c.drawRect(content.left, content.top, content.right - radius, content.bottom, bgPaint);

        return bm;
    }

    private Bitmap createRound() {
        Canvas c = new Canvas(bm);
        RectF rectF = new RectF();
        rectF.set(content.left + radius, content.top, content.right, content.bottom);
        c.drawRoundRect(rectF, radius, radius, bgPaint);
        return bm;
    }

}
