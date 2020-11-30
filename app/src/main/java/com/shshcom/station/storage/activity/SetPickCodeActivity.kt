package com.shshcom.station.storage.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.interfaces.OnInputConfirmListener
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.PickupCode
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil
import com.mt.bbdj.baseconfig.utls.DialogUtil
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils
import com.shshcom.module_base.network.KResults
import com.shshcom.station.storage.domain.StorageCase
import com.shshcom.station.storage.http.ApiStorage
import com.shshcom.station.storage.http.bean.PickCodeRemote
import com.shshcom.station.storage.widget.InputShelfPopupView
import kotlinx.android.synthetic.main.activity_set_pick_code.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import splitties.toast.toast


class SetPickCodeActivity : AppCompatActivity() {

    private val TAG = "SetPickCodeActivity"

    companion object {
        fun openActivity(context: Activity, requestCode: Int, pickupCode: PickupCode) {
            val intent = Intent(context, SetPickCodeActivity::class.java)
            intent.putExtra("pickupCode", pickupCode)
            context.startActivityForResult(intent, requestCode)
        }
    }

    val case = StorageCase

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    lateinit var mPickupCode: PickupCode


    lateinit var activity: SetPickCodeActivity

    val list = ArrayList<PickupCode>()
    val adapter = MyAdapter(list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pick_code)
        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        rl_back.setOnClickListener { finish() }

        activity = this

        mPickupCode = intent.getSerializableExtra("pickupCode") as PickupCode

        initRecyclerView()


        cl_rule.setOnClickListener {
            XPopup.Builder(this)
                    .asCustom(CustomBottomPopupView(this))
                    .show()
        }

        tv_shelf_manage.setOnClickListener {
            SetPickCodeShelfActivity.openActivity(this, 11)
        }
        tv_shelf_add.setOnClickListener {
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

        btn_save.setOnClickListener {
            if (!mPickupCode.isTail) {
                // 判断是否需要填数字
                val startNumber = et_start_number_value.text.toString().trim()
                if (startNumber.isBlank()) {
                    toast("请输入开始编号")
                    return@setOnClickListener
                }
            }

            if (!isOnlyNumberType()) {
                if (list.size == 0) {
                    // 无货架
                    DialogUtil.prompt(activity, "请先创建货架，再保存")
                    return@setOnClickListener
                }
            }
            httpSave()
        }


        initUI()
    }

    private fun initRecyclerView() {
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW //主轴为水平方向，起点在左端。
        layoutManager.flexWrap = FlexWrap.WRAP //按正常方向换行
        layoutManager.justifyContent = JustifyContent.FLEX_START //交叉轴的起点对齐。
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter
    }


    private fun updateCode() {

        if (!mPickupCode.isTail) {
            // 非尾号
            val startNumber = et_start_number_value.text.toString().trim()
            if (startNumber.isNotBlank()) {
                mPickupCode.startNumber = startNumber.toInt()
            }
        }

        mPickupCode.createCurrentNumber()
        mPickupCode.time = System.currentTimeMillis()
    }

