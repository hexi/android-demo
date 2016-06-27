package com.vitamio.mediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.vitamio.mediaplayer.view.SwipeLayout;

/**
 * Created by hexi on 16/6/17.
 */
public class SwipeActivity extends Activity implements SwipeLayout.OnSwipeLayoutListener {

    private static final String TAG = "SwipeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe);

        ((SwipeLayout)findViewById(R.id.swipe_layout)).setOnSwipeLayoutListener(this);
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
}
