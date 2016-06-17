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
    private int contentWidth;
    private int contentLeft;

    public DragLeftViewHelper(View parent) {
        this.parent = parent;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public int clampViewPositionHorizontal(View child, int left, int dx) {
        final int leftBound = parent.getLeft() - contentWidth;
        final int rightBound = parent.getLeft();
        final int newLeft = Math.max(Math.min(left, rightBound), leftBound);
        logd("===clampViewPositionHorizontal, leftBound:%d, rightBound:%d, left:%d, newLeft:%d", leftBound, rightBound, left, newLeft);
        this.contentLeft = newLeft;
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
            left = parent.getLeft() - contentWidth;
        } else {
            //往右华
            left = parent.getLeft();

        }
        this.contentLeft = left;
        return left;
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int right = contentLeft + contentWidth;
        logd("===onLayout, changed:%b, dragLeftContentViewLeft:%d, contentViewRight:%d", changed, contentLeft, right);
        contentView.layout(contentLeft, contentView.getTop(), right, contentView.getBottom());
    }

    public void layout(final int left, final int top) {
        int right = left + contentWidth;
        int bottom = top + contentView.getMeasuredHeight();
        this.contentLeft = left;
        contentView.layout(left, top, right, bottom);
    }

    public void onMeasure() {
        if (contentWidth <= 0) {
            contentWidth = contentView.getMeasuredWidth();
            contentLeft = parent.getLeft() - contentWidth;
            logd("===onMeasure, dragLeftContentViewWidth:%d, dragLeftContentViewLeft:%d", contentWidth, contentLeft);
        }
    }

    public boolean isTarget(View child) {
        return child == contentView;
    }

    public void showContentView() {
        if (contentView.getVisibility() != View.VISIBLE) {
            contentView.setVisibility(View.VISIBLE);
        }
    }
}
