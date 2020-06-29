package com.shshcom.station.storage.http

import com.shshcom.module_base.network.KNetwork
import com.shshcom.module_base.network.Results
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.storage.http.bean.ExpressCompany
import com.shshcom.station.storage.http.bean.ExpressPackInfo
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


    suspend fun <T> processApi(block: suspend () -> BaseResult<T>): Results<T> {
        return withContext(Dispatchers.IO) {
            try {
                val re = block()
                if (re.isSuccess) {
                    Results.success(re.data)
                } else {
                    Results.failure(Exception(re.msg))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Results.failure<T>(e)
            }
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


    suspend fun getPackageInfo(stationId: String, barcode: String): Results<ExpressPackInfo> {
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
    suspend fun outPackage(stationId: String, barcode: String, file : File): Results<Any> {
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

    suspend fun searchSameMobileExpressInfo(stationId: String, mobile: String): Results<List<ExpressPackInfo>> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["mobile"] = mobile
            ApiSignatureUtil.addSignature(map)
            service.searchSameMobileExpressInfo(map).await()
        }
    }


    /**
     * 延时发送短信-确认提交入库
     * number 快递单号
     */
    suspend fun confirmSubmitWarehouse(stationId: String, batch_no: String): Results<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["batch_no"] = batch_no
            ApiSignatureUtil.addSignature(map)
            service.confirmSubmitWarehouse(map).await()
        }
    }


}