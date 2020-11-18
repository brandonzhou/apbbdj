package com.shshcom.station.storage.http

import com.mt.bbdj.baseconfig.db.ScanImage
import com.shshcom.module_base.network.KNetwork
import com.shshcom.module_base.network.KResults
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.storage.http.bean.*
import com.shshcom.station.util.ApiSignatureUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * desc:
 * author: zhhli
 * 2020/6/1
 */
object ApiStorage : KNetwork() {


    private var service: StorageService = ServiceCreator.create()


    suspend fun <T> processApi(block: suspend () -> BaseResult<T>): KResults<T> {
        return withContext(Dispatchers.IO) {
            try {
                val re = block()
                if (re.isSuccess) {
                    KResults.success(re.data)
                } else {
                    KResults.failure(Exception(re.msg), re.code, re.msg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                KResults.failure<T>(e, -1, msg = e.message.toString())
            }
        }

    }

    suspend fun <T> processApiUpload(block: suspend () -> BaseResult<T>): KResults<T> {
        return withContext(Dispatchers.IO) {
            try {
                val re = block()
                if (re.data != null) {
                    KResults.success(re.data)
                } else {
                    KResults.failure(Exception(re.msg), re.code, re.msg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                KResults.failure<T>(e)
            }
        }

    }

    suspend fun queryOSSParams(stationId: String): KResults<OSSConfig> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            ApiSignatureUtil.addSignature(map)

            service.queryOSSParams(map).await()
        }
    }


    suspend fun stationShootUploadExpressBill(image: ScanImage, remoteUrl: String): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["batch_no"] = image.batchNo
            map["code"] = image.pickCode
            map["express_id"] = image.expressCompanyId
            map["number"] = image.eId
            map["station_id"] = image.stationId
            map["blur_score"] = image.blurScore
            map["picture"] = remoteUrl
            ApiSignatureUtil.addSignature(map)


            service.stationShootUploadExpressBill(map).await()
        }
    }


    suspend fun stationInputUploadExpressBill(image: ScanImage, remoteUrl: String): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["batch_no"] = image.batchNo
            map["code"] = image.pickCode
            map["express_id"] = image.expressCompanyId
            map["mobile"] = image.phone
            map["number"] = image.eId
            map["station_id"] = image.stationId
            map["blur_score"] = image.blurScore
            map["picture"] = remoteUrl
            ApiSignatureUtil.addSignature(map)


            service.stationInputUploadExpressBill(map).await()
        }
    }


    suspend fun stationUploadExpressImg3(image: ScanImage): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["batch_no"] = image.batchNo
            map["code"] = image.pickCode
            map["express_id"] = image.expressCompanyId
            map["number"] = image.eId
            map["station_id"] = image.stationId
            map["blur_score"] = image.blurScore
            ApiSignatureUtil.addSignature(map)

            //构建body
            val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

            map.forEach {
                bodyBuilder.addFormDataPart(it.key, it.value.toString())
            }

            val file = File(image.localPath)
            bodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))

            service.stationUploadExpressImg3(bodyBuilder.build()).await()
        }
    }


    suspend fun stationInputUploadExpress(image: ScanImage): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["batch_no"] = image.batchNo
            map["code"] = image.pickCode
            map["express_id"] = image.expressCompanyId
            map["mobile"] = image.phone
            map["number"] = image.eId
            map["station_id"] = image.stationId
            map["blur_score"] = image.blurScore
            ApiSignatureUtil.addSignature(map)

            //构建body
            val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

            map.forEach {
                bodyBuilder.addFormDataPart(it.key, it.value.toString())
            }

            val file = File(image.localPath)
            bodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))

            service.stationInputUploadExpress(bodyBuilder.build()).await()
        }
    }


    suspend fun getExpressCompany(stationId: String): BaseResult<List<ExpressCompany>> {
        return withContext(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["user_id"] = stationId
            ApiSignatureUtil.addSignature(map)
            service.getExpressCompany(map).await()
        }
    }


    suspend fun getPackageInfo(stationId: String, barcode: String): KResults<ExpressPackInfo> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["number"] = barcode
            ApiSignatureUtil.addSignature(map)
            service.getPackageInfo(map).await()
        }
    }


    /**
     * station_id	驿站id	Integer	是	12
    code	快递单号	string	是	12
    type	出库类型	Integer	是	6.app拍照出库
     */
    suspend fun outPackage(stationId: String, barcode: String, file: File): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["number"] = barcode
            ApiSignatureUtil.addSignature(map)
            val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val part = MultipartBody.Part.createFormData("file", file.getName(), requestFile)
            val part2 = MultipartBody.Part.createFormData("station_id", stationId)
            val part3 = MultipartBody.Part.createFormData("number", barcode)


            //构建body
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("station_id", stationId)
                    .addFormDataPart("number", barcode)
                    .addFormDataPart("signature", map.get("signature").toString())
                    .addFormDataPart("file", file.getName(),RequestBody.create(MediaType.parse("image/*"), file))
                    .build()



//            service.barOutWarehouse(map, part).await()
            service.barOutWarehouse(requestBody).await()
        }
    }

    suspend fun searchSameMobileExpressInfo(stationId: String, mobile: String): KResults<List<ExpressPackInfo>> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["mobile"] = mobile
            ApiSignatureUtil.addSignature(map)
            service.searchSameMobileExpressInfo(map).await()
        }
    }

    suspend fun wxOfficeSubscribe(stationId: String, eId: Int): KResults<WxOfficeSubscribeState> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["pie_id"] = eId
            ApiSignatureUtil.addSignature(map)
            service.wxOfficeSubscribe(map).await()
        }
    }


    /**
     * 延时发送短信-确认提交入库
     * number 快递单号
     */
    suspend fun confirmSubmitWarehouse(stationId: String, batch_no: String): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["batch_no"] = batch_no
            ApiSignatureUtil.addSignature(map)
            service.confirmSubmitWarehouse(map).await()
        }
    }

    /**
     * 驿站APP导入运单号
     * user_id	String	是	驿站标识	1
     * number	String	是	运单号	1
     * express_id	Integer	是	快递公司	默认先填 100107
     */
    suspend fun importWaybillNumberApp(stationId: String, barcode: String): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["user_id"] = stationId
            map["express_id"] = "100107"
            map["number"] = barcode
            ApiSignatureUtil.addSignature(map)
            service.importWaybillNumberApp(map).await()
        }
    }


}