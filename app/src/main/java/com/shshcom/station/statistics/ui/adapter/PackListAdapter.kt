package com.shshcom.station.statistics.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mt.bbdj.R
import com.shshcom.station.statistics.http.bean.PackageDetailData
import com.shshcom.station.statistics.ui.PackDetailActivity
import com.shshcom.station.util.AntiShakeUtils
import com.shshcom.station.util.AppTimeUtils
import org.joda.time.DateTime

/**
 * desc:每日库存列表
 * author: zhhli
 * 2020/6/5
 */
class PackListAdapter(var list: List<PackageDetailData>) : RecyclerView.Adapter<PackListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPackId: TextView = itemView.findViewById(R.id._tvPackId)
        val tvPackPhone: TextView = itemView.findViewById(R.id.tvPackPhone)
        val tvPickCode: TextView = itemView.findViewById(R.id.tvPickcode)
        val tvNotifyState: TextView = itemView.findViewById(R.id.tvNotifyState)
        val tvPackState: TextView = itemView.findViewById(R.id.tvPackState)
        val tvPackTime: TextView = itemView.findViewById(R.id.tvPackTime)
        val imageView10: ImageView = itemView.findViewById(R.id.imageView10)
    }

    val formatS = AppTimeUtils.createFormat(AppTimeUtils.defaultFormat)
    val mmddhhmm = AppTimeUtils.createFormat("MM-dd HH:mm")
    val hhmm = AppTimeUtils.createFormat("HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_multi_pack_list, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: PackageDetailData = list[position]
        Glide.with(holder.itemView).load(data.expressIcon).into(holder.imageView10)
        holder.tvPackId.text = data.number
        holder.tvPackPhone.text = "手机号: " + data.mobile
        holder.tvPickCode.text = "取件码: " + data.code

        if (data.smsStates > 0 ) {
            holder.tvNotifyState.text = "已通知用户"
            holder.tvNotifyState.setTextColor(holder.itemView.resources.getColor(R.color.text_grey_6))
        } else {
            holder.tvNotifyState.text = "通知用户失败"
            holder.tvNotifyState.setTextColor(holder.itemView.resources.getColor(R.color.text_red))
        }

        if (data.showTimeInfo.isNullOrEmpty()) {
            data.showTimeInfo = getShowTime(data)
        }

        val res = holder.itemView.context.resources

        if(data.types ==1){
            holder.tvPackState.text = "待出库"
            holder.tvPackState.setTextColor(res.getColor(R.color.white))
            holder.tvPackState.background = res.getDrawable(R.drawable.bg_green_r11)
        }else{
            // 2
            holder.tvPackState.text =  "已出库"
            holder.tvPackState.setTextColor(res.getColor(R.color.text_grey_3))
            holder.tvPackState.background = res.getDrawable(R.drawable.bg_grey_r11)

        }

        holder.tvPackTime.text = data.showTimeInfo

        holder.itemView.setOnClickListener {
            if (AntiShakeUtils.isInvalidClick(it)){
                return@setOnClickListener
            }
            PackDetailActivity.openActivity(it.context as Activity, data)
        }

    }


    private fun getShowTime(data: PackageDetailData): String {


        val inTime = AppTimeUtils.str2Date(data.warehousingTime, formatS)
        val isInToday = AppTimeUtils.isSameDay(DateTime(inTime), DateTime.now())

        val builder = StringBuilder()
        if (isInToday) {
            builder.append(AppTimeUtils.date2String(inTime, hhmm))
        } else {
            builder.append(AppTimeUtils.date2String(inTime, mmddhhmm))
        }
        builder.append("入库")


        if (!data.outTime.isNullOrEmpty()) {
            val outTime = AppTimeUtils.str2Date(data.outTime, formatS)
            val isOutToday = AppTimeUtils.isSameDay(DateTime(outTime), DateTime.now())

            builder.append(" | ")
            if (isOutToday) {
                builder.append(AppTimeUtils.date2String(outTime, hhmm))
            } else {
                builder.append(AppTimeUtils.date2String(outTime, mmddhhmm))
            }
            builder.append("出库")
        }



        return builder.toString()
    }
}