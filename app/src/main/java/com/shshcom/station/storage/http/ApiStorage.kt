package com.shshcom.station.storage.http

import com.shshcom.module_base.network.KNetwork
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.storage.http.bean.ExpressCompany
import com.shshcom.station.util.ApiSignatureUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * desc:
 * author: zhhli
 * 2020/6/1
 */
object ApiStorage : KNetwork(){


    private var service : StorageService = ServiceCreator.create()


    suspend fun getExpressCompany(stationId :String) : BaseResult<List<ExpressCompany>> {
        return withContext(Dispatchers.IO){
            val map = HashMap<String, Any>()
            map["user_id"] = stationId
            ApiSignatureUtil.addSignature(map)
            service.getExpressCompany(map).await()
        }
    }
}