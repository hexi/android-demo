package com.vitamio.mediaplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.vitamio.mediaplayer.R;


/**
 * Created by hexi on 16/3/26.
 */
public class DragLeftLayout extends RelativeLayout {

    private static final String TAG = "DragLeftLayout";

    private ViewDragHelper dragHelper;
    private int drawViewId;
    private View dragView;
    private int contentViewId;
    private View contentView;

    private boolean debug = true;
    boolean enableDrag = true;
    private int contentViewLeft;
    private int contentViewWidth;

    public void setEnableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    public void setDragView(View dragView) {
        this.dragView = dragView;
    }

    public DragLeftLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DragLeftLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DragLeftLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private final static String STATE_PARCELABLE = "state_parcelable";
    private static final String STATE_ENABLE_DRAG = "state_enable_drag";

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(STATE_PARCELABLE));
        enableDrag = bundle.getBoolean(STATE_ENABLE_DRAG, true);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable parcelable = super.onSaveInstanceState();
        bundle.putParcelable(STATE_PARCELABLE, parcelable);
        bundle.putBoolean(STATE_ENABLE_DRAG, enableDrag);
        return bundle;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DragLeftLayout);
        try {
            drawViewId = a.getResourceId(R.styleable.DragLeftLayout_DragLeft_drawViewId, -1);
            contentViewId = a.getResourceId(R.styleable.DragLeftLayout_DragLeft_contentViewId, -1);
        } finally {
            a.recycle();
        }

    }

    private void initViewDragHelper() {
        dragHelper = ViewDragHelper.create(this, 1f, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                if (!enableDrag) {
                    return false;
                }
                logd("===tryCaptureView===");
                if (child == dragView) {
                    dragHelper.captureChildView(contentView, pointerId);
                    return false;
                }
                return false;
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
                final int leftBound = getLeft() - contentViewWidth;
                final int rightBound = getLeft();
                final int newLeft = Math.max(Math.min(left, rightBound), leftBound);
                logd("===clampViewPositionHorizontal, leftBound:%d, rightBound:%d, left:%d, newLeft:%d", leftBound, rightBound, left, newLeft);
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return contentView.getTop();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                boolean isDragView = releasedChild == contentView;
                boolean needSettle = contentView.getRight() < getRight();
                logd("===onViewReleased, xvel:%f, yvel:%f, needSettle:%b, isDragView:%b",
                        xvel, yvel, needSettle, isDragView);
                if (isDragView && needSettle) {
                    int top = contentView.getTop();
                    int left;
                    if (xvel <= 0) {
                        //往左华
                        left = getLeft() - contentViewWidth;
                    } else {
                        //往右华
                        left = getLeft();

                    }
                    contentViewLeft = left;
                    dragHelper.settleCapturedViewAt(left, top);
                    invalidate();
                }
            }

        });

    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
//            ViewCompat.postInvalidateOnAnimation(this);
            invalidate();
        }
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
        contentView = findViewById(contentViewId);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        logd("===onMeasure===");
        if (contentViewWidth <= 0) {
            contentViewWidth = contentView.getMeasuredWidth();
            contentViewLeft = getLeft() - contentViewWidth;
            logd("===onMeasure, contentViewWidth:%d, contentViewLeft:%d", contentViewWidth, contentViewLeft);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int right = contentViewLeft + contentViewWidth;
        logd("===onLayout, changed:%b, contentViewLeft:%d, contentViewRight:%d", changed, contentViewLeft, right);
        contentView.layout(contentViewLeft, contentView.getTop(), right, contentView.getBottom());
    }

}
