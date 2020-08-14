package com.shshcom.station.blockuser.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.mt.bbdj.baseconfig.utls.StringUtil
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.network.KResults
import com.shshcom.station.blockuser.http.ApiBlockUser
import com.shshcom.station.blockuser.http.bean.BlockUser
import com.shshcom.station.blockuser.http.bean.BlockUserData
import kotlinx.android.synthetic.main.activity_block_user_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class BlockUserListActivity : AppCompatActivity(), XRecyclerView.LoadingListener {
    companion object {
        fun openActivity(activity: Activity) {
            val intent = Intent(activity, BlockUserListActivity::class.java)
            activity.startActivity(intent)
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }
    lateinit var activity: BlockUserListActivity

    var isFresh = true

    var page = 1

    var pageSize = 1

    var items = ArrayList<BlockUser>()

    val adapter = BlockUserAdapter(items, scope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_user_list)
        activity = this

        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }


        ll_block_search.setOnClickListener { BlockUserSearchActivity.openActivity(this) }

        tv_block_user_add.setOnClickListener { addBlockUser() }

        recyclerView.isFocusable = false;
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setLoadingListener(this)

        recyclerView.adapter = adapter


        httpUserSearch()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    override fun onLoadMore() {
        isFresh = false
        if (page < pageSize) {
            page++
            httpUserSearch()
        } else {
            recyclerView.loadMoreComplete();
        }
    }

    override fun onRefresh() {
        isFresh = true
        page = 1
        httpUserSearch()
    }


    private fun httpUserSearch() {
        scope.launch {
            val results = ApiBlockUser.blockUserList("", page)
            when (results) {
                is KResults.Success -> refreshUI(results.data)

                is KResults.Failure -> {
                }
            }
        }

    }


    private fun refreshUI(blockUserData: BlockUserData) {
        if (isFresh) {
            recyclerView.refreshComplete()
            items.clear()
            adapter.notifyDataSetChanged()
        } else {
            recyclerView.loadMoreComplete()
        }

        if (blockUserData != null) {
            pageSize = blockUserData.lastPage
            items.addAll(blockUserData.data)
            adapter.notifyDataSetChanged()

            if (isFresh) {
                recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun addBlockUser() {
        XPopup.Builder(this)
                .autoOpenSoftInput(true)
                .asCustom(AddBlockUserPopView(this))
                .show()
    }

    private fun submit(phone: String) {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity)
            val result = ApiBlockUser.addBlockUser(phone)
            LoadDialogUtils.cannelLoadingDialog()
            when (result) {
                is KResults.Success -> {
                    ToastUtil.showShort("添加成功")

                    onRefresh()
                }
                is KResults.Failure -> {
                    ToastUtil.showShort(result.error.message)
                }
            }
        }
    }


    inner class AddBlockUserPopView(context: Context) : CenterPopupView(context) {

        override fun getImplLayoutId(): Int {
            return R.layout.dialog_block_user_add
        }

        override fun onCreate() {

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


    }


}