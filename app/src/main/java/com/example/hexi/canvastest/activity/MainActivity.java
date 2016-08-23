package com.example.hexi.canvastest.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.baidao.retrofitadapter.RetrofitBuilder;
import com.example.hexi.canvastest.QuotationSocketManager;
import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.model.Result;
import com.example.hexi.canvastest.model.WarningSetting;
import com.example.hexi.canvastest.rxAdapter.RxErrorHandlingCallAdapterFactory;
import com.example.hexi.canvastest.rxAdapter.YtxSubscriber;
import com.example.hexi.canvastest.rxAdapter.exception.RetrofitException;
import com.example.hexi.canvastest.service.JryApi;
import com.example.hexi.canvastest.service.MockApi;
import com.example.hexi.canvastest.service.TestService;
import com.example.hexi.canvastest.view.CheckView;
import com.example.hexi.canvastest.view.NoTradePermissionDialog;
import com.example.hexi.canvastest.view.OnCheckStatusChangedListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    boolean bound;
    TestService testService;

    public ServiceConnection bindAndStartConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "===onServiceConnected===");
            testService = ((TestService.ServiceBinder) service).getService();
            bound = true;

            Intent intent = createIntent();
            startService(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "===onServiceDisconnected===");
            bound = false;
        }
    };

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "===onServiceConnected===");
            testService = ((TestService.ServiceBinder) service).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "===onServiceDisconnected===");
            bound = false;
        }
    };
    private Subscription subscription;

    @NonNull
    private Intent createIntent() {
        Intent intent = new Intent(MainActivity.this, TestService.class);
        intent.putExtra("path", "http://www.google.com");
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CheckView) findViewById(R.id.checkbox)).setOnCheckStatusChangedListener(new OnCheckStatusChangedListener() {
            @Override
            public void onCheckStatusChanged(boolean check) {
                Log.d(TAG, "===onCheckStatusChanged:"+check);
            }
        });
    }

    public void bindServiceAndStart(View view) {
        Intent intent = new Intent(this, TestService.class);
        bindService(intent, bindAndStartConnection, Context.BIND_AUTO_CREATE);
    }

    public void bindService(View view) {
        Intent intent = new Intent(this, TestService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void startServiceByIntent(View view) {
        startService(createIntent());
    }

    public void startServiceNoIntent(View view) {
        startService(new Intent(this, TestService.class));
    }

    public void unbindService(View view) {
        unbindService(connection);
    }

    public void unbindServiceWithStartConn(View view) {
        unbindService(bindAndStartConnection);
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
        Retrofit retrofit = new RetrofitBuilder()
                .withDomain("http://test.tt.device.baidao.com/")
                .withDebug(true)
                .build();
        JryApi jryApi = retrofit.create(JryApi.class);

        String deviceId = "4db57fe7dc49fffbcda7b1507412803ae89154f0";
        String sid = "TPME.XAGUSD";
        String token = "rO0ABXQAFsORw7V6KyLCm0IBUHbDt3rCosKSw5M=";
        Call<ArrayList<WarningSetting>> call = jryApi.getWarningSettingsOfSid(deviceId, sid, token);
        call.enqueue(new Callback<ArrayList<WarningSetting>>() {
            @Override
            public void onResponse(Call<ArrayList<WarningSetting>> call, Response<ArrayList<WarningSetting>> response) {
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
        Retrofit retrofit = new RetrofitBuilder()
                .withDomain("http://test.tt.device.baidao.com/")
                .withDebug(true)
                .withCallFactory(RxErrorHandlingCallAdapterFactory.createWithScheduler(Schedulers.io()))
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
        Retrofit retrofit = new RetrofitBuilder()
                .withDomain("http://192.168.5.43:9090/")
                .withDebug(true)
                .withCallFactory(RxErrorHandlingCallAdapterFactory.createWithScheduler(Schedulers.io()))
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
                e.printStackTrace();
                if (e.getKind() == RetrofitException.Kind.HTTP) {
                    try {
                        Result result = e.getErrorBodyAs(Result.class);
                        Log.e(TAG, String.format("===error, code:%d, message:%s", result.code, result.message));
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
        Log.d(TAG, "===onWindowFocusChanged, hasFocus:"+hasFocus);
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
