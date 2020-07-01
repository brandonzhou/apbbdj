package com.shshcom.station.statistics.ui.adapter.timeline

import android.graphics.Color
import com.orient.me.data.ITimeItem
import com.shshcom.station.statistics.http.bean.PackageDetailData
import com.shshcom.station.util.AppTimeUtils

/**
 * desc:
 * author: zhhli
 * 2020/6/8
 */
/**
 * 标题
 * 时间
 *
 * showImage 显示图片 （文字）
 */
class PackTimeItem(val name: String, val time: String, val smsState: String, val notifyInfo: String,
                   val stateOk: Boolean = true, val okNum: Int = 0, val showImage: Boolean = true) : ITimeItem {
    companion object {

        /**
        types	快递库存状态      1.在库2.出

        out_time	快递出库时间戳	Integer	是	0库
        warehousing_time	快递入库时间戳	Integer	是	1591061056

        privacy	快递菜鸟隐私面单   0.不是1.是

        sms_content	短信内容	String	是	0
        sms_states	短信发送状态0.失败 大于0：短信发送成功次数
        sms_type	短信发送状态	Integer	是	1.短信已发送。2：短信待发送
        sms_time	短信发送时间戳

        out_picture	快递出库图片地址	String	否	out_type不是5时 out_picture为空
        out_picture_face	快递出库人脸图片地址	String	否	out_type不是5时 out_picture_face为空
        out_time	快递出库时间戳	String	否	2020-06-04 10:00:00

        out_type	出库类型	出库操作客户端1.驿站出库2.快递员出库3.驿站后台出库4.驿站扫二维码出库5驿站一体机
         */
        fun createList(data: PackageDetailData): List<PackTimeItem> {
            val list = ArrayList<PackTimeItem>()


            val formatS = AppTimeUtils.createFormat(AppTimeUtils.defaultFormat)
            val mmddhhmm = AppTimeUtils.createFormat("MM-dd HH:mm")

            val inTime = AppTimeUtils.str2Date(data.warehousingTime, formatS)
            val inItem = PackTimeItem("拍照入库", mmddhhmm.format(inTime), "", "")
            list.add(inItem)

            val audioItem = PackTimeItem("语音通知", mmddhhmm.format(inTime),
                    "首次存放到驿站，系统自动语音通知", "", showImage = false)
           // list.add(audioItem)

            if (!data.smsTime.isNullOrEmpty()) {
                val smsTime = AppTimeUtils.str2Date(data.smsTime, formatS)
                // 短信已到达
                //您的快递已到达方庄南路18号院，联系方式：
                //18511462203
                var smsState = ""
                if (data.smsType == 1) {
                    val stateOk = data.smsStates != 0
                    smsState = if (stateOk) "短信已到达(${data.smsStates}次)" else "短信未到达用户（不计费）"
                } else if (data.smsType == 2) {
                    smsState = "短信待发送"
                } else if (data.smsType == 3) {
                    smsState = ""
                }
                val smsItem = PackTimeItem("短信通知", mmddhhmm.format(smsTime),
                        smsState, data.smsContent, showImage = false)
                list.add(smsItem)

            }

            if (!data.outTime.isNullOrEmpty()) {
                val outTime = AppTimeUtils.str2Date(data.outTime, formatS)

                var outExceptionInfo = ""
                if (data.isOutException()) {
                    outExceptionInfo = "异常出库：${data.unusualMsg}"
                }

                val outItem = PackTimeItem("快递出库", mmddhhmm.format(outTime),
                        data.getOutTypeStr(), outExceptionInfo)
                list.add(outItem)
            }


            return list
        }
    }

    /**
     * 用户绘制原点的颜色
     * @return 颜色
     */
    override fun getColor(): Int {

        return Color.parseColor("#FF00BE7E")
    }

    override fun getResource(): Int {
        return 0

    }

    override fun getTitle(): String {
        return "操作记录"
    }
}