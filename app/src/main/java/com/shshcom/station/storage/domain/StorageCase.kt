package com.shshcom.station.storage.domain

import com.mt.bbdj.baseconfig.db.core.DbUserUtil
import com.shshcom.module_base.network.Results
import com.shshcom.station.base.ICaseBack
import com.shshcom.station.storage.http.ApiStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * desc:
 * author: zhhli
 * 2020/6/29
 */
object StorageCase {
    val job = Job()
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    fun getStationId(): String {
        return DbUserUtil.getStationId()
    }

    fun confirmSubmitWarehouse(caseBack: ICaseBack<String>) {
        presenterScope.launch {
            val batchNo = ScanStorageCase.getInstance().batchNo
            val results = ApiStorage.confirmSubmitWarehouse(getStationId(), batchNo)

            when (results) {
                is Results.Success -> {
                    caseBack.onSuccess("提交成功")
                }
                is Results.Failure -> {
                    results.error.message?.let { caseBack.onError(it) }
                }
            }

        }
    }
}