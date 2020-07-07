package com.shshcom.station.statistics.ui

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
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.mt.bbdj.community.activity.WebDetailActivity
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.http.bean.NotifyBean
import com.shshcom.station.statistics.http.bean.PackNotifyData
import kotlinx.android.synthetic.main.activity_notify_pack_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotifyPackListActivity : AppCompatActivity(), XRecyclerView.LoadingListener {

    companion object {
        fun openActivity(activity: Activity) {
            Intent().apply {
                setClass(activity, NotifyPackListActivity::class.java)
                activity.startActivity(this)
            }
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    lateinit var activity: Activity

    val stationId = DbUserUtil.getStationId()

    lateinit var myAdapter: MyAdapter

    var isFresh = true

    var page = 1

    var pageSize = 1

    var items: ArrayList<NotifyBean> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_pack_list)

        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        tv_go_wechat.setOnClickListener {
            WebDetailActivity.actionTo(this, "https://guide.shshcom.com/guide.html")
        }


        activity = this

        myAdapter = MyAdapter(items)

        recyclerView.adapter = myAdapter
        recyclerView.isFocusable = false;
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setLoadingListener(this)

        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun fetchData() {
        scope.launch {
            val result = ApiPackageStatistic.queryStationNoticeStats(stationId, page)
            when (result) {
                is Results.Success -> {
                    refreshUI(result.data)
                }

                is Results.Failure -> {
                    ToastUtil.showLong(result.error.message)
                }
            }


        }
    }

    private fun refreshUI(packNotifyData: PackNotifyData) {
        if (isFresh) {
            recyclerView.refreshComplete()
            items.clear()
            myAdapter.notifyDataSetChanged()
        } else {
            recyclerView.loadMoreComplete()
        }

        packNotifyData?.let {
            pageSize = it.lastPage
            it.list?.forEach {
                it.date = it.date.replace("-", ".")
            }
            items.addAll(it.list)
            myAdapter.notifyDataSetChanged()
        }
    }

    override fun onLoadMore() {
        isFresh = false
        if (page < pageSize) {
            page++
            fetchData()
        } else {
            recyclerView.loadMoreComplete();
        }

    }

    override fun onRefresh() {
        isFresh = true
        page = 1
        fetchData()
    }

    inner class MyAdapter(var list: List<NotifyBean>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv_notify_date = itemView.findViewById<TextView>(R.id.tv_notify_date)
            val tv_notify_sms = itemView.findViewById<TextView>(R.id.tv_notify_sms)
            val tv_notify_wechat = itemView.findViewById<TextView>(R.id.tv_notify_wechat)


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_notify_number_sms_wechat, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.tv_notify_date.text = data.date
            holder.tv_notify_sms.text = data.smsTotal.toString()
            holder.tv_notify_wechat.text = data.wechatToal.toString()
        }

    }

}