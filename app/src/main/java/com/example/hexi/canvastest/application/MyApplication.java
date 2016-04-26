package com.example.hexi.canvastest.application;

import android.app.Application;
import android.util.Log;


/**
 * Created by hexi on 16/4/21.
 */
public class MyApplication extends Application implements Foreground.Listener {
    private static final String TAG = "MyApplication";
    Foreground.Binding binding;

    @Override
    public void onCreate() {
        super.onCreate();

        Foreground.init(this);

        if (binding != null) {
            binding.unbind();
        }
        binding = Foreground.get().addListener(this);
    }


    @Override
    public void onBecameForeground() {
        Log.d(TAG, "===onBecameForeground===");
    }

    @Override
    public void onBecameBackground() {
        Log.d(TAG, "===onBecameBackground===");

    }
}
