package com.example.hexi.canvastest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.fragment.TestPostFragment;

/**
 * Created by hexi on 15/11/24.
 */
public class TestPostActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_post);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment, new TestPostFragment())
                .commit();
    }
}
