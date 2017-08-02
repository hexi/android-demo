package com.example.hexi.canvastest.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.example.hexi.canvastest.QuotationSocketManager;
import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.util.NotificationsUtils;
import com.example.hexi.canvastest.view.CheckView;
import com.example.hexi.canvastest.view.OnCheckStatusChangedListener;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CheckView) findViewById(R.id.checkbox)).setOnCheckStatusChangedListener(
                new OnCheckStatusChangedListener() {
                    @Override
                    public void onCheckStatusChanged(boolean check) {
                        Log.d(TAG, "===onCheckStatusChanged:" + check);
                    }
                });
    }

    public void requestPermission(View view) {
        if (!NotificationsUtils.isNotificationEnabled(this)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

//        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    public void toWeixin(View view) {
        if (isWeixinAvilible(this)) {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra("Chat_User", "wxid_bvzvy6ri3or821");
            //intent.addFlags(335544320);
            intent.setComponent(cmp);
            startActivity(intent);
        }
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void stopSocket(View view) {
        QuotationSocketManager.stopSocket(this);
    }

    public void cancelStopSocket(View view) {
        QuotationSocketManager.cancelStopSocket(this);
    }

    public void startSelf(View view) {
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "===onStop===");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "===onWindowFocusChanged, hasFocus:" + hasFocus);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "===onBackPressed===");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "===onStart===");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "===onResume===");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "===onPause===");
    }
}
