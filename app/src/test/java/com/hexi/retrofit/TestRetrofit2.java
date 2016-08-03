package com.hexi.retrofit;

import com.example.hexi.canvastest.adapter.BooleanAsIntAdapter;
import com.example.hexi.canvastest.adapter.DateTimeTypeAdapter;
import com.example.hexi.canvastest.model.QuoteDataList;
import com.example.hexi.canvastest.service.QuoteService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Subscription;

/**
 * Created by hexi on 16/8/3.
 */
public class TestRetrofit2 {

    @Test
    public void testRequestWithRxJava() throws InterruptedException {
        QuoteService quoteService = getQuoteService();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        String sid = "TPME.XAGUSD";
        Subscription subscription = quoteService.getMtDataOfToday(sid, null)
                .subscribe(new Observer<QuoteDataList>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("===onCompleted===");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("===onError===" + e.getMessage());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onNext(QuoteDataList quoteDataList) {
                        System.out.println("===onNext===");
                        System.out.println(gson.toJson(quoteDataList));
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await();
    }

    BooleanAsIntAdapter booleanAsIntAdapter = new BooleanAsIntAdapter();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
            .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
            .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
            .create();

    private QuoteService getQuoteService() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.url();
                URL url = httpUrl.url();

                String schema = httpUrl.scheme();
                String host = url.getHost();
                String path = url.getPath();
                int port = url.getPort();
                String query = url.getQuery();

                System.out.println(String.format("host:%s, path:%s, port:%d, query:%s",
                        host,
                        path,
                        port,
                        query));

                String portStr = port == -1 ? "" : String.valueOf(port);
                System.out.println("http request: " + schema + "://" + host + portStr + path + query);
                return chain.proceed(request);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.baidao.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(QuoteService.class);
    }
}
