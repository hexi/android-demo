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

import com.baidao.retrofitadapter.RetrofitBuilder;
import com.baidao.retrofitadapter.YtxSubscriber;
import com.baidao.retrofitadapter.exception.RetrofitException;
import com.example.hexi.canvastest.QuotationSocketManager;
import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.model.Result;
import com.example.hexi.canvastest.model.WarningSetting;
import com.example.hexi.canvastest.service.JryApi;
import com.example.hexi.canvastest.service.MockApi;
import com.example.hexi.canvastest.util.NotificationsUtils;
import com.example.hexi.canvastest.view.CheckView;
import com.example.hexi.canvastest.view.NoTradePermissionDialog;
import com.example.hexi.canvastest.view.OnCheckStatusChangedListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private Subscription subscription;

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

    public void showDialog(View view) {
        NoTradePermissionDialog dialog = new NoTradePermissionDialog(this);
        dialog.show();
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

    public void getWarning(View view) {
        Retrofit retrofit = new RetrofitBuilder().withDomain("http://test.tt.device.baidao.com/")
                .withDebug(true)
                .build();
        JryApi jryApi = retrofit.create(JryApi.class);

        String deviceId = "4db57fe7dc49fffbcda7b1507412803ae89154f0";
        String sid = "TPME.XAGUSD";
        String token = "rO0ABXQAFsORw7V6KyLCm0IBUHbDt3rCosKSw5M=";
        Call<ArrayList<WarningSetting>> call = jryApi.getWarningSettingsOfSid(deviceId, sid, token);
        call.enqueue(new Callback<ArrayList<WarningSetting>>() {
            @Override
            public void onResponse(Call<ArrayList<WarningSetting>> call,
                    Response<ArrayList<WarningSetting>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "===onNext, warning: " + new Gson().toJson(response.body()));
                } else {
                    Log.e(TAG, "fetch warning error");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WarningSetting>> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "fetch warning error ", t);
            }
        });
    }

    public void deleteWarning(View view) {
        Retrofit retrofit = new RetrofitBuilder().withDomain("http://test.tt.device.baidao.com/")
                .withDebug(true)
                .build();

        JryApi jryApi = retrofit.create(JryApi.class);
        long id = 557288L;
        Observable<Void> observable = jryApi.deleteWarningSettingById(id);
        observable.subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onNext(Void aVoid) {
                Log.d(TAG, "===delete success");
            }
        });
    }

    public void testResponseError(View view) {
        Retrofit retrofit = new RetrofitBuilder().withDomain("http://192.168.5.43:9090/")
                .withDebug(true)
                .withCallFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        MockApi mockApi = retrofit.create(MockApi.class);
        Observable<Void> observable = mockApi.testHello();
        subscription = observable.subscribe(new YtxSubscriber<Void>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "===onCompleted===");
            }

            @Override
            public void onFailed(RetrofitException e) {
                int i = 1 / 0;
                e.printStackTrace();
                if (e.getKind() == RetrofitException.Kind.HTTP) {
                    try {
                        Result result = e.getErrorBodyAs(Result.class);
                        Log.e(TAG, String.format("===error, code:%d, message:%s", result.code,
                                result.message));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onNext(Void aVoid) {
                Log.d(TAG, "===delete success");
            }
        });
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
