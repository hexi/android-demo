package com.vitamio.mediaplayer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/31.
 */
public class VideoService extends Service {
    public static final String INTENT_VIDEO_LAYOUT = "intent_video_layout";
    public static String INTENT_PATH = "intent_path";
    public static final String INTENT_SHOW_MEDIA_CONTROLLER = "intent_show_media_controller";

    public class VideoBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }

    public interface VideoServiceListener {
        void onPrepared(MediaPlayer mp);
        void onBufferingEnd(int extra);
        void onBufferingStart(int extra);
        boolean onError(MediaPlayer mp, int what, int extra);
        void onVideoViewCreated(VideoView videoView);
    }

    private static final String TAG = "VideoService";
    private VideoBinder binder = new VideoBinder();
    private VideoView videoView;
    WifiManager.WifiLock wifiLock;
    VideoServiceListener listener;
    MediaController.OnOrientationChangeListener orientationChangeListener;


    public void setListener(VideoServiceListener listener) {
        this.listener = listener;
    }

    public void setOrientationChangeListener(MediaController.OnOrientationChangeListener orientationChangeListener) {
        this.orientationChangeListener = orientationChangeListener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "===onBind===");

        return binder;
    }

    public void setupVideo(String path, int videoLayout, boolean showController) {
        videoView = new VideoView(getApplicationContext());
        videoView.setVideoPath(path);
        videoView.initVideoLayout(videoLayout);
        videoView.requestFocus();
        if (showController) {
            MediaController mediaController = new MediaController(getApplicationContext());
            if (orientationChangeListener != null) {
                mediaController.setOrientationChangeListener(orientationChangeListener);
            }
            videoView.setMediaController(mediaController);
        }
        videoView.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        wifiLock.acquire();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
                if (listener != null) {
                    listener.onPrepared(mediaPlayer);
                }
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (listener != null) {
                    return listener.onError(mp, what, extra);
                }
                return false;
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //Begin buffer, pauseVideo playing
                        if (listener != null) {
                            listener.onBufferingStart(extra);
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //The buffering is done, resume playing
                        if (listener != null) {
                            listener.onBufferingEnd(extra);
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        //Display video download speed
                        Log.d(TAG, "download rate:" + extra);
                        break;
                }
                return true;
            }
        });

        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                Log.d(TAG, "===onBufferingUpdate, percent:" + percent);
            }
        });
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
        Log.d(TAG, "===pauseVideo===");
        videoView.pause();
    }

    public void release() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "===onCreate===");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "===onStartCommand===");
        release();
        String path = intent.getStringExtra(INTENT_PATH);
        int videoLayout = intent.getIntExtra(INTENT_VIDEO_LAYOUT, VideoView.VIDEO_LAYOUT_FIT_WINDOW_HEIGHT);
        boolean showController = intent.getBooleanExtra(INTENT_SHOW_MEDIA_CONTROLLER, false);
        setupVideo(path, videoLayout, showController);
        if (listener != null) {
            listener.onVideoViewCreated(videoView);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "===onUnbind===");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onUnbind===");
        wifiLock.release();
        release();
    }

}
