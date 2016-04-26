package com.example.hexi.canvastest.event;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.example.hexi.canvastest.R;

public class DefaultEventProcessActivity extends Activity {

    private static final String TAG = "EventProcessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_event_process);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        String actionName = Utils.getActionName(event);
        Log.d(TAG, "dispatchTouchEvent(start) :"+actionName);
        boolean ret = super.dispatchTouchEvent(event);
        Log.d(TAG, "dispatchTouchEvent( end ) :"+actionName+", ret="+ret);
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String actionName = Utils.getActionName(event);
        Log.d(TAG, "onTouchEvent(start) :"+actionName);
        boolean ret = super.onTouchEvent(event);
        Log.d(TAG, "onTouchEvent( end ) :"+actionName+", ret="+ret);
        return ret;
    }
}
