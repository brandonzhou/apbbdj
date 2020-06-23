package com.shshcom.station.setting.http

import com.shshcom.station.setting.http.bean.AutoUrgeData
import com.shshcom.station.storage.http.bean.BaseResult
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * desc:
 * author: zhhli
 * 2020/6/23
 */
interface SettingService {
    /**
     * 获取 快递自动催取
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com:5443/express/station/getPackageUrgeSetting")
    fun getPackageUrgeSetting(@FieldMap fields: Map<String, Any>): Call<BaseResult<AutoUrgeData>>

    /**
     * 设置 快递自动催取
     * express/station/setPackageUrgeSetting
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com:5443/express/station/setPackageUrgeSetting")
    fun setPackageUrgeSetting(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>
}