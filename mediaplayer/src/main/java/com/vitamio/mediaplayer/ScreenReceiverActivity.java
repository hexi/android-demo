package com.vitamio.mediaplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vitamio.mediaplayer.broadcast.ScreenOffOnReceiver;

public class ScreenReceiverActivity extends AppCompatActivity implements ScreenOffOnReceiver.ScreenOnOffListener {
    private static final String TAG = "ScreenReceiverActivity";

    ScreenOffOnReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_receiver);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        receiver = new ScreenOffOnReceiver(this);

        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void screenOn() {
        Log.d(TAG, "===screenOn===");
    }

    @Override
    public void screenOff() {
        Log.d(TAG, "===screenOff===");
    }
}