    private fun setSaveBackResult() {
        GreenDaoUtil.insertPickCode(mPickupCode)
        val intent = Intent()
        intent.putExtra("pickupCodeRule", mPickupCode)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun initUI() {
        tv_rule_value.text = mPickupCode.type

        if (isOnlyNumberType()) {
            // 纯数字
            ll_shelf.visibility = View.GONE
        } else {
            // 有货架
            ll_shelf.visibility = View.VISIBLE

            val listDB = GreenDaoUtil.listPickupCodeHasShelf()

            list.addAll(listDB)
            adapter.notifyDataSetChanged()

        }

        if (mPickupCode.isTail) {
            ll_start_number.visibility = View.GONE
        } else {
            et_start_number_value.setText(mPickupCode.startNumber.toString())
            ll_start_number.visibility = View.VISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            onResultResetShelfData()
        }
    }

    private fun onResultResetShelfData() {
        val listDB = GreenDaoUtil.listPickupCodeHasShelf()
        if (listDB.size == 0) {
            // 货架都被删除了
            mPickupCode = GreenDaoUtil.listPickupCodeOnlyNumber()
            // 纯数字
            tv_rule_value.text = mPickupCode.type
            ll_shelf.visibility = View.GONE
            et_start_number_value.setText(mPickupCode.startNumber.toString())

            updateCode()
            // 之前的可能被删除
            setSaveBackResult()
        } else {
            val finddb = GreenDaoUtil.getPickCode(mPickupCode.shelfId)
            if (finddb == null) {
                mPickupCode = listDB[0]
                listDB.forEach {
                    if (it.time > mPickupCode.time) {
                        mPickupCode = it
                    }
                }

                mPickupCode.type = tv_rule_value.text.toString()

                if (mPickupCode.isTail) {
                    ll_start_number.visibility = View.GONE
                } else {
                    et_start_number_value.setText(mPickupCode.startNumber.toString())
                    ll_start_number.visibility = View.VISIBLE
                }

                updateCode()
                // 之前的可能被删除
                setSaveBackResult()
            }

            list.clear()
            list.addAll(listDB)
            adapter.notifyDataSetChanged()


        }

    }


    private fun updateShelfUI(type: String) {

        if (isOnlyNumberType()) {
            // 从纯数字，切换过来
            // 有货架
            ll_shelf.visibility = View.VISIBLE

            val listDB = GreenDaoUtil.listPickupCodeHasShelf()
            if (listDB.size > 0) {
                mPickupCode = listDB[0]
                listDB.forEach {
                    if (it.time > mPickupCode.time) {
                        mPickupCode = it
//                            mPickupCode.type = tv_rule_value.text.toString()
                    }
                }
            }
            list.clear()
            list.addAll(listDB)
            adapter.notifyDataSetChanged()

        }

        mPickupCode.type = type
        tv_rule_value.text = mPickupCode.type

        if (mPickupCode.isTail) {
            ll_start_number.visibility = View.GONE
        } else {
            et_start_number_value.setText(mPickupCode.startNumber.toString())
            ll_start_number.visibility = View.VISIBLE
        }

    }

    private fun isOnlyNumberType() = mPickupCode.type == PickupCode.Type.type_code.desc


    private fun httpShelfAdd(shelfName: String) {
        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity, false)

            val rule = PickupCode.Type.from(mPickupCode.type).rule
            val pickCodeRemote = PickCodeRemote(1000, rule, 0, shelfName, 0, "")

            val kResults = ApiStorage.savePickupCode(pickCodeRemote, "add")

            LoadDialogUtils.cannelLoadingDialog()

            when (kResults) {
                is KResults.Success -> {
                    val pickupCode = case.toPickupCode(kResults.data)
                    GreenDaoUtil.insertPickCode(pickupCode)
                    mPickupCode = pickupCode

                    list.add(pickupCode)
                    adapter.notifyDataSetChanged()

                    if (mPickupCode.isTail) {
                        ll_start_number.visibility = View.GONE
                    } else {
                        ll_start_number.visibility = View.VISIBLE
                    }

                }

                is KResults.Failure -> {
                    splitties.toast.longToast(kResults.msg)
                }
            }

        }
    }

    var isSaving = false;
    private fun httpSave() {
        if (isSaving) {
            return
        }

        scope.launch {
            LoadDialogUtils.showLoadingDialog(activity, false)
            isSaving = true

            updateCode()

            val pickCodeRemote = PickCodeRemote.from(mPickupCode)

            val kResults = ApiStorage.savePickupCode(pickCodeRemote, "modify")

            LoadDialogUtils.cannelLoadingDialog()

            when (kResults) {
                is KResults.Success -> {
                    setSaveBackResult()
                    finish()

                }

                is KResults.Failure -> {
                    splitties.toast.longToast(kResults.msg)
                    isSaving = false

                }
            }

        }
    }

    inner class MyAdapter(var list: ArrayList<PickupCode>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tv_shelf_name)

            val res = tvName.context.resources

            lateinit var data: PickupCode

            init {
                tvName.setOnClickListener {
                    if (data.uId != mPickupCode.uId) {

                        mPickupCode = data
                        mPickupCode.type = tv_rule_value.text.toString()
                        et_start_number_value.setText(mPickupCode.startNumber.toString())
                        notifyDataSetChanged()
                    }
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pickcode_shelf, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.data = data
            holder.tvName.text = data.shelfNumber

            if (data.uId == mPickupCode.uId) {
                holder.tvName.background = holder.res.getDrawable(R.drawable.shape_bg_white_line_green_r14)
                holder.tvName.setTextColor(holder.res.getColor(R.color.text_green))
            } else {
                holder.tvName.background = holder.res.getDrawable(R.drawable.shape_bg_white_line_grey_r14)
                holder.tvName.setTextColor(holder.res.getColor(R.color.text_grey_3))
            }

        }
    }


    /**
     * 底部弹出菜单 实现取件码规则设置
     */
    inner class CustomBottomPopupView(context: Context) : BottomPopupView(context) {
        override fun getImplLayoutId(): Int {
            return R.layout.layout_set_pick_up_bottom_view
        }

        override fun onCreate() {
            super.onCreate()
            findViewById<View>(R.id.tv_type_code).setOnClickListener { v: View? ->
                mPickupCode = GreenDaoUtil.listPickupCodeOnlyNumber()

                tv_rule_value.text = mPickupCode.type

                ll_shelf.visibility = View.GONE

                et_start_number_value.setText(mPickupCode.startNumber.toString())

                ll_start_number.visibility = View.VISIBLE


                dismiss()
            }
            findViewById<View>(R.id.tv_type_shelf_code).setOnClickListener {
                val type = PickupCode.Type.type_shelf_code.desc
                updateShelfUI(type)
                dismiss()
            }
            findViewById<View>(R.id.tv_type_shelf_date_code).setOnClickListener {
                val type = PickupCode.Type.type_shelf_date_code.desc
                updateShelfUI(type)
                dismiss()
            }
            findViewById<View>(R.id.tv_type_shelf_date_tail).setOnClickListener {
                val type = PickupCode.Type.type_shelf_date_tail.desc
                updateShelfUI(type)
                dismiss()
            }
            findViewById<View>(R.id.tv_type_shelf_tail).setOnClickListener {
                val type = PickupCode.Type.type_shelf_tail.desc
                updateShelfUI(type)
                dismiss()
            }
            findViewById<View>(R.id.tv_cancel).setOnClickListener { dismiss() }
        }


    }


    private var interceptKeyEvent = true
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (interceptKeyEvent && event.keyCode == KeyEvent.KEYCODE_ENTER) {
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, 0)
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }


    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }


}