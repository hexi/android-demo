package com.hexi.rx;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * Created by hexi on 15/9/7.
 */
public class DeferTest {

    @Test
    public void testDefer() throws InterruptedException {
        System.out.println("time: " + System.currentTimeMillis());
        Observable<Integer> observable = Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                System.out.println("Create observable time: " + System.currentTimeMillis());
                return Observable.just(10);
            }
        });
        Thread.sleep(1000);
        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("Sequence complete.");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("Next: " + item);
            }
        });
    }

}
