package com.example.hexi.canvastest.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by hexi on 15/7/3.
 */
public class QuoteData {

    public float open;
    public int index;
    public String percent;
    @SerializedName("updatetime")
    public DateTime updateTime;
    public float updrop;
    public float high;
    public float low;
    public float close;
    public boolean status;
    public float avg;
    public float volume;
    @SerializedName("tradedate")
    public DateTime tradeDate;
}
