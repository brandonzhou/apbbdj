package com.shshcom.station.send.http.bean


import com.google.gson.annotations.SerializedName
import com.mt.bbdj.baseconfig.utls.DateUtil
import java.io.Serializable
import java.util.*

data class MailData(
    @SerializedName("current_page")
    val currentPage: Int, // 1
    @SerializedName("data")
    val `data`: List<MailDetail>,
    @SerializedName("last_page")
    val lastPage: Int, // 2
    @SerializedName("per_page")
    val perPage: Int, // 20
    @SerializedName("total")
    val total: Int // 24
)

data class MailDetail(
    @SerializedName("collect_province")
    val collectProvince: String, // 北京
    @SerializedName("collect_area")
    val collectArea: String, // 东城区
    @SerializedName("collect_city")
    val collectCity: String,
    @SerializedName("collect_address")
    val collectAddress: String, // 哈尔套镇中学
    @SerializedName("collect_name")
    val collectName: String, // 谢忠诚
    @SerializedName("collect_phone")
    val collectPhone: String, // 95013501996
    @SerializedName("collect_region")
    val collectRegion: String, // 辽宁省阜新市彰武县
    @SerializedName("collect_real_phone")
    val collectRealPhone: String, // 16869695858
    @SerializedName("content")
    val content: String,
    @SerializedName("express_name")
    val expressName: String, // 圆通快递
    @SerializedName("express_id")
    val expressId: Int, // 100101
    @SerializedName("flag")
    var flag: Int, // 1 //1-正常 2-取消
    @SerializedName("goods_name")
    val goodsName: String, // goods_name
    @SerializedName("goods_weight")
    val goodsWeight: String, // 1.00kg
    @SerializedName("handover_states")
    val handoverStates: Int, // 1
    @SerializedName("mail_id")
    val mailId: Int, // 107569
    @SerializedName("mailing_momey")
    val mailingMoney: String, // 10.00元
    @SerializedName("order_number")
    val orderNumber: String, // 7971251603071287
    @SerializedName("send_address")
    val sendAddress: String, // 故宫
    @SerializedName("send_name")
    val sendName: String, // 小孩子
    @SerializedName("send_phone")
    val sendPhone: String, // 13654233955
    @SerializedName("send_region")
    val sendRegion: String, // 北京北京市海淀区
    @SerializedName("send_area")
    val sendArea: String, // 海淀区
    @SerializedName("send_city")
    val sendCity: String, // 北京市
    @SerializedName("send_province")
    val sendProvince: String, // 北京
    @SerializedName("sign_code")
    val signCode: String, // 100-101 20
    @SerializedName("states")
    val states: Int, // 2
    @SerializedName("create_time")
    val createTime: Int, // 1603071287
    @SerializedName("transit_code")
    val transitCode: String, //    2020-10-23 14:11
    @SerializedName("transit_place")
    val transitPlace: String, // 北京
    @SerializedName("waybill_number")
    val waybillNumber: String // 777000099906
) : Serializable {
    fun toPrintMapNew(): HashMap<String, String> {

        val recAdds = collectRegion + collectAddress


        // 参照文字长度
        val len_receiver_address1 = "上海市宝山区共和新路4719弄共".length //16
        val len_sender_address1 = "上海市长宁区北曜路1178号（鑫达商务楼）".length //21

        var receiver_address1 = recAdds
        var receiver_address2 = ""

        if (recAdds.length > len_receiver_address1) {
            receiver_address1 = recAdds.substring(0, len_receiver_address1)
            receiver_address2 = recAdds.substring(len_receiver_address1)
        }


        val sendAdds = sendRegion + sendAddress
        var sender_address1 = sendAdds
        var sender_address2 = ""


        if (sendAdds.length > len_sender_address1) {
            sender_address1 = sendAdds.substring(0, len_sender_address1)
            sender_address2 = sendAdds.substring(len_sender_address1)
        }


        val pum = HashMap<String, String>()

        pum["[signCode]"] = signCode // 三段码
        pum["[barcode]"] = waybillNumber // 运单号
        pum["[date]"] = transitCode  // 中转地代码
        pum["[transitPlace]"] = transitPlace
        pum["[Receiver]"] = collectName
        pum["[Receiver_Phone]"] = collectPhone
        pum["[Receiver_address_detail]"] = receiver_address1
        pum["[Receiver_address_detail2]"] = receiver_address2
        pum["[Sender]"] = sendName
        pum["[Sender_Phone]"] = sendPhone
        pum["[Sender_address_detail]"] = sender_address1
        pum["[Sender_address_detail2]"] = sender_address2
        pum["[wight]"] = goodsWeight
        pum["[printTime]"] = DateUtil.getCurrentTimeFormat("yyyy-MM-dd")
        pum["[stageCode]"] = " "
        pum["[goodName]"] = goodsName
        pum["[money]"] = " "
        pum["[servicePhone]"] = "400-775-0008"


        return pum
    }


    //
    fun toPrintMap(): HashMap<String, String> {
        val pum = HashMap<String, String>()

        pum["[signCode]"] = signCode // 三段码
        pum["[barcode]"] = waybillNumber // 运单号
        pum["[transitCode]"] = transitCode  // 中转地代码
        pum["[transitPlace]"] = transitPlace
        pum["[Receiver]"] = collectName
        pum["[Receiver_Phone]"] = collectPhone
        pum["[Receiver_address_detail]"] = collectRegion + collectAddress
        pum["[Sender]"] = sendName
        pum["[Sender_Phone]"] = sendPhone
        pum["[Sender_address_detail]"] = sendRegion + sendAddress
        pum["[wight]"] = goodsWeight
        pum["[printTime]"] = DateUtil.getCurrentTimeFormat("yyyy-MM-dd")
        pum["[stageCode]"] = " "
        pum["[goodName]"] = goodsName
        pum["[money]"] = " "
        pum["[servicePhone]"] = "400-775-0008"

        return pum
    }


    /*
    "express_id": 100101,"express_name": "中通快递"
    "express_id": 100102,"express_name": "圆通快递"
    "express_id": 100103,"express_name": "申通快递"
    "express_id": 100104,"express_name": "韵达快递"
    "express_id": 100105,"express_name": "顺丰快递"
    "express_id": 100106,"express_name": "德邦快递"
    "express_id": 100107,"express_name": "百世快递"
    "express_id": 100108,"express_name": "EMS"
    "express_id": 100109,"express_name": "宅急送"
    "express_id": 100110,"express_name": "优速快递"
    "express_id": 100111,"express_name": "快捷快递"
    "express_id": 100112,"express_name": "安能快递"
    "express_id": 100113,"express_name": "天天快递"
    "express_id": 100114,"express_name": "京东快递"
    "express_id": 100115,"express_name": "天猫超市"
    "express_id": 100116,"express_name": "其他快递"
    "express_id": 100117,"express_name": "中国邮政"
    "express_id": 100118,"express_name": "苏宁物流"
    "express_id": 100119,"express_name": "极兔速递"
     */
    fun expressLogo(): String {
        val logo = when (expressId.toString()) {
            "100101" -> "ic_zhongtong_mini.png"
            "100102" -> "ic_yuantong_mini.png"
            "100103" -> "ic_shentong_mini.png"
            "100107" -> "ic_baishi_mini.png"
            "100110" -> "ic_yousu_mini.png"
            else -> "ic_logo_mini2.png"
        }
        return logo
    }
}

