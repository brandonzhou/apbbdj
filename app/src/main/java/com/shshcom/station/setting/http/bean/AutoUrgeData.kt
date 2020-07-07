package com.shshcom.station.setting.http.bean

import com.google.gson.annotations.SerializedName


/**
 * desc:快递自动催取
 * author: zhhli
 * 2020/6/23
 */
data class AutoUrgeData(
        @SerializedName("cur_urge_type")
        var curUrgeType: Int,
        @SerializedName("send_time")
        val sendTime: String,
        @SerializedName("sms_template")
        val smsTemplate: SmsTemplate,
        @SerializedName("urge_type")
        val urgeTypeList: List<UrgeType>
) {
    fun getCurrentType(): UrgeType? {
        return urgeTypeList.find { it.type == curUrgeType }
    }
}


data class UrgeType(
        @SerializedName("msg")
        val msg: String,
        @SerializedName("type")
        val type: Int
)
