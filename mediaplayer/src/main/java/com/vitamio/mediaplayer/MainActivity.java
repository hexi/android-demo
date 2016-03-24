package com.vitamio.mediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MediaPlayer mediaPlayer;

    String path = "http://live.evideocloud.net/live/testaudio__aEmogVx094LY/testaudio__aEmogVx094LY.m3u8";
//    String path = "http://jorgesys.net/i/irina_delivery@117489/master.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMediaPlayer();
    }

    private void createMediaPlayer() {
        try {
            Log.d(TAG, "===createMediaPlayer===");
            Uri uri = Uri.parse(path);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "===onPrepared===");
                    startPlay();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "===onCompletion, thread:" + Thread.currentThread());
                    release();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, String.format("===onError, what:%d, extra:%d", what, extra));
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_IO:
                        case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                            //TODO
                            Toast.makeText(MainActivity.this, "音频网络异常", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    return false;
                }
            });

            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void startPlay() {
        mediaPlayer.start();
    }
}
