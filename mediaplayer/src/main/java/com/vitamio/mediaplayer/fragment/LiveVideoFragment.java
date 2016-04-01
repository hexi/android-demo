package com.vitamio.mediaplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vitamio.mediaplayer.R;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by hexi on 16/3/17.
 */
public class LiveVideoFragment extends Fragment {
    private static final String TAG = "LiveVideoFragment";
    public static final String INTENT_PATH = "intent.path";

    VideoView videoView;
    ProgressBar progress;

    String path = "rtmp://live1.evideocloud.net/live/test1__8Z2MPDMkP4Nm";
    private boolean needResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_live, container, false);
        videoView = (VideoView) view.findViewById(R.id.surface);
        progress = (ProgressBar) view.findViewById(R.id.progress);

//        this.path = retrievePath(getArguments() != null ? getArguments() : savedInstanceState);

        view.findViewById(R.id.button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "===drag view clicked===");
                Toast.makeText(getActivity(), "我被点了", Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "===screen clicked===");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(getActivity())) {
            Log.e(TAG, "checkVitamioLibs return false");
            return;
        }
        setupVideo(path);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }

    private String retrievePath(Bundle bundle) {
        if (bundle != null) {
            return bundle.getString(INTENT_PATH);
        }
        return null;
    }

    public void setupVideo(String path) {
        Log.e(TAG, "Video path: " + path);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "===onPrepared, isVisibleToUser:" + getUserVisibleHint());
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
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
//                Log.d(TAG, String.format("===onInfo, what:%d, extra:%d===", what, extra));
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //Begin buffer, pauseVideo playing
                        if (videoView.isPlaying()) {
                            pauseVideo();
                            needResume = true;
                        }
                        showProgressBar();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //The buffering is done, resume playing
                        hideProgressBar();
                        if (needResume)
                            startVideo();
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

    private void showProgressBar() {
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INTENT_PATH, this.path);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
//        pauseVideo();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop===");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "===onResume, isVisibleToUser:" + getUserVisibleHint());
        super.onResume();
        if (getUserVisibleHint()) {
            startVideo();
        }
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "===setUserVisibleHint, isVisibleToUser:" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if (isAdded()) {
            if (isVisibleToUser) {
                startVideo();
            } else {
                pauseVideo();
            }
        }
    }

}
