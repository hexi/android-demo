package com.hexi;

import com.example.hexi.canvastest.adapter.BooleanAsIntAdapter;
import com.example.hexi.canvastest.adapter.DateTimeTypeAdapter;
import com.example.hexi.canvastest.model.QuoteDataList;
import com.example.hexi.canvastest.service.QuoteService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

/**
 * Created by hexi on 15/7/3.
 */
public class RxJavaTest {
    BooleanAsIntAdapter booleanAsIntAdapter = new BooleanAsIntAdapter();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
            .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
            .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
            .create();

    @Test
    public void testZip() throws InterruptedException {
        BooleanAsIntAdapter booleanAsIntAdapter = new BooleanAsIntAdapter();
        RestAdapter.Builder builder = new RestAdapter.Builder();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        OkClient okClient = new OkClient(okHttpClient);
        builder.setClient(okClient);
        RestAdapter restAdapter = builder.setClient(new OkClient())
                .setEndpoint("http://api.baidao.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();
        QuoteService quoteService = restAdapter.create(QuoteService.class);

        final String mockResponse = "mockResponse";
        String string2 = "hello world";
        String sid = "PMEC.YDCL";
        String quotationType = "1";
        String tradeDate = null;
        String sort = null;
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        Observable observable0 = Observable.just(mockResponse).delay(0, TimeUnit.SECONDS);
        Observable observable1 = Observable.just(string2).delay(0, TimeUnit.SECONDS);
        Observable.zip(observable0,
                observable1,
                quoteService.getDkLineQuotes(sid, quotationType, tradeDate, sort),
                new Func3<String, String, QuoteDataList, List<Object>>() {
                    @Override
                    public List<Object> call(String s0, String s1, QuoteDataList quoteDataList) {
                        List<Object> ret = new ArrayList<Object>();
                        ret.add(s0);
                        ret.add(s1);
                        ret.add(quoteDataList);
                        return ret;
                    }
                }
        ).subscribe(new Observer<List<Object>>() {
            @Override
            public void onCompleted() {
                System.out.println("===onCompleted===");
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("===onError===");
                countDownLatch.countDown();
            }

            @Override
            public void onNext(List<Object> objects) {
                System.out.println("===onNext, result: " + gson.toJson(objects));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }


    @Test
    public void test1() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        System.out.println("===Main Thread hasFetchToday before, time: " + System.currentTimeMillis());
        Async.toAsync(new Func0<Boolean>() {

            @Override
            public Boolean call() {
                return hasFetchToday();
            }
        }).call()
                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean hasFetchToday) {
                        if (hasFetchToday) {
                            System.out.println("=== time: " + System.currentTimeMillis()
                                    + ", ThreadName: " + Thread.currentThread().getName());
                            countDownLatch.countDown();
                        }
                    }
                });
        System.out.println("===Main Thread hasFetchToday after, time: " + System.currentTimeMillis());
        countDownLatch.await();
    }

    private boolean hasFetchToday() {
        System.out.println("=== hasFetchToday, time: " + System.currentTimeMillis() + ", ThreadName: " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
