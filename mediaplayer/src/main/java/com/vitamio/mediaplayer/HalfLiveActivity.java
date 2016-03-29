package com.vitamio.mediaplayer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/28.
 */
public class HalfLiveActivity extends FragmentActivity {
    private static final String TAG = "HalfLiveActivity";

    private static final String INTENT_LIVE_PATH = "intent_live_path";
    private static final String INTENT_AUDIO_PATH = "intent_audio_path";
    private static final String INTENT_ROOM_ID = "intent_room_id";

    private View contentContainer;
    VideoView videoView;

    private String livePath = "rtmp://live1.evideocloud.net/live/test1__8Z2MPDMkP4Nm";
    private String audioPath;
    private long roomId;
    private boolean needResume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_half_live);
        contentContainer = findViewById(R.id.content_container);
        videoView = (VideoView)findViewById(R.id.surface_view);

        initData(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this)) {
            finish();
            return;
        }
        setupVideo(livePath);
    }

    private void initData(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras() != null ? getIntent().getExtras() : savedInstanceState;
        if (bundle != null) {
            livePath = bundle.getString(INTENT_LIVE_PATH);
            audioPath = bundle.getString(INTENT_AUDIO_PATH);
            roomId = bundle.getLong(INTENT_ROOM_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INTENT_LIVE_PATH, livePath);
        outState.putString(INTENT_AUDIO_PATH, audioPath);
        outState.putLong(INTENT_ROOM_ID, roomId);
    }

    public void setupVideo(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        videoView.initVideoLayout(VideoView.VIDEO_LAYOUT_FIT_WINDOW_WIDTH);
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
//                autoPlayVideoByWifi();//TODO
                startVideo();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
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
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, String.format("===onInfo, what:%d, extra:%d===", what, extra));
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //Begin buffer, pauseVideo playing
                        if (videoView.isPlaying()) {
                            pauseVideo();
                            needResume = true;
                        }
//                        showProgressBar();//TODO
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //The buffering is done, resume playing
//                        hideProgressBar();//TODO
                        if (needResume)
                            startVideo();
//                            autoPlayVideoByWifi(); //TODO
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        //Display video download speed
                        Log.d(TAG, "download rate:" + extra);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
        pauseVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    /**
     * 开始播放
     */
    public void startVideo() {
        if (videoView == null) {
            return;
        }
        videoView.start();
    }

    /**
     * 暂停播放
     */
    public void pauseVideo() {
        if (videoView == null) {
            return;
        }
        videoView.pause();
    }

    public void release() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }

}
