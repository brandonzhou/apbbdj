package com.shshcom.station.storage.domain

import com.mt.bbdj.baseconfig.db.ScanImage
import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil
import com.mt.bbdj.baseconfig.utls.RxFileTool
import com.shshcom.module_base.network.KResults
import com.shshcom.module_base.utils.Utils
import com.shshcom.station.base.ICaseBack
import com.shshcom.station.imageblurdetection.OpenCVData
import com.shshcom.station.storage.http.ApiStorage
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

    fun confirmSubmitWarehouse(caseBack: ICaseBack<String>) {
        presenterScope.launch {
            val batchNo = ScanStorageCase.getInstance().batchNo
            val results = ApiStorage.confirmSubmitWarehouse(getStationId(), batchNo)

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

                file
            }


            val kResults = if (image.phone != null && image.phone.isNotBlank()) {
                ApiStorage.stationInputUploadExpress(image)

            } else {
                ApiStorage.stationUploadExpressImg3(image)

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

            val kResults = if (image.phone != null && image.phone.isNotBlank()) {
                ApiStorage.stationInputUploadExpress(image)

            } else {
                ApiStorage.stationUploadExpressImg3(image)

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


}