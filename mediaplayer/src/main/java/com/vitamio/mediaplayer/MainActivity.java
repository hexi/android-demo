package com.vitamio.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.vitamio.mediaplayer.service.AudioService;
import com.vitamio.mediaplayer.vitamio.PlayAudioByVideoView;
import com.vitamio.mediaplayer.vitamio.VitamioAudioActivity;
import com.vitamio.mediaplayer.vitamio.VitamioVideoActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playVitamioVideo(View view) {
        Intent i = new Intent(this, VitamioVideoActivity.class);
        startActivity(i);
    }

    public void playVitamioAudio(View view) {
        Intent intent = new Intent(this, VitamioAudioActivity.class);
        startActivity(intent);
    }

    public void playAudioByVideoView(View view) {
        Intent i = new Intent(this, PlayAudioByVideoView.class);
        startActivity(i);
    }

    public void showLiveRoom(View view) {
        Intent i = new Intent(this, LiveRoomActivity.class);
        startActivity(i);
    }

    public void showHalfLive(View view) {
        Intent intent = new Intent(this, HalfLiveActivity.class);
        startActivity(intent);
    }

    public void playAudio(View view) {
        Intent intent = new Intent(this, PlayAudioActivity.class);
        startActivity(intent);
    }

    String path = "http://live.evideocloud.net/live/testaudio__aEmogVx094LY/testaudio__aEmogVx094LY.m3u8";
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            Log.d(TAG, "===service:" + binder.getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    public void bindService(View view) {
        Log.d(TAG, "===bindService===");
        bindService(new Intent(this, AudioService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService(View view) {
        unbindService(mConnection);
    }

    public void screenReceiver(View view) {
        Intent intent = new Intent(this, ScreenReceiverActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
    }
}
