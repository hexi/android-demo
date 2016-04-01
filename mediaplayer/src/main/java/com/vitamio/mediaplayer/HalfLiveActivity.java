package com.vitamio.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.vitamio.mediaplayer.service.VideoService;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/28.
 */
public class HalfLiveActivity extends FragmentActivity implements VideoService.VideoServiceListener, MediaController.OnOrientationChangeListener {
    private static final String TAG = "HalfLiveActivity";

    private static final String INTENT_LIVE_PATH = "intent_live_path";
    private static final String INTENT_ROOM_ID = "intent_room_id";

    private ViewGroup contentContainer;
    VideoService videoService;
    private boolean bound;

    private String livePath = "rtmp://live1.evideocloud.net/live/test1__8Z2MPDMkP4Nm";

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            VideoService.VideoBinder binder = (VideoService.VideoBinder) service;
            videoService = binder.getService();
            videoService.setListener(HalfLiveActivity.this);
            videoService.setOrientationChangeListener(HalfLiveActivity.this);

            Intent intent = new Intent(HalfLiveActivity.this, VideoService.class);
            intent.putExtra(VideoService.INTENT_PATH, livePath);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                intent.putExtra(VideoService.INTENT_VIDEO_LAYOUT, VideoView.VIDEO_LAYOUT_FIT_PARENT_LAND);
            } else {
                intent.putExtra(VideoService.INTENT_VIDEO_LAYOUT, VideoView.VIDEO_LAYOUT_FIT_WINDOW_WIDTH);
            }
            intent.putExtra(VideoService.INTENT_SHOW_MEDIA_CONTROLLER, true);
            startService(intent);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===onCreate===");
        setContentView(R.layout.activity_half_live);
        contentContainer = (ViewGroup) findViewById(R.id.content_container);
        if (!LibsChecker.checkVitamioLibs(this)) {
            finish();
            return;
        }

        Intent intent = new Intent(this, VideoService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INTENT_LIVE_PATH, livePath);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bound) {
            videoService.startVideo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
//        pauseVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(mConnection);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoService.startVideo();
    }

    @Override
    public void onBufferingEnd(int extra) {
        videoService.startVideo();
    }

    @Override
    public void onBufferingStart(int extra) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, String.format("===video played error, what:%d, extra:%d", what, extra));
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                return true;
            }
            case MediaPlayer.MEDIA_ERROR_UNKNOWN: {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onVideoViewCreated(VideoView videoView) {
        contentContainer.addView(videoView, 0);
    }

    @Override
    public void toLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void toPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
