package com.shshcom.station.setting.ui.activity.company

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
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.setting.http.ApiSetting
import com.shshcom.station.setting.http.bean.CompanySettingData
import kotlinx.android.synthetic.main.activity_company_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 品牌管理
 */
class CompanyListActivity : AppCompatActivity() {

    companion object {
        fun openActivity(activity: Activity) {
            Intent().apply {
                setClass(activity, CompanyListActivity::class.java)
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

    var list: List<CompanySettingData> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_list)

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
            val result = ApiSetting.getBrandManagement()
            LoadDialogUtils.cannelLoadingDialog()
            when (result) {
                is Results.Success -> {
                    myAdapter.list = result.data
                    myAdapter.notifyDataSetChanged()

                }
                is Results.Failure -> {
                    ToastUtil.showLong(result.error.toString())

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fetchData()
    }


    inner class MyAdapter(var list: List<CompanySettingData>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv_company = itemView.findViewById<TextView>(R.id.tv_company)
            val tv_company_setting = itemView.findViewById<TextView>(R.id.tv_company_setting)

            lateinit var data: CompanySettingData

            init {
                itemView.setOnClickListener {
                    CompanySettingActivity.openActivity(activity, data, 1)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_setting_company, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.data = data
            holder.tv_company.text = data.expressName
            holder.tv_company_setting.text = data.title
        }

    }
}