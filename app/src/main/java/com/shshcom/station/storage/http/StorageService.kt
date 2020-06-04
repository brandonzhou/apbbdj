package com.shshcom.station.storage.http

import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.storage.http.bean.ExpressCompany
import retrofit2.Call
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
    @POST("https://meng.81dja.com/express/warehousing/getExpressCompany")
    fun getExpressCompany(@FieldMap  fields: Map<String, Any>) : Call<BaseResult<List<ExpressCompany>>>


}