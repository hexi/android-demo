package com.example.hexi.canvastest;

import android.app.Application;
import android.util.Log;

import org.joda.time.DateTime;

/**
 * Created by hexi on 15/11/29.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "===onCreate, time:"+ DateTime.now());
    }
}
