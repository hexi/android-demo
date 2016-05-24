package com.example.hexi.canvastest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by hexi on 16/5/24.
 */
public class TestService extends Service {
    private static final String TAG = "TestService";

    public class ServiceBinder extends Binder {
        public TestService getService() {
            return TestService.this;
        }
    }

    ServiceBinder binder = new ServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "===onBind, " + formatIntent(intent));
        return binder;
    }

    private String formatIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String path = null;
        if (bundle != null) {
            path = bundle.getString("path");
        }
        return String.format("intent:%s, path:%s", intent.toString(), path);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "===onCreate===");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "===onStartCommand, " + formatIntent(intent));
        return ret;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean ret = super.onUnbind(intent);
        Log.d(TAG, "===onUnbind, " + formatIntent(intent));
        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
    }
}
