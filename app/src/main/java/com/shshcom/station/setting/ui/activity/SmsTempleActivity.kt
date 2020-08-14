package com.shshcom.station.setting.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.mt.bbdj.baseconfig.utls.StringUtil
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.network.KResults
import com.shshcom.station.setting.http.ApiSetting
import com.shshcom.station.setting.http.bean.CustomSMSTemplateData
import kotlinx.android.synthetic.main.activity_notify_pack_list.rl_back
import kotlinx.android.synthetic.main.activity_sms_temple.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import splitties.toast.longToast
import splitties.toast.toast

class SmsTempleActivity : AppCompatActivity() {

    companion object {
        fun openActivity(activity: Activity) {
            Intent().apply {
                setClass(activity, SmsTempleActivity::class.java)
                activity.startActivity(this)
            }
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    lateinit var activity: Activity
    var data: CustomSMSTemplateData? = null

    val stationId = DbUserUtil.getStationId()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_temple)

        activity = this
        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }


        tv_submit_sms_template.setOnClickListener { data?.let { httpSave(it) } }

        et_sms_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString()
                data?.let {
                    it.phone = phone
                    setSmsText()
                }

                if (phone.length == 11 && !StringUtil.isMobile(phone)) {
                    ToastUtil.showLong("请填写正确的手机号")
                }

            }
        })

        et_sms_address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val address = s.toString()
                if (address.length > 16) {
                    longToast("联系地址字数过多，请精简")
                }

                data?.let {
                    it.customAddress = address
                    setSmsText()
                }


            }
        })



        fetchData()

    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun fetchData() {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity)
            val result = ApiSetting.getCustomSMSTemplate()
            LoadDialogUtils.cannelLoadingDialog()
            when (result) {
                is KResults.Success -> {
                    result.data?.let {
                        data = it
                        et_sms_phone.setText(it.phone)
                        et_sms_address.setText(it.customAddress)
                        setSmsText()

                    }

                }

                is KResults.Failure -> {
                    ToastUtil.showLong(result.error.message)
                }
            }
        }
    }


    private fun setSmsText() {
        if (data == null) {
            return
        }
        val smsTemplate = data!!.customSmsTemplate
        smsTemplate?.let {
            val color = resources.getColor(R.color.text_green)

            val content = it.content

            val list = it.list

            list.forEach {
                if (it.key == "mobile" && data!!.phone.isNotEmpty()) {
                    it.valX = data!!.phone
                }
                if (it.key == "address" && data!!.customAddress.isNotEmpty()) {
                    it.valX = data!!.customAddress
                }
            }

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
    }


    private fun httpSave(data: CustomSMSTemplateData) {
        if (!StringUtil.isMobile(data.phone)) {
            ToastUtil.showLong("请填写正确的手机号")
            return
        }
        if (data.customAddress.length > 16) {
            longToast("联系地址字数过多，请精简")
            return
        }

        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity)
            val result = ApiSetting.saveCustomSMSTemplate(data.phone, data.customAddress)
            LoadDialogUtils.cannelLoadingDialog()
            when (result) {
                is KResults.Success -> {
                    toast("修改成功")
                }

                is KResults.Failure -> {
                    ToastUtil.showLong(result.error.message)
                }
            }
        }
    }


}