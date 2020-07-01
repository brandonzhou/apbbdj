package com.shshcom.station.imageblurdetection

import android.graphics.Bitmap

/**
 * desc:
 * author: zhhli
 * 2020/6/19
 */
data class OpenCVData(val bitmap: Bitmap, val score: Double) {
    fun isValid(): Boolean {
        return score > 100
    }
}