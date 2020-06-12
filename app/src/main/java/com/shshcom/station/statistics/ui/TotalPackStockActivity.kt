package com.shshcom.station.statistics.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.model.TargetEvent
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.http.bean.StockData
import com.shshcom.station.statistics.http.bean.TotalStockData
import com.shshcom.station.statistics.ui.adapter.TotalPackStockAdapter
import kotlinx.android.synthetic.main.activity_total_pack_stock.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 快递总库存列表
 */
class TotalPackStockActivity : AppCompatActivity(), XRecyclerView.LoadingListener {
    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    val stationId = DbUserUtil.getStationId()


    val items = ArrayList<StockData>()

    //val adapter = MultiTypeAdapter()

    val stockAdapter= TotalPackStockAdapter(items)

    var isFresh = true

    var page = 1

    var pageSize = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_total_pack_stock)
        initData()
    }

    fun initData() {
        tv_title.text = "总库存"
        iv_back.setOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = stockAdapter

//        recyclerView.adapter = adapter
//        adapter.register(TotalPackStockBinder())

        recyclerView.isFocusable = false;
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setLoadingListener(this)

        http()

    }



    private fun http() {
        scope.launch {
            val result = ApiPackageStatistic.totalStock(stationId, page)
            when (result) {
                is Results.Success -> {
                        refreshUI(result.data)
                }

                is Results.Failure -> {


                }
            }


        }
    }

    private fun refreshUI(totalStockData : TotalStockData){
        if (isFresh) {
            recyclerView.refreshComplete()
            items.clear()
            stockAdapter.notifyDataSetChanged()
        } else {
            recyclerView.loadMoreComplete()
        }

        if(totalStockData!= null){
            pageSize = totalStockData.lastPage
            items.addAll(totalStockData.stockDataList)
            stockAdapter.notifyDataSetChanged()

            tv_title.text = "总库存(${totalStockData.total}件)"

        }



    }

    override fun onLoadMore() {
        isFresh = false
        if(page<pageSize){
            page++
            http()
        }else{
            recyclerView.loadMoreComplete();
        }

    }

    override fun onRefresh() {
        isFresh = true
        page = 1
        http()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        EventBus.getDefault().unregister(this)
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveMessage( targetEvent: TargetEvent){
        if(targetEvent.target == TargetEvent.UPDATE_PACK_STATISTIC_OUT_SUCCESS){
            isFresh = true
            page = 1
            http()
        }

    }

}