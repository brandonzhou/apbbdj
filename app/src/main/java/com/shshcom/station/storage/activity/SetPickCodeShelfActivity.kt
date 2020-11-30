package com.shshcom.station.storage.activity

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
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnInputConfirmListener
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.PickupCode
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.shshcom.module_base.network.KResults
import com.shshcom.station.storage.domain.StorageCase
import com.shshcom.station.storage.http.ApiStorage
import com.shshcom.station.storage.http.bean.PickCodeRemote
import com.shshcom.station.storage.widget.InputShelfPopupView
import kotlinx.android.synthetic.main.activity_set_pick_code.recyclerView
import kotlinx.android.synthetic.main.activity_set_pick_code.rl_back
import kotlinx.android.synthetic.main.activity_set_pickcode_shelf.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SetPickCodeShelfActivity : AppCompatActivity() {

    companion object {
        fun openActivity(context: Activity, requestCode: Int) {
            val intent = Intent(context, SetPickCodeShelfActivity::class.java)
            context.startActivityForResult(intent, requestCode)
        }
    }

    val case = StorageCase

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    val list = ArrayList<PickupCode>()
    val adapter = MyAdapter(list)

    lateinit var activity: SetPickCodeShelfActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pickcode_shelf)

        activity = this

        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        initRecyclerview()
        initData()

        btn_shelf_add.setOnClickListener {
            val contentView = InputShelfPopupView(activity)
            contentView.inputConfirmListener = OnInputConfirmListener {
                httpShelfAdd(it)
            }
            XPopup.Builder(this)
                    .autoOpenSoftInput(true)
                    .autoFocusEditText(true)
                    .asCustom(contentView)
                    .show()
        }
    }

    private fun initRecyclerview() {
        recyclerView.adapter = adapter

    }

    private fun initData() {
        val listDB = GreenDaoUtil.listPickupCodeHasShelf()
        list.clear()
        list.addAll(listDB)
        adapter.notifyDataSetChanged()
    }


    private fun httpShelfAdd(shelfName: String) {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity, false)

            val pickCodeRemote = PickCodeRemote(1000, 1, 0, shelfName, 0, "")

            val kResults = ApiStorage.savePickupCode(pickCodeRemote, "add")

            LoadDialogUtils.cannelLoadingDialog()

            when (kResults) {
                is KResults.Success -> {

                    val pickupCode = case.toPickupCode(kResults.data)
                    GreenDaoUtil.insertPickCode(pickupCode)

                    list.add(pickupCode)
                    adapter.notifyDataSetChanged()

                    setResult(Activity.RESULT_OK)

                }

                is KResults.Failure -> {
                    splitties.toast.longToast(kResults.msg)

                }
            }

        }
    }

    private fun httpShelfDelete(pickupCode: PickupCode) {

        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity, false)

            val pickCodeRemote = PickCodeRemote(pickupCode.startNumber, 1, pickupCode.shelfId, pickupCode.shelfNumber, 0, "")


            val kResults = ApiStorage.savePickupCode(pickCodeRemote, "del")

            LoadDialogUtils.cannelLoadingDialog()

            when (kResults) {
                is KResults.Success -> {
                    GreenDaoUtil.delPickCode(pickupCode.uId)

                    list.remove(pickupCode)
                    adapter.notifyDataSetChanged()

                    setResult(Activity.RESULT_OK)

                }

                is KResults.Failure -> {
                    splitties.toast.longToast(kResults.msg)

                }
            }

        }
    }


    inner class MyAdapter(var list: ArrayList<PickupCode>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tv_shelf_name)
            val tvLastCode: TextView = itemView.findViewById(R.id.tv_last_code)
            val tv_shelf_del: TextView = itemView.findViewById(R.id.tv_shelf_del)


            lateinit var data: PickupCode

            init {
                tv_shelf_del.setOnClickListener {
                    XPopup.Builder(activity)
                            .asConfirm("提示", "是否删除 ${data.shelfNumber}？", {
                                httpShelfDelete(data)
                            }, {

                            }).show()
                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pickcode_shelf_manage, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.data = data
            holder.tvName.text = data.shelfNumber

            holder.tvLastCode.text = "最后入库编号 ${data.lastCode}"
        }
    }
}