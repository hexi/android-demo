package com.dx168.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.R;
import io.vov.vitamio.widget.VideoView;

/**
 * 大象直播view
 * Created by tong on 16/3/11.
 */
public class DXVideoView extends LinearLayout {
    private static final String TAG = DXVideoView.class.getSimpleName();
    private VideoView mVideoView;
    private View rl_video_container;
    private ImageButton ib_close_video;
    private ImageButton ib_play_pause;

    private ProgressBar pb;

    private Handler handler = new Handler();
    private String videoPath;

    private Runnable cancelRunnable = new Runnable() {
        @Override
        public void run() {
            ib_close_video.setVisibility(View.GONE);
            ib_play_pause.setVisibility(View.GONE);
        }
    };


    public DXVideoView(Context context) {
        this(context, null);
    }

    public DXVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_dx_video,this);

        mVideoView = (VideoView) findViewById(R.id.surface_view);
        ib_close_video = (ImageButton) findViewById(R.id.ib_close_video);
        ib_play_pause = (ImageButton) findViewById(R.id.ib_play_pause);
        pb = (ProgressBar) findViewById(R.id.pb);

        (rl_video_container = findViewById(R.id.rl_video_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ib_close_video.getVisibility() == View.VISIBLE) {
                    ib_close_video.setVisibility(View.GONE);
                    ib_play_pause.setVisibility(View.GONE);
                } else {
                    handler.removeCallbacks(cancelRunnable);
                    ib_close_video.setVisibility(View.VISIBLE);
                    ib_play_pause.setVisibility(View.VISIBLE);

                    handler.postDelayed(cancelRunnable, 1000 * 3);
                }
            }
        });
        ib_close_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        ib_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    pause();
                    ib_play_pause.setImageResource(R.drawable.playbutton);
                } else {
                    start();
                    ib_play_pause.setImageResource(R.drawable.stopbutton);
                }
            }
        });
    }

    public void setVideoPath(String path) {
        //Toast.makeText(tradeFragment.getContext(),path,Toast.LENGTH_LONG).show();
        pb.setVisibility(View.VISIBLE);
        Log.e(TAG, "Video path: " + path);
        this.videoPath = path;
        mVideoView.setVideoPath(path);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        start();
                        pb.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    /**
     * 开始播放
     */
    public void start() {
        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.start();
        ib_play_pause.setImageResource(R.drawable.stopbutton);
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mVideoView.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
        mVideoView.pause();
        ib_play_pause.setImageResource(R.drawable.playbutton);
    }

    /**
     * 播放测试的直播流
     */
    public void playTestStream() {
        setVideoPath("rtmp://live1.evideocloud.net/live/dxzb__mz2gAzJO2jA2");
        start();
    }
}
