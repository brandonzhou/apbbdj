package com.shshcom.station.statistics.http

import com.shshcom.module_base.network.Results
import com.shshcom.module_base.network.ServiceCreator
import com.shshcom.station.statistics.http.bean.*
import com.shshcom.station.storage.http.ApiStorage.await
import com.shshcom.station.storage.http.bean.BaseResult
import com.shshcom.station.util.ApiSignatureUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * desc: 包裹统计
 * author: zhhli
 * 2020/6/3
 */
object ApiPackageStatistic {

    private var service: StatisticService = ServiceCreator.create()

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

    suspend fun todayExpressStatistics(stationId: String): Results<TodayExpressStatistics> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            ApiSignatureUtil.addSignature(map)
            service.todayExpressStatistics(map).await()
        }
    }


    /**
    station_id	驿站id
    express_id	快递公司id
    notice	    通知状态  1全部通知状态 2通知 3 通知失败
    out_type	出库状态  1全部2已出库3未出库。1-3仅限当日日入库快递。4包含历史入库快递但是出库时间是当日
    time	    要查询的时间,  默认当天时间
    page	    当前查询的页数  默认1
    per_page	每页多少条      默认20
    signature	数据签名		是
     */
    suspend fun queryExpressDetail(stationId: String, expressId: Int, notice: Int, outState: Int,
                                   time: String, page: Int, perPage: Int = 10): Results<PackageDetailResult> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            if (expressId > 0) {
                map["express_id"] = expressId
            }
            map["notice"] = notice
            map["out_type"] = outState

            map["time"] = time
            map["page"] = page
            map["per_page"] = perPage
            ApiSignatureUtil.addSignature(map)
            service.queryExpressDetail(map).await()
        }
    }


    /**
     * 总库存
    station_id	驿站id	Integer	是	12
    signature	数据签名	string	是	XXXXXXXXXXX
    page	    当前查询的页数	Integer	否	默认1
    per_page	每页多少条
     */
    suspend fun totalStock(stationId: String, page: Int, perPage: Int = 20): Results<TotalStockData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["page"] = page
            map["per_page"] = perPage

            ApiSignatureUtil.addSignature(map)
            service.totalStock(map).await()

        }
    }


    /**
     * 修改手机号
    station_id	驿站id	Integer	是	12
    mobile	手机号	string	是	13800138000
    pie_id	快递数据唯一id	Integer	是	4
     */
    suspend fun modifyMobile(stationId: String, pie_id: Int, mobile: String): Results<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["mobile"] = mobile
            map["pie_id"] = pie_id
            ApiSignatureUtil.addSignature(map)
            service.modifyMobile(map).await()
        }
    }

    /**
     * 重新发送取件通知
    station_id	驿站id	Integer	是	12
    mobile	手机号	string	是	13800138000
    pie_id	快递数据唯一id	Integer	是	4
     */
    suspend fun reSendSMSNotice(stationId: String, pie_id: Int): Results<PackageDetailData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["pie_id"] = pie_id
            ApiSignatureUtil.addSignature(map)
            service.reSendSMSNotice(map).await()
        }
    }

    /**
     * 获取面单详细信息
     */
    suspend fun getOutPie(pie_id: Int): Results<PackageDetailData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["pie_id"] = pie_id
            ApiSignatureUtil.addSignature(map)
            service.getOutPie(map).await()
        }
    }


    /**
     * 出库
     * number 快递单号
     */
    suspend fun outWarehouse2(stationId: String, number: String): Results<Any> {
        return processApi {
            val map = HashMap<String, Any>()
            map["user_id"] = stationId
            map["pie_data"] = number
            ApiSignatureUtil.addSignature(map)
            service.outWarehouse2(map).await()
        }
    }


    /**
     * 首页 今日微信公众号通知了x个快递
     */
    suspend fun queryWeChatTodayNotice(stationId: String): Results<WeChatTodayNotice> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            ApiSignatureUtil.addSignature(map)
            service.queryWeChatTodayNotice(map).await()
        }
    }

    /**
     * 取件通知统计数据查询接口
     */
    suspend fun queryStationNoticeStats(stationId: String, page: Int): Results<PackNotifyData> {
        return processApi {
            val map = HashMap<String, Any>()
            map["station_id"] = stationId
            map["page"] = page
            ApiSignatureUtil.addSignature(map)
            service.queryStationNoticeStats(map).await()
        }
    }


}