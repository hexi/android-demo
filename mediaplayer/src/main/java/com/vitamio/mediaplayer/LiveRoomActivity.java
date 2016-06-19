package com.vitamio.mediaplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.vitamio.mediaplayer.fragment.AndroidAudioFragment;
import com.vitamio.mediaplayer.fragment.LiveVideoFragment;
import com.vitamio.mediaplayer.listener.OnEnablePagingListener;
import com.vitamio.mediaplayer.view.SwitchViewPage;

/**
 * Created by hexi on 16/3/24.
 */
public class LiveRoomActivity extends FragmentActivity implements OnEnablePagingListener {
    private static final String TAG = "OriginalMediaPlayer";

    SwitchViewPage viewPager;
    LivePageAdapter pageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original_media_player);
        viewPager = (SwitchViewPage) findViewById(R.id.viewPager);
        pageAdapter = new LivePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.setPagingEnabled(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
    }

    @Override
    public void enablePaging(boolean enablePaging) {
        Log.d(TAG, "===enablePaging, enablePaging:" + enablePaging);
        this.viewPager.setPagingEnabled(enablePaging);
    }

    public class LivePageAdapter extends FragmentPagerAdapter {

        public LivePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return createVideoFragment();
            }
            return createTextLiveFragment();
        }

        @NonNull
        private LiveVideoFragment createVideoFragment() {
            LiveVideoFragment liveVideoFragment = new LiveVideoFragment();
            return liveVideoFragment;
        }

        private AndroidAudioFragment createTextLiveFragment() {
            AndroidAudioFragment textLiveFragment = new AndroidAudioFragment();
            return textLiveFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
