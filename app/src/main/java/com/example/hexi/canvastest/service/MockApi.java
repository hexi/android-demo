package com.example.hexi.canvastest.service;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by hexi on 16/8/9.
 */
public interface MockApi {

    @GET("v2/hello.json")
    public Observable<Void> testHello();
}
