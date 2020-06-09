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
class PackTimeItem(val name: String, val time: String, val state: String, val notifyInfo: String,
                   val stateOk: Boolean = true, val showImage: Boolean = true) : ITimeItem {
    companion object {

        /**
        types	快递库存状态      1.在库2.出库

        out_time	快递出库时间戳	Integer	是	0
        warehousing_time	快递入库时间戳	Integer	是	1591061056

        privacy	快递菜鸟隐私面单   0.不是1.是

        sms_content	短信内容	String	是	0
        sms_states	短信发送状态0.失败1.成功	String	是	1
        sms_time	短信发送时间戳

        picture	快递入库图片url地址	String
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
            list.add(audioItem)

            if (!data.smsTime.isNullOrEmpty()) {
                val smsTime = AppTimeUtils.str2Date(data.smsTime, formatS)
                // 短信已到达
                //您的快递已到达方庄南路18号院，联系方式：
                //18511462203
                val stateOk = data.smsStates == 1
                val smsState = if (stateOk) "短信已到达" else "短信未到达用户（不计费）"
                val smsItem = PackTimeItem("短信通知", mmddhhmm.format(smsTime),
                        smsState, data.smsContent, showImage = false)
                list.add(smsItem)

            }

            if (!data.outTime.isNullOrEmpty()) {
                val outTime = AppTimeUtils.str2Date(data.outTime, formatS)

                val outItem = PackTimeItem("快递出库", mmddhhmm.format(outTime),
                        "一体机扫描出库", "")
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