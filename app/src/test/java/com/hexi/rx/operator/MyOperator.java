package com.hexi.rx.operator;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by hexi on 15/9/8.
 */
public class MyOperator implements Observable.Operator<Integer, Integer> {


    @Override
    public Subscriber<? super Integer> call(final Subscriber<? super Integer> subscriber) {
        return new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
            }

            @Override
            public void onNext(Integer item) {
                if (!subscriber.isUnsubscribed()) {
                    Integer transformedItem = transform(item);
                    subscriber.onNext(transformedItem);
                }
            }
        };
    }

    private Integer transform(Integer item) {
        return item + 1;
    }
}
