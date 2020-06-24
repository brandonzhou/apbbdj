package com.shshcom.station.shop.http

import com.shshcom.station.storage.http.bean.BaseResult
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * desc:
 * author: zhhli
 * 2020/6/24
 */
interface ShopService {

    /**
     * 设置 快递自动催取
     * https://shop.81dja.com/take/AppV2/modifyStock
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://shop.81dja.com:5443/take/AppV2/modifyStock")
    fun modifyStock(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>
}