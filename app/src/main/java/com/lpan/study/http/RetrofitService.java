package com.lpan.study.http;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lpan on 2018/5/28.
 */

public class RetrofitService {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    public static final String BASE_URL="http://68.183.182.56:8080";

    private static GirlService sGirlService;
    private static RxJava2CallAdapterFactory sRxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();
    private static GsonConverterFactory sGsonConverterFactory = GsonConverterFactory.create();

    public static GirlService getGirlService() {
        if (sGirlService == null) {
            sGirlService = new Retrofit.Builder()
                    .client(MyHttpClient.getInstance().getOkHttpClient())
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(sRxJava2CallAdapterFactory)
                    .addConverterFactory(sGsonConverterFactory)
                    .build()
                    .create(GirlService.class);
        }
        return sGirlService;
    }
}
