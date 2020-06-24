package com.shshcom.station.shop.http

import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.Results
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.storage.http.ApiStorage.await
import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.util.ApiSignatureUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * desc:
 * author: zhhli
 * 2020/6/24
 */
object ApiShop {
    private var service: ShopService = ServiceCreator.create()

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

    /**
     *
    type	String	是	add增加 reduce	reduce
    goods_id	Integer	是	商品id	12
    stock	Integer	是	增加、减少的数量	12 , 克
     */
    suspend fun modifyStock(goodId: String, stock: Long, isAdd: Boolean): Results<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = getStationId()
            map["goods_id"] = goodId
            map["stock"] = stock
            map["type"] = if (isAdd) {
                "add"
            } else {
                "reduce"
            }
            ApiSignatureUtil.addSignature(map)
            service.modifyStock(map).await()
        }
    }
}