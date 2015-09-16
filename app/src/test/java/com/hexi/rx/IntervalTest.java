package com.hexi.rx;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by hexi on 15/9/7.
 */
public class IntervalTest {

    @Test
    public void testInterval() throws InterruptedException {
        Observable.interval(1000, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                System.out.println("Sequence complete.");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onNext(Long item) {
                System.out.println("Next: " + item);
            }
        });
        Thread.sleep(10 * 1000);
    }
}
