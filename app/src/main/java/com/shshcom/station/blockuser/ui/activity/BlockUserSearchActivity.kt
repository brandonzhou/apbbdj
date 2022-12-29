package com.shshcom.station.blockuser.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.mt.bbdj.R
import com.shshcom.module_base.network.KResults
import com.shshcom.station.blockuser.http.ApiBlockUser
import com.shshcom.station.blockuser.http.bean.BlockUser
import com.shshcom.station.blockuser.http.bean.BlockUserData
import kotlinx.android.synthetic.main.activity_block_user_list.recyclerView
import kotlinx.android.synthetic.main.activity_block_user_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class BlockUserSearchActivity : AppCompatActivity(), XRecyclerView.LoadingListener {
    companion object {
        fun openActivity(activity: Activity) {
            val intent = Intent(activity, BlockUserSearchActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }
    lateinit var activity: BlockUserListActivity

    var isFresh = true

    var page = 1

    var pageSize = 1

    var items = ArrayList<BlockUser>()

    val adapter = BlockUserAdapter(items, scope)

    var searchkey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_user_search)

        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        tv_block_search_cancel.setOnClickListener { finish() }

        recyclerView.isFocusable = false;
        recyclerView.isNestedScrollingEnabled = false;
        recyclerView.setLoadingListener(this)

        recyclerView.adapter = adapter



        et_block_phone.isFocusable = true
        et_block_phone.isFocusableInTouchMode = true
        et_block_phone.requestFocus()

        et_block_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchkey = s.toString()
                onRefresh()
            }
        })
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
        if (searchkey.isEmpty()) {
            items.clear()
            adapter.notifyDataSetChanged()
            showDataUI()
            return
        }

        val key = searchkey

        scope.launch {
            val results = ApiBlockUser.blockUserList(key, page)
            if (key == searchkey) {
                when (results) {
                    is KResults.Success -> refreshUI(results.data)

                    is KResults.Failure -> {
                    }
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

        showDataUI()
    }

    private fun showDataUI() {
        if (items.isEmpty()) {
            recyclerView.visibility = View.GONE
            cl_search_no_result.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            cl_search_no_result.visibility = View.GONE
        }
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
            //隐藏键盘
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    //点击空白处，EditText隐藏
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (v != null) {
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }

    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            // 点击的是输入框区域，保留点击EditText的事件
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }
}