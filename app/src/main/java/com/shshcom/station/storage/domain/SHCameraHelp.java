package com.shshcom.station.storage.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.utls.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import me.shouheng.compress.Compress;
import me.shouheng.compress.strategy.Strategies;
import me.shouheng.compress.strategy.config.ScaleMode;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class SHCameraHelp {


    private String createFileDir(Context context, String fileName) {
        File imgFile = context.getExternalFilesDir("scan_image");
        if (!imgFile.exists()) {
            imgFile.mkdir();
        }
        File file = new File(imgFile.getAbsolutePath() + File.separator +
                fileName + ".jpg");

        return file.getAbsolutePath();
    }


    public String saveImage(Context context, String fileName, Bitmap bitmap) {
        String filePath = createFileDir(context, fileName);

        boolean success = saveImage(filePath, bitmap);
        if (success) {
            return filePath;
        } else {
            return null;
        }
    }

    public String saveImage(Context context, String fileName, byte[] data) {
        String filePath = createFileDir(context, fileName);

        Bitmap bitmap = getImageBitmap(data);

        boolean success = saveImage(filePath, bitmap);
        if (success) {
            return filePath;
        } else {
            return null;
        }

    }


    private static boolean saveImage(String path, Bitmap bitmap) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            printBitmapInfo(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if ((bitmap != null) && (!bitmap.isRecycled())) {
            bitmap.recycle();
            return true;
        }


        return false;
    }

    private static void printBitmapInfo(Bitmap bitmap) {
        final int height = bitmap.getHeight();
        ;
        final int width = bitmap.getWidth();
        ;
        LogUtil.d("ScanStorageCase", "height:" + height + " width:" + width);

    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        LogUtil.d("ScanStorageCase", "height:" + height + " width:" + width);
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }


    public Bitmap getImageBitmap(byte[] data) {
        // https://github.com/Shouheng88/Compressor/blob/master/README-zh.md
        Compress compress = Compress.Companion.with(MyApplication.getInstance(), data);

        return compress.strategy(Strategies.INSTANCE.compressor())
                .setMaxHeight(1080)
                .setMaxWidth(1920)
                .setScaleMode(ScaleMode.SCALE_SMALLER)
                .asBitmap().get();
    }

}
