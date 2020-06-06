package com.shshcom.station.statistics.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.http.bean.PackageDetailData
import com.shshcom.station.statistics.ui.adapter.PackListAdapter
import kotlinx.android.synthetic.main.act_pack_stock_list.*
import kotlinx.android.synthetic.main.activity_total_pack_stock.recyclerView
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*

/**
 * 每日库存列表
 */
class PackStockListActivity : AppCompatActivity(), XRecyclerView.LoadingListener {
    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }


    val stationId = DbUserUtil.getStationId()


    val items = ArrayList<PackageDetailData>()

    val adapter = PackListAdapter(items)


    var isFresh = true

    var page = 1
    var pageSize = 1

    lateinit var time :String
    // 1全部 2已出库 3未出库
    var outType = 1

    companion object{
        /**
         * 1全部 2已出库 3未出库
         */
        fun openActivity(activity: Context, time :String, outType: Int = 1){
            val intent = Intent(activity,PackStockListActivity::class.java)
            intent.putExtra("time",time)
            intent.putExtra("outType",outType)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_pack_stock_list)
        initView()
        initData()
        initTopSelect()
    }

    private fun initView() {
        iv_back.setOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isFocusable = false;
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setLoadingListener(this)
        recyclerView.adapter = adapter

    }

    private fun initData() {
        time = intent.getStringExtra("time")
        outType = intent.getIntExtra("outType",1)

        val timeToday = DateTime.now().toString("yyyy-MM-dd")
        if(timeToday.equals(time)){
            tv_title.text = "今日入库"
        }else{
            tv_title.text = "每日入库"
        }
        fetchData()
    }

    override fun onLoadMore() {
        isFresh = false
        if(page<pageSize) {
            page++
            fetchData()
        }else{
            recyclerView.loadMoreComplete();
        }
    }

    override fun onRefresh() {
        isFresh = true
        page = 1
        fetchData()
    }

    private fun fetchData(){
        scope.launch {
            val result = ApiPackageStatistic.queryExpressDetail(stationId,"",1,outType,time,page)
            when(result){
                is Results.Success ->{
                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }

                    if(null != result.data){
                        pageSize = result.data.lastPage
                        if(isFresh){
                            items.clear()
                        }
                        items.addAll(result.data.dataList)
                        adapter.list = items
                        adapter.notifyDataSetChanged()
                    }

                }

                is Results.Failure ->{
                    ToastUtil.showShort(result.error.message)
                }
            }
        }
    }


    private fun initTopSelect(){
        tvCompany.setOnClickListener {

        }

        tvNofify.setOnClickListener {

        }

        tvPackStatus.setOnClickListener {

        }
    }





}