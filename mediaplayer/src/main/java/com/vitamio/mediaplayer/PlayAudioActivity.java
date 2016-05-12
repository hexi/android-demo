package com.vitamio.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vitamio.mediaplayer.fragment.AndroidAudioFragment;
import com.vitamio.mediaplayer.service.AudioService;

public class PlayAudioActivity extends AppCompatActivity {

    private static final String TAG = "PlayAudioActivity";
    boolean bound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            Log.d(TAG, "===service:" + binder.getService());
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "===onServiceDisconnected===");
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_fragment_container, new AndroidAudioFragment())
                .commit();

        bindService();
    }

    public void bindService() {
        bindService(new Intent(this, AudioService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        if (bound) {
            unbindService(mConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        unBindService();
    }
}
