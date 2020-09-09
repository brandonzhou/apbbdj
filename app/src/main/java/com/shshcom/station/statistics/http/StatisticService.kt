package com.shshcom.station.statistics.http

import com.shshcom.config.meng
import com.shshcom.station.statistics.http.bean.*
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
    @POST("$meng/express/Station/todayExpressStatistics")
    fun todayExpressStatistics(@FieldMap fields: Map<String, Any>) : Call<BaseResult<TodayExpressStatistics>>

    /**
     * 查询今日快递明细接口
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/Station/queryExpressDetail")
    fun queryExpressDetail(@FieldMap fields: Map<String, Any>) : Call<BaseResult<PackageDetailResult>>

    /**
     * 总库存
     * 列表，按日期
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/Station/totalStock")
    fun totalStock(@FieldMap fields: Map<String, Any>) : Call<BaseResult<TotalStockData>>


    /**
     * 面单 修改手机号
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/Station/modifyMobile")
    fun modifyMobile(@FieldMap fields: Map<String, Any>) : Call<BaseResult<Any>>


    /**
     * 重新发送取件通知
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/Station/reSendSMSNotice")
    fun reSendSMSNotice(@FieldMap fields: Map<String, Any>) : Call<BaseResult<PackageDetailData>>

    /**
     * 出库后，获取快递详细信息
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/getOutPie")
    fun getOutPie(@FieldMap fields: Map<String, Any>) : Call<BaseResult<PackageDetailData>>

    /**
     * 快递出库 outWarehouse2
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/Express/Warehousing/outWarehouse2")
    fun outWarehouse2(@FieldMap fields: Map<String, Any>): Call<BaseResult<Any>>


    /**
     * 今日微信公众号通知了wechat_toal个快递
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/queryStationTodayNoticeStats")
    fun queryWeChatTodayNotice(@FieldMap fields: Map<String, Any>): Call<BaseResult<WeChatTodayNotice>>


    /**
     * 取件通知统计数据查询接口
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("$meng/express/station/queryStationNoticeStats")
    fun queryStationNoticeStats(@FieldMap fields: Map<String, Any>): Call<BaseResult<PackNotifyData>>


}