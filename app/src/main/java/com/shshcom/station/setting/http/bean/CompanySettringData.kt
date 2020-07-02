package com.shshcom.station.setting.http.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * desc:快递公司 品牌管理
 * author: zhhli
 * 2020/7/2
 */
data class CompanySettingData(
        @SerializedName("express_id")
        val expressId: Int,
        @SerializedName("express_name")
        val expressName: String,
        @SerializedName("option")
        val option: List<Option>,
        @SerializedName("title")
        var title: String,
        @SerializedName("type")
        var type: Int
) : Serializable

data class Option(
        @SerializedName("msg")
        val msg: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: Int
) : Serializable