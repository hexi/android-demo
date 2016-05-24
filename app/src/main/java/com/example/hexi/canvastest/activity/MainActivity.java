package com.example.hexi.canvastest.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.hexi.canvastest.QuotationSocketManager;
import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.service.TestService;
import com.example.hexi.canvastest.view.NoTradePermissionDialog;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    boolean bound;
    TestService testService;

    public ServiceConnection bindAndStartConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "===onServiceConnected===");
            testService = ((TestService.ServiceBinder) service).getService();
            bound = true;

            Intent intent = createIntent();
            startService(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "===onServiceDisconnected===");
            bound = false;
        }
    };

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "===onServiceConnected===");
            testService = ((TestService.ServiceBinder) service).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "===onServiceDisconnected===");
            bound = false;
        }
    };

    @NonNull
    private Intent createIntent() {
        Intent intent = new Intent(MainActivity.this, TestService.class);
        intent.putExtra("path", "http://www.google.com");
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cr).setClickable(true);
    }

    public void bindServiceAndStart(View view) {
        Intent intent = new Intent(this, TestService.class);
        bindService(intent, bindAndStartConnection, Context.BIND_AUTO_CREATE);
    }

    public void bindService(View view) {
        Intent intent = new Intent(this, TestService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void startServiceByIntent(View view) {
        startService(createIntent());
    }

    public void startServiceNoIntent(View view) {
        startService(new Intent(this, TestService.class));
    }

    public void unbindService(View view) {
        unbindService(connection);
    }

    public void unbindServiceWithStartConn(View view) {
        unbindService(bindAndStartConnection);
    }

    public void showDialog(View view) {
        NoTradePermissionDialog dialog = new NoTradePermissionDialog(this);
        dialog.show();
    }

    public void stopSocket(View view) {
        QuotationSocketManager.stopSocket(this);
    }

    public void cancelStopSocket(View view) {
        QuotationSocketManager.cancelStopSocket(this);
    }

    public void startSelf(View view) {
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop===");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "===onWindowFocusChanged, hasFocus:"+hasFocus);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "===onBackPressed===");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "===onStart===");
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
}
