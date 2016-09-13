package com.vitamio.mediaplayer.view.helper;

import android.support.v4.widget.ScrollerCompat;
import android.util.Log;
import android.view.View;

/**
 * Created by hexi on 16/9/13.
 */
public class ScrollViewHelper {

    private ScrollerCompat mScroller;
    private View view;

    public ScrollViewHelper(View view) {
        this.view = view;
        mScroller = ScrollerCompat.create(view.getContext());
    }

    public void settleViewAt(final int finalLeft, final int finalTop) {
        final int startLeft = view.getLeft();
        final int startTop = view.getTop();
        final int dx = finalLeft - startLeft;
        final int dy = finalTop - startTop;
        Log.d("ScrollViewHelper", String.format("===settleViewAt, finalLeft:%d, left:%d, startLeft:%d", finalLeft, view.getLeft(), startLeft));

        if (dx == 0 && dy == 0) {
            // Nothing to do. Send callbacks, be done.
            mScroller.abortAnimation();
        }

        mScroller.startScroll(startLeft, startTop, dx, dy);
    }
}
