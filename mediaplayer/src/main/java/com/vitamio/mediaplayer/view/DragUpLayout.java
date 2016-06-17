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
public class DragUpLayout extends RelativeLayout {

    private static final String TAG = "DragLayout";

    private ViewDragHelper dragHelper;
    private View dragView;
    private int drawViewId;
    private boolean debug = false;
    private int clickViewId;
    private View clickView;
    private MotionEvent currentDownEvent;
    private double MOVE_THRESHOLD = 5;
    OnClickListener onClickListener;
    OnDragUpListener onDragUpListener;
    boolean enableDrag = true;
    private int dragViewTop;
    private int drawViewHeight;

    public void setEnableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    public void setOnDragUpListener(OnDragUpListener onDragListener) {
        this.onDragUpListener = onDragListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    public DragUpLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DragUpLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DragUpLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DragUpLayout);
        try {
            drawViewId = a.getResourceId(R.styleable.DragUpLayout_drawViewId, -1);
            clickViewId = a.getResourceId(R.styleable.DragUpLayout_clickViewId, -1);
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
                final int topBound = getHeight() - drawViewHeight;
                final int bottomBound = getHeight();

                if (onDragUpListener == null) {
                    return;
                }
                if (top == topBound) {
                    onDragUpListener.onDragViewUp();
                } else if (top == bottomBound) {
                    onDragUpListener.onDragViewDown();
                }
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
                final int topBound = getHeight() - drawViewHeight;
                final int bottomBound = getHeight();
                logd("===clampViewPositionVertical, top:%d, dy:%d, topBound:%d, bottomBound:%d",
                        top, dy, topBound, bottomBound);
                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
                dragViewTop = newTop;
                return newTop;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int topBound = getHeight() - drawViewHeight;
                int left = dragView.getLeft();
                logd("===onViewReleased, xvel:%f, yvel:%f, childTop:%d, topBound:%d, isDragView:%b",
                        xvel, yvel, releasedChild.getTop(), topBound, (releasedChild == dragView));
                if (releasedChild == dragView && releasedChild.getTop() > topBound) {
                    int top;
                    if (yvel < 0) {
                        top = topBound;
                    } else {
                        top = getHeight();

                    }
                    dragViewTop = top;
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
        clickView = findViewById(clickViewId);
        if (clickViewId != -1) {
            clickView = findViewById(clickViewId);
        }
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
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            if (currentDownEvent != null) {
                currentDownEvent.recycle();
            }
            currentDownEvent = MotionEvent.obtain(event);
        } else if (action == MotionEvent.ACTION_UP) {
            double distance = distance(event, currentDownEvent);
            if (distance < MOVE_THRESHOLD && onClickListener != null) {
                if (touchInClickView(currentDownEvent)) {
                    clickView.performClick();
                } else {
                    onClickListener.onClick(this);
                }
            }
        }
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        logd("===onMeasure===");
        if (drawViewHeight <= 0) {
            drawViewHeight = dragView.getMeasuredHeight();
            dragViewTop = getMeasuredHeight();
            logd("===onMeasure, drawViewTop:%d, drawViewHeight:%d", dragViewTop, drawViewHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int bottom = dragViewTop + drawViewHeight;
        logd("===onLayout, changed:%b, dragViewTop:%d, dragViewBottom:%d", changed, dragViewTop, bottom);
        dragView.layout(dragView.getLeft(), dragViewTop, dragView.getRight(), bottom);
    }

    private boolean touchInClickView(MotionEvent event) {
        if (clickView == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        if (x >= clickView.getLeft() && x <= clickView.getRight()
                && y >= clickView.getTop() && y <= clickView.getBottom()) {
            return true;
        }
        return false;
    }

    private double distance(MotionEvent end, MotionEvent start) {
        float disX = Math.abs(end.getX() - start.getX());
        float disY = Math.abs(end.getY() - start.getY());
        double dis = Math.sqrt(disX * disX + disY * disY);
        return dis;
    }

    public interface OnDragUpListener {
        void onDragViewUp();
        void onDragViewDown();
    }
}
