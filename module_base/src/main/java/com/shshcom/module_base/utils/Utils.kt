package com.shshcom.module_base.utils

import android.content.Context

/*
 *
 *
 * @author: zhhli
 * @date: 2020/8/3
 */
object Utils {
    @JvmStatic
    lateinit var context: Context

    public fun init(context: Context) {
        this.context = context
    }
}