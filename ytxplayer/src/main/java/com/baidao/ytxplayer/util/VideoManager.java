package com.baidao.ytxplayer.util;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.baidao.ytxplayer.widget.MediaController;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;


/**
 * Created by hexi on 16/3/31.
 */
public class VideoManager implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "VideoService";

    public static final int LAYOUT_PORTRAIT_FULL_SCREEN = 0;
    public static final int LAYOUT_PORTRAIT_HALL_SCREEN = 1;
    public static final int LAYOUT_LANDSCAPE_FULL_SCREEN = 2;

    public interface VideoServiceListener {
        void onVideoPrepared(PLMediaPlayer mp);
        void onVideoBufferingEnd(int extra);
        boolean onVideoError(PLMediaPlayer mp, int errorCode);

        void onVideoBufferingStart(int extra);
        void onVideoLossFocus();
        void onVideoGainFocus();

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


    private PLVideoTextureView videoView;
    private Param param;
    VideoServiceListener listener;
    MediaController.OnOrientationChangeListener orientationChangeListener;
    private Context context;
    private boolean needReopen;
    private int windowWidth;
    private int windowHeight;

    public void setNeedReopen() {
        needReopen = true;
    }

    public boolean isNeedReopen() {
        return needReopen;
    }

    public VideoManager(Context context, PLVideoTextureView videoView, Param param) {
        this.context = context.getApplicationContext();
        this.videoView = videoView;
        this.param = param;

        Pair<Integer, Integer> res = ScreenResolution.getResolution(context);
        this.windowWidth = res.first.intValue();
        this.windowHeight = res.second.intValue();
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

        if (param.showController) {
            initMediaController();
        }

        initListener();

        videoView.requestFocus();
        videoView.setVideoPath(param.path);
    }

    private void initMediaController() {
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        if (orientationChangeListener != null) {
            mediaController.setOrientationChangeListener(orientationChangeListener);
        }
        videoView.setMediaController(mediaController);
    }

    private void initListener() {
        videoView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (param.videoLayout == LAYOUT_PORTRAIT_HALL_SCREEN) {
                    int videoHeight = bottom - top;
                    int marginTop = windowHeight - (int) (windowHeight/2 + windowHeight*0.118 + videoHeight/2);
                    if (marginTop <= 0) {
                        return;
                    }
                    videoView.setTranslationY(marginTop);
                }
            }
        });

        videoView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer mediaPlayer) {
                Log.d(TAG, "===onPrepared===");
                if (listener != null) {
                    listener.onVideoPrepared(mediaPlayer);
                }
            }
        });

        videoView.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer mp, int errorCode) {
                if (listener != null) {
                    return listener.onVideoError(mp, errorCode);
                }
                return false;
            }
        });

        videoView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer mp, int what, int extra) {
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
                            listener.onVideoBufferingStart(extra);
                        }
                        break;
                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //The buffering is done, resume playing
                        if (listener != null) {
                            listener.onVideoBufferingEnd(extra);
                        }
                        break;
                }
                return true;
            }
        });

        videoView.setOnBufferingUpdateListener(new PLMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(PLMediaPlayer mp, int percent) {
//                Log.d(TAG, "===onBufferingUpdate, percent:" + percent);
            }
        });

        videoView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer mp) {
                Log.d(TAG, "===onCompletion===");
                if (needReopen) {
                    unRegisterFocusListener();
                    initVideoView();
                }
            }
        });
    }

    private void unRegisterFocusListener() {
        try {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(this);
        } catch (Exception e) {
            Log.e(TAG, "abandonAudioFocus error ", e);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause playback
            if (listener != null) {
                listener.onVideoLossFocus();
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
            if (listener != null) {
                listener.onVideoGainFocus();
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Stop playback
            if (listener != null) {
                listener.onVideoLossFocus();
            }
        }
    }

    public void toLandscape() {
        int videoWidth = videoView.getWidth();
        int videoHeight = videoView.getHeight();
        float scaleX = (float) windowWidth / videoHeight;
        float scaleY = (float) windowHeight / videoWidth;
//
        videoView.setTranslationY(-videoView.getTop());
        videoView.setPivotX(0);
        videoView.setPivotY(0);
        videoView.setRotation(90);
        videoView.setTranslationX(windowWidth);
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
        Log.d(TAG, "===startVideo===");
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
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
        unRegisterFocusListener();
    }

    public static boolean isLiveStreaming(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.startsWith("rtmp://")
                || (url.startsWith("http://") && url.endsWith(".m3u8"))
                || (url.startsWith("http://") && url.endsWith(".flv"))) {
            return true;
        }
        return false;
    }
}
