package com.vitamio.mediaplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vitamio.mediaplayer.fragment.AndroidAudioFragment;

public class PlayAudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_fragment_container, new AndroidAudioFragment())
                .commit();
    }
}
