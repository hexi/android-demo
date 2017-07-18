package com.example.hexi.toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hexi on 15/8/31.
 */
public class ViewPagerFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_test_fragment1, container, false);

        mView.findViewById(R.id.button).setOnClickListener(this);
        mView.findViewById(R.id.button1).setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            getActivity().startActivity(new Intent(getActivity(), FullScreenActivity.class));
        } else if (v.getId() == R.id.button1) {
            getActivity().startActivity(new Intent(getActivity(), CustomTitleActivity.class));
        }
    }
}
