package com.example.hexi.canvastest.rxAdapter;

import com.example.hexi.canvastest.rxAdapter.exception.RetrofitException;

import rx.Subscriber;

/**
 * Created by hexi on 16/8/9.
 */
public abstract class YtxSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public final void onError(Throwable e) {
        try {
            onFailed((RetrofitException) e);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (!this.isUnsubscribed()) {
                this.unsubscribe();
            }
        }
    }

    protected abstract void onFailed(RetrofitException e);


}
