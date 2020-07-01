package com.shshcom.station.statistics.http.bean


import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * 查询今日快递明细
 *
total	总条数	Integer	是	4
per_page	每页多少条	Integer	是	30
current_page	当前页数	Integer	是	1
last_page	总页数	Integer	是	1
data	查询出来的数据	Object	是	10
 */
data class PackageDetailResult(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val dataList: List<PackageDetailData>,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("total")
        val total: Int
)

/**
 *
http://task.shkjplus.cn:8100/T629

{
"pie_id": 152320,
"station_id": 2910,
"unusual_type": "0",
"mobile": "18811321042",
"unusual_msg": "业务员取出",
"number": "1234567890",
"code": "1000",
"express_id": 100101,
"types": 3,
"out_time": "2020-06-22 19:35:20",
"warehousing_time": "2020-06-22 19:35:02",
"in_picture": "https:\/\/bbsh-com.oss-cn-beijing.aliyuncs.com\/picture\/ruku\/20200622\/67811f7aded8395a92ecd0565c4735ce8b996847.jpeg",
"privacy": 0,
"out_type": 1,
"unusual": 1,
"out_picture": "",
"sms_content": "【兵兵到家】快递已到大柳树路21号，取件码:1000，电话:18811321040",
"sms_states": 1,
"sms_time": "2020-06-22 19:35:02",
"voice_states": 0,
"voice_time": "",
"express_icon": "https:\/\/bbsh-com.oss-cn-beijing.aliyuncs.com\/icon\/100101.png"
}

 */
data class PackageDetailData(
        @SerializedName("code")
    val code: String,
        @SerializedName("express_icon")
    val expressIcon: String,
        @SerializedName("express_id")
    val expressId: Int,
        @SerializedName("in_picture")
        val inPicture: String,
        @SerializedName("mobile")
        var mobile: String,
        @SerializedName("number")
        val number: String,
        @SerializedName("out_type")
        val outType: Int,
        @SerializedName("out_time")
        val outTime: String,
        @SerializedName("out_picture")
        val outPicture: String,
        @SerializedName("out_picture_face")
        val outPictureFace: String,
        @SerializedName("pie_id")
        val pieId: Int,
        @SerializedName("privacy")
        val privacy: Int,
        @SerializedName("sms_content")
        var smsContent: String,
        @SerializedName("sms_type")
        var smsType: Int,// sms_type	短信发送状态	Integer	是	 1.短信已发送。2：短信待发送 3.不展示提示信息
        @SerializedName("sms_states")
        var smsStates: Int,
        @SerializedName("sms_time")
        var smsTime: String,
        @SerializedName("station_id")
        val stationId: Int,
        @SerializedName("types")
        val types: Int,
        @SerializedName("unusual")
        val unusual: Int,
        @SerializedName("unusual_msg")
        val unusualMsg: String,
        @SerializedName("unusual_type")
        val unusualType: String,
        @SerializedName("voice_states")
        val voiceStates: Int,
        @SerializedName("voice_time")
        val voiceTime: String,
        @SerializedName("warehousing_time")
        val warehousingTime: String
):Serializable{
        var showTimeInfo = ""

    /**
     * 出库操作客户端1. 驿站出库 2. 快递员出库 3. 驿站后台出库 4. 驿站扫二维码出库 5 驿站一体机
     */
    fun getOutTypeStr(): String {
        return when (outType) {
            1 -> "驿站出库"
            2 -> "快递员出库"
            3 -> "驿站后台出库"
            4 -> "驿站扫二维码出库"
            5 -> "驿站一体机"
            else -> "app拍照出库"
        }
    }

    // 是否异常出库
    fun isOutException(): Boolean {
        return types == 3
    }
}

