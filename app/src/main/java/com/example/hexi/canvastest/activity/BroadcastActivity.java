package com.example.hexi.canvastest.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.example.hexi.canvastest.MyBroadcastReceiver;
import com.example.hexi.canvastest.R;

/**
 * Created by hexi on 15/10/9.
 */
public class BroadcastActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter("myBroadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(new MyBroadcastReceiver(), intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent("myBroadcast");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
