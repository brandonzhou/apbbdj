package com.shshcom.station.blockuser.ui.activity

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.shshcom.module_base.network.Results
import com.shshcom.station.blockuser.http.ApiBlockUser
import com.shshcom.station.blockuser.http.bean.BlockUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * desc: 敏感用户列表
 * author: zhhli
 * 2020/6/22
 */
class BlockUserAdapter(var list: ArrayList<BlockUser>, val scope: CoroutineScope) : RecyclerView.Adapter<BlockUserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_block_user, parent, false)
        return ViewHolder(view, scope)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.data = data
        holder.position = position
        holder.tv_block_phone.text = data.mobile
        holder.tv_block_time.text = data.time
    }

    inner class ViewHolder(itemView: View, val scope: CoroutineScope) : RecyclerView.ViewHolder(itemView) {
        val tv_block_phone = itemView.findViewById<TextView>(R.id.tv_block_phone)
        val tv_block_time = itemView.findViewById<TextView>(R.id.tv_block_time)
        val ll_block_delete = itemView.findViewById<View>(R.id.ll_block_delete)


        lateinit var data: BlockUser
        var position: Int? = null


        init {
            ll_block_delete.setOnClickListener {
                val activity = itemView.context as Activity
                XPopup.Builder(activity)
                        .asConfirm("提示", "删除敏感用户?", object : OnConfirmListener {
                            override fun onConfirm() {
                                httpDelete()

                            }
                        }).show()
            }

        }

        private fun httpDelete() {
            scope.launch {
                val activity = itemView.context as Activity
                LoadDialogUtils.showLoadingDialog(activity)
                val results = ApiBlockUser.delBlockUser(data.blockId)
                LoadDialogUtils.cannelLoadingDialog()
                when (results) {
                    is Results.Success -> {
                        list.remove(data)
                        notifyDataSetChanged()

                    }

                    is Results.Failure -> {
                    }
                }
            }

        }
    }


}