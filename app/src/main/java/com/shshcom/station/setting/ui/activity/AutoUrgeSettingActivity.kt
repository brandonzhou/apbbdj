package com.shshcom.station.setting.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.setting.http.ApiSetting
import com.shshcom.station.setting.http.bean.AutoUrgeData
import com.shshcom.station.setting.http.bean.UrgeType
import kotlinx.android.synthetic.main.activity_auto_urge_setting.*
import kotlinx.android.synthetic.main.activity_block_user_list.rl_back
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 快递自动催取
 */
class AutoUrgeSettingActivity : AppCompatActivity() {
    companion object {
        fun openActivity(activity: Activity, requestCode: Int) {
            Intent().apply {
                setClass(activity, AutoUrgeSettingActivity::class.java)
                activity.startActivityForResult(this, requestCode)
            }
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    lateinit var activity: Activity

    var data: AutoUrgeData? = null
    var urgeType: UrgeType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_urge_setting)
        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        activity = this

        ll_urge_type.setOnClickListener {
            if (data != null) {
                showDialog()
            }

        }

        httpFetchData()

    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()
    }

    private fun httpFetchData() {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity)
            val result = ApiSetting.getPackageUrgeSetting()
            LoadDialogUtils.cannelLoadingDialog()

            when (result) {
                is Results.Success -> {
                    data = result.data

                    updateUI()
                }
                is Results.Failure -> {
                    ToastUtil.showLong(result.error.message)
                }
            }

        }


    }

    private fun updateUI() {
        data?.let {
            urgeType = it.getCurrentType()

            tv_urge_type.text = urgeType!!.msg


            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("type", tv_urge_type.text.toString())
            })

            if (urgeType!!.type == 0) {
                ll_urge_info.visibility = View.GONE
            } else {
                ll_urge_info.visibility = View.VISIBLE
                tv_send_time.text = it.sendTime
                setSmsText()
            }


        }

    }

    private fun setSmsText() {
        val color = resources.getColor(R.color.text_green)

        val content = data!!.smsTemplate.content
        val list = data!!.smsTemplate.list

        var styleContent = SpannableStringBuilder(content)

        for (templateKey in list) {
            val begin = styleContent.indexOf(templateKey.key)
            if (begin > -1) {
                // -1 未找到
                var end = begin + templateKey.key.length
                styleContent = styleContent.replace(begin, end, templateKey.valX)
                end = begin + templateKey.valX.length
                styleContent.setSpan(ForegroundColorSpan(color), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        tv_sms_template.text = styleContent
    }


    private fun showDialog() {
//        val list = data!!.urgeTypeList.map { it.msg }
//
//        val array = arrayListOf<String>()
//        array.addAll(list)

        XPopup.Builder(activity)
                .maxHeight(1600)
                .asCustom(MyBottomPopupView(activity))
                .show()
    }

    private fun httpSet(urgeType: UrgeType) {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity)
            val result = ApiSetting.setPackageUrgeSetting(urgeType.type)
            LoadDialogUtils.cannelLoadingDialog()

            when (result) {
                is Results.Success -> {
                    data!!.curUrgeType = urgeType.type
                    updateUI()
                }
                is Results.Failure -> {
                    ToastUtil.showLong(result.error.message)
                }
            }
        }


    }

    inner class MyBottomPopupView(context: Context) : BottomPopupView(context) {
        val adapter = MyAdapter(data!!.urgeTypeList, data!!.getCurrentType())

        override fun getImplLayoutId(): Int {
            return R.layout.dialog_urge_setting
        }

        override fun onCreate() {
            super.onCreate()
            findViewById<View>(R.id.tv_cancel).setOnClickListener { dismiss() }
            findViewById<View>(R.id.tv_submit).setOnClickListener {
                /**
                 * selectType 保存在 MyAdapter 内部只在 当前 dialog有效
                 * 点击确认， 传递出去
                 */
                httpSet(adapter.selectType!!)
                dismiss()
            }

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }


    inner class MyAdapter(val list: List<UrgeType>, var selectType: UrgeType?) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv_urge_type = itemView.findViewById<TextView>(R.id.tv_urge_type)

            lateinit var urgeType: UrgeType
            var position: Int? = null

            init {
                itemView.setOnClickListener {
                    // 点击选中
                    selectType = urgeType
                    notifyDataSetChanged()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_urge_type, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val pData = list[position]
            holder.urgeType = pData
            holder.position = position
            holder.tv_urge_type.text = pData.msg
            val res = holder.itemView.context.resources
            if (pData.type == selectType!!.type) {
                holder.tv_urge_type.setTextColor(res.getColor(R.color.text_green))
            } else {
                holder.tv_urge_type.setTextColor(res.getColor(R.color.text_main))
            }


        }
    }
}