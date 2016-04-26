package com.example.hexi.canvastest.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.util.DeviceUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rjhy on 15-12-7.
 */
public class NoTradePermissionDialog extends Dialog {

    Activity activity;

    public NoTradePermissionDialog(Context context) {
        super(context, R.style.ExpertDialog);
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_no_trade_permission);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.inject(this);
    }

    @Override
    public void show() {
        super.show();
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() - DeviceUtil.dp2px(activity.getResources(), 40));
        this.getWindow().setAttributes(lp);
    }


    @OnClick(R.id.iv_close)
    public void closeDialog(View view) {
        dismiss();
    }

    @OnClick(R.id.tv_contact_service)
    public void contactService(View view) {
        dismiss();
    }

    @OnClick(R.id.tv_open_account)
    public void openAccount(View view) {
        dismiss();
    }
}
