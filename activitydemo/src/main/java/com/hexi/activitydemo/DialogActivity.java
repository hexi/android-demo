package com.hexi.activitydemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

/**
 * Created by hexi on 2017/7/24.
 */

public class DialogActivity extends BaseActivity implements DialogUtil.onDialogDismiss {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        DialogUtil.showDialog(this, this);
    }

    @Override
    public void onDismiss() {
        finish();
    }
}
