package com.shshcom.station.storage.ml

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lxj.xpopup.core.CenterPopupView
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.utls.StringUtil

//import com.mt.bbdj.databinding.DialogOcrResultBinding

class ExpressInfoPopView(
    context: Context,
    val expressname: String,
    val barcode: String,
    var phone: String
) : CenterPopupView(context) {

    var onDisMiss: OnDisMiss? = null
    var onConfirm: OnConfirm? = null
    private lateinit var tvTrackingCompanyValue: TextView
    private lateinit var etTrackingNumberValue: TextView
    private lateinit var etPhoneValue: EditText
    private lateinit var btnDialogOK: Button

    interface OnDisMiss {
        fun onDismiss()
    }

    interface OnConfirm {
        fun onConfirm(phone: String)
    }


    //private val viewBinding by viewBinding(DialogOcrResultBinding::bind)
//    private val viewBinding by viewBinding{
//        DialogOcrResultBinding.bind(findViewById(R.id.cl))
//    }


    override fun getImplLayoutId(): Int {
        return R.layout.dialog_ocr_result
    }


    override fun onCreate() {
        super.onCreate()

        // ok
//        val viewBinding = DialogOcrResultBinding.bind(centerPopupContainer.findViewById(R.id.cl))

        etTrackingNumberValue = findViewById(R.id.et_tracking_number_value)
        etPhoneValue = findViewById(R.id.et_phone_value)
        tvTrackingCompanyValue = findViewById(R.id.tv_tracking_company_value)
        btnDialogOK = findViewById(R.id.btnDialogOK)


//        viewBinding.apply {
        tvTrackingCompanyValue.text = expressname
        etTrackingNumberValue.text = barcode
        etPhoneValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val isMobile = isMobilePhone(s.toString()) || StringUtil.is95013Num(s.toString())
                btnDialogOK.isEnabled = isMobile

//                if(StringUtil.is95013Num(s.toString())){
//                    btnDialogOK.text = "暂不支持95号码入库"
//                }else{
//                    btnDialogOK.text = "确定"
//
//                }
            }
        })
        etPhoneValue.setText(phone)
        etPhoneValue.requestFocus()

        findViewById<View>(R.id.btnDialogCancel).setOnClickListener {
            dismiss()
            onDisMiss?.onDismiss()

        }
        findViewById<View>(R.id.btnDialogOK).setOnClickListener {
            dismiss()
            onConfirm?.onConfirm(etPhoneValue.text.toString().trim())
        }
//        }


    }


    fun isMobilePhone(mobile: String?): Boolean {
        if (mobile?.isEmpty() == true) {
            return false
        }
        return mobile?.startsWith("1") == true && mobile?.length == 11
    }


}