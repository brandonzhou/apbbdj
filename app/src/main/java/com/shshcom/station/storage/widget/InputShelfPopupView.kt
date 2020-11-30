package com.shshcom.station.storage.widget

import android.content.Context
import android.view.View
import android.widget.EditText
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.interfaces.OnInputConfirmListener
import com.mt.bbdj.R

/*
 * 
 * 
 * @author: zhhli
 * @date: 2020/11/27
 */

class InputShelfPopupView(context: Context) : CenterPopupView(context) {

    var inputConfirmListener: OnInputConfirmListener? = null

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_pickcode_shelf_add
    }

    override fun onCreate() {
        super.onCreate()

        val et = findViewById<EditText>(R.id.et_shelf_name)

        findViewById<View>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }


        findViewById<View>(R.id.tv_shelf_add).setOnClickListener {
            val text = et.text.toString().trim()
            if (text.isBlank()) {
                splitties.toast.toast("请输入货架号")
                return@setOnClickListener
            }


            inputConfirmListener?.onConfirm(text)
            dismiss()

        }

    }
}