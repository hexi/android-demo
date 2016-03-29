package com.vitamio.mediaplayer.fragment;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vitamio.mediaplayer.MyMediaPlayer;
import com.vitamio.mediaplayer.R;

import java.io.IOException;

/**
 * Created by hexi on 16/3/24.
 */
public class AndroidAudioFragment extends Fragment {

    private static final String TAG = "AndroidAudioFragment";

    private MyMediaPlayer mediaPlayer;

    String path = "http://live.evideocloud.net/live/testaudio__aEmogVx094LY/testaudio__aEmogVx094LY.m3u8";
    //    String path = "http://jorgesys.net/i/irina_delivery@117489/master.m3u8";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_android_audio, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createMediaPlayer();
    }

    private void createMediaPlayer() {
        try {
            Log.d(TAG, "===createMediaPlayer===");
            Uri uri = Uri.parse(path);
            mediaPlayer = new MyMediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            mediaPlayer.setDataSource(getActivity(), uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.setPrepared(true);
                    Log.d(TAG, "===onPrepared===");
                    if (getUserVisibleHint()) {
                        startPlay();
                    }
                }
            });
            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, String.format("===onInfo, what:%d, extra:%d===", what, extra));
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            //Begin buffer, pauseVideo playing
                            if (mediaPlayer.isPlaying()) {
                                pausePlay();
                            }
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            //The buffering is done, resume playing
                            startPlay();
                            break;

                    }
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "===onCompletion, thread:" + Thread.currentThread());
                    mediaPlayer.setPrepared(false);
                    release();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, String.format("===onError, what:%d, extra:%d", what, extra));
                    mediaPlayer.setPrepared(false);
                    switch (extra) {
                        case MyMediaPlayer.MEDIA_ERROR_IO:
                        case MyMediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                            Log.e(TAG, "===音频网路异常===");
                            mediaPlayer.reset();
                            return true;
                        }
                    }
                    return true;
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
    public void onResume() {
        super.onResume();
        boolean isVisibleToUser = getUserVisibleHint();
        Log.d(TAG, "===onResume, isVisibleToUser:" + isVisibleToUser);
        if (isVisibleToUser) {
            startPlay();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "===setUserVisibleHint, isVisibleToUser:" + isVisibleToUser);
        if (isAdded()) {
            if (isVisibleToUser) {
                startPlay();
            } else {
                pausePlay();
            }
        }
    }

    public void pausePlay() {
        Log.d(TAG, "===pause===");
        if (mediaPlayer == null
                || !mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        release();
    }

    private void release() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.release();
        mediaPlayer.setPrepared(false);
        mediaPlayer = null;
    }

    private void startPlay() {
        Log.d(TAG, "===startPlay, isPrepared:" + mediaPlayer.isPrepared());
        if (mediaPlayer.isPrepared()) {
            mediaPlayer.start();
        } else {
            mediaPlayer.prepareAsync();
        }
    }
}
