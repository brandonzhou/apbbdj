package com.shshcom.station.storage.domain

import com.google.gson.Gson
import com.mt.bbdj.baseconfig.db.PickupCode
import com.mt.bbdj.baseconfig.db.ScanImage
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil
import com.mt.bbdj.baseconfig.utls.RxFileTool
import com.shshcom.module_base.network.KResults
import com.shshcom.module_base.utils.Utils
import com.shshcom.station.base.ICaseBack
import com.shshcom.station.imageblurdetection.OpenCVData
import com.shshcom.station.storage.http.ApiStorage
import com.shshcom.station.storage.http.bean.PickCodeRemote
import kotlinx.coroutines.*
import java.io.File

/**
 * desc:
 * author: zhhli
 * 2020/6/29
 */
object StorageCase {
    val job = Job()
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    fun getStationId(): String {
        return DbUserUtil.getStationId()
    }

    private var retry = 0;

    fun queryOSSParams() {

        presenterScope.launch {
            val results = ApiStorage.queryOSSParams(getStationId())
            when (results) {
                is KResults.Success -> {

                    OSSWrapper.sharedWrapper().setConfig(results.data)
                    retry = 0

                }
                is KResults.Failure -> {
                    results.error.message?.let {
                        if (retry < 3) {
                            retry++
                            queryOSSParams();
                        }

                    }
                }
            }
        }

    }

    fun confirmSubmitWarehouse(caseBack: ICaseBack<String>) {
        presenterScope.launch {
            val batchNo = ScanStorageCase.getInstance().batchNo

            val list = ArrayList<PickCodeRemote>()
            val listDb = GreenDaoUtil.listPickupCodeAll()
            listDb.forEach {
                val remote = PickCodeRemote.from(it)
                list.add(remote)
            }
            val gson = Gson()
            val shelves = gson.toJson(list)

            val results = ApiStorage.confirmSubmitWarehouse(getStationId(), batchNo, shelves)

            when (results) {
                is KResults.Success -> {
                    caseBack.onSuccess("提交成功")
                }
                is KResults.Failure -> {
                    results.error.message?.let { caseBack.onError(it) }
                }
            }

        }
    }

    /**
     * 拍照入库，上传
     */
    fun stationUploadExpress(image: ScanImage, cvData: OpenCVData, caseBack: ICaseBack<String>) {
        presenterScope.launch {
            val file = withContext(Dispatchers.IO) {
                val shCameraHelp = SHCameraHelp()
                val file = shCameraHelp.saveImage(Utils.context, image.getEId(), cvData.bitmap)


                image.localPath = file
                image.state = ScanImage.State.uploading.name
                GreenDaoUtil.updateScanImage(image)

                val remoteUrl = OSSWrapper.sharedWrapper().asyncPutImage(image, file)

                remoteUrl

            }

            if (file.isBlank()) {
                image.state = ScanImage.State.upload_fail.name
                GreenDaoUtil.updateScanImage(image)
                return@launch
            }


            val kResults = if (image.phone != null && image.phone.isNotBlank()) {
                ApiStorage.stationInputUploadExpressBill(image, file)

            } else {
                ApiStorage.stationShootUploadExpressBill(image, file)

            }

            when (kResults) {
                is KResults.Success -> {
                    image.state = ScanImage.State.upload_success.name
                    GreenDaoUtil.updateScanImage(image)
                    RxFileTool.deleteFile(file)
                    caseBack.onSuccess(kResults.msg)
                }

                is KResults.Failure -> {
                    image.state = ScanImage.State.upload_fail.name
                    GreenDaoUtil.updateScanImage(image)
                    // 已入库等
                    val msg = if (kResults.code != 5002) {
                        image.eId + " : " + kResults.msg
                    } else {
                        GreenDaoUtil.deleteScanImage(image.eId);
                        RxFileTool.deleteFile(file)
                        image.eId + " : " + kResults.msg
                    }
                    caseBack.onError(msg)
                }
            }

        }


    }


    fun retryUpload(image: ScanImage, caseBack: ICaseBack<String>) {
        presenterScope.launch {

            val remoteUrl = withContext(Dispatchers.IO) {

                val remoteUrl = OSSWrapper.sharedWrapper().asyncPutImage(image, image.localPath)

                remoteUrl

            }

            if (remoteUrl.isBlank()) {
                val db = GreenDaoUtil.findScanImage(image.eId)
                if (db != null && db.state == ScanImage.State.uploading.name) {
                    image.state = ScanImage.State.upload_fail.name
                    GreenDaoUtil.updateScanImage(image)
                }
                return@launch
            }

            val kResults = if (image.phone != null && image.phone.isNotBlank()) {
                ApiStorage.stationInputUploadExpressBill(image, remoteUrl)

            } else {
                ApiStorage.stationShootUploadExpressBill(image, remoteUrl)

            }

            val file = File(image.localPath)

            when (kResults) {
                is KResults.Success -> {
                    image.state = ScanImage.State.upload_success.name
                    GreenDaoUtil.updateScanImage(image)
                    RxFileTool.deleteFile(file)
                    caseBack.onSuccess(kResults.msg)
                }

                is KResults.Failure -> {
                    image.state = ScanImage.State.upload_fail.name
                    GreenDaoUtil.updateScanImage(image)
                    // 已入库等
                    val msg = if (kResults.code != 5002) {
                        image.eId + " : " + kResults.msg
                    } else {
                        GreenDaoUtil.deleteScanImage(image.eId);
                        RxFileTool.deleteFile(file)
                        image.eId + " : " + kResults.msg

                    }
                    caseBack.onError(msg)
                }
            }

        }
    }


    fun toPickupCode(remote: PickCodeRemote): PickupCode {
        val pickupCode = PickupCode()
        pickupCode.stationId = getStationId()
        pickupCode.startNumber = remote.number
        pickupCode.shelfId = remote.shelvesId
        pickupCode.shelfNumber = remote.shelvesName
        pickupCode.time = remote.time
        pickupCode.lastCode = remote.lastCode

        val type = PickupCode.Type.from(remote.rule)

        pickupCode.type = type.desc

        val db = GreenDaoUtil.getPickCode(remote.shelvesId)
        if (db != null) {
            pickupCode.uId = db.uId
        }

        return pickupCode
    }

    fun httpRestorePickCode(caseBack: ICaseBack<String>?) {
        presenterScope.launch {
            val kResults = ApiStorage.queryPickupCode()
            when (kResults) {
                is KResults.Success -> {
                    val list = ArrayList<PickupCode>()
                    kResults.data.forEach {
                        val pickupCode = toPickupCode(it)
                        list.add(pickupCode)
                    }
                    GreenDaoUtil.restorePickCodeList(list)

                    if (caseBack != null) {
                        caseBack.onSuccess("")
                    }

                }
                is KResults.Failure -> {

                    if (caseBack != null) {
                        caseBack.onError("取件码更新失败：" + kResults.msg)
                    }
                }

            }

        }
    }


}