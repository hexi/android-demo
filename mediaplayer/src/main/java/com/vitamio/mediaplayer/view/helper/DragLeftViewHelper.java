package com.vitamio.mediaplayer.view.helper;

import android.util.Log;
import android.view.View;

/**
 * Created by hexi on 16/6/17.
 */
public class DragLeftViewHelper {
    private static final String TAG = "DragLeftViewHelper";
    private View contentView;
    private View parent;
    private int width;
    private int left;

    public DragLeftViewHelper(View parent) {
        this.parent = parent;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public int clampViewPositionHorizontal(View child, int left, int dx) {
        final int leftBound = parent.getLeft() - width;
        final int rightBound = parent.getLeft();
        final int newLeft = Math.max(Math.min(left, rightBound), leftBound);
        logd("===clampViewPositionHorizontal, leftBound:%d, rightBound:%d, left:%d, newLeft:%d", leftBound, rightBound, left, newLeft);
        this.left = newLeft;
        return newLeft;
    }

    public int clampViewPositionVertical(View child, int top, int dy) {
        return contentView.getTop();
    }

    private void logd(String tpl, Object... args) {
        Log.d(TAG, String.format(tpl, args));
    }

    public boolean needSettle(View releasedChild, float xvel, float yvel) {
        boolean isDragLeft = isTarget(releasedChild);
        boolean needSettle = contentView.getRight() < parent.getRight();
        logd("===onViewReleased, xvel:%f, yvel:%f, needSettle:%b, isDragView:%b",
                xvel, yvel, needSettle, isDragLeft);
        return isDragLeft && needSettle;
    }

    public int settleLeftTo(View releasedChild, float xvel, float yvel) {
        final int left;
        if (xvel <= 0) {
            //往左华
            left = parent.getLeft() - width;
        } else {
            //往右华
            left = parent.getLeft();

        }
        this.left = left;
        return left;
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int right = left + width;
        logd("===onLayout, changed:%b, dragLeftContentViewLeft:%d, contentViewRight:%d", changed, left, right);
        contentView.layout(left, contentView.getTop(), right, contentView.getBottom());
    }

    public void onMeasure() {
        if (width <= 0) {
            width = contentView.getMeasuredWidth();
            left = parent.getLeft() - width;
            logd("===onMeasure, dragLeftContentViewWidth:%d, dragLeftContentViewLeft:%d", width, left);
        }
    }

    public boolean isTarget(View child) {
        return child == contentView;
    }
}
