package com.hexi.retrofit;

import android.support.annotation.NonNull;

import com.baidao.retrofit2_log_adapter.RetrofitLogFactory;
import com.example.hexi.canvastest.adapter.BooleanAsIntAdapter;
import com.example.hexi.canvastest.adapter.DateTimeTypeAdapter;
import com.example.hexi.canvastest.model.LoginInfoResult;
import com.example.hexi.canvastest.model.QuoteDataList;
import com.example.hexi.canvastest.service.MasApi;
import com.example.hexi.canvastest.service.QuoteService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Subscription;

/**
 * Created by hexi on 16/8/3.
 */
public class TestRetrofit2 {

    @Test
    public void testRequestWithRxJava() throws InterruptedException {
        QuoteService quoteService = getQuoteService();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        String sid = "TPME.XAGUSD";
        Subscription subscription = quoteService.getMtDataOfToday(sid, null)
                .subscribe(new Observer<QuoteDataList>() {
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
                    public void onNext(QuoteDataList quoteDataList) {
                        System.out.println("===onNext===");
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await();
    }

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

    BooleanAsIntAdapter booleanAsIntAdapter = new BooleanAsIntAdapter();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
            .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
            .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
            .create();

    private QuoteService getQuoteService() {

        OkHttpClient client = getOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.baidao.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(QuoteService.class);
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
                    .addInterceptor(RetrofitLogFactory.create(true))
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
    }
}
