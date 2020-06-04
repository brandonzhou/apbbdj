package com.shshcom.module_base.utils

import android.util.Log

/**
 * desc:
 * author: zhhli
 * 2020/5/30
 */
object KLog {

    val V = 1
    val D = 2
    val I = 3
    val W = 4
    val E = 5

    private var level = 0

    fun init(level: Int) {
        this.level = level
    }

    private fun createMsg(vararg msg:Any): String {
        val stringBuilder = StringBuilder()
        for ( m in msg){
            stringBuilder.append(m)
        }
        return stringBuilder.toString()
    }

    fun d(tag: String = "KLog", vararg msg:Any) {
        if (level >= D) {
            Log.d(tag, createMsg(msg))
        }
    }


    fun e(tag: String = "KLog", msg: String) {
        if (level >= E) {
            Log.d(tag, msg)
        }
    }

}