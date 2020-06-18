package com.shshcom.station.storage.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.ycbjie.ycstatusbarlib.StatusBarUtils
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar
import com.google.zxing.BarcodeFormat
import com.king.zxing.CaptureActivity
import com.king.zxing.CaptureHelper
import com.king.zxing.camera.FrontLightMode
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.mt.bbdj.R
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.utls.*
import com.shshcom.module_base.network.Results
import com.shshcom.station.storage.domain.SHCameraHelp
import com.shshcom.station.storage.http.ApiStorage
import com.shshcom.station.storage.http.bean.ExpressPackInfo
import com.shshcom.station.storage.http.bean.ExpressPackInfoList
import kotlinx.android.synthetic.main.activity_scan_pick_out.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*

/**
 * 扫描出库
 */
class ScanPickOutActivity : CaptureActivity() {
    private val TAG = "ScanPickOutActivity"

    companion object {
        fun openActivity(context: Activity) {
            val intent = Intent(context, ScanPickOutActivity::class.java)
            context.startActivityForResult(intent, 1)
        }
    }

    val job = Job()
    val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }
    lateinit var activity: ScanPickOutActivity

    /*请求系统权限-摄像头*/
    private val PERMISSION_REQUEST_CODE_CAMERA = 1
    private lateinit var helper: CaptureHelper

    private var currentBarCode: String = ""

    private var barCodeSanRepeatTime = 0



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
        return R.layout.activity_scan_pick_out
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
            else -> {}
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

        if (barCodeSanRepeatTime < 3) {
            barCodeSanRepeatTime++
            helper.restartPreviewAndDecode()
            return true
        }

        if (result == currentBarCode) {
            helper.restartPreviewAndDecode()
            return true
        }

        barCodeSanRepeatTime = 0
        currentBarCode = result!!

        httpInfo(currentBarCode)


        return true
    }

    val json = "{\n" +
            "  \"pie_id\": 152309,\n" +
            "  \"number\": \"1112899611576\",\n" +
            "  \"code\": \"AA-17-1002\",\n" +
            "  \"express_id\": 100108,\n" +
            "  \"express_name\": \"EMS\",\n" +
            "  \"mobile\": \"18811321040\"\n" +
            "}"

//    val gson = Gson()
    private fun httpInfo(barcode : String){
        scope.launch {
            val stationId = DbUserUtil.getStationId()
            LoadDialogUtils.showLoadingDialog(activity)
            val results = ApiStorage.getPackageInfo(stationId, barcode)
            when(results){
                is Results.Success->{
                    LoadDialogUtils.cannelLoadingDialog()
                    takePicture(results.data)

                }
                is Results.Failure ->{
                    LoadDialogUtils.cannelLoadingDialog()

                    showErrorDialog(results.error.message)

                   // takePicture(gson.fromJson(json, ExpressPackInfo::class.java))


                }
            }
        }
    }

    private fun showErrorDialog(message :String?){
        XPopup.Builder(activity).setPopupCallback(object : SimpleCallback() {
            override fun onDismiss() {
                super.onDismiss()
                restartScan()
            }
        }).asConfirm("提示",message) { }.show()
    }

    private fun takePicture(info : ExpressPackInfo){

        val camera = helper.cameraManager.openCamera.camera
        camera.startPreview()

        camera.takePicture(null, null, object : Camera.PictureCallback{
            override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
                barCodeSanRepeatTime = 0

                scope.launch {
                    val file = withContext(Dispatchers.IO){
                        val shCameraHelp = SHCameraHelp()
                        shCameraHelp.saveImage(rl_title.context, "out_"+info.number, data)
                    }

                    info.localFile = file

                    XPopup.Builder(activity)
                            .autoOpenSoftInput(true)
                            .setPopupCallback(object : SimpleCallback(){
                                override fun onDismiss() {
                                    super.onDismiss()
                                    restartScan()
                                }
                            })
                            .asCustom(SubmitPopView(activity, info))
                            .show()
                }



            }

        })
    }



    private fun restartScan(){
        currentBarCode = ""
        barCodeSanRepeatTime = 0
        val camera = helper.cameraManager?.openCamera?.camera
        camera?.startPreview()
        helper.restartPreviewAndDecode()
    }


    inner class SubmitPopView(context: Context, var info : ExpressPackInfo?) : CenterPopupView(context) {
        override fun getImplLayoutId(): Int {
            return R.layout.dialog_pack_pick_out
        }

        override fun onCreate() {
            if(info == null){
                findViewById<View>(R.id.cl_pack_none).visibility = View.VISIBLE
                findViewById<View>(R.id.cl_pack_submit).visibility = View.GONE
                findViewById<View>(R.id.tv_pack_close).setOnClickListener { dismiss()}
            }else{
                findViewById<View>(R.id.cl_pack_none).visibility = View.GONE
                findViewById<View>(R.id.cl_pack_submit).visibility = View.VISIBLE
                showInfo()
            }

        }

        @SuppressLint("SetTextI18n")
        private fun showInfo(){
            findViewById<TextView>(R.id.tv_pack_company_name).text = "${info!!.expressName}："
            findViewById<TextView>(R.id.tv_pack_barcode).text = info!!.number
            findViewById<TextView>(R.id.tv_pack_phone).text = info!!.mobile
            findViewById<TextView>(R.id.tv_pack_pickcode).text = info!!.code
            findViewById<View>(R.id.tv_cancel).setOnClickListener {  dismiss() }
            findViewById<View>(R.id.tv_submit).setOnClickListener { httpPickOut(info!!) }
        }



        private fun httpPickOut(info : ExpressPackInfo){
            scope.launch {
                LoadDialogUtils.showLoadingDialog(activity)
                val stationId = DbUserUtil.getStationId()
                val results = ApiStorage.outPackage(stationId, info.number, File(info.localFile))

                when(results){
                    is Results.Success->{
                        ToastUtil.showShort("出库成功")
                    }

                    is Results.Failure ->{
                        LoadDialogUtils.cannelLoadingDialog()

                        showErrorDialog(results.error.message)
                    }
                }

                httpSearchSameInfo(stationId, info.mobile)

                RxFileTool.deleteFile(info.localFile)

                dismiss()



            }
        }


        private fun httpSearchSameInfo(stationId: String, phone : String){
            scope.launch {
                val results = ApiStorage.searchSameMobileExpressInfo(stationId, phone)
                when(results) {
                    is Results.Success -> {
                        LoadDialogUtils.cannelLoadingDialog()
                        val data = results.data
                        if(data.isNotEmpty()){
                            PickOutShowSameActivity.openActivity(activity, ExpressPackInfoList(data))
                        }
                    }
                    is Results.Failure ->{
                       // ToastUtil.showLong(results.error.message)
                    }
                }
            }
        }

    }




}