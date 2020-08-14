package com.shshcom.station.blockuser.http

import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.KResults
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.blockuser.http.bean.BlockUserData
import com.shshcom.station.storage.http.ApiStorage.await
import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.util.ApiSignatureUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * desc:敏感用户列表
 * author: zhhli
 * 2020/6/22
 */
object ApiBlockUser {
    private var service: BlockUserService = ServiceCreator.create()

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

    /**
     * 字段	类型	必填	描述	备注
    signature	String	是	数据签名
    station_id	Integer	是	驿站 id	12
    page	Integer	是	页码	1
    keyword	String	是	搜索关键词
     */
    suspend fun blockUserList(keyword: String, page: Int): KResults<BlockUserData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["keyword"] = keyword
            map["page"] = page
            ApiSignatureUtil.addSignature(map)
            service.blockUserList(map).await()
        }
    }

    /**
    station_id	Integer	是	驿站 id	12
    mobile	String	是	手机号	18310576535
     */
    suspend fun addBlockUser(mobile: String): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["mobile"] = mobile
            ApiSignatureUtil.addSignature(map)
            service.addBlockUser(map).await()
        }
    }

    /**
    station_id	Integer	是	驿站 id	12
    block_id	Integer	是	名单标识	2
     */
    suspend fun delBlockUser(blockId: Int): KResults<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["block_id"] = blockId
            ApiSignatureUtil.addSignature(map)
            service.delBlockUser(map).await()
        }
    }
}