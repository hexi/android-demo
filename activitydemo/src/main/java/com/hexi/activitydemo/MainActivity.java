package com.hexi.activitydemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }

    public void startHalfActivity(View view) {
        startActivity(new Intent(this, HalfActivity.class));
    }

    public void startDialogActivity(View view) {
        startActivity(new Intent(this, DialogActivity.class));
    }

    public void startDialog(View view) {
        DialogUtil.showDialog(this, null);
    }

    public void startMultiActivity(View view) {
        startActivity(new Intent(this, MultiActivity.class));
    }
}
