package com.vitamio.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.vitamio.mediaplayer.fragment.TestFragment;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showLiveRoom(View view) {
        Intent i = new Intent(this, LiveRoomActivity.class);
        startActivity(i);
    }

    public void showHalfLive(View view) {
        Intent intent = new Intent(this, HalfLiveActivity.class);
        startActivity(intent);
    }

    public void startDragLeftActivity(View view) {
        Intent intent = new Intent(this, DragLeftActivity.class);
        startActivity(intent);
    }

    public void getAudioManager(View view) {
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, "===am:" + am);
    }

    public void screenReceiver(View view) {
        Intent intent = new Intent(this, ScreenReceiverActivity.class);
        startActivity(intent);
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

    }

}
