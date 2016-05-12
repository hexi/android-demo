package com.vitamio.mediaplayer.fragment;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import com.vitamio.mediaplayer.adapter.MyAdapter;
import com.vitamio.mediaplayer.view.MyRecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.danmaku.util.IOUtils;
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
    private DanmakuContext mContext;
    private BaseDanmakuParser mParser;

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        private Drawable mDrawable;

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if(drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if(mDanmakuView != null) {
                                mDanmakuView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };
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
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);

        mDanmakuView = (IDanmakuView) root.findViewById(R.id.sv_danmaku);
        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 0)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        if (mDanmakuView != null) {
            mParser = createParser(null);
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
            mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
                @Override
                public void onDanmakuClick(BaseDanmaku latest) {
                    Log.d("DFM", "onDanmakuClick text:" + latest.text);
                }

                @Override
                public void onDanmakuClick(IDanmakus danmakus) {
                    Log.d("DFM", "onDanmakuClick danmakus size:" + danmakus.size());
                }
            });
            mDanmakuView.prepare(mParser, mContext);
            mDanmakuView.enableDanmakuDrawingCache(true);

//            ((View) mDanmakuView).setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    mMediaController.setVisibility(View.VISIBLE);
//                }
//            });
        }

        timer = new Timer();
        timer.schedule(new AsyncAddTask(), 0, 1000);
    }

    class AsyncAddTask extends TimerTask {

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                addDanmaku(true);
                SystemClock.sleep(20);
            }
        }
    }

    private void addDanmaku(boolean islive) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        // for(int i=0;i<100;i++){
        // }
        danmaku.text = "这是一条弹幕" + System.nanoTime();
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.time = mDanmakuView.getCurrentTime() + 1200;
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
//        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);

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
