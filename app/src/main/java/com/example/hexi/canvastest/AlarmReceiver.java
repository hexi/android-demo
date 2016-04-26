package com.example.hexi.canvastest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hexi on 16/4/21.
 */
public class AlarmReceiver extends BroadcastReceiver{
    private static final String TAG = "AlarmReceiver";
    private static final String ACTION_STOP_QUOTATION_SOCKET = "action_stop_quotation_socket";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "===AlarmReceiver===");
    }
}
