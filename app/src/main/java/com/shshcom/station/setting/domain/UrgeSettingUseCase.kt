package com.shshcom.station.setting.domain

import com.shshcom.module_base.network.KResults
import com.shshcom.station.base.ICaseBack
import com.shshcom.station.setting.http.ApiSetting
import com.shshcom.station.setting.http.bean.AutoUrgeData
import com.shshcom.station.setting.http.bean.SystemNotifyBean
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
                is KResults.Success -> {
                    caseBack.onSuccess(result.data)
                }
                is KResults.Failure -> {
                    val msg = result.error.message
                    caseBack.onError(msg.orEmpty())
                }
            }

        }
    }


    fun getNoticeList(caseBack: ICaseBack<List<SystemNotifyBean>>) {
        presenterScope.launch {
            val result = ApiSetting.getNoticeList(1, true)

            when (result) {
                is KResults.Success -> {
                    caseBack.onSuccess(result.data)
                }
                is KResults.Failure -> {
                    val msg = result.error.message
                    caseBack.onError(msg.orEmpty())
                }
            }

        }
    }

}