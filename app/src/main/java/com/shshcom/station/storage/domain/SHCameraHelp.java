package com.shshcom.station.storage.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.mt.bbdj.baseconfig.utls.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class SHCameraHelp {

    int reqWidth = 720;
    int reqHeight = 360;

    private String createFileDir(Context context, String fileName) {
        File imgFile = context.getExternalFilesDir("scan_image");
        if (!imgFile.exists()) {
            imgFile.mkdir();
        }
        File file = new File(imgFile.getAbsolutePath() + File.separator +
                fileName + ".jpg");

        return file.getAbsolutePath();
    }


    public String saveImage(Context context, String fileName, byte[] data) {
        String filePath = createFileDir(context, fileName);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        try {
            options.inSampleSize = calculateInSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            boolean success = saveImage(filePath, bitmap);
            if (success) {
                return filePath;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;

    }


    private static boolean saveImage(String path, Bitmap bitmap) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
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


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        LogUtil.d("ScanStorageCase", "height:" + height + " width:" + width);
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }




    public Bitmap getImageBitmap(byte[] data) throws Exception {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        LogUtil.d("ScanStorageCase", "height:" + options.outHeight + " width:" + options.outWidth);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

//        String path = createFileDir(MyApplication.getInstance(),"123");
//
//
//        try {
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        FileInputStream fis = new FileInputStream(path);
//        return BitmapFactory.decodeStream(fis);

        return bitmap;

    }

}

/*

File imgFile = this.getExternalFilesDir("image");
        if (!imgFile.exists()){
            imgFile.mkdir();
        }
        try {
            File file = new File(imgFile.getAbsolutePath() + File.separator +
                    System.currentTimeMillis() + ".jpg");
            // 使用openInputStream(uri)方法获取字节输入流
            InputStream fileInputStream = getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            // 文件可用新路径 file.getAbsolutePath()
        } catch (Exception e) {
            e.printStackTrace();
        }


 */
