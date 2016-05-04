package com.baidao.activitylunchmode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

/**
 * Created by hexi on 16/1/31.
 */
public class BaseActivity extends Activity {
    protected String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===onCreate " + this.toString() + ", taskId:" + getTaskId());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "===onStart " + this.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "===onResume " + this.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause " + this.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop " + this.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy " + this.toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "===onNewIntent " + this.toString());
    }

    public void startStandard(View view) {
        Intent i = new Intent(this, StandardActivity.class);
        startActivity(i);
    }

    public void startSingleTop(View view) {
        Intent i = new Intent(this, SingleTopActivity.class);
        startActivity(i);
    }

    public void startSingleTask(View view) {
        Intent i = new Intent(this, SingleTaskActivity.class);
        startActivity(i);
    }

    public void startInstance(View view) {
        Intent i = new Intent(this, SingleInstanceActivity.class);
        startActivity(i);
    }

    public void startInstance2(View view) {
        Intent i = new Intent(this, SingleInstance2Activity.class);
        startActivity(i);
    }
}
