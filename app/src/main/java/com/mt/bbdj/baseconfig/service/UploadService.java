package com.mt.bbdj.baseconfig.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.mt.bbdj.baseconfig.internet.ImageTransfer;
import com.mt.bbdj.baseconfig.model.Constant;

/**
 * @Author : ZSK
 * @Date : 2019/12/3
 * @Description :
 */
public class UploadService extends IntentService {

    public UploadService() {
        super("UploadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("图片==", "onCreate()");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("图片==", "onHandleIntent()");
        Constant.isBegin = intent.getBooleanExtra("isBegin",false);

        while (Constant.isBegin){
            ImageTransfer.uploadImage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("图片==", "onDestroy()");
    }
}
