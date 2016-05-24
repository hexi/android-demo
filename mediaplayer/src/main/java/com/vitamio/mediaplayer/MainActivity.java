package com.vitamio.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.vitamio.mediaplayer.fragment.TestFragment;
import com.vitamio.mediaplayer.service.AudioService;
import com.vitamio.mediaplayer.vitamio.PlayAudioByVideoView;
import com.vitamio.mediaplayer.vitamio.VitamioAudioActivity;
import com.vitamio.mediaplayer.vitamio.VitamioVideoActivity;

public class MainActivity extends FragmentActivity {

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

    public void getAudioManager(View view) {
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, "===am:" + am);
    }

    public void playAudio(View view) {
        Intent intent = new Intent(this, PlayAudioActivity.class);
        startActivity(intent);
    }

    boolean bound;
    AudioService audioService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            Log.d(TAG, "===service:" + audioService);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "===onServiceDisconnected===");
            bound = false;
        }
    };

    public void bindService(View view) {
        Log.d(TAG, "===bindService===");
        bindService(new Intent(this, AudioService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService(View view) {
        unbindService();
    }

    public void screenReceiver(View view) {
        Intent intent = new Intent(this, ScreenReceiverActivity.class);
        startActivity(intent);
    }

    public void finishService(View view) {
        unbindService();
        stopService(new Intent(this, AudioService.class));
    }

    TestFragment testFragment;

    public void testArguments(View view) {
        testFragment = new TestFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, testFragment)
                .commit();
    }

    public void detachFragment(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(testFragment).commit();
    }

    public void removeFragment(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(testFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        unbindService();

    }

    private void unbindService() {
        if (bound) {
            bound = false;
            unbindService(mConnection);
        }
    }
}
