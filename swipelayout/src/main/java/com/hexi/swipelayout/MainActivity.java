package com.hexi.swipelayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    SwipeLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = (SwipeLayout) findViewById(R.id.content);

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "===onClick===");
            }
        });

        content.setOnSwipeGesture(new OnSwipeListener() {
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                Log.d(TAG, "===swipeBottom===");
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                Log.d(TAG, "===onSwipeTop===");
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Log.d(TAG, "===onSwipeLeft===");
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Log.d(TAG, "===onSwipeRight===");
            }


        });

        content.setOnSlideListener(new SlideDetector.OnSlideListener() {
            @Override
            public void slideComplete(String direction, float distance) {
                Log.d(TAG, String.format("===slideComplete, direction:%s, distance:%f", direction, distance));
            }

            @Override
            public void sliding(String direction, float distance) {
                Log.d(TAG, String.format("===sliding, direction:%s, distance:%f", direction, distance));
            }
        });
    }


}
