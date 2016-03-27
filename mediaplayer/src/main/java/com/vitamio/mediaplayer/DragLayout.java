package com.vitamio.mediaplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by hexi on 16/3/26.
 */
public class DragLayout extends RelativeLayout {

    private static final String TAG = "DragLayout";

    private ViewDragHelper dragHelper;
    private View dragView;
    private int drawViewId;
    private boolean debug = false;

    public DragLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DragLayout);
        try {
            drawViewId = a.getResourceId(R.styleable.DragLayout_drawViewId, -1);
        } finally {
            a.recycle();
        }

    }

    private void initViewDragHelper() {
        dragHelper = ViewDragHelper.create(this, 1f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                logd("===tryCaptureView===");
                if (dragView.getVisibility() != VISIBLE) {
                    dragView.setVisibility(VISIBLE);
                }
                if (child != dragView) {
                    dragHelper.captureChildView(dragView, pointerId);
                    return false;
                }

                return true;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                logd("===onViewPositionChanged, left:%d, top:%d, dx:%d, dy:%d", left, top, dx, dy);
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                logd("===onViewDragStateChanged, state:%d", state);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return dragView.getLeft();
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getHeight() - dragView.getHeight();
                final int bottomBound = getHeight();
                logd("===clampViewPositionVertical, top:%d, dy:%d, topBound:%d, bottomBound:%d",
                        top, dy, topBound, bottomBound);
                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
                return newTop;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int topBound = getHeight() - dragView.getHeight();
                int left = dragView.getLeft();
                logd("===onViewReleased, xvel:%f, yvel:%f, childTop:%d, topBound:%d, isDragView:%b",
                        xvel, yvel, releasedChild.getTop(), topBound, (releasedChild == dragView));
                if (releasedChild == dragView && releasedChild.getTop() > topBound) {
                    if (yvel < 0) {
                        //fling up
//                        dragHelper.flingCapturedView(releasedChild.getLeft(), topBound, releasedChild.getLeft(), getHeight());
                        dragHelper.settleCapturedViewAt(left, topBound);
                    } else {
                        dragHelper.settleCapturedViewAt(left, getHeight());
                    }
                    invalidate();
                }
            }

        });

    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
//            ViewCompat.postInvalidateOnAnimation(this);
        }
        invalidate();
    }

    private void logd(String tpl, Object... args) {
        if (debug) {
            Log.d(TAG, String.format(tpl, args));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        logd("===onFinishInflate===");
        dragView = findViewById(drawViewId);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        logd("===onAttachedToWindow===");
        initViewDragHelper();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            dragHelper.cancel();
            return false;
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

}
