package com.shshcom.station.shop.http.bean


import com.google.gson.annotations.SerializedName

/**
 * 管理门店商品的商品
 */
data class GoodDetail(
        @SerializedName("goods_id")
        val goodsId: String,
        @SerializedName("goods_name")
        val goodsName: String,
        @SerializedName("img")
        val img: String,
        @SerializedName("is_special")
        val isSpecial: String,
        @SerializedName("price")
        val price: String,
        @SerializedName("states")
        val states: String,
        @SerializedName("stock")
        val stock: String,
        @SerializedName("type") // type类型 1-计件 2-称重
        val type: String
)