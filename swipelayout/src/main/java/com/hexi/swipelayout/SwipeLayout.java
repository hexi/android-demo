package com.hexi.swipelayout;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by hexi on 16/3/17.
 */
public class SwipeLayout extends RelativeLayout {
    private GestureDetector gestureDetector;
    private SlideDetector slideDetector;

    public void setOnSwipeGesture(OnSwipeListener onSwipeGesture) {
        gestureDetector = new GestureDetector(getContext(), onSwipeGesture);
    }

    public void setOnSlideListener(SlideDetector.OnSlideListener onSlideListener) {
        this.slideDetector = new SlideDetector(onSlideListener);
    }

    public SwipeLayout(Context context) {
        super(context);

        init(context, null, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean ret = false;
        if (gestureDetector != null) {
            ret = gestureDetector.onTouchEvent(event);
        }
        if (slideDetector != null) {
            ret = ret | slideDetector.onTouchEvent(event);
        }

        if (action == MotionEvent.ACTION_DOWN) {
            super.onTouchEvent(event);
        } else {
            ret = ret || super.onTouchEvent(event);
        }
        return ret;

    }
}
