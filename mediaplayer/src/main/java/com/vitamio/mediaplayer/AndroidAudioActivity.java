package com.vitamio.mediaplayer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.vitamio.mediaplayer.fragment.AndroidAudioFragment;

/**
 * Created by hexi on 16/3/24.
 */
public class AndroidAudioActivity extends FragmentActivity {
    private static final String TAG = "OriginalMediaPlayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original_media_player);

        AndroidAudioFragment fragment = new AndroidAudioFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_fragment, fragment)
                .commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
    }

}
