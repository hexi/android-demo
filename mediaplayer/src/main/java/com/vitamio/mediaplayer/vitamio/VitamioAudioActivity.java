package com.vitamio.mediaplayer.vitamio;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

/**
 * Created by hexi on 16/3/24.
 */
public class VitamioAudioActivity extends Activity {
    private static final String TAG = "VitamioAudioActivity";
    String audioPath = "rtmp://live1.evideocloud.net/live/testaudio__aEmogVx094LY";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this)) {
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try {
            mediaPlayer = new MediaPlayer(this);
            mediaPlayer.setDataSource(this, Uri.parse(audioPath));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "===onPrepared===");
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, String.format("===onError, what:%d, extra:%d", what, extra));
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "===onCompletion===");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
