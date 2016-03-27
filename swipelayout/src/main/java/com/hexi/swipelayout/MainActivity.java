package com.hexi.swipelayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    SwipeLayout content;

    View roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        roomList = findViewById(R.id.roomer_list);

        content = (SwipeLayout) findViewById(R.id.content);

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "===onClick===");
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
