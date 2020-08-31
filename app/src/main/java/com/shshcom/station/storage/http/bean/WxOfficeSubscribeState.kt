package com.shshcom.station.storage.http.bean


import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 微信公众号 订阅状态
 */
data class WxOfficeSubscribeState(
        @SerializedName("qrcode")
        val qrcode: String,
        @SerializedName("state")
        val state: Int
) : Serializable