package com.shshcom.station.storage.ml

import android.content.Context
import android.graphics.Bitmap
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.lxj.xpopup.core.CenterPopupView
import com.mt.bbdj.R
//import com.mt.bbdj.databinding.DialogBarcodeResultBinding
import com.shshcom.station.imageblurdetection.ImageDetectionUseCase

class ImagePopView : CenterPopupView {
    lateinit var bitmap: Bitmap
    lateinit var info: String
    lateinit var expressname: String
    lateinit var barcode: String
    var onDisMiss: OnDisMiss? = null
    var onConfirm: OnConfirm? = null
    private lateinit var tvTrackingCompanyValue: TextView
    private lateinit var etTrackingNumberValue: TextView
    private lateinit var etPhoneValue: EditText
    private lateinit var btnDialogOK: Button
    private lateinit var btnDialogCancel: Button
    private lateinit var tvTitle: TextView
    interface OnDisMiss{
        fun  onDismiss()
    }

    interface OnConfirm{
        fun onConfirm()
    }

//    private val viewBinding by viewBinding(DialogBarcodeResultBinding::bind)


    constructor(context: Context) : super(context) {}
    constructor(context: Context, bitmap: Bitmap, info: String) : super(context) {
        this.bitmap = bitmap
        this.info = info
    }

    constructor(context: Context, bitmap: Bitmap, expressname: String, barcode: String):super(context){
        this.bitmap = bitmap
        this.expressname = expressname
        this.barcode = barcode
        this.info = ""
    }


    override fun getImplLayoutId(): Int {
        return R.layout.dialog_barcode_result
    }


    override fun onCreate() {
        super.onCreate()
        tvTitle = findViewById(R.id.tv_title)
        etTrackingNumberValue = findViewById(R.id.et_tracking_number_value)
        etPhoneValue = findViewById(R.id.et_phone_value)
        tvTrackingCompanyValue = findViewById(R.id.tv_tracking_company_value)
        btnDialogOK = findViewById(R.id.btnDialogOK)
        btnDialogCancel = findViewById(R.id.btnDialogCancel)


//        viewBinding.apply {
            tvTitle.text = "识别中..."
            tvTrackingCompanyValue.text = expressname
            etTrackingNumberValue.text = barcode
            etPhoneValue.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {

                    btnDialogOK.isEnabled = isMobilePhone(s.toString())
                    btnDialogCancel.isEnabled = isMobilePhone(s.toString())
                }
            })
            etPhoneValue.requestFocus()

            val imageView = findViewById<ImageView>(R.id.ivDialogContent)
            val tvDialogContent = findViewById<TextView>(R.id.tvDialogContent)
            btnDialogCancel.isEnabled = false
            btnDialogOK.isEnabled = false

            imageView.setImageBitmap(bitmap)
            tvDialogContent.text = info + " \n ocr 识别中..."

            val bitmapCV = ImageDetectionUseCase.cvtColor(bitmap)

            imageView.postDelayed({
                imageView?.setImageBitmap(bitmapCV)
            }, 2000)
//        }



        /*StorageCase.getOCRResult(bitmapCV, object: ICaseBack<String>{
            override fun onSuccess(result: String) {
                tvDialogContent.text = result
            }

            override fun onError(error: String) {
            }

        })*/
//        StorageCase.getOCRResultObject(bitmapCV, object : ICaseBack<OcrResultData> {
//            override fun onSuccess(result: OcrResultData) {
//                btnDialogCancel.isEnabled = true
////                btnDialogOK.isEnabled = true
//                tvDialogContent.text = "识别完成 \n 耗时：" + result.spendTime
//                if (result.isStatus) {
//                    et_phone_value.setText(result.mobile)
//                    tv_title.text = "成功"
//                    et_phone_value.setSelection(result.mobile.length)
//                } else {
//                    tv_title.text = "失败"
//                }
//            }
//
//            override fun onError(error: String) {
//                tv_title.text = "失败:" + error
//                btnDialogCancel.isEnabled = true
//            }
//
//        })

        findViewById<View>(R.id.btnDialogCancel).setOnClickListener {
            dismiss()
            onDisMiss?.onDismiss()

        }
        findViewById<View>(R.id.btnDialogOK).setOnClickListener {
            dismiss()
            onConfirm?.onConfirm()
        }


    }


    fun isMobilePhone(mobile: String?): Boolean{
        if (mobile?.isEmpty() == true) {
            return false
        }
        return mobile?.startsWith("1")== true && mobile?.length == 11
    }


}