package com.shshcom.station.statistics.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.StringUtil
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

    companion object {
        fun openActivity(context: Activity, data: PackageDetailData) {
            val intent = Intent(context, PackDetailActivity::class.java)
            intent.putExtra("data", data)
            context.startActivityForResult(intent, 1)
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }


    private val stationId = DbUserUtil.getStationId()

    private lateinit var data: PackageDetailData

    private lateinit var decoration: TimeLine

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
        Glide.with(this).load(data.expressIcon).into(ivCompany)
        tvPackId.text = data.number
        tvPickCode.text = data.code
        tvPackPhone.text = if (data.privacy == 0) data.mobile else "菜鸟隐私号码"

        if (data.types == 1) {
            tvPackState.text = "待出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_green))
        } else {
            tvPackState.text = "已出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_grey_9))
            ll_do_pack_out.visibility = View.GONE
        }


        val list = PackTimeItem.createList(data)

        val adapter = PackDetailTimelineAdapter(list, data)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        decoration = TimeLine.Builder(this, list) // 添加数据
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
            XPopup.Builder(this)
                    .asCustom(SendSmsPopView(this))
                    .show()
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
        XPopup.Builder(this)
                .autoOpenSoftInput(true)
                .asCustom(ModifyPhonePopView(this))
                .show()


    }


    private fun resendSMSHttp() {
        scope.launch {
            val result = ApiPackageStatistic.reSendSMSNotice(stationId, data.pieId)
            when (result) {
                is Results.Success -> {
                    val dataH = result.data
                    data.smsStates = dataH.smsStates
                    data.smsTime = dataH.smsTime
                    data.smsContent = dataH.smsContent

                    val list = PackTimeItem.createList(data)
                    val adapter = recyclerView.adapter as PackDetailTimelineAdapter
                    adapter.list = list
                    decoration.replace(list)
                    adapter.notifyDataSetChanged()

                    // 更新上一页
                    setResult(Activity.RESULT_OK, intent.putExtra("data", data))

                    ToastUtil.showShort("发送成功")
                }

                is Results.Failure -> {
                    ToastUtil.showShort("发送失败:\n" + result.error.message)
                }
            }
        }

    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:${data.mobile}")
        startActivity(intent)
    }

    private fun packOutError() {
        OutExceptionActivity.actionTo(this, data.expressId.toString(), data.number)

    }

    private fun packOut() {

    }


    inner class ModifyPhonePopView(context: Context) : CenterPopupView(context) {
        var sendSMS = false

        override fun getImplLayoutId(): Int {
            return R.layout.dialog_pack_detail_modify_phone
        }

        override fun onCreate() {
            super.onCreate()
            val tv_des_phone = findViewById<TextView>(R.id.tv_des)
            tv_des_phone.text = if (data.privacy == 0) "原号码（${data.mobile}）" else "原号码（菜鸟隐私号码）"


            findViewById<View>(R.id.ll_check).setOnClickListener {
                val iv = findViewById<ImageView>(R.id.iv_check_send_sms)
                sendSMS = !sendSMS
                val image = resources.getDrawable(
                        if (sendSMS) R.drawable.ic_node_status_check
                        else R.drawable.ic_node_status_pre_check)
                iv.setImageDrawable(image)
            }
            val etPhone: EditText = findViewById(R.id.et_phone)

            findViewById<View>(R.id.tv_cancel).setOnClickListener { dismiss() }
            findViewById<View>(R.id.tv_submit).setOnClickListener {
                val phone = etPhone.text.trim().toString()
                if (!StringUtil.isMobile(phone)) {
                    ToastUtil.showShort(resources.getString(R.string.phone_format_error))
                } else {
                    dismiss()
                    submit(phone)
                }
            }


        }

        private fun submit(phone: String) {
            data.mobile = phone
            scope.launch {
                val result = ApiPackageStatistic.modifyMobile(data.stationId.toString(), data.pieId, phone)
                when (result) {
                    is Results.Success -> {
                        ToastUtil.showShort("修改成功")
                        tvPackPhone.text = if (data.privacy == 0) data.mobile else "菜鸟隐私号码"
                        // 更新上一页
                        setResult(Activity.RESULT_OK, intent.putExtra("data", data))
                        if (sendSMS) {
                            resendSMSHttp()
                        }


                    }
                    is Results.Failure -> {
                        ToastUtil.showShort("修改失败")
                    }
                }
            }
        }

    }

    inner class SendSmsPopView(context: Context) : CenterPopupView(context) {
        override fun getImplLayoutId(): Int {
            return R.layout.dialog_pack_detail_resend_sms
        }

        override fun onCreate() {
            super.onCreate()
            val tv_des_phone = findViewById<TextView>(R.id.tv_des)
            tv_des_phone.text = if (data.privacy == 0) "原号码（${data.mobile}）" else "原号码（菜鸟隐私号码）"

            if (data.privacy != 0) {
                findViewById<View>(R.id.ll_send_sms).visibility = View.GONE
                findViewById<View>(R.id.tv_cancel_1).setOnClickListener { dismiss() }
            } else {
                findViewById<View>(R.id.ll_privacy).visibility = View.GONE
                findViewById<TextView>(R.id.tv_send_sms_content).text = data.smsContent


                findViewById<View>(R.id.tv_cancel).setOnClickListener { dismiss() }
                findViewById<View>(R.id.tv_submit).setOnClickListener {
                    dismiss()
                    resendSMSHttp()
                }

            }


        }


    }


}