package com.shshcom.station.statistics.http.bean

import com.google.gson.annotations.SerializedName


/**
 * desc: 取件通知统计数据查询接口
 * author: zhhli
 * 2020/7/3
 */

data class PackNotifyData(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val list: List<NotifyBean>,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("total")
        val total: Int
)

// desc: 取件通知统计数据查询接口
data class NotifyBean(
        @SerializedName("date")
        var date: String,
        @SerializedName("sms_total")
        val smsTotal: Int,
        @SerializedName("wechat_toal")
        val wechatToal: Int
)

data class WeChatTodayNotice(
        @SerializedName("wechat_toal")
        val wechatTotal: Int
)