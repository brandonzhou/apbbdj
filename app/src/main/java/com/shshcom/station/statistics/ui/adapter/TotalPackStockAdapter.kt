package com.shshcom.station.statistics.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mt.bbdj.R
import com.shshcom.station.statistics.http.bean.StockData

/**
 * desc:
 * author: zhhli
 * 2020/6/5
 */
class TotalPackStockAdapter(var list: List<StockData>) : RecyclerView.Adapter<TotalPackStockAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime : TextView = itemView.findViewById(R.id.tv_stock_time)
        val tvNumber : TextView = itemView.findViewById(R.id.tv_stock_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_multi_total_pack_stock, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data : StockData = list[position]
        holder.tvTime.text = data.inTime.replace("-",".")
        holder.tvNumber.text = data.total.toString()
    }


}