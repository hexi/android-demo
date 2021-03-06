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
import com.vitamio.mediaplayer.view.helper.DragLeftViewHelper;
import com.vitamio.mediaplayer.view.helper.DragUpViewHelper;

/**
 * Created by hexi on 16/3/26.
 */
public class SwipeLayout extends RelativeLayout {

    private static final String TAG = "SwipeLayout";

    private ViewDragHelper dragHelper;

    //drag left
    private int drawLeftViewId;
    private View dragLeftView;
    private int dragLeftContentViewId;
    private View dragLeftContentView;
    private DragLeftViewHelper dragLeftViewHelper;

    //drag up
    private int dragUpContentViewId;
    private View dragUpContentView;
    private DragUpViewHelper dragUpViewHelper;

    //click view
    private int clickViewId;
    private View clickView;

    private MotionEvent currentDownEvent;
    private static final double MOVE_THRESHOLD = 5;
    OnClickListener onClickListener;
    OnSwipeLayoutListener onSwipeLayoutListener;

    private boolean debug = true;
    boolean enableDrag = true;
    boolean enableDragUp = true;

    public void setEnableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    /**
     * setg enable drag up
     */
    public void setEnableDragUp(boolean enableDragUp) {
        logd(TAG, "===setEnableDragUp===");
        this.enableDragUp = enableDragUp;
    }

    public void setDragLeftView(View dragLeftView) {
        this.dragLeftView = dragLeftView;
    }

    public void setOnSwipeLayoutListener(OnSwipeLayoutListener onSwipeLayoutListener) {
        this.onSwipeLayoutListener = onSwipeLayoutListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
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

    private static final String STATE_PARCELABLE = "state_parcelable";
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

        initStyle(attrs);

        initView();
    }

    private void initStyle(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
        try {
            drawLeftViewId = a.getResourceId(R.styleable.SwipeLayout_SwipeLayout_dragLeftId, -1);
            dragLeftContentViewId =
                    a.getResourceId(R.styleable.SwipeLayout_SwipeLayout_dragLeftContentId, -1);

            dragUpContentViewId =
                    a.getResourceId(R.styleable.SwipeLayout_SwipeLayout_dragUpContentId, -1);

            clickViewId = a.getResourceId(R.styleable.SwipeLayout_SwipeLayout_clickViewId, -1);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        initDragLeftHelper();
        initDragUpHelper();
    }

    private void initDragUpHelper() {
        this.dragUpViewHelper = new DragUpViewHelper(this);
        this.dragUpViewHelper.setDebug(debug);
    }

    private void initDragLeftHelper() {
        this.dragLeftViewHelper = new DragLeftViewHelper(this);
        this.dragLeftViewHelper.setDebug(debug);
    }

    private void initViewDragHelper() {
        dragHelper = ViewDragHelper.create(this, 1f, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                if (!enableDrag) {
                    return false;
                }
                logd("===tryCaptureView===");
                if (touchInDragLeftView(currentDownEvent)) {
                    logd("===tryCaptureView dragLeftView===");
                    dragLeftViewHelper.showContentView();
                    dragHelper.captureChildView(dragLeftContentView, pointerId);
                    return false;
                } else if (child != dragLeftContentView && enableDragUp) {
                    logd("===tryCaptureView dragUpView===");
                    dragUpViewHelper.showContentView();
                    dragHelper.captureChildView(dragUpContentView, pointerId);
                    return false;
                }

                return false;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                logd("===onViewPositionChanged, left:%d, top:%d, dx:%d, dy:%d", left, top, dx, dy);
                boolean isDragUpView = dragUpViewHelper.isTarget(changedView);
                if (isDragUpView) {
                    logd("===dragging up view===");
                    final int topBound = getHeight() - dragUpContentView.getHeight();
                    final int bottomBound = getHeight();

                    if (onSwipeLayoutListener == null) {
                        return;
                    }
                    if (top == topBound) {
                        onSwipeLayoutListener.onDragViewUp();
                    } else if (top == bottomBound) {
                        onSwipeLayoutListener.onDragViewDown();
                    }
                }
                boolean isDragLeftView = dragLeftViewHelper.isTarget(changedView);
                if (isDragLeftView) {
                    final int leftBound = dragLeftViewHelper.getLeftBound();
                    final int rightBound = dragLeftViewHelper.getRightBound();
                    logd("===dragging left view, left:%d", left);

                    onLeftViewPositionChanged(left, dx);
                    if (left > leftBound && left < rightBound) {
                        onLeftViewShowingOrHiding();
                    } else if (left == leftBound) {
                        onLeftViewHidden();
                    } else if (left == rightBound) {
                        onLeftViewShown();
                    }
                }
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                logd("===onViewDragStateChanged, state:%d", state);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (dragLeftViewHelper.isTarget(child)) {
                    return dragLeftViewHelper.clampViewPositionHorizontal(child, left, dx);
                } else if (dragUpViewHelper.isTarget(child)) {
                    return dragUpViewHelper.clampViewPositionHorizontal(child, left, dx);
                } else {
                    return left;
                }
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (dragLeftViewHelper.isTarget(child)) {
                    return dragLeftViewHelper.clampViewPositionVertical(child, top, dy);
                } else if (dragUpViewHelper.isTarget(child)) {
                    return dragUpViewHelper.clampViewPositionVertical(child, top, dy);
                } else {
                    return top;
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                boolean needSettleLeft = dragLeftViewHelper.needSettle(releasedChild, xvel, yvel);
                if (needSettleLeft) {
                    int top = dragLeftContentView.getTop();
                    int left = dragLeftViewHelper.settleLeftTo(releasedChild, xvel, yvel);
                    logd("===leftContentView, targetLeft:" + left);
                    dragHelper.settleCapturedViewAt(left, top);
                    invalidate();
                }
                boolean needSettleUp = dragUpViewHelper.needSettle(releasedChild, xvel, yvel);
                if (needSettleUp) {
                    final int left = dragUpContentView.getLeft();
                    final int top = dragUpViewHelper.settleTopTo(releasedChild, xvel, yvel);
                    dragHelper.settleCapturedViewAt(left, top);
                    invalidate();
                }
            }
        });
    }

