package com.hexi.activitydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by hexi on 2017/7/23.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected String tag = this.getClass().getName() + this.hashCode();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("===onCreate===");
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
    protected void onPostResume() {
        super.onPostResume();
        log("===onPostResume===");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        log("===onSaveInstanceState===");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        log("===onBackPressed===");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        log("===onAttachFragment===");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        log("===onRestart===");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("===onDestroy===");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        log("===onNewIntent===");
    }

    private void log(String msg) {
        Log.d(tag, msg);
    }

}
