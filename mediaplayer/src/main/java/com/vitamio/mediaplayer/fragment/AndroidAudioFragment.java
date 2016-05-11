package com.vitamio.mediaplayer.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;

import com.vitamio.mediaplayer.R;
import com.vitamio.mediaplayer.service.AudioService;


/**
 * Created by hexi on 16/3/24.
 */
public class AndroidAudioFragment extends Fragment implements AudioService.AudioServiceListener {

    private static final String TAG = "AndroidAudioFragment";

    String path = "http://ytxhls.evideocloud.net/live/audio_ytxlive1__8X7KVoa865E0/audio_ytxlive1__8X7KVoa865E0.m3u8";

    View audioStateContainer;
    ImageView audioStateView;
    private boolean isAudioPlaying;
    private boolean needPlayAudio;

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

            audioService.createMediaPlayer(path);
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

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().bindService(new Intent(this.getActivity(), AudioService.class), mConnection, Context.BIND_AUTO_CREATE);

        createPlayerButton();
    }

    private void createPlayerButton() {
        View root = getView();
        audioStateContainer = root.findViewById(R.id.iv_audio_state_container);
        audioStateView = (ImageView) audioStateContainer.findViewById(R.id.iv_audio_state);
        audioStateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAudioStateClicked(v);
            }
        });
    }

    public void onAudioStateClicked(View view) {
        Log.d(TAG, "===onAudioStateClicked===");
        if (isAudioPlaying) {
            needPlayAudio = false;
            pausePlay();
        } else {
            needPlayAudio = true;
            startPlay();
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

    private void startPlay() {
        if (!bound) {
            return;
        }
        if (needPlayAudio && audioService.startPlay()) {
            audioStateView.setBackgroundDrawable(getResources().getDrawable(R.drawable.audio_state_playing));
            audioStateView.setImageResource(R.drawable.audio_playing);
            ((Animatable) audioStateView.getDrawable()).start();
            isAudioPlaying = true;
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
        if (!bound) {
            return;
        }
        Log.d(TAG, "===pausePlay===");
        audioService.pausePlay();
        audioStateView.setImageResource(R.drawable.audio_state_paused);
        audioStateView.setBackgroundColor(Color.TRANSPARENT);
        isAudioPlaying = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        unbindAudioService();
    }

    private void unbindAudioService() {
        try {
            if (bound) {
                getActivity().unbindService(mConnection);
                audioService = null;
                bound = false;
                isAudioPlaying = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioPrepared(MediaPlayer mp) {
        Log.d(TAG, "===onAudioPrepared, visibleToUser:" + getUserVisibleHint());
        if (getUserVisibleHint()) {
            startPlay();
        }
    }

    @Override
    public void onAudioBufferingEnd(int extra) {
        if (getUserVisibleHint()) {
            startPlay();
        }
    }

    @Override
    public void onAudioBufferingStart(int extra) {

    }

    @Override
    public boolean onAudioError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, String.format("===onAudioError, what:%d, extra:%d", what, extra));
        switch (extra) {
//            case MediaPlayer.MEDIA_ERROR_IO:
//            case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
//                pausePlay();
//                return true;
//            }
//            default:
//                return false;
        }
        pausePlay();
        return true;
    }
}
