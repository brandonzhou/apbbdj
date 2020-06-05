package com.shshcom.station.statistics.domain

/**
 * desc:
 * author: zhhli
 * 2020/6/5
 */
interface  ICaseBack<T> {
    fun onSuccess(result: T)
    fun onError(error : String)
}