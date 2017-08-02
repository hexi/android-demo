package com.hexi.retrofit;

import android.support.annotation.NonNull;
import com.example.hexi.canvastest.model.LoginInfoResult;
import com.google.gson.Gson;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;

/**
 * Created by hexi on 16/8/3.
 */
public class TestRetrofit2 {

    @Test
    public void testPostWithRxJava() throws InterruptedException {
        MasApi masApi = getMasApi();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        String username = "167000000000631";
        String password = "111111";
        int serverId = 1;
        masApi.login(username, password, serverId)
                .subscribe(new Observer<LoginInfoResult>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("===onCompleted===");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("===onError===" + e.getMessage());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onNext(LoginInfoResult result) {
                        System.out.println("===onNext===");
                        System.out.println("===result: " + new Gson().toJson(result));
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await();
    }

    private MasApi getMasApi() {
        OkHttpClient client = getOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.19.162:8080/mas-client/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(MasApi.class);
    }

    @NonNull
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
    }
}
