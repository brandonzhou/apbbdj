package com.shshcom.station.statistics.http

import com.shshcom.station.statistics.http.bean.PackageDetailData
import com.shshcom.station.statistics.http.bean.PackageDetailResult
import com.shshcom.station.statistics.http.bean.TodayExpressStatistics
import com.shshcom.station.statistics.http.bean.TotalStockData
import com.shshcom.station.storage.http.bean.BaseResult
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * desc: 快递包裹 库存统计
 * author: zhhli
 * http://xwiki.shkjplus.cn:8001/bin/view/产品管理/驿站相关/驿站端APP/驿站端AppV1.0版本产品设计/
 *
 * 接口： http://task.shkjplus.cn:8100/T629
 * 2020/6/3
 */
interface StatisticService{

    /**
     * 今日快递数量统计
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com/express/Station/todayExpressStatistics")
    fun todayExpressStatistics(@FieldMap fields: Map<String, Any>) : Call<BaseResult<TodayExpressStatistics>>

    /**
     * 查询今日快递明细接口
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com/express/Station/queryExpressDetail")
    fun queryExpressDetail(@FieldMap fields: Map<String, Any>) : Call<BaseResult<PackageDetailResult>>

    /**
     * 总库存
     * 列表，按日期
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com/express/Station/totalStock")
    fun totalStock(@FieldMap fields: Map<String, Any>) : Call<BaseResult<TotalStockData>>


    /**
     * 面单 修改手机号
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com/express/Station/modifyMobile")
    fun modifyMobile(@FieldMap fields: Map<String, Any>) : Call<BaseResult<Any>>


    /**
     * 重新发送取件通知
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("https://meng.81dja.com/express/Station/reSendSMSNotice")
    fun reSendSMSNotice(@FieldMap fields: Map<String, Any>) : Call<BaseResult<PackageDetailData>>


}