package com.vitamio.mediaplayer.fragment;

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

import com.vitamio.mediaplayer.R;

import java.io.IOException;

/**
 * Created by hexi on 16/3/24.
 */
public class AndroidAudioFragment extends Fragment {

    private static final String TAG = "AndroidAudioFragment";

    private MediaPlayer mediaPlayer;

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
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getActivity(), uri);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "===onPrepared===");
                    startPlay();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "===onCompletion, thread:" + Thread.currentThread());
                    release();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, String.format("===onError, what:%d, extra:%d", what, extra));
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_IO:
                        case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                            //TODO
                            Toast.makeText(getActivity(), "音频网络异常", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    return false;
                }
            });

            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        release();
    }

    private void release() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void startPlay() {
        mediaPlayer.start();
    }
}
