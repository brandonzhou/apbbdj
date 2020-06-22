package com.shshcom.station.blockuser.http.bean

import com.google.gson.annotations.SerializedName


/**
 * desc:敏感用户
 * author: zhhli
 * 2020/6/22
 */

data class BlockUserData(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<BlockUser>,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("total")
        val total: Int
)

data class BlockUser(
        @SerializedName("block_id")
        val blockId: Int,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("time")
        val time: String
)