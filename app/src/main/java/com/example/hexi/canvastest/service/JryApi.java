package com.example.hexi.canvastest.service;


import com.example.hexi.canvastest.model.WarningSetting;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by rjhy on 14-12-1.
 */
public interface JryApi {

    @GET("jry-device/ajax/warningSettings/id_v2.do")
    public Call<ArrayList<WarningSetting>> getWarningSettingsOfSid(@Query("deviceId") String deviceId,
                                                                   @Query("sid") String sid,
                                                                   @Query("token") String token);

    @DELETE("jry-device/ajax/warningSetting_v2.do")
    public Observable<Void> deleteWarningSettingById(@Query("id") long id);

}
