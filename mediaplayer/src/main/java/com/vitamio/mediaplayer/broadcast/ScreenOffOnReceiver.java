package com.vitamio.mediaplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by hexi on 16/4/1.
 */
public class ScreenOffOnReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenOffOnReceiver";

    public interface ScreenOnOffListener {
        void screenOn();
        void screenOff();
    }

    ScreenOnOffListener listener;

    public ScreenOffOnReceiver(ScreenOnOffListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            if (listener != null) {
                listener.screenOn();
            }
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            if (listener != null) {
                listener.screenOff();
            }
        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        return intentFilter;
    }
}
