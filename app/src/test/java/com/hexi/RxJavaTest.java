package com.hexi;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

/**
 * Created by hexi on 15/7/3.
 */
public class RxJavaTest {

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
