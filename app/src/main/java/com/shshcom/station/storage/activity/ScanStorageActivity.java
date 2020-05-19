package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.king.zxing.CaptureActivity;
import com.king.zxing.CaptureHelper;
import com.king.zxing.camera.FrontLightMode;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.shshcom.station.storage.domain.SHCameraHelp;

import java.util.EnumSet;

/**
 * desc:拍照入库
 * author: zhhli
 * 2020/5/18
 */
public class ScanStorageActivity extends CaptureActivity {

    boolean isContinuousScan = true;

    private CaptureHelper helper;
    private Camera camera;

    private Activity activity;

    @Override
    public int getLayoutId() {
        return R.layout.act_scan_storage;
    }

    @Override
    public int getIvTorchId() {
        return 0;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        helper = getCaptureHelper();

        helper.playBeep(true)//播放音效
                .vibrate(true)//震动
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .decodeFormats(EnumSet.of(BarcodeFormat.CODE_128))//设置只识别二维码会提升速度
                .frontLightMode(FrontLightMode.AUTO)//设置闪光灯模式
                .tooDarkLux(45f)//设置光线太暗时，自动触发开启闪光灯的照度值
                .brightEnoughLux(100f)//设置光线足够明亮时，自动触发关闭闪光灯的照度值
                .continuousScan(false);//是否连扫



    }


    /**
     * 扫码结果回调
     * @param result 扫码结果
     * @return
     */
    @Override
    public boolean onResultCallback(String result) {


        ToastUtil.showShort(result);
        takePicture(result);


        return true;
    }


    private void takePicture(String fileName){
        if(camera== null){
            camera = helper.getCameraManager().getOpenCamera().getCamera();
        }




        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.stopPreview();
                SHCameraHelp shCameraHelp = new SHCameraHelp();
                String file = shCameraHelp.saveImage(activity, fileName,data);
                LogUtil.d("ScanStorageCase", file);
                camera.startPreview();

                helper.restartPreviewAndDecode();
            }
        });
    }



}
