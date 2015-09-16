package com.hexi.rx;

import com.hexi.rx.operator.MyOperator;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by hexi on 15/9/8.
 */
public class MyOperatorTest {

    @Test
    public void testMyOperator() {
        Observable<Integer> barObservable = Observable.from(new Integer[]{1, 2, 3, 4});
        Observable<String> fooObservable = barObservable.map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer item) {
                return item * 2;
            }
        }).lift(new MyOperator()).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer item) {
                return "transformed value: " + item;
            }
        });

        fooObservable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("completed!");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("error!");
            }

            @Override
            public void onNext(String item) {
                System.out.println("Next: " + item);
            }
        });
    }
}
