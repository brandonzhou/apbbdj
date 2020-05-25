package com.mt.bbdj.baseconfig.utls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.lang.ref.WeakReference;

/**
 * 功能描述 :
 * 创建人 : Administrator 创建时间: 20/1/10
 */
public class UtilDialog {

    /**
     * 提示
     * @param context
     * @param message
     */
    public static void showDialog(Context context, String message) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(weakReference.get())
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
    /**
     * 提示
     * @param context
     * @param message
     */
    public static void showDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(weakReference.get())
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("确定", listener);
        builder.create().show();
    }
}
