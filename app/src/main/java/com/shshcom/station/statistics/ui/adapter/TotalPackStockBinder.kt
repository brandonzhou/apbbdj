package com.shshcom.station.statistics.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.mt.bbdj.R
import com.shshcom.station.statistics.http.bean.StockData

/**
 * desc:
 * author: zhhli
 * 2020/6/5
 */
class TotalPackStockBinder : ItemViewBinder<StockData, TotalPackStockBinder.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime : TextView = itemView.findViewById(R.id.tv_stock_time)
        val tvNumber : TextView = itemView.findViewById(R.id.tv_stock_number)

    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_multi_total_pack_stock, parent, false))
    }

    override fun onBindViewHolder(hold: ViewHolder, data: StockData) {
        hold.tvTime.text = data.inTime.replace("-",".")
        hold.tvNumber.text = data.total.toString()
    }
}