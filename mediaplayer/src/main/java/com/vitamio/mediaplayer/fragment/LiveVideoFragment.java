package com.vitamio.mediaplayer.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.baidao.ytxplayer.util.VideoManager;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.vitamio.mediaplayer.R;
import com.vitamio.mediaplayer.adapter.DanmakuAdapter;
import com.vitamio.mediaplayer.adapter.MyAdapter;
import com.vitamio.mediaplayer.listener.OnEnablePagingListener;
import com.vitamio.mediaplayer.model.DanmakuChat;
import com.vitamio.mediaplayer.view.SwipeLayout;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

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
public class LiveVideoFragment extends Fragment implements VideoManager.VideoServiceListener, SwipeLayout.OnSwipeLayoutListener {
    private static final String TAG = "LiveVideoFragment";
    public static final String INTENT_PATH = "intent.path";

    SwipeLayout swipeLayout;
    PLVideoTextureView videoView;
    ProgressBar progress;
    MyAdapter adapter;
    VideoManager videoService;
    private OnEnablePagingListener enablePagingListener;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEnablePagingListener) {
            enablePagingListener = (OnEnablePagingListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_live, container, false);
        swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);
        videoView = (PLVideoTextureView) view.findViewById(R.id.surface);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        adapter = new MyAdapter(getActivity());
        adapter.setData(mockData());

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
        swipeLayout.setOnSwipeLayoutListener(this);

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

    @Override
    public void onVideoPrepared(PLMediaPlayer mp) {
        videoService.startVideo();
    }

    @Override
    public void onVideoBufferingEnd(int extra) {
        videoService.startVideo();
    }

    @Override
    public void onVideoBufferingStart(int extra) {

    }

    @Override
    public void onVideoLossFocus() {
        pauseVideo();
    }

    @Override
    public void onVideoGainFocus() {
        startVideo();
    }

    @Override
    public boolean onVideoError(PLMediaPlayer mp, int errorCode) {
        Log.d(TAG, String.format("===video played error, errorCode:%d", errorCode));
        switch (errorCode) {
            case PLMediaPlayer.ERROR_CODE_IO_ERROR:
            case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
            case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
            case MediaPlayer.MEDIA_ERROR_UNKNOWN: {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDragViewUp() {

    }

    @Override
    public void onDragViewDown() {

    }

    @Override
    public void onLeftViewShowing() {
        enablePaging(false);
    }

    @Override
    public void onLeftViewHidden() {
        enablePaging(true);
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
                SystemClock.sleep(100);
            }
        }
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
        if (!isLiveStreaming(path)) {
            Log.e(TAG, "checkVitamioLibs return false");
            return;
        }
        setupVideo(path);
    }

    private boolean isLiveStreaming(String url) {
        if (url.startsWith("rtmp://")
                || (url.startsWith("http://") && url.endsWith(".m3u8"))
                || (url.startsWith("http://") && url.endsWith(".flv"))) {
            return true;
        }
        return false;
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
        videoService = new VideoManager(this.getActivity(), videoView,
                new VideoManager.Param(path, VideoManager.LAYOUT_PORTRAIT_FULL_SCREEN, false));
        videoService.setListener(this);
        videoService.initVideoView();
        videoView.setBufferingIndicator(progress);
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

    private void enablePaging(boolean enable) {
        if (enablePagingListener != null) {
            enablePagingListener.enablePaging(enable);
        }
    }

}
