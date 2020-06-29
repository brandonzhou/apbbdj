package com.shshcom.station.base

/**
 * desc:
 * author: zhhli
 * 2020/6/5
 */
interface  ICaseBack<T> {
    fun onSuccess(result: T)
    fun onError(error : String)
}