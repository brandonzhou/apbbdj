package com.shshcom.station.imageblurdetection

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfDouble
import org.opencv.imgproc.Imgproc
import java.text.DecimalFormat

/**
 * desc:
 * author: zhhli
 * 2020/6/19
 */
object ImageDetectionUseCase {



    /**
     *
     *
     *

    Mat destination = new Mat();
    Mat matGray = new Mat();
    Mat sourceMatImage = new Mat();
    Utils.bitmapToMat(bitmap, sourceMatImage);
    Imgproc.cvtColor(sourceMatImage, matGray, Imgproc.COLOR_BGR2GRAY);
    Imgproc.Laplacian(matGray, destination, 3);
    MatOfDouble median = new MatOfDouble();
    MatOfDouble std = new MatOfDouble();
    Core.meanStdDev(destination, median, std);
     */


    fun getSharpnessScoreFromOpenCV(bitmap1: Bitmap): Double {
        val bitmap = resizeBitmap(bitmap1, 500,500)
        val destination = Mat()
        val matGray = Mat()

        val sourceMatImage = Mat()
        Utils.bitmapToMat(bitmap, sourceMatImage)
        Imgproc.cvtColor(sourceMatImage, matGray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.Laplacian(matGray, destination, 3)
        val median = MatOfDouble()
        val std = MatOfDouble()
        Core.meanStdDev(destination, median, std)
        return DecimalFormat("0.00").format(Math.pow(std.get(0, 0)[0], 2.0)).toDouble()
    }



    private fun resizeBitmap(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var width = image.width
        var height = image.height

        when {
            width > height -> { //landscape image
                val ratio = (width / maxWidth.toFloat())
                width = maxWidth
                height = (height / ratio).toInt()
            }
            height > width -> { //portrait image
                val ratio = height / maxHeight.toFloat()
                height = maxHeight
                width = (width / ratio).toInt()
            }
            else -> {
                width = maxWidth
                height = maxHeight
            }
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }



    fun cvtColor(bitmap: Bitmap): Bitmap {
        val binaryMat = Mat()
        val matGray = Mat()

        val sourceMatImage = Mat()
        Utils.bitmapToMat(bitmap, sourceMatImage)
        Imgproc.cvtColor(sourceMatImage, matGray, Imgproc.COLOR_RGB2GRAY)
        Imgproc.threshold(matGray, binaryMat, 0.0, 255.0, Imgproc.THRESH_OTSU)

        val white = Core.countNonZero(binaryMat)

        val black = binaryMat.size().area() - white


        val dst = binaryMat.clone()
        Core.bitwise_not(binaryMat,dst);


        val bitmap1 = Bitmap.createBitmap(bitmap)

        Utils.matToBitmap(dst, bitmap1)

        return bitmap1
    }







}