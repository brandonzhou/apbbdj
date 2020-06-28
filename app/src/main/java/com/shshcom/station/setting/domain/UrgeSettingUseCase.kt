package com.shshcom.station.setting.domain

import com.shshcom.module_base.network.Results
import com.shshcom.station.setting.http.ApiSetting
import com.shshcom.station.setting.http.bean.AutoUrgeData
import com.shshcom.station.statistics.domain.ICaseBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * desc:
 * author: zhhli
 * 2020/6/28
 */
object UrgeSettingUseCase {
    val job = Job()
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    fun getPackageUrgeSetting(caseBack: ICaseBack<AutoUrgeData>) {
        presenterScope.launch {
            val result = ApiSetting.getPackageUrgeSetting()

            when (result) {
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
}