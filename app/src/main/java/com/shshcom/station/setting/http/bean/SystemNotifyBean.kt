package com.shshcom.station.setting.http.bean


import com.google.gson.annotations.SerializedName

/**
 * 通知公告
 */
data class SystemNotifyBean(
    @SerializedName("create_time")
    val createTime: String, // 2021-02-04 15:20:02
    @SerializedName("link")
    val link: String, // https://station-v1.shshcom.com/Article/ndetails/userinfoid/12/detailsid/1
    @SerializedName("notice_id")
    val noticeId: Int, // 1
    @SerializedName("states")
    var states: Int, // 0-未读 1-已读
    @SerializedName("title")
    val title: String // 1212121221
)