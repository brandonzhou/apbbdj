package com.shshcom.module_base.network;

import com.shshcom.module_base.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * desc:
 * author: zhhli
 * 2020/1/10
 */
public class SHRetrofitManager {
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private static class HttpManagerHolder {
        private static final SHRetrofitManager INSTANCE = new SHRetrofitManager();
    }

    private SHRetrofitManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder() //RetrofitUrlManager 初始化
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLogInterceptor());
        }

        this.mOkHttpClient = builder.build();

        this.mRetrofit = new Retrofit.Builder()
                //.baseUrl(Api.APP_DEFAULT_DOMAIN)
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用rxjava
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .client(mOkHttpClient)
                .build();

    }

    public static SHRetrofitManager getInstance() {
        return HttpManagerHolder.INSTANCE;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }


}
