package com.shshcom.station.statistics.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.http.bean.StockData
import com.shshcom.station.statistics.ui.adapter.TotalPackStockAdapter
import kotlinx.android.synthetic.main.activity_total_pack_stock.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/**
 * 快递总库存
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_pack_stock)
        initView()
        initData()
    }

    fun initView() {
        tv_title.text = "总入库"
        iv_back.setOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = stockAdapter

//        recyclerView.adapter = adapter
//        adapter.register(TotalPackStockBinder())

        recyclerView.isFocusable = false;
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setLoadingListener(this)


    }

    private fun initData() {
        http()
    }

    private fun http() {
        scope.launch {
            val result = ApiPackageStatistic.totalStock(stationId, page)
            when (result) {
                is Results.Success -> {
                    val stockList = result.data.stockDataList
                    refreshUI(stockList)
                }

                is Results.Failure -> {


                }
            }


        }
    }

    private fun refreshUI(stockList : List<StockData>){
        if (isFresh) {
            recyclerView.refreshComplete();
        } else {
            recyclerView.loadMoreComplete();
        }

        stockAdapter.list = stockList
        stockAdapter.notifyDataSetChanged()


    }

    override fun onLoadMore() {
        isFresh = false
        page++
        http()
    }

    override fun onRefresh() {
        isFresh = true
        page = 1
        http()
    }

    override fun onResume() {
        super.onResume()
        recyclerView.refresh()
    }
}