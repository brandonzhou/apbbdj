package com.shshcom.station.storage.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.XPopupImageLoader
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.ScanImage
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil
import com.mt.bbdj.baseconfig.utls.RxFileTool
import com.shshcom.station.storage.domain.ScanStorageCase
import com.shshcom.station.util.AppTimeUtils
import kotlinx.android.synthetic.main.act_scan_image_local_list.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/*
 * 
 * 
 * @author: zhhli
 * @date: 2020/9/28
 */
class ScanImageFailLocalActivity : AppCompatActivity() {
    lateinit var activity: Activity
    private var list = ArrayList<ScanImage>()
    private val adapter = MyAdapter(list)

    val storageCase = ScanStorageCase.getInstance()

    companion object {
        fun openActivity(activity: Activity) {
            Intent().apply {
                setClass(activity, ScanImageFailLocalActivity::class.java)
                activity.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_scan_image_local_list)
        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        activity = this

        recyclerView.adapter = adapter
        loadData()
    }


    private fun loadData() {
        val failList = storageCase.getScanImageList(ScanImage.State.upload_fail)
        adapter.list = ArrayList(failList)
        adapter.notifyDataSetChanged()
    }

    inner class MyAdapter(var list: ArrayList<ScanImage>) : RecyclerView.Adapter<ViewHolder>() {
        val mmddhhmm = AppTimeUtils.createFormat("MM-dd HH:mm")

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_scan_image_info, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.data = data
            holder.tvPackId.text = data.eId
            holder.tvPackPhone.text = "手机号：${data.phone}"
            holder.tvPickCode.text = "取件码：${data.pickCode}"
            holder.tvBatchNo.text = "批次号：${data.batchNo}"

            val time = AppTimeUtils.date2String(Date(data.time), mmddhhmm)
            holder.tvPackTime.text = time

            Glide.with(holder.itemView).load(data.localPath).into(holder.imageView10)

        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPackId: TextView = itemView.findViewById(R.id._tvPackId)
        val tvPackPhone: TextView = itemView.findViewById(R.id.tvPackPhone)
        val tvPickCode: TextView = itemView.findViewById(R.id.tvPickcode)
        val tvBatchNo: TextView = itemView.findViewById(R.id.tvBatchNo)
        val tvPackTime: TextView = itemView.findViewById(R.id.tvPackTime)
        val tvPackDelete: TextView = itemView.findViewById(R.id.tvPackDelete)
        val imageView10: ImageView = itemView.findViewById(R.id.imageView10)
        lateinit var data: ScanImage

        init {
            itemView.setOnClickListener {
                XPopup.Builder(it.context)
                        .asImageViewer(imageView10, data.localPath, true,
                                -1, -1, 50, false,
                                object : XPopupImageLoader {
                                    override fun loadImage(position: Int, uri: Any, imageView: ImageView) {
                                        Glide.with(imageView).load(uri).apply(RequestOptions().override(Int.MIN_VALUE)).into(imageView)
                                    }

                                    override fun getImageFile(context: Context, uri: Any): File {
                                        return File(uri as String)
                                    }

                                })
                        .show()
            }

            tvPackDelete.setOnClickListener {
                GreenDaoUtil.deleteScanImage(data.eId)
                RxFileTool.deleteFile(File(data.localPath))
                loadData()
            }
        }
    }

}