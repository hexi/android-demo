package com.vitamio.mediaplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baidao.logutil.YtxLog;
import com.baidao.ytxplayer.YtxMediaPlayer;
import com.baidao.ytxplayer.util.VideoManager;
import com.baidao.ytxplayer.widget.MediaController;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.vitamio.mediaplayer.broadcast.ScreenOffOnReceiver;

import java.io.IOException;


/**
 * Created by hexi on 16/3/28.
 */
public class HalfLiveActivity extends FragmentActivity implements VideoManager.VideoServiceListener,
        MediaController.OnOrientationChangeListener,
        ScreenOffOnReceiver.ScreenOnOffListener {

    private static final String TAG = "HalfLiveActivity";
    private static final String INTENT_LIVE_PATH = "intent_live_path";

    ScreenOffOnReceiver screenOffOnReceiver;

    private String videoPath = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
//    private String videoPath = "rtmp://xianctc169.rt1.gensee.com/96e49642a025412c9ef14a4afa6a1f9a_13072_0_8808440164_1478524890_20cbc64c/video";
    PLVideoTextureView videoView;
    VideoManager videoManager;

    private String audioPath = "rtmp://wuhan247.rt1.gensee.com/96e49642a025412c9ef14a4afa6a1f9a_13072_0_8809029162_1478764328_c60ec1d3/audio";
    private YtxMediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===onCreate===");
        setContentView(R.layout.activity_half_live);
        if (!isLiveStreaming(audioPath)) {
            finish();
            return;
        }

        videoView = (PLVideoTextureView) findViewById(R.id.surface_view);

        initVideo();

        createMediaPlayer();

        screenOffOnReceiver = new ScreenOffOnReceiver(HalfLiveActivity.this);
        registerReceiver(screenOffOnReceiver, ScreenOffOnReceiver.getIntentFilter());
    }

    private void createMediaPlayer() {
        AVOptions avOptions = createAVOptions();

        Uri uri = Uri.parse(audioPath);
        mediaPlayer = new YtxMediaPlayer(this.getApplicationContext(), avOptions);
        mediaPlayer.setLooping(true);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(PLMediaPlayer mp) {
                    mediaPlayer.setPrepared(true);
                    YtxLog.d(TAG, "===onAudioPrepared===");
                }
            });
            mediaPlayer.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(PLMediaPlayer mp, int what, int extra) {
                    YtxLog.d(TAG, String.format("===onInfo, what:%d, extra:%d===", what, extra));
                    switch (what) {
                        case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            //Begin buffer, pauseVideo playing
                            Log.d(TAG, "===start buffer===");
                            break;
                        case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            //The buffering is done, resume playing
                            Log.d(TAG, "===end buffer===");
                            break;

                    }
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(PLMediaPlayer mp) {
                    YtxLog.d(TAG, "===onCompletion, thread:" + Thread.currentThread());
                    //这个方法在本地缓存播放完了会回调，对于网络流并不知道什么时候播放结束
                }
            });
            mediaPlayer.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(PLMediaPlayer mp, int errorCode) {
                    YtxLog.d(TAG, String.format("===onAudioError, errorCode:%d", errorCode));
                    mediaPlayer.setPrepared(false);
                    return true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AVOptions createAVOptions() {
        boolean isLiveStreaming = true;
        AVOptions avOptions = new AVOptions();
        // the unit of timeout is ms
        avOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        avOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        avOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming ? 1 : 0);
        // 是否开启"延时优化"，只在在线直播流中有效
        // 默认值是：0
        avOptions.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, isLiveStreaming ? 1 : 0);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = 0;
        avOptions.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // whether start play automatically after prepared, default value is 1
        avOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        return avOptions;
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
        if (!TextUtils.isEmpty(videoPath)) {
            videoView.setVisibility(View.VISIBLE);
            int videoLayout;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                videoLayout = VideoManager.LAYOUT_LANDSCAPE_FULL_SCREEN;
            } else {
                videoLayout = VideoManager.LAYOUT_PORTRAIT_HALL_SCREEN;
            }
            videoManager = new VideoManager(this, videoView,
                    new VideoManager.Param(videoPath, videoLayout, true, true));
            videoManager.setListener(this);
            videoManager.setOrientationChangeListener(this);
            videoManager.initVideoView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INTENT_LIVE_PATH, videoPath);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "===onResume===");
        videoManager.startVideo();
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
        if (videoManager != null) {
            videoManager.release();
            videoManager = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(screenOffOnReceiver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onVideoPrepared(PLMediaPlayer mp) {
        videoManager.startVideo();
    }

    @Override
    public void onVideoBufferingEnd(int extra) {
        videoManager.startVideo();
    }

    @Override
    public void onVideoRenderingStart(int i) {

    }

    @Override
    public void onVideoAudioRenderingStart(int i) {

    }

    @Override
    public void onVideoBufferingStart(int extra) {

    }

    @Override
    public void onVideoLossFocus() {
        pauseVideo(null);
    }

    @Override
    public void onVideoGainFocus() {
        startVideo(null);
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
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        videoManager.toLandscape();
    }

    @Override
    public void toPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        videoManager.toHalfPortrait();
    }

    @Override
    public void screenOn() {

    }

    @Override
    public void screenOff() {
        pauseVideoAndStartAudio();
    }

    private void pauseVideoAndStartAudio() {
        pauseVideo(null);
    }

    public void startVideo(View view) {
        if (videoManager != null) {
            videoManager.startVideo();
        }
    }

    public void pauseVideo(View view) {
        if (videoManager != null) {
            videoManager.pauseVideo();
        }
    }

    public void startAudio(View view) {
        if (mediaPlayer != null
                && mediaPlayer.isPrepared()) {
            Log.d(TAG, "===startAudio===");
            mediaPlayer.start();
        }
    }

    public void closeAudio(View view) {
        if (mediaPlayer !=  null
                && mediaPlayer.isPlaying()) {
            Log.d(TAG, "===closeAudio");
            mediaPlayer.pause();
        }
    }

}
