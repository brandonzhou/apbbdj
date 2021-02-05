package com.shshcom.station.setting.http

import com.shshcom.config.meng
import com.shshcom.config.passport
import com.shshcom.station.setting.http.bean.AutoUrgeData
import com.shshcom.station.setting.http.bean.CompanySettingData
import com.shshcom.station.setting.http.bean.CustomSMSTemplateData
import com.shshcom.station.setting.http.bean.SystemNotifyBean
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
    @POST("$meng/express/station/getPackageUrgeSetting")
    fun getPackageUrgeSetting(@FieldMap fields: Map<String, Any>): Call<BaseResult<AutoUrgeData>>

    /**
     * 设置 快递自动催取
     * express/station/setPackageUrgeSetting
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/setPackageUrgeSetting")
    fun setPackageUrgeSetting(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>


    /**
     * 设置 品牌管理-获取品牌
     * express/station/setPackageUrgeSetting
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/brandManagement")
    fun getBrandManagement(@FieldMap fields: Map<String, Any>): Call<BaseResult<List<CompanySettingData>>>

    /**
     * 设置 品牌管理-保存品牌设置
     * express/station/setPackageUrgeSetting
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/saveBrandManagement")
    fun saveBrandManagement(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>

    /**
     * 短信模版-获取驿站自定义短信内容
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/customSMSTemplate")
    fun getCustomSMSTemplate(@FieldMap fields: Map<String, Any>): Call<BaseResult<CustomSMSTemplateData>>

    /**
     * 短信模版-获取驿站自定义短信内容
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/saveCustomSMSTemplate")
    fun saveCustomSMSTemplate(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>


    /**
     * 短信模版-获取驿站自定义短信内容
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$passport/station/Station/noticeList")
    fun getNoticeList(@FieldMap fields: Map<String, Any>): Call<BaseResult<List<SystemNotifyBean>>>
}