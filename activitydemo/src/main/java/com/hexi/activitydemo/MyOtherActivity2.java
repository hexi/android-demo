package com.hexi.activitydemo;

import android.os.Bundle;

public class MyOtherActivity2 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_other2);
    }
}
