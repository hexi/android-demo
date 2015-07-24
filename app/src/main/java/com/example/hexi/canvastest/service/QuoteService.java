package com.example.hexi.canvastest.service;

import com.example.hexi.canvastest.model.QuoteDataList;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by hexi on 15/7/3.
 */
public interface QuoteService {

    @GET("/api/hq/dkdata.do")
    public Observable<QuoteDataList> getDkLineQuotes(@Query("sid") String sid,
                                                     @Query("quotationType") String quotationType,
                                                     @Query("tradedate") String tradeDate,
                                                     @Query("sort") String sort);
}
