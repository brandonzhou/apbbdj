package com.shshcom.module_base.network

import com.shshcom.module_base.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * desc:
 * author: zhhli
 * 2020/5/30
 */
object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"

    private fun getOkHttpClient(): OkHttpClient{
        val  builder = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor( HttpLogInterceptor());
        }

        return builder.build()
    }


    private val retrofit = Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}