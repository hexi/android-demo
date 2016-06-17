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

    public DragUpViewHelper(View view) {
        this.parent = view;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public void showContentView() {
        if (contentView.getVisibility() != View.VISIBLE) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isTarget(View child) {
        return child == contentView;
    }

    public int clampViewPositionHorizontal(View child, int left, int dx) {
        return contentView.getLeft();
    }

    public int clampViewPositionVertical(View child, int top, int dy) {
        final int topBound = parent.getHeight() - contentHeight;
        final int bottomBound = parent.getHeight();
        logd("===clampViewPositionVertical, top:%d, dy:%d, topBound:%d, bottomBound:%d",
                top, dy, topBound, bottomBound);
        final int newTop = Math.min(Math.max(top, topBound), bottomBound);
        contentTop = newTop;

        return newTop;
    }

    private void logd(String tpl, Object... args) {
        Log.d(TAG, String.format(tpl, args));
    }

    public boolean needSettle(View releasedChild, float xvel, float yvel) {
        boolean isDragUp = isTarget(releasedChild);
        boolean needSettle = releasedChild.getTop() > (parent.getHeight() - contentHeight);
        logd("===onViewReleased, xvel:%f, yvel:%f, isDragUp:%b, needSettle:%b",
                xvel, yvel, isDragUp, needSettle);
        return isDragUp && needSettle;
    }

    public int settleTopTo(View releasedChild, float xvel, float yvel) {
        final int top;
        int topBound = parent.getHeight() - contentHeight;
        if (yvel < 0) {
            top = topBound;
        } else {
            top = parent.getHeight();

        }
        contentTop = top;

        return top;
    }

    public void onMeasure() {
        if (contentTop <= 0) {
            contentTop = parent.getBottom();
            contentHeight = contentView.getMeasuredHeight();
            logd("===onMeasure, contentTop:%d, contentHeight:%d", contentTop, contentHeight);
        }
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int bottom = contentTop + contentHeight;
        logd("===onLayout, changed:%b, contentTop:%d, dragViewBottom:%d", changed, contentTop, bottom);
        contentView.layout(contentView.getLeft(), contentTop, contentView.getRight(), bottom);
    }
}