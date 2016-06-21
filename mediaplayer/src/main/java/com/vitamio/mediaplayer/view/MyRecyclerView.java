package com.vitamio.mediaplayer.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by hexi on 16/4/5.
 */
public class MyRecyclerView extends RecyclerView {

    private static final String TAG = "MyRecyclerView";

    private boolean canScroll = true;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        boolean ret;
        if (canScroll) {
            ret = super.dispatchTouchEvent(ev);
        } else {
            ret = false;
        }
        Log.d(TAG, String.format("===dispatchTouchEvent, action:%d, ret:%b", action, ret));
        return ret;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        boolean ret;
        if (!canScroll) {
            ret =  true;
        } else {
            ret = super.onInterceptTouchEvent(e);
        }
        Log.d(TAG, String.format("===onInterceptTouchEvent, action:%d, ret:%b", action, ret));
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        boolean ret = super.onTouchEvent(e);
        Log.d(TAG, String.format("===onTouchEvent, action:%d, ret:%b", action, ret));
        return ret;
    }
}
