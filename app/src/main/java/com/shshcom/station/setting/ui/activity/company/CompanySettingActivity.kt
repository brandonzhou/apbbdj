package com.shshcom.station.setting.ui.activity.company

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.setting.http.ApiSetting
import com.shshcom.station.setting.http.bean.CompanySettingData
import com.shshcom.station.util.AntiShakeUtils
import kotlinx.android.synthetic.main.activity_block_user_list.rl_back
import kotlinx.android.synthetic.main.activity_company_setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CompanySettingActivity : AppCompatActivity() {

    companion object {
        fun openActivity(activity: Activity, data: CompanySettingData, requestCode: Int) {
            Intent().apply {
                setClass(activity, CompanySettingActivity::class.java)
                putExtra("data", data)
                activity.startActivityForResult(this, requestCode)
            }
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    lateinit var activity: Activity
    lateinit var data: CompanySettingData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_setting)

        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        activity = this

        data = intent.getSerializableExtra("data") as CompanySettingData

        tv_title.text = data.expressName

        tv_company_setting_1_title.text = data.option.get(0).title
        tv_company_setting_1_msg.text = data.option.get(0).msg

        tv_company_setting_2_title.text = data.option.get(1).title
        tv_company_setting_2_msg.text = data.option.get(0).msg



        refreshView()


        cl_company_setting_1.setOnClickListener {
            if (AntiShakeUtils.isInvalidClick(it)) {
                return@setOnClickListener
            }
            if (data.type == 2) {
                httpSave(1)
            }
        }
        cl_company_setting_2.setOnClickListener {
            if (AntiShakeUtils.isInvalidClick(it)) {
                return@setOnClickListener
            }
            if (data.type == 1) {
                httpSave(2)
            }
        }
    }

    private fun refreshView() {
        if (data.type == 1) {
            cl_company_setting_1.background = resources.getDrawable(R.drawable.shape_bg_white_line_green_r5)
            cl_company_setting_2.background = resources.getDrawable(R.drawable.shape_bg_white_r5)

            iv_company_setting_1.setImageResource(R.drawable.ic_check_all)
            iv_company_setting_2.setImageResource(R.drawable.ic_check_false)
        } else {
            cl_company_setting_1.background = resources.getDrawable(R.drawable.shape_bg_white_r5)
            cl_company_setting_2.background = resources.getDrawable(R.drawable.shape_bg_white_line_green_r5)

            iv_company_setting_1.setImageDrawable(resources.getDrawable(R.drawable.ic_check_false))
            iv_company_setting_2.setImageDrawable(resources.getDrawable(R.drawable.ic_check_all))
        }
    }

    private fun httpSave(type: Int) {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity)
            val result = ApiSetting.saveBrandManagement(data.expressId, type)
            LoadDialogUtils.cannelLoadingDialog()

            when (result) {
                is Results.Success -> {
                    data.type = type
                    refreshView()
                    setResult(Activity.RESULT_OK)
                }
                is Results.Failure -> {
                    ToastUtil.showLong(result.error.message)
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


}