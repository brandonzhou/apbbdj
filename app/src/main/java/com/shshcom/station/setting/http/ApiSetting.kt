package com.shshcom.station.setting.http

import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.KResults
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.setting.http.bean.AutoUrgeData
import com.shshcom.station.setting.http.bean.CompanySettingData
import com.shshcom.station.setting.http.bean.CustomSMSTemplateData
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

    private suspend fun <T> processApi(block: suspend () -> BaseResult<T>): KResults<T> {
        return withContext(Dispatchers.IO) {
            try {
                val re = block()
                if (re.isSuccess) {
                    KResults.success(re.data)
                } else {
                    KResults.failure(Exception(re.msg))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                KResults.failure<T>(e)
            }
        }
    }

    private fun getStationId(): String {
        return DbUserUtil.getStationId()
    }

    suspend fun getPackageUrgeSetting(): KResults<AutoUrgeData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            ApiSignatureUtil.addSignature(map)
            service.getPackageUrgeSetting(map).await()
        }
    }

    suspend fun setPackageUrgeSetting(cur_urge_type: Int): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["cur_urge_type"] = cur_urge_type
            ApiSignatureUtil.addSignature(map)
            service.setPackageUrgeSetting(map).await()
        }
    }


    suspend fun getBrandManagement(): KResults<List<CompanySettingData>> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()

            ApiSignatureUtil.addSignature(map)
            service.getBrandManagement(map).await()
        }
    }

    suspend fun saveBrandManagement(express_id: Int, type: Int): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["express_id"] = express_id
            map["type"] = type
            ApiSignatureUtil.addSignature(map)
            service.saveBrandManagement(map).await()
        }
    }


    suspend fun getCustomSMSTemplate(): KResults<CustomSMSTemplateData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            ApiSignatureUtil.addSignature(map)
            service.getCustomSMSTemplate(map).await()
        }
    }

    suspend fun saveCustomSMSTemplate(custom_phone: String, custom_address: String): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["custom_account"] = custom_phone
            map["custom_address"] = custom_address
            ApiSignatureUtil.addSignature(map)
            service.saveCustomSMSTemplate(map).await()
        }
    }


}