package com.shshcom.station.storage.http

import com.shshcom.config.jijian_v1
import com.shshcom.config.meng
import com.shshcom.config.passport
import com.shshcom.config.qrcode
import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.storage.http.bean.ExpressCompany
import com.shshcom.station.storage.http.bean.ExpressPackInfo
import com.shshcom.station.storage.http.bean.WxOfficeSubscribeState
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
    @POST("$qrcode/bbapi/submit/stationUploadExpressImg3")
    fun stationUploadExpressImg3(@Body file: RequestBody): Call<BaseResult<Any>>

    @JvmSuppressWildcards
    @POST("$qrcode/bbapi/submit2/stationInputUploadExpress")
    fun stationInputUploadExpress(@Body file: RequestBody): Call<BaseResult<Any>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/warehousing/getExpressCompany")
    fun getExpressCompany(@FieldMap fields: Map<String, Any>): Call<BaseResult<List<ExpressCompany>>>


    /**
     * 拍照出库，查询快递信息
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/getExpressInfo")
    fun getPackageInfo(@FieldMap  fields: Map<String, Any>) : Call<BaseResult<ExpressPackInfo>>

    /**
     * 拍照出库，确认出库
     */
//    @JvmSuppressWildcards
//    @Multipart
//    @POST("$meng/express/station/barOutWarehouse")
//    fun barOutWarehouse(@PartMap queryMap: Map<String, Any>, @Part  file: MultipartBody.Part) : Call<BaseResult<Any>>

    @JvmSuppressWildcards
    @POST("$meng/express/station/barOutWarehouse")
    fun barOutWarehouse( @Body file: RequestBody) : Call<BaseResult<Any>>


    /**
     * 10.拍照出库，查询未出库快递
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/searchSameMobileExpressInfo")
    fun searchSameMobileExpressInfo(@FieldMap fields: Map<String, Any>): Call<BaseResult<List<ExpressPackInfo>>>

    /**
     * 通过运单号，查询 公众号关注状态
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$jijian_v1/express/Pieoutpack/pirStatNum")
    fun wxOfficeSubscribe(@FieldMap fields: Map<String, Any>): Call<BaseResult<WxOfficeSubscribeState>>


    /**
     * 14.延时发送短信-确认提交入库
     * http://task.shkjplus.cn:8100/T777
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/confirmSubmitWarehouse")
    fun confirmSubmitWarehouse(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>


    /**
     * 15.驿站APP导入运单号
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$passport/user/Station/importWaybillNumberApp")
    fun importWaybillNumberApp(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>

}