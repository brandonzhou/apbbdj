package com.shshcom.station.statistics.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.http.bean.PackageDetailData
import com.shshcom.station.statistics.ui.adapter.PackListAdapter
import com.shshcom.station.statistics.ui.dialog.SelectItem
import com.shshcom.station.statistics.ui.dialog.SelectItemPopupView
import com.shshcom.station.storage.http.ApiStorage
import com.shshcom.station.storage.http.bean.ExpressCompany
import kotlinx.android.synthetic.main.act_pack_stock_list.*
import kotlinx.android.synthetic.main.activity_total_pack_stock.recyclerView
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
    private var pageSize = 1
    private var totalSize = 0


    lateinit var time: String

    // 1全部 2已出库 3未出库
    private var outState = 1

    private var expressId = 0

    // 	1全部通知状态 2通知
    private var notify = 1


    private lateinit var companyList: List<ExpressCompany>


    private var isToday : Boolean = true

    companion object {
        /**
         * 1当日日入库快递2已出库3未出库。1-3仅限当日日入库快递。4包含历史入库快递但是出库时间是当日
         */
        fun openActivity(activity: Context, time: String, outType: Int = 1, isToday : Boolean = true) {
            val intent = Intent(activity, PackStockListActivity::class.java)
            intent.putExtra("time", time)
            intent.putExtra("outType", outType)
            intent.putExtra("isToday", isToday)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_pack_stock_list)
        initView()
        initTopSelect()
        initData()
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
        outState = intent.getIntExtra("outType", 1)
        isToday = intent.getBooleanExtra("isToday", true)

        if (isToday) {
            tv_title.text = "今日入库"
            if (outState == 4){
                tv_title.text = "今日出库"
                ll_pack_state.visibility = View.GONE
            }
        } else {
            tv_title.text = time
            ll_notify.visibility = View.GONE
            ll_pack_state.visibility = View.GONE
        }


        // 1全部 2 已出库 3 未出库
        tv_pack_state.text = when(outState){
            1 -> "全部"
            2 -> "已出库"
            else -> "未出库"
        }

        fetchData()
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

    private fun fetchData() {
        scope.launch {
            val result = ApiPackageStatistic.queryExpressDetail(stationId, expressId,
                    notify, outState, time, page)
            when (result) {
                is Results.Success -> {
                    if (isFresh) {
                        recyclerView.refreshComplete()
                        items.clear()
                        adapter.notifyDataSetChanged()
                    } else {
                        recyclerView.loadMoreComplete()
                    }

                    if (null != result.data) {
                        pageSize = result.data.lastPage
                        items.addAll(result.data.dataList)
                        adapter.list = items
                        adapter.notifyDataSetChanged()
                        totalSize = result.data.total
                    }else{
                        totalSize = 0
                    }

                    if (isToday) {
                        if(outState == 4){
                            tv_title.text = "今日出库 (${totalSize}件)"
                        }else{
                            tv_title.text = "今日入库 (${totalSize}件)"
                        }

                    } else {
                        tv_title.text = "$time (${totalSize}件)"
                    }

                }

                is Results.Failure -> {
                    ToastUtil.showShort(result.error.message)
                }
            }
        }
    }

    var popupView: BasePopupView? = null

    private fun initTopSelect() {
        fetchCompany()
        ll_company.setOnClickListener {
            if(popupView!= null && popupView!!.isShow){
                popupView?.dismiss()
                return@setOnClickListener
            }
            val list = ArrayList<SelectItem>(20)
            list.add(SelectItem(0, "全部", expressId == 0))

            for (com in companyList){
                val id = com.express_id
                list.add(SelectItem(id, com.express_name, expressId == id))
            }

            iv_company.setImageDrawable(resources.getDrawable(R.drawable.icon_drop_up))

            popupView = showPopView(list) {
                expressId = it.id
                tv_company.text = it.title
                actionSelect()
            }


        }

        ll_notify.setOnClickListener {
            if(popupView!= null && popupView!!.isShow){
                popupView?.dismiss()
                return@setOnClickListener
            }

            // 1 全部通知状态 2通知
            val item1 = SelectItem(1, "全部", notify ==1)
            val item2 = SelectItem(2, "已通知", notify ==2)
            val list = listOf(item1, item2)
            iv_notify.setImageDrawable(resources.getDrawable(R.drawable.icon_drop_up))

            popupView = showPopView(list) {
                notify = it.id
                tv_notify.text = it.title
                actionSelect()
            }

        }

        ll_pack_state.setOnClickListener {
            if(popupView!= null && popupView!!.isShow){
                popupView?.dismiss()
                return@setOnClickListener
            }

            // 1全部 2 已出库 3 未出库
            val item1 = SelectItem(1, "全部", outState ==1)
            val item2 = SelectItem(2, "已出库", outState ==2)
            val item3 = SelectItem(3, "未出库", outState ==3)

            val list = listOf(item1, item2, item3)

            iv_pack_state.setImageDrawable(resources.getDrawable(R.drawable.icon_drop_up))
            popupView = showPopView(list) {
                outState = it.id
                tv_pack_state.text = it.title
                actionSelect()
            }

        }
    }
    private fun actionSelect() {
        popupView?.dismiss()
        isFresh = true
        page = 1
        fetchData()
    }




    private fun showPopView(list: List<SelectItem>, listener: ((SelectItem) -> Unit)): BasePopupView {
        return XPopup.Builder(this)
                .atView(cl_select)
                .setPopupCallback(My())
                .asCustom(SelectItemPopupView(this, list, listener))
                .show()
    }


    private fun fetchCompany() {
        scope.launch {
            val baseResult = ApiStorage.getExpressCompany(stationId)
            if (baseResult.isSuccess && baseResult.data != null) {
                companyList = baseResult.data
            }
        }
    }

    inner class My : SimpleCallback(){
        override fun onDismiss() {
            super.onDismiss()
            iv_company.setImageDrawable(resources.getDrawable(R.drawable.icon_dropdown))
            iv_notify.setImageDrawable(resources.getDrawable(R.drawable.icon_dropdown))
            iv_pack_state.setImageDrawable(resources.getDrawable(R.drawable.icon_dropdown))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if(resultCode == Activity.RESULT_OK){
            val item= intent?.getSerializableExtra("data") as PackageDetailData
            if(item!= null){
                items.forEach {
                    if(item.pieId.equals(it.pieId)){
                        it.mobile = item.mobile
                        adapter.notifyDataSetChanged()
                        return@forEach
                    }
                }
            }


        }
    }

}