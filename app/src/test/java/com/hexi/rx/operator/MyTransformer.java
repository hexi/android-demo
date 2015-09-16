package com.hexi.rx.operator;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by hexi on 15/9/8.
 */
public class MyTransformer implements Observable.Transformer<Integer, String> {

    @Override
    public Observable<String> call(Observable<? extends java.lang.Integer> source) {
        return source.map(new Func1<Integer, String>() {
            @Override
            public String call(Integer item) {
                return String.valueOf(item);
            }
        });
    }
}
