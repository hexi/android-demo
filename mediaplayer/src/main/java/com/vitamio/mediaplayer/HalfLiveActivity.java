package com.vitamio.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.baidao.ytxplayer.service.AudioService;
import com.baidao.ytxplayer.util.VideoManager;
import com.baidao.ytxplayer.widget.MediaController;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.vitamio.mediaplayer.broadcast.ScreenOffOnReceiver;


/**
 * Created by hexi on 16/3/28.
 */
public class HalfLiveActivity extends FragmentActivity implements VideoManager.VideoServiceListener,
        MediaController.OnOrientationChangeListener,
        ScreenOffOnReceiver.ScreenOnOffListener,
        AudioService.AudioServiceListener {
    private static final String TAG = "HalfLiveActivity";

    private static final String INTENT_LIVE_PATH = "intent_live_path";

    private ViewGroup contentContainer;
    VideoManager videoService;
    ScreenOffOnReceiver screenOffOnReceiver;
    PLVideoTextureView videoView;


    private String livePath = "rtmp://live1.evideocloud.net/live/test1__8Z2MPDMkP4Nm";

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
                audioService.createMediaPlayer(path);
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
        if (!isLiveStreaming(path)) {
            finish();
            return;
        }

        videoView = (PLVideoTextureView) findViewById(R.id.surface_view);

        initVideo();

        screenOffOnReceiver = new ScreenOffOnReceiver(HalfLiveActivity.this);
        registerReceiver(screenOffOnReceiver, ScreenOffOnReceiver.getIntentFilter());
    }

    private boolean isLiveStreaming(String url) {
        if (url.startsWith("rtmp://")
                || (url.startsWith("http://") && url.endsWith(".m3u8"))
                || (url.startsWith("http://") && url.endsWith(".flv"))) {
            return true;
        }
        return false;
    }

    private void initVideo() {
        if (!TextUtils.isEmpty(livePath)) {
            videoView.setVisibility(View.VISIBLE);
            int videoLayout;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                videoLayout = VideoManager.LAYOUT_LANDSCAPE_FULL_SCREEN;
            } else {
                videoLayout = VideoManager.LAYOUT_PORTRAIT_HALL_SCREEN;
            }
            videoService = new VideoManager(this, videoView,
                    new VideoManager.Param(livePath, videoLayout, true));
            videoService.setListener(this);
            videoService.setOrientationChangeListener(this);
            videoService.initVideoView();
        }
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
        videoService.startVideo();
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
        if (videoService != null) {
            videoService.release();
            videoService = null;
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
    public void onVideoPrepared(PLMediaPlayer mp) {
        videoService.startVideo();
    }

    @Override
    public void onVideoBufferingEnd(int extra) {
        videoService.startVideo();
    }

    @Override
    public void onVideoBufferingStart(int extra) {

    }

    @Override
    public void onVideoLossFocus() {
        pauseVideo();
    }

    @Override
    public void onVideoGainFocus() {
        startVideo();
    }

    @Override
    public boolean onVideoError(PLMediaPlayer mp, int errorCode) {
        Log.d(TAG, String.format("===video played error, errorCode:%d", errorCode));
        switch (errorCode) {
            case PLMediaPlayer.ERROR_CODE_IO_ERROR:
            case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
            case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
            case MediaPlayer.MEDIA_ERROR_UNKNOWN: {
                return true;
            }
        }
        return false;
    }

    @Override
    public void toLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        videoService.toLandscape();
    }

    @Override
    public void toPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        videoService.toHalfPortrait();
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
        if (videoService != null) {
            videoService.startVideo();
        }
    }

    public void pauseVideo() {
        if (videoService != null) {
            videoService.pauseVideo();
        }
    }

    @Override
    public void onAudioPrepared(PLMediaPlayer mp) {
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
    public boolean onAudioError(PLMediaPlayer mp, int errorCode) {
        return true;
    }

    @Override
    public void onAudioLossFocus() {
        audioService.pausePlay();
    }

    @Override
    public void onAudioGainFocus() {
        audioService.startPlay();
    }
}
