package com.vitamio.mediaplayer.service;

import android.content.Context;
import android.util.Log;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/31.
 */
public class VideoService {
    private static final String TAG = "VideoService";

    public interface VideoServiceListener {
        void onPrepared(MediaPlayer mp);
        void onBufferingEnd(int extra);
        void onBufferingStart(int extra);
        boolean onError(MediaPlayer mp, int what, int extra);
    }

    public static class Param {
        public String path;
        public int videoLayout;
        public boolean showController;

        public Param(String path, int videoLayout, boolean showController) {
            this.path = path;
            this.videoLayout = videoLayout;
            this.showController = showController;
        }

        public Param(String path, int videoLayout) {
            this.path = path;
            this.videoLayout = videoLayout;
        }
    }


    private VideoView videoView;
    private Param param;
    VideoServiceListener listener;
    MediaController.OnOrientationChangeListener orientationChangeListener;
    private Context context;
    private boolean needReopen;

    public void setNeedReopen() {
        needReopen = true;
    }

    public boolean isNeedReopen() {
        return needReopen;
    }

    public VideoService(Context context, VideoView videoView, Param param) {
        this.context = context.getApplicationContext();
        this.videoView = videoView;
        this.param = param;
    }


    public void setListener(VideoServiceListener listener) {
        this.listener = listener;
    }

    public void setOrientationChangeListener(MediaController.OnOrientationChangeListener orientationChangeListener) {
        this.orientationChangeListener = orientationChangeListener;
    }

    public void initVideoView() {
        needReopen = false;
        videoView.setVideoPath(param.path);
        videoView.setHardwareDecoder(true);
        videoView.initVideoLayout(param.videoLayout);
        videoView.requestFocus();
        if (param.showController) {
            MediaController mediaController = new MediaController(context);
            if (orientationChangeListener != null) {
                mediaController.setOrientationChangeListener(orientationChangeListener);
            }
            videoView.setMediaController(mediaController);
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "===onPrepared===");
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
//                Log.d(TAG, String.format("===onInfo, what:%d, extra:%d", what, extra));
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:

                        break;
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

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "===onCompletion===");
                if (needReopen) {
                    initVideoView();
                }
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

}
