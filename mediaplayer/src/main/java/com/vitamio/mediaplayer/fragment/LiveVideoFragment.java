package com.vitamio.mediaplayer.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vitamio.mediaplayer.R;
import com.vitamio.mediaplayer.adapter.DanmakuAdapter;
import com.vitamio.mediaplayer.adapter.MyAdapter;
import com.vitamio.mediaplayer.model.DanmakuChat;
import com.vitamio.mediaplayer.view.MyRecyclerView;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.danmaku.util.SystemClock;

/**
 * Created by hexi on 16/3/17.
 */
public class LiveVideoFragment extends Fragment {
    private static final String TAG = "LiveVideoFragment";
    public static final String INTENT_PATH = "intent.path";

    VideoView videoView;
    ProgressBar progress;
    MyRecyclerView recyclerView;
    MyAdapter adapter;

    private IDanmakuView mDanmakuView;
    private DanmakuAdapter danmakuAdapter;

    private Timer timer;

    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }

    String path = "rtmp://live1.evideocloud.net/live/test1__8Z2MPDMkP4Nm";
    private boolean needResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_live, container, false);
        videoView = (VideoView) view.findViewById(R.id.surface);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        recyclerView = (MyRecyclerView) view.findViewById(R.id.recycler_view);

        adapter = new MyAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.setData(mockData());

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

        view.findViewById(R.id.disable_scroll_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableScroll();
            }
        });

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "===recycler view clicked");
                recyclerView.setCanScroll(true);
            }
        });

        view.findViewById(R.id.drag_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "===drag view clicked===");
            }
        });

        disableScroll();

        initDanmaku(view);

        return view;
    }

    private void initDanmaku(View root) {
        mDanmakuView = (IDanmakuView) root.findViewById(R.id.sv_danmaku);
        danmakuAdapter = new DanmakuAdapter(mDanmakuView);
        danmakuAdapter.init();
        timer = new Timer();
        timer.schedule(new AsyncAddTask(), 0, 1000);
    }

    class AsyncAddTask extends TimerTask {
        final AtomicLong counter = new AtomicLong(0);

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                long id = counter.getAndAdd(1);
                String nickname = String.format("模拟用户[%d]", id);
                String content = String.format(":这是一条弹幕, contentId:%d", id);
                danmakuAdapter.addDanmaku(new DanmakuChat(nickname, content));
                SystemClock.sleep(500);
            }
        }
    }

    public void disableScroll() {
        boolean isIntercept = recyclerView.isCanScroll();
        Log.d(TAG, "===isIntercept: "+ isIntercept);
        recyclerView.setCanScroll(false);
        adapter.notifyDataSetChanged();
    }


    private String[] mockData() {
        int size = 5;
        String[] s = new String[size];
        for (int i = 0; i < size; i++) {
            s[i] = "测试评论" + i;
        }
        return s;
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
                Log.d(TAG, "===onAudioPrepared, isVisibleToUser:" + getUserVisibleHint());
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
