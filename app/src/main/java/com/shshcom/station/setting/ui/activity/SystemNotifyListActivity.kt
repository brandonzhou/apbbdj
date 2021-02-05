package com.shshcom.station.setting.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.mt.bbdj.community.activity.WebDetailActivity
import com.shshcom.module_base.network.KResults
import com.shshcom.station.setting.http.ApiSetting
import com.shshcom.station.setting.http.bean.SystemNotifyBean
import kotlinx.android.synthetic.main.activity_system_notify_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SystemNotifyListActivity : AppCompatActivity() {


    companion object {
        fun openActivity(activity: Activity) {
            Intent().apply {
                setClass(activity, SystemNotifyListActivity::class.java)
                activity.startActivity(this)
            }
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    lateinit var activity: Activity
    lateinit var myAdapter: MyAdapter

    var list: List<SystemNotifyBean> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_notify_list)

        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        activity = this

        myAdapter = MyAdapter(list)

        recyclerView.adapter = myAdapter

        fetchData()
    }

    private fun fetchData() {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity)
            val result = ApiSetting.getNoticeList(20, false)
            LoadDialogUtils.cannelLoadingDialog()
            when (result) {
                is KResults.Success -> {
                    myAdapter.list = result.data
                    myAdapter.notifyDataSetChanged()

                }
                is KResults.Failure -> {
                    ToastUtil.showLong(result.error.toString())

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    inner class MyAdapter(var list: List<SystemNotifyBean>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val iv_item_bg = itemView.findViewById<ImageView>(R.id.iv_item_bg)
            val item_iv_unread = itemView.findViewById<ImageView>(R.id.item_iv_unread)

            val tv_item_title = itemView.findViewById<TextView>(R.id.tv_item_title)
            val tv_item_time = itemView.findViewById<TextView>(R.id.tv_item_time)
            val tv_item_content = itemView.findViewById<TextView>(R.id.tv_item_content)

            lateinit var data: SystemNotifyBean

            init {
                itemView.setOnClickListener {
                    WebDetailActivity.actionTo(activity, data.link)
                    data.states = 1
                    notifyDataSetChanged()
                }
            }
        }




        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.data = data
            holder.tv_item_title.text = "通知公告"
            holder.tv_item_time.text = data.createTime
            holder.tv_item_content.text = data.title

            holder.item_iv_unread.visibility = if(data.states ==1){
                View.GONE
            }else{
                View.VISIBLE
            }

        }
    }
}