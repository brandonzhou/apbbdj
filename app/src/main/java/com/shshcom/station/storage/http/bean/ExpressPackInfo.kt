package com.shshcom.station.storage.http.bean
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * desc: 拍照出库，查询快递信息
 * author: zhhli
 * 2020/6/16
 */
data class ExpressPackInfo(
    @SerializedName("code")
    val code: String,
    @SerializedName("express_id")
    val expressId: Int,
    @SerializedName("express_name")
    val expressName: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("pie_id")
    val pieId: Int,
    @SerializedName("warehousing_time")
    val warehousingTime: String
): Serializable{
    var localFile = ""
}

data class ExpressPackInfoList(val list: List<ExpressPackInfo>):Serializable