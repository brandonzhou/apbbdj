package com.shshcom.station.storage.http

import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.storage.http.bean.ExpressCompany
import com.shshcom.station.storage.http.bean.ExpressPackInfo
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * desc:
 * author: zhhli
 * 2020/6/1
 */
interface StorageService {

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com:5443/express/warehousing/getExpressCompany")
    fun getExpressCompany(@FieldMap  fields: Map<String, Any>) : Call<BaseResult<List<ExpressCompany>>>


    /**
     * 拍照出库，查询快递信息
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com:5443/express/station/getExpressInfo")
    fun getPackageInfo(@FieldMap  fields: Map<String, Any>) : Call<BaseResult<ExpressPackInfo>>

    /**
     * 拍照出库，确认出库
     */
//    @JvmSuppressWildcards
//    @Multipart
//    @POST("https://meng.81dja.com:5443/express/station/barOutWarehouse")
//    fun barOutWarehouse(@PartMap queryMap: Map<String, Any>, @Part  file: MultipartBody.Part) : Call<BaseResult<Any>>

    @JvmSuppressWildcards
    @POST("https://meng.81dja.com:5443/express/station/barOutWarehouse")
    fun barOutWarehouse( @Body file: RequestBody) : Call<BaseResult<Any>>


    /**
     * 10.拍照出库，查询未出库快递
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com:5443/express/station/searchSameMobileExpressInfo")
    fun searchSameMobileExpressInfo(@FieldMap fields: Map<String, Any>): Call<BaseResult<List<ExpressPackInfo>>>


    /**
     * 14.延时发送短信-确认提交入库
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com:5443/express/station/confirmSubmitWarehouse")
    fun confirmSubmitWarehouse(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>

}