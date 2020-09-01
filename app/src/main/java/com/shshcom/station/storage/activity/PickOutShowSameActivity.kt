package com.shshcom.station.storage.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.bumptech.glide.Glide
import com.mt.bbdj.R
import com.shshcom.station.storage.http.bean.ExpressPackInfo
import com.shshcom.station.storage.http.bean.ExpressPackInfoList
import com.shshcom.station.storage.http.bean.WxOfficeSubscribeState
import kotlinx.android.synthetic.main.activity_pick_out_show_same.*

class PickOutShowSameActivity : AppCompatActivity() {

    companion object {
        fun openActivity(activity: Activity, data: ExpressPackInfoList, wxState: WxOfficeSubscribeState?) {
            val intent = Intent(activity, PickOutShowSameActivity::class.java)
            intent.putExtra("data", data)
            // val wxState2 = WxOfficeSubscribeState( "http://weixin.qq.com/q/02D3KftrL1eLh1k8Kcxvcf",1)
            intent.putExtra("wxState", wxState)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_out_show_same)
        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }
        tv_restart_scan.setOnClickListener { finish() }

        val data = intent.getSerializableExtra("data") as ExpressPackInfoList

        var wxOfficeSubscribeState = intent.getSerializableExtra("wxState") as WxOfficeSubscribeState?
        wxOfficeSubscribeState?.let {
            showQrCode(it)
        }

        val phone = data.list[0].mobile
        val size = data.list.size.toString() + "件"
        tv_pack_phone.text = phone
        tv_pack_size.text = size.toString()

        val adapter = MyAdapter(data.list)

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()


    }


    private fun showQrCode(subscribeState: WxOfficeSubscribeState) {
        val state = subscribeState.state
        if (state == 1) {
            tv_wx_state.text = "已绑定公众号"
        } else {
            tv_wx_state.text = "未绑定公众号"
            if (subscribeState.qrcode.isEmpty()) {
                return
            }
            ll_wx_qr.visibility = View.VISIBLE
            tv_wx_state.setTextColor(tv_wx_state.resources.getColor(R.color.text_red))

            Glide.with(this).load(subscribeState.qrcode).into(iv_wx_qr);
        }

    }

    inner class MyAdapter(val list: List<ExpressPackInfo>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv_pack_company_name = itemView.findViewById<TextView>(R.id.tv_pack_company_name)
            val tv_pack_barcode = itemView.findViewById<TextView>(R.id.tv_pack_barcode)
            val tv_pack_pickcode = itemView.findViewById<TextView>(R.id.tv_pack_pickcode)
            val tv_pack_in_time = itemView.findViewById<TextView>(R.id.tv_pack_in_time)


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_scan_pickout_same, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val info = list[position]

            holder.tv_pack_company_name.text = "${info!!.expressName}："
            holder.tv_pack_barcode.text = info.number
            holder.tv_pack_pickcode.text = info.code
            holder.tv_pack_in_time.text = info.warehousingTime
        }

    }


}