
package com.shshcom.station.storage.ml

import android.graphics.*
import androidx.camera.core.ImageProxy
import com.king.mlkit.vision.camera.util.LogUtils
import java.io.ByteArrayOutputStream

fun Bitmap.drawBitmap(block: (canvas: Canvas,paint: Paint) -> Unit): Bitmap {
    var result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    try {
        val canvas = Canvas(result)
        canvas.drawBitmap(this, 0f, 0f, null)
        val paint = Paint()
        paint.strokeWidth = 4f
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = Color.RED

        block(canvas,paint)

        canvas.save()
        canvas.restore()
    } catch (e: Exception) {
        LogUtils.w(e.message)
    }
    return result
}

fun Bitmap.drawRect(block: (canvas: Canvas,paint: Paint) -> Unit): Bitmap {
    var result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    try {
        val canvas = Canvas(result)
        canvas.drawBitmap(this, 0f, 0f, null)
        val paint = Paint()
        paint.strokeWidth = 6f
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED

        block(canvas,paint)

        canvas.save()
        canvas.restore()
    } catch (e: Exception) {
        LogUtils.w(e.message)
    }
    return result
}


fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}



