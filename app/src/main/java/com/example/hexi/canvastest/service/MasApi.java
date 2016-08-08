package com.example.hexi.canvastest.service;

import com.example.hexi.canvastest.model.LoginInfoResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by hexi on 16/8/4.
 */
public interface MasApi {

    //domain: http://192.168.19.162:8080/mas-client/

    @FormUrlEncoded
    @POST("api/1/user/android/login")
    public Observable<LoginInfoResult> login(@Field("loginStr") String phoneNumber,
                                             @Field("password") String password,
                                             @Field("serverId") int serverId);
}
