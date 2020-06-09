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

pie_id	快递数据唯一id	Integer	是	4
station_id	驿站唯一id	Integer	是	30
mobile	用户手机号	String	是	18911011011
number	快递单号	String	是	73130864866718
code	取件码	String	是	skks-1002
express_id	快递公司唯一id	Integer	是	100101
express_name	快递公司名称	String	是	圆通快递
types	快递库存状态      1.在库2.出库
out_time	快递出库时间戳	Integer	是	0
warehousing_time	快递入库时间戳	Integer	是	1591061056
picture	快递入库图片url地址	String
privacy	快递菜鸟隐私面单   0.不是1.是
sms_content	短信内容	String	是	0
sms_states	短信发送状态0.失败1.成功	String	是	1
sms_time	短信发送时间戳
 */
data class PackageDetailData(
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
        @SerializedName("out_time")
        val outTime: String,
        @SerializedName("picture")
        val picture: String,
        @SerializedName("pie_id")
        val pieId: Int,
        @SerializedName("privacy")
        val privacy: Int,
        @SerializedName("sms_content")
        val smsContent: String,
        @SerializedName("sms_states")
        val smsStates: Int,
        @SerializedName("sms_time")
        val smsTime: String,
        @SerializedName("station_id")
        val stationId: Int,
        @SerializedName("types")
        val types: Int,
        @SerializedName("warehousing_time")
        val warehousingTime: String
): Serializable{
        var showTimeInfo = ""
}