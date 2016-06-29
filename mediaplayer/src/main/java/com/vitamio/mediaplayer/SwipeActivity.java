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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe);

        findViewById(R.id.bottom_button).setOnClickListener(this);
        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.fuck_button).setOnClickListener(this);

        ((SwipeLayout)findViewById(R.id.swipe_layout)).setOnSwipeLayoutListener(this);
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
