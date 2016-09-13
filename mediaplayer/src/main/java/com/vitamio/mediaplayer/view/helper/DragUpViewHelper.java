package com.vitamio.mediaplayer.view.helper;

import android.util.Log;
import android.view.View;

/**
 * Created by hexi on 16/6/17.
 */
public class DragUpViewHelper {
    private static final String TAG = "DragUpViewHelper";
    private View parent;
    private View contentView;
    private int contentHeight;
    private int contentTop;
    private boolean debug = false;

    public DragUpViewHelper(View parent) {
        this.parent = parent;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    /**
     * show content view
     */
    public void showContentView() {
        if (contentView.getVisibility() != View.VISIBLE) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * is target view
     *
     * @param child view
     * @return boolean
     */
    public boolean isTarget(View child) {
        return child == contentView;
    }

    /**
     * 处理横向滑动
     *
     * @param child view
     * @param left left
     * @param dx x
     * @return left position
     */
    public int clampViewPositionHorizontal(View child, int left, int dx) {
        return contentView.getLeft();
    }

    /**
     * 处理纵向滑动
     *
     * @param child view
     * @param top top
     * @param dy y
     * @return top position
     */
    public int clampViewPositionVertical(View child, int top, int dy) {
        final int topBound = parent.getHeight() - contentHeight;
        final int bottomBound = parent.getHeight();
        final int newTop = Math.min(Math.max(top, topBound), bottomBound);
        logd("===clampViewPositionVertical, top:%d, dy:%d, topBound:%d, bottomBound:%d, newTop:%d",
                top, dy, topBound, bottomBound, newTop);
        contentTop = newTop;

        return newTop;
    }

    private void logd(String tpl, Object... args) {
        if (debug) {
            Log.d(TAG, String.format(tpl, args));
        }
    }

    /**
     * need setttle
     *
     * @param releasedChild view
     * @param xvel xvel
     * @param yvel yvel
     * @return boolean
     */
    public boolean needSettle(View releasedChild, float xvel, float yvel) {
        boolean isDragUp = isTarget(releasedChild);
        final int topBound = parent.getHeight() - contentHeight;
        final int bottomBound = parent.getHeight();
        boolean needSettle =
                releasedChild.getTop() > topBound && releasedChild.getTop() < bottomBound;
        logd("===onViewReleased, xvel:%f, yvel:%f, isDragUp:%b, needSettle:%b", xvel, yvel,
                isDragUp, needSettle);
        return isDragUp && needSettle;
    }

    /**
     * settle top
     *
     * @param releasedChild view
     * @param xvel xvel
     * @param yvel yvel
     * @return top position
     */
    public int settleTopTo(View releasedChild, float xvel, float yvel) {
        final int top;
        int topBound = parent.getBottom() - contentHeight;
        if (yvel < 0) {
            top = topBound;
        } else {
            top = parent.getBottom();
        }
        contentTop = top;

        return top;
    }

    /**
     * measure
     */
    public void onMeasure() {
        if (contentView == null) {
            return;
        }
        if (contentTop <= 0 || contentHeight <= 0) {
            contentTop = parent.getBottom();
            contentHeight = contentView.getMeasuredHeight();
            logd("===onMeasure, contentTop:%d, contentHeight:%d", contentTop, contentHeight);
        }
    }

    /**
     * set layout
     *
     * @param changed is changed
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (contentView == null) {
            return;
        }
        int bottom = contentTop + contentHeight;
        logd("===onLayout, changed:%b, contentTop:%d, dragViewBottom:%d", changed, contentTop,
                bottom);
        contentView.layout(contentView.getLeft(), contentTop, contentView.getRight(), bottom);
    }

    /**
     * is content view shown
     *
     * @return boolean
     */
    public boolean isContentViewShown() {
        if (contentView == null) {
            return false;
        }
        final int topBound = parent.getHeight() - contentHeight;
        return contentView.getTop() == topBound;
    }

    /**
     * hide content view
     */
    public void hideContentView() {
        if (contentView == null) {
            return;
        }
        contentTop = parent.getHeight();
        final int left = contentView.getLeft();
        final int right = contentView.getRight();
        final int bottom = contentTop + contentHeight;
        contentView.layout(left, contentTop, right, bottom);
    }
}
