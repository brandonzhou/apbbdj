package com.shshcom.station.storage.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.google.zxing.BarcodeFormat
import com.king.zxing.CaptureActivity
import com.king.zxing.CaptureHelper
import com.king.zxing.camera.FrontLightMode
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.*
import com.shshcom.module_base.network.KResults
import com.shshcom.station.storage.http.ApiStorage
import kotlinx.android.synthetic.main.activity_scan_only_barcode.*
import kotlinx.android.synthetic.main.activity_scan_pick_out.rl_back
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/**
 * 免通知入库
 *
 * 只扫码
 */
class ScanOnlyBarcodeActivity : CaptureActivity() {
    private val TAG = "ScanOnlyBarcodeActivity"

    companion object {
        fun openActivity(context: Activity) {
            val intent = Intent(context, ScanOnlyBarcodeActivity::class.java)
            context.startActivityForResult(intent, 1)
        }
    }


    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }
    lateinit var activity: ScanOnlyBarcodeActivity

    /*请求系统权限-摄像头*/
    private val PERMISSION_REQUEST_CODE_CAMERA = 1
    private lateinit var helper: CaptureHelper

    private var currentBarCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        YCAppBar.setStatusBarLightMode(this, Color.WHITE)
        StatusBarUtils.StatusBarLightMode(this)
        activity = this
        rl_back.setOnClickListener { finish() }
        initPermission()
        initCapture()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_scan_only_barcode
    }

    override fun getViewfinderViewId(): Int {
        return 0
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    private fun initPermission() {
        //请求Camera权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE_CAMERA)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE_CAMERA ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.i(TAG, "onRequestPermissionsResult granted")
                } else {
                    LogUtil.i(TAG, "onRequestPermissionsResult denied")
                    UtilDialog.showDialog(this, "请前往设置中开启摄像头权限")
                }
            else -> {
            }
        }
    }


    private fun initCapture() {
        helper = captureHelper
        helper.fullScreenScan(true)
                .supportVerticalCode(true) //支持扫垂直条码，建议有此需求时才使用。
                .decodeFormats(EnumSet.of(BarcodeFormat.CODE_128)) //设置只识别二维码会提升速度
                .frontLightMode(FrontLightMode.AUTO) //设置闪光灯模式
                .tooDarkLux(45f) //设置光线太暗时，自动触发开启闪光灯的照度值
                .brightEnoughLux(100f) //设置光线足够明亮时，自动触发关闭闪光灯的照度值
                .continuousScan(false) //是否连扫
    }

    override fun onResultCallback(result: String?): Boolean {

        if (!StringUtil.isMatchExpressCode(result)) {
            helper.restartPreviewAndDecode()
            return true
        }


        if (result == currentBarCode) {
            SoundHelper.getInstance().playNotifiRepeatSound()
            rl_back.postDelayed({
                if (rl_back != null) {
                    helper.restartPreviewAndDecode()
                }
            }, 1000)
            return true
        }

        currentBarCode = result!!
        tv_bar_code.text = currentBarCode

        httpInfo(currentBarCode)


        return true
    }


    private fun httpInfo(barcode: String) {
        scope.launch {
            val stationId = DbUserUtil.getStationId()
            LoadDialogUtils.showLoadingDialog(activity)
            val results = ApiStorage.importWaybillNumberApp(stationId, barcode)
            LoadDialogUtils.cannelLoadingDialog()

            when (results) {
                is KResults.Success -> {
                    SoundHelper.getInstance().scanInSuccess(activity)
                    rl_back.postDelayed({
                        if (rl_back != null) {
                            helper.restartPreviewAndDecode()
                        }
                    }, 1500)
                }
                is KResults.Failure -> {
                    SoundHelper.getInstance().scanInFail(activity)

                    showErrorDialog(results.error.message)

                }
            }
        }
    }

    private fun showErrorDialog(message: String?) {
        XPopup.Builder(activity).setPopupCallback(object : SimpleCallback() {
            override fun onDismiss() {
                super.onDismiss()
                helper.restartPreviewAndDecode()

            }
        }).asConfirm("提示", message) { }.show()
    }

    private fun restartScan() {
        val camera = helper.cameraManager?.openCamera?.camera
        camera?.startPreview()
        helper.restartPreviewAndDecode()
    }
}