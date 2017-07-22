package com.android.example.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";

    public static class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "===onServiceConnected===");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "===onServiceDisconnected===");
        }
    }

    ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        connection = new MyServiceConnection();
    }

    public void startService(View v) {
        startService(new Intent(this, HelloService.class));
    }

    public void stopService(View v) {
        stopService(new Intent(this, HelloService.class));
    }

    public void bindService(View v) {
        bindService(new Intent(this, HelloService.class), connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(View v) {
        unbindService(connection);
    }

    public void startActivity2(View v) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    public void destroySelf(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "===onResume===");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "===onStart===");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop===");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
    }
}
