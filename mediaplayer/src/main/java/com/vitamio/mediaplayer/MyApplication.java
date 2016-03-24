package com.vitamio.mediaplayer;

import android.app.Application;

import io.vov.vitamio.Vitamio;

/**
 * Created by hexi on 16/3/24.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Vitamio.initialize(getApplicationContext(), getResources().getIdentifier("libarm", "raw", getPackageName()));
            }
        }).start();
    }
}
