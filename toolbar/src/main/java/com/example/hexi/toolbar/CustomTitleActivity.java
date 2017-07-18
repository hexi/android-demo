package com.example.hexi.toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.hexi.toolbar.widgets.YtxTitle;

public class CustomTitleActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_title);

        int color = Color.parseColor("#de1111");
        YtxTitle ytxTitle = (YtxTitle) findViewById(R.id.ytx_title);
        ytxTitle.setBackgroundColor(color);
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

        StatusBarUtil.setStatusBarColor(this, color);
    }


}
