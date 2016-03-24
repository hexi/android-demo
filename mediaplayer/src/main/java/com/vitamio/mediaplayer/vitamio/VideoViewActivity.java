package com.vitamio.mediaplayer.vitamio;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.vitamio.mediaplayer.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/24.
 */
public class VideoViewActivity extends Activity {
    private static final String TAG = "VitamioAudioActivity";
//    String audioPath = "rtmp://live1.evideocloud.net/live/testaudio__aEmogVx094LY";
    String audioPath = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        videoView = new VideoView(this);
//        videoView = (VideoView) findViewById(R.id.surface_view);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_video_container);
        frameLayout.addView(videoView);

        videoView.setVideoPath(audioPath);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "===onPrepared===");
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
                Log.d(TAG, String.format("===onError, what:%d, extra:%d", what, extra));
                return true;
            }
        });
    }
}
