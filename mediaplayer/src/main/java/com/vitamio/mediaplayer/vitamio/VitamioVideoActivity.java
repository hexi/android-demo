package com.vitamio.mediaplayer.vitamio;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.vitamio.mediaplayer.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/24.
 */
public class VitamioVideoActivity extends Activity {
    private static final String TAG = "VitamioAudioActivity";
    String path = "rtmp://live1.evideocloud.net/live/test1__8Z2MPDMkP4Nm";
    VideoView videoView;
    ProgressBar progress;
    private boolean needResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        setContentView(R.layout.activity_video_view);
        videoView = (VideoView) findViewById(R.id.surface_view);
        progress = (ProgressBar) findViewById(R.id.progress);


//        videoView = new VideoView(this);
//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_video_container);
//        frameLayout.addView(videoView);

        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "===onAudioPrepared===");
                startVideo();
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //Begin buffer, pauseVideo playing
                        if (videoView.isPlaying()) {
                            needResume = true;
                            pauseVideo();
                        }
                        showProgressBar();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //The buffering is done, resume playing
                        hideProgressBar();
                        if (needResume)
                            startVideo();
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        //Display video download speed
//                        Log.d(TAG, "download rate:" + extra);
                        break;
                }
                return true;
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

    private void startVideo() {
        videoView.start();
    }

    private void hideProgressBar() {
        this.progress.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        this.progress.setVisibility(View.VISIBLE);
    }

    private void pauseVideo() {
        videoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        videoView.stopPlayback();
        videoView = null;
    }
}
