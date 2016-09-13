package com.vitamio.mediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vitamio.mediaplayer.view.SwipeLayout;
import com.vitamio.mediaplayer.view.helper.ScrollViewHelper;

/**
 * Created by hexi on 16/6/17.
 */
public class SwipeActivity extends Activity implements SwipeLayout.OnSwipeLayoutListener, View.OnClickListener {

    private static final String TAG = "SwipeActivity";
    private View showOrHideCommentView;
    private ScrollViewHelper scrollViewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe);

//        findViewById(R.id.bottom_button).setOnClickListener(this);
//        findViewById(R.id.button0).setOnClickListener(this);
//        findViewById(R.id.fuck_button).setOnClickListener(this);

        ((SwipeLayout)findViewById(R.id.swipe_layout)).setOnSwipeLayoutListener(this);
        showOrHideCommentView = findViewById(R.id.rl_show_hide_comment_layout);

        scrollViewHelper = new ScrollViewHelper(showOrHideCommentView);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.bottom_button) {
//            Toast.makeText(this, "bottom button clicked", Toast.LENGTH_SHORT).show();
//        } else if (v.getId() == R.id.button0) {
//            Toast.makeText(this, "我被点击了", Toast.LENGTH_SHORT).show();
//        } else if (v.getId() == R.id.fuck_button) {
//            Toast.makeText(this, "fuck button clicked", Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        showOrHideCommentView.post(new Runnable() {
            @Override
            public void run() {
                int dragLeftWidth = showOrHideCommentView.getMeasuredWidth();
                Log.d(TAG, "===dragLeftWidth:" + dragLeftWidth);

//                int targetLeft = -dragLeftWidth/2;
//                int right = targetLeft + dragLeftWidth;
//                showOrHideCommentView.layout(targetLeft, showOrHideCommentView.getTop(), right, showOrHideCommentView.getBottom());

//                showOrHideCommentView.animate().translationXBy(-dragLeftWidth/2).setDuration(0).start();
//                showOrHideCommentView.animate().x(-dragLeftWidth/2).setDuration(0).start();

                scrollViewHelper.settleViewAt(-dragLeftWidth/2, showOrHideCommentView.getTop());
            }
        });
    }

    @Override
    public void onDragViewUp() {

    }

    @Override
    public void onDragViewDown() {

    }

    @Override
    public void onLeftViewShowing() {
        Log.d(TAG, "===onLeftViewShowing===");
    }

    @Override
    public void onLeftViewShown() {
        Log.d(TAG, "===onLeftViewShown===");
    }

    @Override
    public void onLeftViewHidden() {
        Log.d(TAG, "===onLeftViewShown===");
    }

    @Override
    public void onLeftViewPositionChanged(int left, int top, int dx, int dy) {
        int dragViewLeft = showOrHideCommentView.getLeft();
        float dragX = showOrHideCommentView.getX();
        Log.d(TAG, String.format("===onLeftViewPositionChanged, origin:%d, target:%d, dragX:%f", dragViewLeft, dragViewLeft+dx, dragX));
//        showOrHideCommentView.setX(dragViewLeft);


//        showOrHideCommentView.animate().x(dragX+dx).setDuration(0).start();

        scrollViewHelper.settleViewAt(dragViewLeft+dx, showOrHideCommentView.getTop());
    }
}
