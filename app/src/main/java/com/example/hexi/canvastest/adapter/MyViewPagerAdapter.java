package com.example.hexi.canvastest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hexi on 16/5/25.
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {
    List<String> pagerNames = new ArrayList<>();

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return pagerNames.size();
    }
}
