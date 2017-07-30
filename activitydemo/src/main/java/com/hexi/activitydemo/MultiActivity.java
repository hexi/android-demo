package com.hexi.activitydemo;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by hexi on 2017/7/24.
 */

public class MultiActivity extends ActivityGroup {
    private String tag = "MultiActivity";

    LinearLayout layout;
    LinearLayout layout_s1;
    LinearLayout layout_s2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("===onCreate===");

        setContentView(R.layout.multiview);
        layout = (LinearLayout) findViewById(R.id.multiview_layout);
        layout_s1 = (LinearLayout) findViewById(R.id.my_view_1);
        layout_s2 = (LinearLayout) findViewById(R.id.my_view_2);

        LocalActivityManager mgr = getLocalActivityManager();

        layout_s1.addView((mgr.startActivity("MyOtherActivityInstance1", new Intent(this, MyOtherActivity1.class))).getDecorView());
        layout_s2.addView((mgr.startActivity("MyOtherActivityInstance2", new Intent(this, MyOtherActivity2.class))).getDecorView());

    }

    @Override
    protected void onStart() {
        super.onStart();
        log("===onStart===");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("===onResume===");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("===onPause===");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("===onStop===");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("===onDestroy===");
    }

    private void log(String msg) {
        Log.d(tag, msg);
    }
}
