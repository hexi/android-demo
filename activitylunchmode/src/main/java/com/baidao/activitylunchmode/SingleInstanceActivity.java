package com.baidao.activitylunchmode;

import android.os.Bundle;

/**
 * Created by hexi on 16/1/31.
 */
public class SingleInstanceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_instance);
    }
}
