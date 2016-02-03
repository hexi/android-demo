package com.example.hexi.canvastest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.view.ClearRect;

/**
 * Created by hexi on 15/11/24.
 */
public class TestPostFragment extends Fragment {
    private static final String TAG = "TestPostFragment";
    ClearRect clearRect;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_post, container, false);
        clearRect = (ClearRect) view.findViewById(R.id.clearRect);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearRect.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "===perform in action");
            }
        });
        Log.d(TAG, "===after resume===");
    }
}
