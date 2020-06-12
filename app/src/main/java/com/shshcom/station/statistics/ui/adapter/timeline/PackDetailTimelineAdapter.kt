package com.shshcom.station.statistics.ui.adapter.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mt.bbdj.R
import com.shshcom.station.statistics.http.bean.PackageDetailData
import com.shshcom.station.statistics.ui.PackDetailImageActivity
import com.shshcom.station.util.AntiShakeUtils

/**
 * desc:
 * author: zhhli
 * 2020/6/8
 */
class PackDetailTimelineAdapter(var list: List<PackTimeItem>, val packData : PackageDetailData) : RecyclerView.Adapter<PackDetailTimelineAdapter.ViewHolder>() {
     class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tv_detail_name : TextView = itemView.findViewById(R.id.tv_detail_name)
        val tv_detail_time : TextView = itemView.findViewById(R.id.tv_detail_time)
        val tv_detail_scan_image : TextView = itemView.findViewById(R.id.tv_detail_scan_image)
        val tv_detail_state : TextView = itemView.findViewById(R.id.tv_detail_state)
        val tv_detail_notify_info : TextView = itemView.findViewById(R.id.tv_detail_notify_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = R.layout.item_pack_timeline
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data : PackTimeItem= list[position]
        holder.tv_detail_name.text = data.name
        holder.tv_detail_time.text = data.time




        holder.tv_detail_scan_image.visibility = if(data.showImage) {
            holder.tv_detail_scan_image.setOnClickListener {
                if(AntiShakeUtils.isInvalidClick(it)){
                    return@setOnClickListener
                }

                PackDetailImageActivity.openActivity(holder.itemView.context, packData, position==0)

            }
            holder.tv_detail_scan_image.text = if(data.name.equals("拍照入库")){
                "查看入库照片 >"
            }else{
                "查看出库照片 >"
            }

            View.VISIBLE
        } else {
            View.GONE
        }


        holder.tv_detail_state.visibility = if(data.state.isEmpty()){
            View.GONE
        }else{
            holder.tv_detail_state.text = data.state
            val res = holder.itemView.context.resources
            val color = if(data.stateOk){
                res.getColor(R.color.text_grey_6)
            }else{
                res.getColor(R.color.text_red)
            }
            holder.tv_detail_state.setTextColor(color)
            View.VISIBLE
        }

        holder.tv_detail_notify_info.visibility = if(data.notifyInfo.isEmpty()){
            View.GONE
        }else{
            holder.tv_detail_notify_info.text = data.notifyInfo
            View.VISIBLE
        }



    }


}