    private void onLeftViewPositionChanged(int left, int dx) {
        if (onSwipeLayoutListener != null) {
            onSwipeLayoutListener.onLeftViewPositionChanged(left, dx);
        }
    }

    private void onLeftViewShowingOrHiding() {
        if (!dragLeftViewHelper.isShow()) {
            onLeftViewShowing();
        } else {
            onLeftViewHiding();
        }
    }

    private void onLeftViewHiding() {
        if (onSwipeLayoutListener != null) {
            onSwipeLayoutListener.onLeftViewHiding();
        }
    }

    private void onLeftViewShowing() {
        if (onSwipeLayoutListener != null) {
            onSwipeLayoutListener.onLeftViewShowing();
        }
    }

    private void onLeftViewHidden() {
        this.dragLeftViewHelper.setShown(false);
        if (onSwipeLayoutListener != null) {
            onSwipeLayoutListener.onLeftViewHidden();
        }
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
        dragLeftView = findViewById(drawLeftViewId);
        dragLeftContentView = findViewById(dragLeftContentViewId);
        dragLeftViewHelper.setContentView(dragLeftContentView);

        dragUpContentView = findViewById(dragUpContentViewId);
        dragUpViewHelper.setContentView(dragUpContentView);

        if (clickViewId != -1) {
            clickView = findViewById(clickViewId);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        logd("===onAttachedToWindow===");
        initViewDragHelper();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.dragLeftViewHelper = null;
        this.dragUpViewHelper = null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = false;
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            if (currentDownEvent != null) {
                currentDownEvent.recycle();
            }
            currentDownEvent = MotionEvent.obtain(ev);
            if (enableDrag && !touchInDragUpContent(ev) && touchInDragLeftView(ev)) {
                ret = true;
            } else {
                ret = false;
            }
        } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            dragHelper.cancel();
            ret = false;
        }

