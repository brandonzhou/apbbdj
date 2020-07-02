package com.shshcom.station.setting.http

import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.Results
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.setting.http.bean.AutoUrgeData
import com.shshcom.station.setting.http.bean.CompanySettingData
import com.shshcom.station.storage.http.ApiStorage.await
import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.util.ApiSignatureUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * desc:
 * author: zhhli
 * 2020/6/23
 */
object ApiSetting {
    private var service: SettingService = ServiceCreator.create()

    private suspend fun <T> processApi(block: suspend () -> BaseResult<T>): Results<T> {
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

    private fun getStationId(): String {
        return DbUserUtil.getStationId()
    }

    suspend fun getPackageUrgeSetting(): Results<AutoUrgeData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            ApiSignatureUtil.addSignature(map)
            service.getPackageUrgeSetting(map).await()
        }
    }

    suspend fun setPackageUrgeSetting(cur_urge_type: Int): Results<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["cur_urge_type"] = cur_urge_type
            ApiSignatureUtil.addSignature(map)
            service.setPackageUrgeSetting(map).await()
        }
    }


    suspend fun getBrandManagement(): Results<List<CompanySettingData>> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()

            ApiSignatureUtil.addSignature(map)
            service.getBrandManagement(map).await()
        }
    }

    suspend fun saveBrandManagement(express_id: Int, type: Int): Results<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["express_id"] = express_id
            map["type"] = type
            ApiSignatureUtil.addSignature(map)
            service.saveBrandManagement(map).await()
        }
    }


}