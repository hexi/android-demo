package com.vitamio.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
    }
}
