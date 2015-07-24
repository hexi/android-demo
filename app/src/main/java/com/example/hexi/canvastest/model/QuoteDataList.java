package com.example.hexi.canvastest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by hexi on 15/7/3.
 */
public class QuoteDataList {

    @SerializedName("datas")
    public CopyOnWriteArrayList<QuoteData> data = new CopyOnWriteArrayList<>();
    public CategoryInfo info;
}
