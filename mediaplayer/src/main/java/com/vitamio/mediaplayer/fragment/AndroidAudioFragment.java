package com.vitamio.mediaplayer.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vitamio.mediaplayer.MyMediaPlayer;
import com.vitamio.mediaplayer.R;
import com.vitamio.mediaplayer.service.AudioService;
import com.vitamio.mediaplayer.service.VideoService;

import java.io.IOException;

import io.vov.vitamio.widget.VideoView;


/**
 * Created by hexi on 16/3/24.
 */
public class AndroidAudioFragment extends Fragment implements View.OnClickListener, AudioService.AudioServiceListener {

    private static final String TAG = "AndroidAudioFragment";


    String path = "http://live.evideocloud.net/live/testaudio__aEmogVx094LY/testaudio__aEmogVx094LY.m3u8";

    AudioService audioService;

    private boolean bound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            audioService.setAudioServiceListener(AndroidAudioFragment.this);
            Log.d(TAG, "===service:" + audioService);

            Intent intent = new Intent(getActivity(), AudioService.class);
            intent.putExtra(AudioService.INTENT_PATH, path);
            getActivity().startService(intent);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_android_audio, container, false);

        view.findViewById(R.id.bindService).setOnClickListener(this);
        view.findViewById(R.id.startService).setOnClickListener(this);
        view.findViewById(R.id.unBindService).setOnClickListener(this);
        view.findViewById(R.id.stopService).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bindService) {
            bindService(v);
        } else if (v.getId() == R.id.startService) {
            startService(v);
        } else if (v.getId() == R.id.unBindService) {
            getActivity().unbindService(mConnection);
        } else if (v.getId() == R.id.stopService) {
            getActivity().stopService(new Intent(this.getActivity(), AudioService.class));
        }
    }

    public void bindService(View view) {
        Log.d(TAG, "===bindService===");
        Intent intent = new Intent(this.getActivity(), AudioService.class);
        intent.putExtra(AudioService.INTENT_PATH, path);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void startService(View view) {
        Log.d(TAG, "===startService===");
        Intent intent = new Intent(this.getActivity(), AudioService.class);
        intent.putExtra(AudioService.INTENT_PATH, path);
        getActivity().startService(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().bindService(new Intent(this.getActivity(), AudioService.class), mConnection, Context.BIND_AUTO_CREATE);
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

    private void startPlay() {
        if (bound) {
            audioService.startPlay();
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

    private void pausePlay() {
        if (bound) {
            audioService.pausePlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        if (bound) {
            getActivity().unbindService(mConnection);
            getActivity().stopService(new Intent(getActivity().getApplicationContext(), AudioService.class));
        }
    }

    @Override
    public void onAudioPrepared(MediaPlayer mp) {
        startPlay();
    }

    @Override
    public void onAudioBufferingEnd(int extra) {
        startPlay();
    }

    @Override
    public void onAudioBufferingStart(int extra) {

    }

    @Override
    public boolean onAudioError(MediaPlayer mp, int what, int extra) {
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                pausePlay();
                return true;
            }
            default:
                return false;
        }
    }
}
