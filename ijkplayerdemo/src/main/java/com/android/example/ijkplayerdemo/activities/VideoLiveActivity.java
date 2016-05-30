package com.android.example.ijkplayerdemo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.example.ijkplayerdemo.R;
import com.android.example.ijkplayerdemo.widget.media.IjkVideoView;


/**
 * Created by hexi on 16/4/14.
 */
public class VideoLiveActivity extends Activity {

    private IjkVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_live);

        String path = "rtmp://live1.evideocloud.net/live/test1__8Z2MPDMkP4Nm";

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
//        mVideoView.setMediaController(mMediaController);
//        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        mVideoView.setVideoPath(path);
        mVideoView.start();
    }

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, VideoLiveActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release(true);
    }
}
