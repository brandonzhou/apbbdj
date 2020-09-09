package com.shshcom.station.blockuser.http

import com.shshcom.config.qrcode
import com.shshcom.station.blockuser.http.bean.BlockUserData
import com.shshcom.station.storage.http.bean.BaseResult
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * desc:敏感用户列表
 * author: zhhli
 * 2020/6/22
 */
interface BlockUserService {

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$qrcode/station/user/blockUserList")
    fun blockUserList(@FieldMap fields: Map<String, Any>): Call<BaseResult<BlockUserData>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$qrcode/station/user/addBlockUser")
    fun addBlockUser(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$qrcode/station/user/delBlockUser")
    fun delBlockUser(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>

}