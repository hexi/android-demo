package com.example.hexi.canvastest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hexi on 15/10/9.
 */
public class MyBroadcastReceiver extends BroadcastReceiver{
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "===receive my broadcast");
    }
}