//| 字段            | 描述         | 类型    | 必填 | 备注 |
//| :-------------- | :----------- | :------ | :--- | :--- |
//| order_number    | 我方订单号   | String  | 是   |      |
//| express_id      | 快递公司ID   | Integer | 是   |      |
//| express_name    | 快递公司名称 | String  | 是   |      |
//| waybill_number  | 运单号       | String  | 是   |      |
//| sign_code       | 三段码       | String  | 是   |      |
//| transit_place   | 中转地       | String  | 是   |      |
//| transit_code    | 中转地代码   | String  | 是   |      |
//| send_name       | 寄件人       | String  | 是   |      |
//| send_phone      | 寄件人电话   | String  | 是   |      |
//| send_region     | 寄件地区     | String  | 是   |      |
//| send_address    | 寄件详细地址 | String  | 是   |      |
//| collect_name    | 收件人       | String  | 是   |      |
//| collect_phone   | 收件人电话   | String  | 是   |      |
//| collect_region  | 收件地区     | String  | 是   |      |
//| collect_address | 收件地址     | String  | 是   |      |
//| goods_name      | 物品名称     | String  | 是   |      |
//| goods_weight    | 重量         | String  | 是   |      |

// {
//    "data": {
//        "code": "100-101 20 ",
//        "collect_address": "紫竹园1号",
//        "collect_name": "杨柳青青",
//        "collect_phone": "16869695858",
//        "collect_region": "北京北京市海淀区",
//        "content": "",
//        "dingdanhao": "M202108177060811629170498",
//        "express_id": "100102",
//        "express_name": "圆通快递",
//        "goods_name": "文件",
//        "mail_id": "147226",
//        "mailing_momey": "0.00元",
//        "number": "J010-0001",
//        "place": "北京",
//        "send_address": "好了",
//        "send_name": "好",
//        "send_phone": "13811414692",
//        "send_region": "北京北京市海淀区",
//        "time": "1629170498",
//        "transit": "  2021-08-18  17:09",
//        "weight": "1.00kg",
//        "yundanhao": "777000049708"
//    },
//
//  "data2": {
//        "collect_address": "紫竹园1号",
//        "collect_area": "海淀区",
//        "collect_city": "北京市",
//        "collect_name": "杨柳青青",
//        "collect_phone": "16869695858",
//        "collect_province": "北京",
//        "collect_real_phone": "16869695858",
//        "collect_region": "北京北京市海淀区",
//        "content": "",
//        "express_id": 100102,
//        "express_name": "圆通快递",
//        "face_site": "B001",
//        "flag": 1,
//        "goods_name": "文件",
//        "goods_weight": "1.00kg",
//        "mail_id": 147226,
//        "order_number": "M202108177060811629170498",
//        "send_address": "好了",
//        "send_name": "好",
//        "send_phone": "13811414692",
//        "send_region": "北京北京市海淀区",
//        "sign_code": "100-101 20 ",
//        "states": 2,
//        "time": 1629170498,
//        "transit_code": "  2021-08-18  16:56",
//        "transit_place": "北京",
//        "waybill_number": "777000049708"
//    }
//}