package com.hexi.swipelayout;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Created by hexi on 16/3/17.
 */
public class SlideDetector {
    public final static String DIRECTION_UP = "up";
    public final static String DIRECTION_DOWN = "down";
    public final static String DIRECTION_LEFT = "left";
    public final static String DIRECTION_RIGHT = "right";

    private static final int SLIDE_THRESHOLD = 40;

    private MotionEvent currentDownEvent;

    private OnSlideListener onSlideListener;

    public SlideDetector(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = false;
        int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            if (currentDownEvent != null) {
                currentDownEvent.recycle();
            }
            currentDownEvent = MotionEvent.obtain(event);
            ret = true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float diffX = event.getX() - currentDownEvent.getX();
            float diffY = event.getY() - currentDownEvent.getY();
            float distance = Math.max(Math.abs(diffX), Math.abs(diffY));
            if (distance < SLIDE_THRESHOLD) {
                return false;
            }
            String direction = calcDirection(diffX, diffY);
            onSlideListener.sliding(direction, distance);
            ret = true;
        } else if (action == MotionEvent.ACTION_UP) {
            float diffX = event.getX() - currentDownEvent.getX();
            float diffY = event.getY() - currentDownEvent.getY();
            float distance = Math.max(Math.abs(diffX), Math.abs(diffY));
            if (distance < SLIDE_THRESHOLD) {
                return false;
            }
            String direction = calcDirection(diffX, diffY);
            onSlideListener.slideComplete(direction, distance);
            ret = true;
        }

        return ret;
    }

    private String calcDirection(float diffX, float diffY) {
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (diffX > 0) {
                return DIRECTION_RIGHT;
            } else {
                return DIRECTION_LEFT;
            }
        } else {
            if (diffY > 0) {
                return DIRECTION_DOWN;
            } else {
                return DIRECTION_UP;
            }
        }
    }



    public interface OnSlideListener {
        void slideComplete(String direction, float distance);

        void sliding(String direction, float distance);
    }
}
