package com.shshcom.station.statistics.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.ui.adapter.TotalPackStockBinder
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
class TotalPackStockActivity : AppCompatActivity() {
    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    val stationId = DbUserUtil.getStationId()


    val items = ArrayList<Any>()

    val adapter = MultiTypeAdapter()


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
        recyclerView.adapter = adapter

        adapter.register(TotalPackStockBinder())


    }

    private fun initData() {
        http()
    }

    private fun http() {
        scope.launch {
            val result = ApiPackageStatistic.totalStock(stationId, 1)
            when (result) {
                is Results.Success -> {
                    val stockList = result.data.stockDataList
                    adapter.items = stockList
                    adapter.notifyDataSetChanged()
                }

                is Results.Failure -> {


                }
            }


        }
    }
}