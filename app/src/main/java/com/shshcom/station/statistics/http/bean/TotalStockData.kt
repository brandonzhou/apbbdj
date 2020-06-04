package com.shshcom.station.statistics.http.bean


import com.google.gson.annotations.SerializedName

data class TotalStockData(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("data")
    val stockDataList: List<StockData>,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total")
    val total: Int
)


/**
 *
time	入库日期时间戳	Integer	是	1591027200
total	总库存	Integer	是	4
 */
data class StockData(
        @SerializedName("time")
        val inTime: String,
        @SerializedName("total")
        val total: Int
)