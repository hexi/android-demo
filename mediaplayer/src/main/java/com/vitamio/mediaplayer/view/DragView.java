package com.vitamio.mediaplayer.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by hexi on 16/4/11.
 */
public class DragView extends LinearLayout {
    private MotionEvent currentDownEvent;
    private double MOVE_THRESHOLD = 8;

    public DragView(Context context) {
        super(context);
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private double distance(MotionEvent end, MotionEvent start) {
        float disX = Math.abs(end.getX() - start.getX());
        float disY = Math.abs(end.getY() - start.getY());
        double dis = Math.sqrt(disX * disX + disY * disY);
        return dis;
    }
}