        if (action != MotionEvent.ACTION_CANCEL && action != MotionEvent.ACTION_UP) {
            ret |= dragHelper.shouldInterceptTouchEvent(ev);
        }
        logd("===onInterceptTouchEvent, action:%d, ret:%b", action, ret);
        return ret;
    }

    private boolean touchInDragUpContent(MotionEvent ev) {
        return isTouchInViw(ev, dragUpContentView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        boolean ret = super.dispatchTouchEvent(ev);
        logd("===dispatchTouchEvent, action:%d, ret:%b", action, ret);
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_UP) {
            double distance = distance(event, currentDownEvent);
            if (distance < MOVE_THRESHOLD) {
                if (enableDrag && !touchInDragUpContent(currentDownEvent) && touchInDragLeftView(
                        currentDownEvent)) {
                    showOrHideLeftView();
                } else if (clickView != null
                        && findTouchedViewExclude(currentDownEvent, clickView) == null
                        && isTouchInViw(currentDownEvent, clickView)) {
                    if (onClickListener != null) {
                        onClickListener.onClick(clickView);
                    }
                }
            }
        }
        dragHelper.processTouchEvent(event);
        logd("===onTouchEvent, action:%d, ret:%b", action, true);
        return true;
    }

    private View findTouchedViewExclude(MotionEvent event, View excludeView) {
        final int count = getChildCount();

        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child == excludeView) {
                continue;
            }

            if (!child.isClickable()) {
                continue;
            }

            if (isTouchInViw(event, child)) {
                return child;
            }
        }
        return null;
    }

    private boolean isTouchInViw(MotionEvent event, View child) {
        if (child == null || child.getVisibility() != VISIBLE) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();

        float viewLeft = child.getX();
        float viewRight = viewLeft + child.getMeasuredWidth();
        float viewTop = child.getY();
        float viewBottom = viewTop + child.getMeasuredHeight();
        if ((x >= viewLeft && x <= viewRight) && (y >= viewTop
                && y <= viewBottom)) {
            return true;
        }
        return false;
    }

    /**
     * show or hide left view
     */
    public void showOrHideLeftView() {
        if (!enableDrag) {
            return;
        }
        logd(TAG, "===showOrHideLeftView===");
        dragLeftViewHelper.showContentView();
        final int leftBound = dragLeftViewHelper.getLeftBound();
        final int rightBound = dragLeftViewHelper.getRightBound();
        final int left = dragLeftContentView.getLeft();
        if (left == leftBound) {
            //show left view
            onLeftViewShowingOrHiding();
            dragLeftViewHelper.layout(rightBound, dragLeftContentView.getTop());
            onLeftViewPositionChanged((rightBound - leftBound), (rightBound - leftBound));
            onLeftViewShown();
        } else if (left == rightBound) {
            //hide left view
            onLeftViewShowingOrHiding();
            dragLeftViewHelper.layout(leftBound, dragLeftContentView.getTop());
            onLeftViewPositionChanged((leftBound - rightBound), (leftBound - rightBound));
            onLeftViewHidden();
        }
    }

    private void onLeftViewShown() {
        dragLeftViewHelper.setShown(true);
        if (onSwipeLayoutListener != null) {
            onSwipeLayoutListener.onLeftViewShown();
        }
    }

    private boolean touchInDragLeftView(MotionEvent event) {
        return isTouchInViw(event, dragLeftView);
    }

    private double distance(MotionEvent end, MotionEvent start) {
        float disX = Math.abs(end.getX() - start.getX());
        float disY = Math.abs(end.getY() - start.getY());
        double dis = Math.sqrt(disX * disX + disY * disY);
        return dis;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        logd("===onMeasure===");
        dragLeftViewHelper.onMeasure();
        dragUpViewHelper.onMeasure();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        dragLeftViewHelper.onLayout(changed, l, t, r, b);
        dragUpViewHelper.onLayout(changed, l, t, r, b);
    }

    /**
     * get left view
     *
     * @return view
     */
    public View getDragLeftView() {
        return this.dragLeftView;
    }

    /**
     * is up content view shown
     *
     * @return boolean
     */
    public boolean isUpContentViewShown() {
        return dragUpViewHelper.isContentViewShown();
    }

    /**
     * hide content view
     */
    public void hideUpContentView() {
        dragUpViewHelper.hideContentView();
        if (onSwipeLayoutListener != null) {
            onSwipeLayoutListener.onDragViewDown();
        }
    }

    /**
     * listener
     */
    public interface OnSwipeLayoutListener {
        /**
         * drap up method
         */
        void onDragViewUp();

        /**
         * drag down method
         */
        void onDragViewDown();

        /**
         * left view showing method
         */
        void onLeftViewShowing();

        /**
         * left view hiding
         */
        void onLeftViewHiding();

        /**
         * left view shown method
         */
        void onLeftViewShown();

        /**
         * left view hidden method
         */
        void onLeftViewHidden();

        /**
         * on left view position changed
         */
        void onLeftViewPositionChanged(final int left, final int dx);

    }
}
