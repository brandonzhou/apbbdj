package com.shshcom.station.statistics.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mt.bbdj.R
import com.shshcom.station.statistics.http.bean.PackageDetailData
import kotlinx.android.synthetic.main.layout_pack_detail_top.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * 快递留证
 */
class PackDetailImageActivity : AppCompatActivity() {

    companion object{
        /**
         * inImage 显示 入库图片
         */
        fun openActivity(context: Context, data : PackageDetailData, inImage : Boolean = true){
            val intent = Intent(context, PackDetailImageActivity::class.java)
            intent.putExtra("data", data)
            intent.putExtra("inImage", inImage)
            context.startActivity(intent)
        }
    }

    private lateinit var data: PackageDetailData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pack_detail_image)

        initData()
    }


    private fun initData() {
        tv_title.text = "快递留证"
        iv_back.setOnClickListener { finish() }

        data = intent.getSerializableExtra("data") as PackageDetailData
        tvPackId.text = data.number
        tvPickCode.text = data.code
        tvPackPhone.text = if (data.privacy == 0) data.mobile else "菜鸟隐私号码"

        if (data.types == 1) {
            tvPackState.text = "待出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_green))
        } else {
            tvPackState.text = "已出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_grey_9))
        }
    }
}