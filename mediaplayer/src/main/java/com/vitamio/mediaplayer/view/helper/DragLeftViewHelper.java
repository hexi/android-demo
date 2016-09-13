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
    private boolean debug = true;

    public DragLeftViewHelper(View parent) {
        this.parent = parent;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    /**
     * 处理横向的拖动
     *
     * @param child child view
     * @param left left position
     * @param dx x
     * @return new left position
     */
    public int clampViewPositionHorizontal(View child, int left, int dx) {
        final int leftBound = parent.getLeft() - contentWidth;
        final int rightBound = parent.getLeft();
        final int newLeft = Math.max(Math.min(left, rightBound), leftBound);
        logd("===clampViewPositionHorizontal, leftBound:%d, rightBound:%d, left:%d, newLeft:%d",
                leftBound, rightBound, left, newLeft);
        this.contentLeft = newLeft;
        return newLeft;
    }

    /**
     * 处理纵向的拖动
     *
     * @param child child view
     * @param top top position
     * @param dy y
     * @return new top position
     */
    public int clampViewPositionVertical(View child, int top, int dy) {
        return contentView.getTop();
    }

    private void logd(String tpl, Object... args) {
        if (debug) {
            Log.d(TAG, String.format(tpl, args));
        }
    }

    /**
     * need Settle
     *
     * @param releasedChild need realease view
     * @param xvel xvel
     * @param yvel yvel
     * @return boolean
     */
    public boolean needSettle(View releasedChild, float xvel, float yvel) {
        boolean isDragLeft = isTarget(releasedChild);
        final int leftBound = parent.getLeft() - contentWidth;
        final int rightBound = parent.getLeft();
        final int left = this.contentView.getLeft();
        boolean needSettle = left > leftBound && left < rightBound;
        logd("===onViewReleased, xvel:%f, yvel:%f, left:%d, leftBound:%d, rightBound:%d, needSettle:%b, isDragView:%b",
                xvel, yvel, left, leftBound, rightBound, needSettle, isDragLeft);
        return isDragLeft && needSettle;
    }

    /**
     * settle left
     *
     * @param releasedChild realese view
     * @param xvel xvel
     * @param yvel yvel
     * @return left position
     */
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

    /**
     * set layout
     *
     * @param changed is changed
     * @param l left position
     * @param t top position
     * @param r right position
     * @param b bottom position
     */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (contentView == null) {
            return;
        }
        int right = contentLeft + contentWidth;
        logd("===onLayout, changed:%b, dragLeftContentViewLeft:%d, contentViewRight:%d", changed,
                contentLeft, right);
        contentView.layout(contentLeft, contentView.getTop(), right, contentView.getBottom());
    }

    /**
     * set layout
     *
     * @param left left position
     * @param top top position
     */
    public void layout(final int left, final int top) {
        if (contentView == null) {
            return;
        }
        logd(String.format("===layout, leftTo:%d, contentWidth:%d", left, contentWidth));
        int right = left + contentWidth;
        int bottom = top + contentView.getMeasuredHeight();
        this.contentLeft = left;
        contentView.layout(left, top, right, bottom);
    }

    /**
     * measure
     */
    public void onMeasure() {
        if (contentView == null) {
            return;
        }
        if (contentWidth <= 0) {
            contentWidth = contentView.getMeasuredWidth();
            contentLeft = parent.getLeft() - contentWidth;
            logd("===onMeasure, dragLeftContentViewWidth:%d, dragLeftContentViewLeft:%d",
                    contentWidth, contentLeft);
        }
    }

    /**
     * is target
     *
     * @param child view
     * @return boolean
     */
    public boolean isTarget(View child) {
        return child == contentView;
    }

    /**
     * show content view
     */
    public void showContentView() {
        if (contentView.getVisibility() != View.VISIBLE) {
            contentView.setVisibility(View.VISIBLE);
        }
    }
}
