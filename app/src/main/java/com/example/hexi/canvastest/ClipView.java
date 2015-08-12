package com.example.hexi.canvastest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hexi on 15/8/10.
 */
public class ClipView extends View {

    private Paint mPaint;
    private Path mPath;

    public ClipView(Context context) {
        super(context);
        init();
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6);
        mPaint.setTextSize(16);
        mPaint.setTextAlign(Paint.Align.RIGHT);

        mPath = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);

        // Region.Op.REPLACE//显示第二次的
        // Region.Op.UNION////全部显示
        // Region.Op.XOR//补集，就是全集的减去交集剩余部分显示
        // Region.Op.INTERSECT//是交集显示
        // Region.Op.REVERSE_DIFFERENCE//第二次不同于第一次的部分显示
        // Region.Op.DIFFERENCE//是第一次不同于第二次的部分显示出来

        canvas.save();
        canvas.translate(10, 10);
        drawScene(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate(160, 10);
        canvas.clipRect(10, 10, 90, 90);// 第一次
        canvas.clipRect(30, 30, 70, 70, Region.Op.DIFFERENCE);// 第二次
        drawScene(canvas);
        canvas.restore();
//
        canvas.save();
        canvas.translate(300, 10);
        canvas.clipRect(10, 10, 90, 90);// 第一次
        canvas.clipRect(30, 30, 70, 70, Region.Op.REVERSE_DIFFERENCE);// 第二次
        drawScene(canvas);
        canvas.restore();
//
//        canvas.save();
//        canvas.translate(10, 160);
//        mPath.reset();
//        canvas.clipPath(mPath); // makes the clip empty// 第一次
//        mPath.addCircle(50, 50, 50, Path.Direction.CCW);
//        canvas.clipPath(mPath, Region.Op.REPLACE);// 第二次
//        drawScene(canvas);
//        canvas.restore();

//        canvas.save();
//        canvas.translate(160, 160);
//        canvas.clipRect(0, 0, 60, 60);// 第一次
//        canvas.clipRect(40, 40, 100, 100, Region.Op.UNION);// 第二次
//        drawScene(canvas);
//        canvas.restore();
//
//        canvas.save();
//        canvas.translate(10, 310);
//        canvas.clipRect(0, 0, 60, 60);// 第一次
//        canvas.clipRect(40, 40, 100, 100, Region.Op.XOR);// 第二次
//        drawScene(canvas);
//        canvas.restore();
//
//        canvas.save();
//        canvas.translate(160, 310);
//        canvas.clipRect(0, 0, 60, 60);// 第一次
//        canvas.clipRect(40, 40, 100, 100, Region.Op.REVERSE_DIFFERENCE);// 第二次
//        drawScene(canvas);
//        canvas.restore();
    }

    private void drawScene(Canvas canvas) {
        canvas.clipRect(0, 0, 100, 100);

        canvas.drawColor(Color.WHITE);

        mPaint.setColor(Color.RED);
        canvas.drawLine(0, 0, 100, 100, mPaint);

        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(30, 70, 30, mPaint);

        mPaint.setColor(Color.BLUE);
        canvas.drawText("Clipping", 100, 30, mPaint);
    }
}
