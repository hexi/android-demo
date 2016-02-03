//package com.example.hexi.canvastest.service;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.squareup.okhttp.OkHttpClient;
//
//import org.joda.time.DateTime;
//
//import java.util.concurrent.TimeUnit;
//
//import retrofit.RestAdapter;
//import retrofit.client.OkClient;
//import retrofit.converter.GsonConverter;
//
///**
// * Created by hexi on 15/7/21.
// */
//public class ServiceAdapter {
//    private static Gson quoteServiceConverter;
//    static {
//        BooleanAsIntAdapter booleanAsIntAdapter = new BooleanAsIntAdapter();
//        quoteServiceConverter = new GsonBuilder()
//                .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
//                .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
//                .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
//                .create();
//    }
//
//    private static String DOMAIN_OF_CALENDAR = "http://yg.mobile-service.baidao.com";
//    private static String DOMAIN_OF_QUOTE = "http://api.baidao.com";
//
//    public static void setCalendarDomain(String calendarDomain) {
//        DOMAIN_OF_CALENDAR = calendarDomain;
//    }
//
//    public static void setQuoteDomain(String quoteDomain) {
//        DOMAIN_OF_QUOTE = quoteDomain;
//    }
//
//    private static RestAdapter createAdapter(String domain, Gson converter) {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
//        okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
//        OkClient okClient = new OkClient(okHttpClient);
//
//        RestAdapter.Builder builder = new RestAdapter.Builder();
//        if (converter != null) {
//            builder.setConverter(new GsonConverter(converter));
//        }
//        return builder.setClient(okClient)
//                .setEndpoint(domain)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .build();
//    }
//
//
//    public static QuoteService getQuoteService() {
//        RestAdapter restAdapter = createAdapter(DOMAIN_OF_QUOTE, quoteServiceConverter);
//        return restAdapter.create(QuoteService.class);
//    }
//}
