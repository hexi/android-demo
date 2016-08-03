package com.example.hexi.canvastest.service;


import com.example.hexi.canvastest.model.QuoteDataList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Bruce on 2/28/15.
 */
public interface QuoteService {

    /**
     *
     * @return
     * http://api.baidao.com/api/hq/hismtdata2.do?sid=TPME.XAGUSD
     */
    @GET("api/hq/hismtdata2.do")
    public Observable<QuoteDataList> getHistoryMtData(@Query("sid") String sid);

    /**
     * @param sid
     * @param updateTime 时间，如：20140716 10:00  （大于此时间的分钟数据）
     * @return
     * http://api.baidao.com/api/hq/mtdata2.do?id=TPME.XAGUSD&updatetime=20140716 10:00
     */
    @GET("api/hq/mtdata3.do")
    public Observable<QuoteDataList> getMtDataOfToday(@Query("sid") String sid, @Query("updatetime") String updateTime);

    /**
     *
     * @param sid
     * @param quotationType 1：日线 7：周线 30：月线
     * @param tradeDate 交易日，如：20140701 （大于或小于此交易日数据）
     * @param sort 排序，（lt往前取 gt/gte往后取 默认为往前取）
     * @return
     * http://api.baidao.com/api/hq/dkdata.do?id=TPME.XAGUSD&quotationType=1&tradedate=20140701&sort=gt
     */
    @GET("api/hq/dkdata.do")
    public Observable<QuoteDataList> getDkLineQuotes(@Query("sid") String sid,
                                                     @Query("quotationType") String quotationType,
                                                     @Query("tradedate") String tradeDate,
                                                     @Query("sort") String sort);


    @GET("api/hq/mkdata.do?limit=300")
    public Call<Object> getMkLineQuotes(@Query("sid") String sid,
                                        @Query("quotationType") String quotationType,
                                        @Query("tradedate") String tradeDate,
                                        @Query("sort") String sort);
}