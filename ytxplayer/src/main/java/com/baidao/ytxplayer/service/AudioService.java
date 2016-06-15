package com.baidao.ytxplayer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidao.ytxplayer.MyMediaPlayer;
import com.pili.pldroid.player.PLMediaPlayer;

import java.io.IOException;

/**
 * Created by hexi on 16/3/31.
 */
public class AudioService extends Service implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "AudioService";

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
    public interface AudioServiceListener {
        void onAudioPrepared(PLMediaPlayer mp);
        void onAudioBufferingEnd(int extra);
        void onAudioBufferingStart(int extra);
        boolean onAudioError(PLMediaPlayer mp, int errorCode);

        void onAudioLossFocus();
        void onAudioGainFocus();
    }

    private MyMediaPlayer mediaPlayer;
    AudioBinder binder = new AudioBinder();
    WifiLock wifiLock;
    AudioServiceListener audioServiceListener;
    private String path;
    private boolean needResume;

    public void setAudioServiceListener(AudioServiceListener audioServiceListener) {
        this.audioServiceListener = audioServiceListener;
    }

    public boolean isMediaPlayerCreated() {
        return mediaPlayer != null;
    }

    public void setPath(String path) {
        this.path = path;
        this.needResume = true;
        if (mediaPlayer != null) {
            mediaPlayer.setPrepared(false);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "===onBind===");

        return binder;
    }

    public void createMediaPlayer(String path) {
        try {
            Log.d(TAG, "===createMediaPlayer===");
            this.path = path;
            this.needResume = false;
            Uri uri = Uri.parse(path);
            mediaPlayer = new MyMediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
            wifiLock.acquire();

            mediaPlayer.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(PLMediaPlayer mp) {
                    mediaPlayer.setPrepared(true);
                    Log.d(TAG, "===onAudioPrepared===");
                    if (audioServiceListener != null) {
                        audioServiceListener.onAudioPrepared(mp);
                    }
                }
            });
            mediaPlayer.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(PLMediaPlayer mp, int what, int extra) {
                    Log.d(TAG, String.format("===onInfo, what:%d, extra:%d===", what, extra));
                    switch (what) {
                        case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            //Begin buffer, pauseVideo playing
                            if (audioServiceListener != null) {
                                audioServiceListener.onAudioBufferingStart(extra);
                            }
                            break;
                        case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            //The buffering is done, resume playing
                            if (audioServiceListener != null) {
                                audioServiceListener.onAudioBufferingEnd(extra);
                            }
                            break;

                    }
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(PLMediaPlayer mp) {
                    Log.d(TAG, "===onCompletion, thread:" + Thread.currentThread());
                    //这个方法在本地缓存播放完了会回调，对于网络流并不知道什么时候播放结束
                }
            });
            mediaPlayer.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(PLMediaPlayer mp, int errorCode) {
                    Log.d(TAG, String.format("===onAudioError, errorCode:%d", errorCode));
                    mediaPlayer.setPrepared(false);
                    needResume = true;
                    if (audioServiceListener != null) {
                        return audioServiceListener.onAudioError(mp, errorCode);
                    } else {
                        return false;
                    }
                }
            });

        } catch (IOException e) {
            if (mediaPlayer != null) {
                mediaPlayer.setPrepared(false);
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause playback
            if (audioServiceListener != null) {
                audioServiceListener.onAudioLossFocus();
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
            if (audioServiceListener != null) {
                audioServiceListener.onAudioGainFocus();
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Stop playback
            if (audioServiceListener != null) {
                audioServiceListener.onAudioLossFocus();
            }
        }
    }

    public boolean startPlay() {
        Log.d(TAG, "===startPlay===");
        if (mediaPlayer == null) {
            return false;
        }
        if (needResume) {
            return resume();
        }
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return false;
        }
        if (mediaPlayer.isPrepared()) {
            mediaPlayer.start();
            return true;
        }
        return false;
    }

    public void pausePlay() {
        Log.d(TAG, "===pause===");
        if (mediaPlayer == null
                || !mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer.pause();
    }


    public boolean resume() {
        if (mediaPlayer == null || !needResume) {
            return false;
        }
        unRegisterFocusListener();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
            mediaPlayer.prepareAsync();
            needResume = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void unRegisterFocusListener() {
        try {
            AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(this);
        } catch (Exception e) {
            Log.e(TAG, "abandonAudioFocus error ", e);
        }
    }

    private void release() {
        if (mediaPlayer == null) {
            return;
        }
        unRegisterFocusListener();
        mediaPlayer.release();
        mediaPlayer.setPrepared(false);
        mediaPlayer = null;

        if (wifiLock != null) {
            wifiLock.release();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "===onCreate===");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "===onUnbind===");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        release();
        audioServiceListener = null;
    }

}
