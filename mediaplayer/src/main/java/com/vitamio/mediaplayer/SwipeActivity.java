package com.vitamio.mediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vitamio.mediaplayer.view.SwipeLayout;

/**
 * Created by hexi on 16/6/17.
 */
public class SwipeActivity extends Activity implements SwipeLayout.OnSwipeLayoutListener, View.OnClickListener {

    private static final String TAG = "SwipeActivity";

    private SwipeLayout swipeLayout;
    private View showOrHideCommentView;
    private View hideLeftView;
    private View showLeftView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe);

        findViewById(R.id.bottom_button).setOnClickListener(this);
        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.fuck_button).setOnClickListener(this);

        swipeLayout = ((SwipeLayout) findViewById(R.id.swipe_layout));
        swipeLayout.setOnSwipeLayoutListener(this);
        showOrHideCommentView = findViewById(R.id.rl_show_hide_comment_layout);
        hideLeftView = findViewById(R.id.rl_hide_comment);
        showLeftView = findViewById(R.id.rl_show_comment);

        initLeftTools();
    }

    private void initLeftTools() {
        showOrHideCommentView.post(new Runnable() {
            @Override
            public void run() {
                int dragLeftWidth = showOrHideCommentView.getMeasuredWidth();
                showOrHideCommentView.animate().xBy(-dragLeftWidth / 2).setDuration(0).start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bottom_button) {
            Toast.makeText(this, "bottom button clicked", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.button0) {
            Toast.makeText(this, "我被点击了", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.fuck_button) {
            Toast.makeText(this, "fuck button clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        hideLeftView.setVisibility(View.INVISIBLE);
        showLeftView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLeftViewHiding() {
        Log.d(TAG, "===onLeftViewShowing===");
        hideLeftView.setVisibility(View.VISIBLE);
        showLeftView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLeftViewShown() {
        Log.d(TAG, "===onLeftViewShown===");
        hideLeftView.setVisibility(View.VISIBLE);
        showLeftView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLeftViewHidden() {
        Log.d(TAG, "===onLeftViewShown===");
        hideLeftView.setVisibility(View.INVISIBLE);
        showLeftView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLeftViewPositionChanged(final int left, final int dx) {
        Log.d(TAG, String.format("===onLeftViewPositionChanged, left:%d, dx:%d", left, dx));

        showOrHideCommentView.animate().xBy(dx).setDuration(0).start();
    }
}
