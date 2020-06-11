package com.shshcom.station.statistics.domain

import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import com.shshcom.station.statistics.http.bean.TodayExpressStatistics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * desc:
 * author: zhhli
 * 2020/6/5
 */
object PackageUseCase {
    val job = Job()
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }
    
    fun getStationId() : String{
        return DbUserUtil.getStationId()
    }

    fun todayExpressStatistics(caseBack: ICaseBack<TodayExpressStatistics>){
        presenterScope.launch {
            val result =  ApiPackageStatistic.todayExpressStatistics("12")

            when(result){
                is Results.Success -> {
                    caseBack.onSuccess(result.data)
                }
                is Results.Failure -> {
                    val msg = result.error.message
                    caseBack.onError(msg.orEmpty())
                }
            }
        }
    }

    suspend fun modifyPhone(pid:Int, phone : String){
        val results = ApiPackageStatistic.modifyMobile(getStationId(),pid, phone)
    }
}