package com.android.example.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
