package com.example.hexi.canvastest.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.hexi.canvastest.QuotationSocketManager;
import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.view.NoTradePermissionDialog;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showDialog(View view) {
        NoTradePermissionDialog dialog = new NoTradePermissionDialog(this);
        dialog.show();
    }

    public void stopSocket(View view) {
        QuotationSocketManager.stopSocket(this);
    }

    public void cancelStopSocket(View view) {
        QuotationSocketManager.cancelStopSocket(this);
    }

    public void startSelf(View view) {
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop===");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "===onWindowFocusChanged, hasFocus:"+hasFocus);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "===onBackPressed===");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "===onStart===");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "===onResume===");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
    }
}
