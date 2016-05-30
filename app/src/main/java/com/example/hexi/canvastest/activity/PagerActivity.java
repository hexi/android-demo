package com.example.hexi.canvastest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.hexi.canvastest.R;

/**
 * Created by hexi on 16/5/25.
 */
public class PagerActivity extends Activity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

    }
}
