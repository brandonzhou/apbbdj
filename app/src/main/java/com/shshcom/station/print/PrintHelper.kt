package com.shshcom.station.print

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.king.mlkit.vision.camera.util.PermissionUtils
import com.mt.bbdj.baseconfig.db.ScanImage
import com.mt.bbdj.baseconfig.utls.LogUtil
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil
import com.mt.bbdj.baseconfig.utls.ToastUtil
import com.shshcom.module_base.utils.Utils
import com.shshcom.station.send.http.bean.MailDetail
import com.shshcom.station.util.AppTimeUtils
//import cpcl.PrinterHelper
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


/*
 * 打印取件码
 * 
 * @author: zhhli
 * @date: 22/4/14
 */
//object PrintHelper {
//
//    private val dateFormat = AppTimeUtils.createFormat("MM/dd")
//    private val dayTime: String = AppTimeUtils.date2String(Date(), dateFormat)
//
//
//    private val job = Job()
//    private val scope: CoroutineScope by lazy {
//        CoroutineScope(Dispatchers.Main + job)
//    }
//
//    private val jobIO = Job()
//    private val scopeIO: CoroutineScope by lazy {
//        CoroutineScope(Dispatchers.IO + jobIO)
//    }
//
//
//
//    fun savePrintPageSize(size: String) {
//        SharedPreferencesUtil.putString("print_page_size", size)
//    }
//
//
//    fun getPrintPageSize(): String {
//        return SharedPreferencesUtil.getString("print_page_size", "40*30")
//    }
//
//    fun savePrintAddress(address: String, name: String) {
//        SharedPreferencesUtil.putString("sp_bt_print_address", address)
//        SharedPreferencesUtil.putString("sp_bt_print_name", name)
//    }
//
//
//    fun getPrintAddress(): String {
//        return SharedPreferencesUtil.getString("sp_bt_print_address", "")
//    }
//
//    fun getPrintName(): String {
//        return SharedPreferencesUtil.getString("sp_bt_print_name", "")
//    }
//
//    fun isAutoPrint(): Boolean {
//        return SharedPreferencesUtil.getBoolean("isAutoPrint", false)
//    }
//
//
//    /******************************************* 初始化蓝牙  */
//
//    var isConnected = false
//
//
//    @SuppressLint("NewApi")
//    fun requestPermission(activity: AppCompatActivity, reqCode: Int) {
//        // https://developer.android.com/guide/topics/connectivity/bluetooth/permissions#declare-android11-or-lower
//        // 若始终拒绝了 位置权限，则无法发现蓝牙设备，即使手动打开权限，需要重新安装
//        PermissionUtils.requestPermission(
//            activity,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            reqCode
//        )
//
//
//    }
//
//    private fun disconnectBluetooth() {
//        PrinterHelper.portClose()
//    }
//
//
//    fun connectBluetooth() {
//        if (!isAutoPrint()) {
//            return
//        }
//
//        //获取蓝牙mvc地址
//        val toothAddress = getPrintAddress()
//
//        connectBluetooth(toothAddress)
//
//
//    }
//
//    fun connectBluetooth(toothAddress: String) {
//
//
//        if (!toothAddress.contains(":")) {
//            ToastUtil.showShort("请先选择蓝牙打印机")
//            return
//        }
//
//        // 打印机未开启时，会导致长时间 loading
//        // LoadDialogUtils.showLoadingDialog(activity)
//
//        scopeIO.launch {
//
//            var connecting = 0
//            while (connecting < 3) {
//                PrinterHelper(Utils.context, PrinterHelper.PRINT_NAME_A300)
//                val portOpen = PrinterHelper.PortOpen("Bluetooth,$toothAddress")
//                PrinterHelper.logcat("portOpen:$portOpen")
//
//                isConnected = if (portOpen != 0) {
//                    delay(3000)
//                    connecting++
//                    false
//                } else {
//                    connecting = 5
//                    true
//                }
//
//            }
//
//
//
//            withContext(Dispatchers.Main) {
////                isConnected = if (portOpen != 0) {
////                    ToastUtil.showShort("蓝牙连接失败，请检查打印机是否开启")
////                    false
////                } else {
////                    ToastUtil.showShort("蓝牙连接成功")
////                    true
////                }
//
//                if (isConnected) {
//                    ToastUtil.showShort("打印机连接成功")
//                } else {
//                    ToastUtil.showShort("打印机连接失败，请检查打印机是否开启")
//                }
//            }
//            // LoadDialogUtils.cannelLoadingDialog()
//
//
//        }
//
//
//    }
//
//    fun checkPrintPickCode(eId: String, pickCode: String) {
//        if (!isAutoPrint()) {
//            return
//        }
//
//        if (!isConnected) {
//            ToastUtil.showShort("蓝牙未连接打印机，请连接！")
//            return
//        }
//
//        LogUtil.d("zzz", "开始打印----launch启动")
//        ToastUtil.showShort("开始打印......")
//
//        scopeIO.launch {
//            val c1 = async {
//                printPickCode(eId, pickCode)
//            }
//
//            c1.await()
//        }
//
//    }
//
//    fun printList(list: List<ScanImage>) {
//        LogUtil.d("zzz", "开始打印----launch启动")
//
////
////        PrinterHelper.openEndStatic(true)
////        PrinterHelper.printAreaSize("0", "200", "200", "100", "1")
////        PrinterHelper .Text(PrinterHelper.TEXT,"4","0","0","0","TEXT")
////        PrinterHelper .Beep("16")
////        PrinterHelper .Form()
////        PrinterHelper .Print()
//
//
////        STexpress()
////
//
//        scopeIO.launch {
//
//            list.forEach {
//                LogUtil.d("zzz", "开始打印----${it.pickCode}")
//                val c1 = async {
//                    printPickCode2(it.eId, it.pickCode)
//                }
//                c1.await()
//                LogUtil.d("zzz", "完成打印----${it.pickCode}")
//            }
//
////            withContext(Dispatchers.Main){
////                ToastUtil.showShort("结束打印")
////            }
//
//
//        }
//
//        LogUtil.d("zzz", "开始打印----launch结束")
//
//
//    }
//
//
//    private suspend fun printPickCode2(eId: String, pickCode: String) {
//
//        var pickCodeTop = ""
//        var pickCodeBig = ""
//
//        if (pickCode.length > 5) {
//            // 包含 “-”, 进行分割
//            val index = pickCode.length - 4
//            pickCodeTop = pickCode.substring(0, index)
//            pickCodeBig = pickCode.substring(index)
//            LogUtil.d("xxxxx", "[$pickCodeTop]  [$pickCodeBig]")
//        } else {
//            pickCodeBig = pickCode
//        }
//
//        val pageSize = getPrintPageSize()
//        val file = if ("60*40" == pageSize) {
//            "print_pick_code_w60_h40.txt"
//
//        } else {
//            "print_pick_code_w40_h30.txt"
//        }
//
//        val map = mutableMapOf<String, Any>()
//        map["[dayTime]"] = dayTime
//        map["[pickCodeTop]"] = pickCodeTop
//        map["[pickCodeBig]"] = pickCodeBig
//
//        map["[barcode]"] = eId
//
//
//        printFile(file, map) {
//
//        }
//
//
//    }
//
//
//    private suspend fun printPickCode(eId: String, pickCode: String) {
//        printIO {
//            val pageSize = getPrintPageSize()
//
//            if ("60*40" == pageSize) {
//                printPickCode_w60_h40(eId, pickCode)
//
//            }
//            if ("40*30" == pageSize) {
//                printPickCode_w40_h30(eId, pickCode)
//            }
//        }
//    }
//
//
//    private fun printPickCode_w60_h40(eId: String, pickCode: String) {
//
//        try {
//
//            //printAreaSize( offset, Horizontal,  Vertical, height, qty)
//            // qty 重复次数
//            // 200dot / 8(dot/mm) = 25mm
//            // 70*50、60*40、40*30  *8 = 560*400 480*320 320*240
//            PrinterHelper.printAreaSize("0", "200", "200", "320", "1")
//
//
//            PrinterHelper.SetBold("0")//关闭加粗
//            PrinterHelper.SetMag("1", "1")//关闭放大
//
//
//            PrinterHelper.Align(PrinterHelper.CENTER)
//
//
//            PrinterHelper.SetBold("2")//加粗(1-5)
//            PrinterHelper.SetMag("1", "1")//对下面的字体进行放大(如不需要不用添加)
//
//            // Text( command, font, size , x, y, data)
//
//            //y轴起始位置
//            var y = 10
//
//            val x = (74 - 60) / 2 * 8
//
//            PrinterHelper.Align(PrinterHelper.LEFT)
//            PrinterHelper.Text(
//                PrinterHelper.TEXT,
//                "24",
//                "10",
//                x.toString(),
//                y.toString(),
//                dayTime
//            )
//
//            y += 10
//
//            PrinterHelper.Align(PrinterHelper.CENTER)
//
//            if (pickCode.length > 5) {
//                // 包含 “-”, 进行分割
//                val index = pickCode.length - 4
//                val code_start = pickCode.substring(0, index)
//                val code_end = pickCode.substring(index)
//                LogUtil.d("xxxxx", "[$code_start]  [$code_end]")
//
//
//
//                PrinterHelper.SetMag("2", "2")
//                PrinterHelper.Text(
//                    PrinterHelper.TEXT,
//                    "24",
//                    "0",
//                    "0",
//                    y.toString(),
//                    code_start
//                )
//
//                y += 40 * 1 + 20
//                //y += 24 + 10
//
//                PrinterHelper.SetMag("5", "5")
//                PrinterHelper.Text(
//                    PrinterHelper.TEXT,
//                    "24",//font
//                    "0",// size 固定 0
//                    "0",
//                    y.toString(),
//                    code_end
//                )
//
//            } else {
//                y += 40 * 1 + 20
//                PrinterHelper.SetMag("5", "5")
//                PrinterHelper.Text(
//                    PrinterHelper.TEXT,
//                    "24",//font
//                    "0",
//                    "0",
//                    y.toString(),
//                    pickCode
//                )
//
//            }
//
//            y += 40 * 3
//
//
//            PrinterHelper.SetBold("1")//加粗(1-5)
//            PrinterHelper.SetMag("1", "1")//对下面的字体进行放大(如不需要不用添加)
//
//
////                PrinterHelper.Text(
////                    PrinterHelper.TEXT,
////                    "24",
////                    "0",
////                    "0",
////                    y.toString(),
////                    "运单号：${eId.uppercase()}"
////                )
//
////                y += 40
//
//
//            // BARCODE-TEXT 7 2 5
//            // BARCODE 128    1   1    80   220   705   [barcode]
//            // BARCODE 128    2   2    90   0   90   [barcode]
//            // BARCODE 128    3   3    110   0   90   [barcode]
//            // var0    var1 var2 var3 var4 var5  var6   var11 ;
//            // BARCODE-TEXT var8 var9 var10
//            // Barcode( command, type, width,  ratio, height, x, y,boolean undertext, number, size, offset,  data )
//            PrinterHelper.Barcode(
//                "BARCODE",
//                "128",
//                "1",
//                "1",
//                "60",//h
//                "10",
//                y.toString(),
//                true,
//                "7",
//                "0",
//                "5",
//                eId.uppercase()
//            )
//
//            PrinterHelper.SetBold("0")//关闭加粗
//            PrinterHelper.SetMag("1", "1")//关闭放大
//            PrinterHelper.Align(PrinterHelper.LEFT)
//
//
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//
//
//    }
//
//    private fun printPickCode_w40_h30(eId: String, pickCode: String) {
//
//        try {
//
//            //printAreaSize( offset, Horizontal,  Vertical, height, qty)
//            // qty 重复次数
//            // 200dot / 8(dot/mm) = 25mm
//            // 70*50、60*40、40*30  *8 = 560*400 480*320 320*240
//            PrinterHelper.printAreaSize("0", "200", "200", "240", "1")
//            //PrinterHelper.PageWidth("320")
//
//
//            PrinterHelper.SetBold("0")//关闭加粗
//            PrinterHelper.SetMag("1", "1")//关闭放大
//
//
//            PrinterHelper.Align(PrinterHelper.CENTER)
//
//
//            PrinterHelper.SetBold("2")//加粗(1-5)
//            PrinterHelper.SetMag("1", "1")//对下面的字体进行放大(如不需要不用添加)
//
//            // Text( command, font, size , x, y, data)
//
//            //y轴起始位置
//            var y = 10
//            //val x = (74 - 40) / 2 * 8
//            val x = 8
//
//            PrinterHelper.Align(PrinterHelper.LEFT)
//            PrinterHelper.Text(
//                PrinterHelper.TEXT,
//                "24",
//                "10",
//                x.toString(),
//                //"80",
//                y.toString(),
//                dayTime
//            )
//
//            y += 5
//
//            PrinterHelper.Align(PrinterHelper.CENTER)
//
//            if (pickCode.length > 5) {
//                // 包含 “-”, 进行分割
//                val index = pickCode.length - 4
//                val code_start = pickCode.substring(0, index)
//                val code_end = pickCode.substring(index)
//                LogUtil.d("xxxxx", "[$code_start]  [$code_end]")
//
//
//
//                PrinterHelper.SetMag("2", "2")
//                PrinterHelper.Text(
//                    PrinterHelper.TEXT,
//                    "20",
//                    "0",
//                    "0",
//                    y.toString(),
//                    code_start
//                )
//
//                //y += 40 * 1 + 10
//                y += 24 + 10
//
//                PrinterHelper.SetMag("4", "4")
//                PrinterHelper.Text(
//                    PrinterHelper.TEXT,
//                    "24",//font
//                    "0",// size 固定 0
//                    "0",
//                    y.toString(),
//                    code_end
//                )
//
////                    PrinterHelper.PrintTextCPCL(PrinterHelper.TEXT, 32, "0", y.toString(), code_end, 15,true, 100)
//
//
//            } else {
//                y += 40 * 1 + 10
//                PrinterHelper.SetMag("4", "4")
//                PrinterHelper.Text(
//                    PrinterHelper.TEXT,
//                    "24",//font
//                    "0",
//                    "0",
//                    y.toString(),
//                    pickCode
//                )
//
//            }
//
//            y += 40 * 2 + 10
//
//
//            PrinterHelper.SetBold("1")//加粗(1-5)
//            PrinterHelper.SetMag("1", "1")//对下面的字体进行放大(如不需要不用添加)
//
//
////                PrinterHelper.Text(
////                    PrinterHelper.TEXT,
////                    "24",
////                    "0",
////                    "0",
////                    y.toString(),
////                    "运单号：${eId.uppercase()}"
////                )
//
////                y += 40
//
//
//            // BARCODE-TEXT 7 2 5
//            // BARCODE 128    1   1    80   220   705   [barcode]
//            // var0    var1 var2 var3 var4 var5  var6   var11 ;
//            // BARCODE-TEXT var8 var9 var10
//            // Barcode( command, type, width,  ratio, height, x, y,boolean undertext, number, size, offset,  data )
//            PrinterHelper.Barcode(
//                "BARCODE",
//                "128",
//                "1",
//                "1",
//                "50",
//                "0",
//                y.toString(),
//                true,
//                "7",
//                "0",
//                "5",
//                eId.uppercase()
//            )
//
//            PrinterHelper.SetBold("0")//关闭加粗
//            PrinterHelper.SetMag("1", "1")//关闭放大
//            PrinterHelper.Align(PrinterHelper.LEFT)
//
//
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//
//    /**
//     * 打印面单 IO线程
//     */
//    fun printExpressOrder(mailDetail: MailDetail) {
//        scopeIO.launch {
//            printIO {
//                val data = mailDetail
//
//                val resources = Utils.context.resources
//                val map = mailDetail.toPrintMapNew()
//
//                try {
//
//                    //val afis = resources.assets.open("Z_xiang_you_xie_tong.txt") //打印模版放在assets文件夹里
//                    val afis = resources.assets.open("print_mail_sto_cpcl.txt") //打印模版放在assets文件夹里
////                    val afis = resources.assets.open("STO_CPCL.txt") //打印模版放在assets文件夹里
//                    //打印模版以utf-8无bom格式保存
//                    var path = String(inputStreamToByte(afis), charset("utf-8"))
//                    //LogUtil.d("zzz", "开始打印.....读文本")
//
//
//                    map.forEach {
//                        path = path.replace(it.key, it.value)
//                    }
//
//                    //LogUtil.d("zzz", "开始打印.....匹配文本")
//
//                    PrinterHelper.PrintData(path) //打印机打印
//
//
//                    val topLogo = resources.assets.open("ic_logo_mini2.png") // 顶部 app logo
//                    val bitmap = BitmapFactory.decodeStream(topLogo)
//                    //PrinterHelper.Expanded("370", "10", bitmap, 0, 0)//第一联 顶部app logo
//                    PrinterHelper.Expanded("15", "690", bitmap, 0, 0)// 底部 湘邮logo
//
//
//                    val expressLogo = resources.assets.open(data.expressLogo())
//                    val bitmap5 = BitmapFactory.decodeStream(expressLogo)
//                    PrinterHelper.Expanded("410", "622", bitmap5, 0, 0) //第一联 快递公司logo
//
//
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                    0
//                }
//            }
//        }
//
//
//    }
//
//
//    private suspend fun printIO(blockPrint: suspend () -> Unit) {
//
//        withContext(Dispatchers.Main) {
//            ToastUtil.showShort("开始打印！")
//        }
//
//        LogUtil.d("zzz", "开始打印-调用  ${Thread.currentThread().name}")
//
//        val endStatus = try {
//            // 不能设置，否则会导致先走一段距离再打印
//            //PrinterHelper.openEndStatic(true) //开启
//
//            blockPrint()
//
//            PrinterHelper.Form()
//            PrinterHelper.Print()
//
//
//            //阻塞查询 获取打印状态 设置超时时间(单位:秒)
//            val endStatus = PrinterHelper.getEndStatus(10)
//
//            // PrinterHelper.openEndStatic(false) //关闭
//
////                delay(60 *1000)
//
//            endStatus
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            0
//        }
//
//        LogUtil.d("zzz", "打印结束-调用  ${Thread.currentThread().name}")
//
//        withContext(Dispatchers.Main) {
//            LogUtil.d("zzz", "打印结束 toast -调用 ${Thread.currentThread().name}  $endStatus")
//
//            if (endStatus == 1) { // 1 缺纸 2，纸仓未关闭
//                ToastUtil.showShort("打印失败！缺纸")
//            }
//
//            if (endStatus == 2) { // 1 缺纸 2，纸仓未关闭
//                ToastUtil.showShort("打印失败！纸仓未关闭")
//            }
//
//        }
//
//    }
//
//
//
//    @Throws(IOException::class)
//    private fun inputStreamToByte(inS: InputStream): ByteArray {
//        val bytestream = ByteArrayOutputStream()
//        var ch: Int
//        while (inS.read().also { ch = it } != -1) {
//            bytestream.write(ch)
//        }
//        val imgdata = bytestream.toByteArray()
//        bytestream.close()
//        return imgdata
//    }
//
//
//    private suspend fun printFile(file: String, map: Map<String, Any>, blockPrint: () -> Unit) {
//        printIO {
//
//            val resources = Utils.context.resources
//
//            try {
//
//                val afis = resources.assets.open(file) //打印模版放在assets文件夹里
//                //打印模版以utf-8无bom格式保存
//                var path = String(inputStreamToByte(afis), charset("utf-8"))
//                map.forEach {
//                    path = path.replace(it.key, it.value.toString())
//                }
//
//                PrinterHelper.printText(path)
//                blockPrint()
//
//
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//                0
//            }
//        }
//    }
//
//
//    private fun STexpress() {
//        try {
//            val resources = Utils.context.resources
//            PrinterHelper.openEndStatic(true)
//            val pum = HashMap<String, String>()
//            pum["[barcode]"] = "363604310467"
//            pum["[distributing]"] = "上海 上海市 长宁区"
//            pum["[receiver_name]"] = "申大通"
//            pum["[receiver_phone]"] = "13826514987"
//            pum["[receiver_address1]"] = "上海市宝山区共和新路4719弄共"
//            pum["[receiver_address2]"] = "和小区12号306室" //收件人地址第一行
//            pum["[sender_name]"] = "快小宝" //收件人第二行（若是没有，赋值""）
//            pum["[sender_phone]"] = "13826514987" //收件人第三行（若是没有，赋值""）
//            pum["[sender_address1]"] = "上海市长宁区北曜路1178号（鑫达商务楼）"
//            pum["[sender_address2]"] = "1号楼305室"
//            val keySet: Set<String> = pum.keys
//            val iterator = keySet.iterator()
//            val afis: InputStream = resources.getAssets().open("STO_CPCL.txt") //打印模版放在assets文件夹里
//            var path = String(inputStreamToByte(afis), charset("utf-8")) //打印模版以utf-8无bom格式保存
//            while (iterator.hasNext()) {
//                val string = iterator.next()
//                path = path.replace(string, pum[string]!!)
//            }
//            PrinterHelper.printText(path)
//            val inbmp: InputStream = resources.getAssets().open("ic_shentong_mini.png")
//            val bitmap = BitmapFactory.decodeStream(inbmp)
//            val inbmp2: InputStream = resources.getAssets().open("ic_shentong_mini.png")
//            val bitmap2 = BitmapFactory.decodeStream(inbmp2)
//            PrinterHelper.Expanded("10", "20", bitmap, 0, 0) //向打印机发送LOGO
//            PrinterHelper.Expanded("10", "712", bitmap2, 0, 0) //向打印机发送LOGO
//            PrinterHelper.Expanded("10", "1016", bitmap2, 0, 0) //向打印机发送LOGO
//            //if ("1" == Activity_Main.paper) {
//            PrinterHelper.Form()
//            // }
//            PrinterHelper.Print()
//            //			PrinterHelper.getEndStatus(16);
//        } catch (e: Exception) {
//            Log.e(
//                "HPRTSDKSample", StringBuilder("Activity_Main --> PrintSampleReceipt ")
//                    .append(e.message).toString()
//            )
//        }
//
//    }
//
//
//}

//val topLogo = resources.assets.open("ic_logo_mini_2.png") // 顶部 app logo
//val bitmap = BitmapFactory.decodeStream(topLogo)
//PrinterHelper.Expanded("370", "10", bitmap, 0, 0)//第一联 顶部app logo

//详细地址，处理跨行
//                    PrinterHelper.AutLine(
//                        "65",
//                        "395",
//                        500,
//                        5,
//                        true,
//                        false,
//                        data.collectRegion + data.collectAddress
//                    )
//                    PrinterHelper.AutLine(
//                        "65",
//                        "532",
//                        500,
//                        5,
//                        true,
//                        false,
//                        data.sendRegion + data.sendAddress
//                    )