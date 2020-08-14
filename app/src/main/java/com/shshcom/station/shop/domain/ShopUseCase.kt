package com.shshcom.station.shop.domain

import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.KResults
import com.shshcom.station.base.ICaseBack
import com.shshcom.station.shop.http.ApiShop
import com.shshcom.station.statistics.domain.PackageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * desc:
 * author: zhhli
 * 2020/6/24
 */
object ShopUseCase {
    val job = Job()
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    fun modifyStock(goodId: String, stock: Long, isAdd: Boolean, caseBack: ICaseBack<String>) {
        PackageUseCase.presenterScope.launch {
            val stationId = DbUserUtil.getStationId()
            val result = ApiShop.modifyStock(goodId, stock, isAdd)

            when (result) {
                is KResults.Success -> {
                    caseBack.onSuccess("修改成功")
                }
                is KResults.Failure -> {
                    val msg = result.error.message
                    caseBack.onError(msg.orEmpty())
                }
            }
        }
    }
}