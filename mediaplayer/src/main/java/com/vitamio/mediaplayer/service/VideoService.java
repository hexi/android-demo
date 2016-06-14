package com.vitamio.mediaplayer.service;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.baidao.ytxplayer.util.ScreenResolution;
import com.baidao.ytxplayer.widget.MediaController;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;


/**
 * Created by hexi on 16/3/31.
 */
public class VideoService {
    private static final String TAG = "VideoService";
    public static final int LAYOUT_PORTRAIT_FULL_SCREEN = 0;
    public static final int LAYOUT_PORTRAIT_HALL_SCREEN = 1;
    public static final int LAYOUT_LANDSCAPE_FULL_SCREEN = 2;

    public interface VideoServiceListener {
        void onPrepared(PLMediaPlayer mp);

        void onBufferingEnd(int extra);

        void onBufferingStart(int extra);

        boolean onError(PLMediaPlayer mp, int errorCode);
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

        public Param(String path, boolean showController) {
            this.path = path;
            this.showController = showController;
        }
    }


    private PLVideoTextureView videoView;
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

    public VideoService(Context context, PLVideoTextureView videoView, Param param) {
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
        AVOptions options = new AVOptions();
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, 1);
        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
        videoView.setAVOptions(options);

        if (param.videoLayout == LAYOUT_PORTRAIT_HALL_SCREEN) {
            videoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
        } else if (param.videoLayout == LAYOUT_LANDSCAPE_FULL_SCREEN) {
            videoView.setDisplayOrientation(0);
            videoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        } else {
            videoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        }
        videoView.requestFocus();
        videoView.setVideoPath(param.path);

        if (param.showController) {
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(videoView);
            if (orientationChangeListener != null) {
                mediaController.setOrientationChangeListener(orientationChangeListener);
            }
            videoView.setMediaController(mediaController);
        }

        videoView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer mediaPlayer) {
                Log.d(TAG, "===onPrepared===");
                if (listener != null) {
                    listener.onPrepared(mediaPlayer);
                }
            }
        });

        videoView.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer mp, int errorCode) {
                if (listener != null) {
                    return listener.onError(mp, errorCode);
                }
                return false;
            }
        });

        videoView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
                Log.d(TAG, String.format("===onInfo, what:%d, extra:%d", what, extra));
                switch (what) {
                    case PLMediaPlayer.MEDIA_INFO_UNKNOWN:
                        //未知消息
                        break;

                    case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //第一帧视频已成功渲染
                        break;

                    case PLMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        //第一帧音频已成功播放
                        break;

                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //Begin buffer, pauseVideo playing
                        if (listener != null) {
                            listener.onBufferingStart(extra);
                        }
                        break;
                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //The buffering is done, resume playing
                        if (listener != null) {
                            listener.onBufferingEnd(extra);
                        }
                        break;
                }
                return true;
            }
        });

        videoView.setOnBufferingUpdateListener(new PLMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int percent) {
                Log.d(TAG, "===onBufferingUpdate, percent:" + percent);
            }
        });

        videoView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer mp) {
                Log.d(TAG, "===onCompletion===");
                if (needReopen) {
                    initVideoView();
                }
            }
        });

    }

    public void toLandscape() {
        int videoWidth = videoView.getWidth();
        int videoHeight = videoView.getHeight();
        Pair<Integer, Integer> res = ScreenResolution.getResolution(videoView.getContext());
        int windowWidth = res.first.intValue();
        int windowHeight = res.second.intValue();
        float scaleX = (float) windowWidth / videoHeight;
        float scaleY = (float) windowHeight / videoWidth;

        float tx = windowWidth - videoHeight;
        float ty = windowHeight - videoWidth;

        Log.d(TAG, String.format("===toLandscape, videoWidth:%d, videoHeight:%d, " +
                        "windowWidth:%d, windowHeight:%d, scaleX:%f, scaleY:%f, " +
                "tx:%f, ty:%f", videoWidth, videoHeight
                , windowWidth, windowHeight, scaleX, scaleY, tx, ty));

        videoView.setDisplayOrientation(270);

        videoView.setTranslationX(tx);

        videoView.setScaleX(scaleX);
        videoView.setScaleY(scaleY);
    }

    public void toHalfPortrait() {
        videoView.setDisplayOrientation(-90);
        videoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
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
