package com.shshcom.station.statistics.ui;



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.LogUtil
import com.shshcom.station.statistics.http.bean.PackageDetailData
import kotlinx.android.synthetic.main.activity_pack_detail_image.*
import kotlinx.android.synthetic.main.layout_pack_detail_top.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


/**
 * 快递留证
 */
class PackDetailImageActivity : AppCompatActivity() , OnTabSelectListener{

    companion object{
        /**
         * inImage 显示 入库图片
         */
        fun openActivity(context: Context, data : PackageDetailData, inImage : Boolean){
            val intent = Intent(context, PackDetailImageActivity::class.java)
            intent.putExtra("data", data)
            intent.putExtra("inImage", inImage)
            context.startActivity(intent)
        }
    }




    private  lateinit var data :PackageDetailData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pack_detail_image);

        initData()
    }

    private fun initData() {
        tv_title.text = "快递留证"
        iv_back.setOnClickListener { finish() }

        data = intent.getSerializableExtra("data") as PackageDetailData
        tvPackId.text = data.number
        tvPickCode.text = data.code
        tvPackPhone.text = if (data.privacy == 0) data.mobile else "菜鸟隐私号码"
        loadImage(data.expressIcon, ivCompany)

        if (data.types == 1) {
            tvPackState.text =  "待出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_green))
        } else {
            tvPackState.text =  "已出库"
            tvPackState.setTextColor(resources.getColor(R.color.text_grey_9))
        }

        loadImage(data.inPicture, iv_pack_detail_1)




        val list: ArrayList<CustomTabEntity> = ArrayList(2);
        list.add( TabItem(true,"入库照片"))
        list.add(TabItem(false, "出库照片"))

        commonTabLayout.setTabData(list)

        commonTabLayout.setOnTabSelectListener(this)
    }


    fun loadImage(url :String ?, imageView : ImageView){
        if(!url.isNullOrEmpty()){
            Glide.with(this).load(url).into(imageView)
        }else{
            imageView.visibility = View.GONE
        }
    }

    override fun onTabSelect(position: Int) {
        if(position == 0){
            loadImage(data.inPicture, iv_pack_detail_1)
            cl_none_image.visibility = View.GONE
        }else {

            if(data.outPicture.isNullOrEmpty() && data.outPictureFace.isNullOrEmpty()){
                cl_none_image.visibility = View.VISIBLE
            }else{
                loadImage(data.outPicture, iv_pack_detail_1)
                loadImage(data.outPictureFace, iv_pack_detail_2)
                cl_none_image.visibility = View.GONE
            }

        }
    }

    override fun onTabReselect(position: Int) {
        LogUtil.d("zhhli", data.toString())
    }



    private class TabItem(val isIn: Boolean, val title : String) : CustomTabEntity{
        override fun getTabUnselectedIcon(): Int {
            return 0
        }

        override fun getTabSelectedIcon(): Int {
            return 0
        }

        override fun getTabTitle(): String {
            return title
        }

    }


}

