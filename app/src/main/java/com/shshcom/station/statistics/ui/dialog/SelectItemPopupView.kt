package com.shshcom.station.statistics.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.impl.PartShadowPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.mt.bbdj.R

/**
 * desc:
 * author: zhhli
 * 2020/6/10
 */
class SelectItemPopupView(context: Context, val list: List<SelectItem>, val listener: ((SelectItem) -> Unit)) : PartShadowPopupView(context){



    override fun getImplLayoutId(): Int {
        return R.layout.dialog_recyclerview
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .66f).toInt()
    }

    override fun onCreate() {
        super.onCreate()

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.addItemDecoration(decoration)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SelectItemAdapter(list, listener)
        recyclerView.adapter?.notifyDataSetChanged()
    }


}

class SelectItemAdapter(val list: List<SelectItem>, val listener: ((SelectItem) -> Unit)) : RecyclerView.Adapter<SelectItemAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_title = itemView.findViewById<TextView>(R.id.tv_item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog_text, parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tv_title.text = data.title
        val res = holder.tv_title.resources
        if(data.check){
            holder.tv_title.setTextColor(res.getColor(R.color.text_green))
        }else{
            holder.tv_title.setTextColor(res.getColor(R.color.text_grey_6))
        }

        holder.itemView.setOnClickListener {
            listener.invoke(data)
        }

    }

}

