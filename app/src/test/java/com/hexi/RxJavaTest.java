package com.hexi;

import android.content.res.Resources;
import android.util.Log;

import com.example.hexi.canvastest.adapter.BooleanAsIntAdapter;
import com.example.hexi.canvastest.adapter.DateTimeTypeAdapter;
import com.example.hexi.canvastest.model.QuoteDataList;
import com.example.hexi.canvastest.service.QuoteService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import org.joda.time.DateTime;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
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

//    @Test
//    public void testZip() throws InterruptedException {
//
//        QuoteService quoteService = ServiceAdapter.getQuoteService();
//
//        final String mockResponse = "mockResponse";
//        String string2 = "hello world";
//        String sid = "PMEC.YDCL";
//        String quotationType = "1";
//        String tradeDate = null;
//        String sort = null;
//        final CountDownLatch countDownLatch = new CountDownLatch(2);
//
//        Observable observable0 = Observable.just(mockResponse).delay(0, TimeUnit.SECONDS);
//        Observable observable1 = Observable.just(string2).delay(0, TimeUnit.SECONDS);
//        Subscription subscription = Observable.zip(observable0,
//                observable1,
//                quoteService.getDkLineQuotes(sid, quotationType, tradeDate, sort),
//                new Func3<String, String, QuoteDataList, List<Object>>() {
//                    @Override
//                    public List<Object> call(String s0, String s1, QuoteDataList quoteDataList) {
//                        List<Object> ret = new ArrayList<Object>();
//                        ret.add(s0);
//                        ret.add(s1);
//                        ret.add(quoteDataList);
//                        return ret;
//                    }
//                }
//        ).subscribe(new Observer<List<Object>>() {
//            @Override
//            public void onCompleted() {
//                System.out.println("===onCompleted===");
//                countDownLatch.countDown();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.println("===onError===");
//                countDownLatch.countDown();
//            }
//
//            @Override
//            public void onNext(List<Object> objects) {
//                System.out.println("===onNext, result: " + gson.toJson(objects));
//                countDownLatch.countDown();
//            }
//        });
//        subscription.unsubscribe();
//        countDownLatch.await();
//    }


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

    @Test
    public void test2() throws InterruptedException {
        Observable.just(1, 2, 3, 4, 6)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("doOnNext: " + integer);
                    }
                })
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("onNext: " + o);
                    }
                });
    }

//    @Test
//    public void mockInterruptedIOException() throws InterruptedException {
//        QuoteService quoteService = ServiceAdapter.getQuoteService();
//
//        final CountDownLatch countDownLatch = new CountDownLatch(1);
//        String sid = "SZPEX.QHO50S";
//        Subscription subscription = quoteService.getMtDataOfToday(sid, null)
//                .subscribe(new Observer<QuoteDataList>() {
//                    @Override
//                    public void onCompleted() {
//                        System.out.println("===onCompleted===");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        System.out.println("===onError===" + e.getMessage());
//                        countDownLatch.countDown();
//                    }
//
//                    @Override
//                    public void onNext(QuoteDataList quoteDataList) {
//                        System.out.println("===onNext===");
//                        countDownLatch.countDown();
//                    }
//                });
//
//        Thread.sleep(705);
//        subscription.unsubscribe();
//        countDownLatch.await();
//    }

    @Test
    public void test3() throws InterruptedException {
        Observable observable0 = Observable.just("0");
        Observable observable1 = Observable.just("1");
        Observable.zip(observable0.map(new Func1<String, String>() {
            @Override
            public String call(String o) {
                return o + ".";
            }
        })
                , observable1.map(new Func1<String, String>() {
            @Override
            public String call(String o) {
                return o + ";";
            }
        })
                , new Func2<String, String, String>() {
            @Override
            public String call(String o, String o2) {
//                throw new IllegalStateException("获取数据失败");
                return o + o2;
            }
        }).doOnNext(new Action1() {
            @Override
            public void call(Object o) {
                System.out.println("===doOnNext0: " + o);
            }
        }).doOnNext(new Action1<String>() {
            @Override
            public void call(String o) {
                System.out.println("===doOnNext1: " + o);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Observer() {
                    @Override
                    public void onCompleted() {
                        System.out.println("===onCompleted===");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("===onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("===onNext: " + o);
                    }
                });
    }

    @Test
    public void test4() throws InterruptedException {
        Observable observable0 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> observer) {
                try {
                    Thread.sleep(4000);
                    if (!observer.isUnsubscribed()) {
                        observer.onNext(0);
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
        Observable observable1 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> observer) {
                try {
                    Thread.sleep(2000);
                    if (!observer.isUnsubscribed()) {
                        observer.onNext(1);
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final long start = System.currentTimeMillis();
        Observable.zip(observable0,
                observable1,
                new Func2<Integer, Integer, String>() {
                    @Override
                    public String call(Integer integer, Integer integer2) {
                        return integer + ":" + integer2;
                    }
                }).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("===onError, time:" + (System.currentTimeMillis() - start));
                countDownLatch.countDown();
            }

            @Override
            public void onNext(String o) {
                System.out.println("===result:"+o);
                System.out.println("===onNext, time:" + (System.currentTimeMillis() - start));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    @Test
    public void test0() throws InterruptedException {
        Observable observable0 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> observer) {
                try {
                    Thread.sleep(3000);
                    if (!observer.isUnsubscribed()) {
                        observer.onNext(0);
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        });
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final long start = System.currentTimeMillis();
        observable0.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer o) {
                        Schedulers.newThread().createWorker().schedule(new Action0() {
                            @Override
                            public void call() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("===onError, time:" + (System.currentTimeMillis() - start));
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onNext(String o) {
                        System.out.println("===result:" + o);
                        System.out.println("===onNext, time:" + (System.currentTimeMillis() - start));
                        countDownLatch.countDown();
                    }
                });
        countDownLatch.await();
    }

    @Test
    public void testStream() throws InterruptedException {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> observer) {
                System.out.println("===onCall===");
                try {
                    for (int i = 0; i < 10; i++) {
                        if (!observer.isUnsubscribed()) {
                            observer.onNext(UUID.randomUUID().toString());
                        }
                        Thread.sleep(2000);
                    }
                    observer.onCompleted();
                } catch (InterruptedException e) {
                    observer.onError(e);
                }
            }
        });

        observable
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("testStream0, onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("testStream0, onError");
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("testStream0, onNext:" + s + ", time:" + DateTime.now().toString());
                    }
                });

        observable
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("testStream1, onNext:" + s + ", time:" + DateTime.now().toString());
            }
        });



        Thread.sleep(1 * 24 * 360 * 1000);
    }

}
