package com.vitamio.mediaplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vitamio.mediaplayer.R;

/**
 * Created by hexi on 16/5/19.
 */
public class TestFragment extends Fragment {

    private static final String TAG = "TestFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "===onCreateView===");
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "===onStart===");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "===onResume===");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===onCreate===");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "===onAttach===");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop===");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "===onDestroyView===");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "===onDetach===");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "===onSaveInstanceState===");
    }
}
