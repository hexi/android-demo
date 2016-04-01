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

import com.vitamio.mediaplayer.broadcast.ScreenOffOnReceiver;
import com.vitamio.mediaplayer.service.AudioService;
import com.vitamio.mediaplayer.service.VideoService;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/28.
 */
public class HalfLiveActivity extends FragmentActivity implements VideoService.VideoServiceListener, MediaController.OnOrientationChangeListener, ScreenOffOnReceiver.ScreenOnOffListener, AudioService.AudioServiceListener {
    private static final String TAG = "HalfLiveActivity";

    private static final String INTENT_LIVE_PATH = "intent_live_path";

    private ViewGroup contentContainer;
    VideoService videoService;
    private boolean bound;
    ScreenOffOnReceiver screenOffOnReceiver;


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

    String path = "http://live.evideocloud.net/live/testaudio__aEmogVx094LY/testaudio__aEmogVx094LY.m3u8";

    AudioService audioService;
    private boolean audioBound;
    private ServiceConnection audioConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            audioService.setAudioServiceListener(HalfLiveActivity.this);

            if (!audioService.isMediaPlayerCreated()) {
                Intent intent = new Intent(HalfLiveActivity.this, AudioService.class);
                intent.putExtra(AudioService.INTENT_PATH, path);
                startService(intent);
            } else {
                audioService.startPlay();
            }
            audioBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            audioBound = false;
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

        screenOffOnReceiver = new ScreenOffOnReceiver(HalfLiveActivity.this);
        registerReceiver(screenOffOnReceiver, ScreenOffOnReceiver.getIntentFilter());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INTENT_LIVE_PATH, livePath);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "===onResume===");
        if (bound) {
            videoService.startVideo();
        }
        if (audioBound) {
            audioService.pausePlay();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop===");
        if (!AppUtil.isAppOnForeground(getApplicationContext())) {
            Log.d(TAG, "===app is on background===");
            pauseVideoAndStartAudio();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(mConnection);
            stopService(new Intent(this, VideoService.class));
        }
        if (audioBound) {
            unbindService(audioConnection);
            stopService(new Intent(this, AudioService.class));
        }
        unregisterReceiver(screenOffOnReceiver);
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

    @Override
    public void screenOn() {

    }

    @Override
    public void screenOff() {
        pauseVideoAndStartAudio();
    }

    private void pauseVideoAndStartAudio() {
        pauseVideo();
        if (audioBound) {
            audioService.startPlay();
        } else {
            Intent intent = new Intent(this, AudioService.class);
            bindService(intent, audioConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void startVideo() {
        if (bound) {
            videoService.startVideo();
        }
    }

    public void pauseVideo() {
        if (bound) {
            videoService.pauseVideo();
        }
    }

    @Override
    public void onAudioPrepared(android.media.MediaPlayer mp) {
        audioService.startPlay();
    }

    @Override
    public void onAudioBufferingEnd(int extra) {
        audioService.startPlay();
    }

    @Override
    public void onAudioBufferingStart(int extra) {

    }

    @Override
    public boolean onAudioError(android.media.MediaPlayer mp, int what, int extra) {
        return true;
    }
}
