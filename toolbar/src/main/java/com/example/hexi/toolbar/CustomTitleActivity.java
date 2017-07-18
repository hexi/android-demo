package com.example.hexi.toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.hexi.toolbar.widgets.YtxTitle;

public class CustomTitleActivity extends FragmentActivity {
    private static final String TAG = "CustomTitleActivity";
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_title);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        int color = Color.parseColor("#de1111");
        YtxTitle ytxTitle = (YtxTitle) findViewById(R.id.ytx_title);
        ytxTitle.setBackgroundColor(Color.TRANSPARENT);
        ytxTitle.setOnActionListener(new YtxTitle.OnActionClickListener() {
            @Override
            public void onClickedLeftAction() {
                finish();
            }

            @Override
            public void onClickedRightAction() {
                Toast.makeText(CustomTitleActivity.this, "Share button clicked", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        StatusBarUtil.hideStatusBar(this);
        final int targetY = getResources().getDimensionPixelSize(R.dimen.common_title_height);
        Log.d(TAG, "===targetY:" + targetY);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int preScrollY = 0;

            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                Log.d(TAG, String.format("===onScrollChanged, preScrollY:%d, scrollY:%d", preScrollY, scrollY));
                if (scrollY >= targetY) {
                    StatusBarUtil.setStatusBarColor(CustomTitleActivity.this, Color.WHITE);
                } else if (scrollY < targetY) {
                    StatusBarUtil.hideStatusBar(CustomTitleActivity.this);
                }
                preScrollY = scrollY;
            }
        });
    }


}
