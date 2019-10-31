package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Size;

import java.io.ByteArrayOutputStream;

/**
 * Author : ZSK
 * Date : 2019/1/16
 * Description :  图片处理的工具类
 */
public class BitmapUtil {

    /**
     * 图片转化为16进制数据
     *
     * @param bitmap
     * @return
     */
    public static String bitmapTo16hex(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("将要转化为16进制数据的图片不能为null");
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bt = stream.toByteArray();
        String photoStr = byte2hex(bt);
        return photoStr;
    }

    /**
     * 二进制转换字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                sb.append("0"+stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }

    //从asset中获取bitmap文件
    public static Bitmap getAssertBitmap(Context context,int drawableBitmap) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),drawableBitmap);
        return bitmap;
    }

    //字节转换为bitmap
    public static Bitmap byteToBitmap(byte[] b, int width ,int height){
        YuvImage yuvimage=new YuvImage(b, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 100, baos);  //这里 80 是图片质量，取值范围 0-100，100为品质最高
        byte[] jdata = baos.toByteArray();//这时候 bmp 就不为 null 了
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
        return bmp;
    }
}
