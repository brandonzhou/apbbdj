package com.shshcom.station.statistics.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.mt.bbdj.community.activity.OutExceptionActivity
import com.orient.me.widget.rv.itemdocration.timeline.SingleTimeLineDecoration
import com.orient.me.widget.rv.itemdocration.timeline.TimeLine
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.http.bean.PackageDetailData
import com.shshcom.station.statistics.ui.adapter.timeline.PackDetailTimelineAdapter
import com.shshcom.station.statistics.ui.adapter.timeline.PackTimeItem
import com.shshcom.station.statistics.ui.adapter.timeline.StepSTLDecoration
import kotlinx.android.synthetic.main.activity_pack_detail.*
import kotlinx.android.synthetic.main.layout_pack_detail_top.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 快递包裹 详细信息
 */
class PackDetailActivity : AppCompatActivity() {

    companion object{
        fun openActivity(context: Context, data :PackageDetailData){
            val intent = Intent(context,PackDetailActivity::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }


    private val stationId = DbUserUtil.getStationId()

    private lateinit var data: PackageDetailData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pack_detail)

        initData()

        initClick()
    }


    private fun initData() {
        tv_title.text = "快递详情"
        iv_back.setOnClickListener { finish() }

        data = intent.getSerializableExtra("data") as PackageDetailData
        tvPackId.text = data.number
        tvPickCode.text = data.code
        tvPackPhone.text = if (data.privacy == 0) data.mobile else "菜鸟隐私号码"

        if (data.types == 1) {
            tvPackState.text =  "待出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_green))
        } else {
            tvPackState.text =  "已出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_grey_9))
        }


        val list = PackTimeItem.createList(data)

        val adapter = PackDetailTimelineAdapter(list, data)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val decoration = TimeLine.Builder(this, list) // 添加数据
        // 标题颜色 和 标题文本的大小为20sp
        .setTitle(Color.parseColor("#FF333333"), 16)
                // 标题位置设置为子视图上边，所占高度为40dp
                .setTitleStyle(SingleTimeLineDecoration.FLAG_TITLE_TYPE_TOP, 40)
                // 时间线风格 绘制时间线区域的宽度（非时间线宽度）为50dp
                .setLine(SingleTimeLineDecoration.FLAG_LINE_DIVIDE, 50, Color.parseColor("#FFD8D8D8"))
                // 时间点的样式 此处为自绘制
                .setDot(SingleTimeLineDecoration.FLAG_DOT_DRAW)
                // 相同的标题隐藏
                .setSameTitleHide()
                // 设置实现的时间轴
                .build(StepSTLDecoration::class.java)


        recyclerView.addItemDecoration(decoration)

        adapter.notifyDataSetChanged()


    }

    private fun initClick() {
        tv_modify_phone.setOnClickListener {
            modifyPhone()
        }

        tv_resend_sms.setOnClickListener {
            resendSMS()
        }

        tv_call_phone.setOnClickListener {
            callPhone()
        }

        tv_btn_pack_out_error.setOnClickListener {
            packOutError()
        }
        tv_btn_pack_out.setOnClickListener {
            packOut()
        }

    }

    private fun modifyPhone() {
        R.layout.dialog_pack_detail_modify_phone

    }

    private fun resendSMS() {
        scope.launch {
            val result = ApiPackageStatistic.reSendSMSNotice(stationId,data.pieId)
            when(result){
                is Results.Success ->{
                    ToastUtil.showShort("发送成功")
                }

                is Results.Failure ->{
                    ToastUtil.showShort("发送失败:\n"+ result.error.message)
                }
            }
        }

    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$data.mobile")
        startActivity(intent)
    }

    private fun packOutError() {
        OutExceptionActivity.actionTo(this, data.expressId.toString(), data.number)

    }

    private fun packOut() {

    }


}