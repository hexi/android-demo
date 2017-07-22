package com.android.example.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by hexi on 2017/7/17.
 */

public class HelloService extends Service {
    private static final String TAG = "HelloService";

    static class HelloBinder extends Binder {

    }

    private HelloBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new HelloBinder();
        Log.d(TAG, "===onCreate===");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "===onStartCommand===");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "===onUnbind===");
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "===onRebind===");
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "===onTaskRemoved===");
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "===onBind===");
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "===onDestroy===");
        super.onDestroy();
    }
}
