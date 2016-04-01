package com.vitamio.mediaplayer.vitamio;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.vitamio.mediaplayer.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/24.
 */
public class PlayAudioByVideoView extends Activity {
    private static final String TAG = "VideoViewToPlayAudio";

    String audioPath = "rtmp://live1.evideocloud.net/live/testaudio__aEmogVx094LY";
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio_by_video_view);

        videoView = new VideoView(this);
//        FrameLayout fl = (FrameLayout)findViewById(R.id.surface_container);
//        fl.addView(videoView);
//        fl.setVisibility(View.GONE);

        videoView.setVideoPath(audioPath);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "===onAudioPrepared===");
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "===onCompletion===");
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, String.format("===onAudioError, what:%d, extra:%d", what, extra));
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        videoView.stopPlayback();
        videoView = null;
    }
}
