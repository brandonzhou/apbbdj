package com.shshcom.station.statistics.http.bean


import com.google.gson.annotations.SerializedName

/**
 * desc: 今日快递数量统计
 * author: zhhli
 * 2020/6/3
 */
data class TodayExpressStatistics(
    @SerializedName("enter")
    val enter: Int,
    @SerializedName("out")
    val `out`: Int,
    @SerializedName("total")
    val total: Int
)