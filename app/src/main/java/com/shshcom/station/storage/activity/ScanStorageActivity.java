package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.king.zxing.CaptureActivity;
import com.king.zxing.CaptureHelper;
import com.king.zxing.camera.FrontLightMode;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.shshcom.station.storage.domain.ScanStorageCase;

import java.util.EnumSet;

/**
 * desc:拍照入库
 * author: zhhli
 * 2020/5/18
 */
public class ScanStorageActivity extends CaptureActivity implements View.OnClickListener {
    private static final String TAG = "ScanStorageActivity";
    /*请求码-配置取件码*/
    private static final int REQUEST_CODE_SET_PICK_UP_NUMBER = 1;

    // 取件码
    private TextView tv_pickup_code;
    // 扫出的条码信息
    private TextView tv_bar_code;
    private TextView tv_last_code_info;
    private TextView tv_total_number;

    private CaptureHelper helper;
    private Camera camera;

    private Activity activity;

    private ScanStorageCase storageCase;

    @Override
    public int getLayoutId() {
        return R.layout.act_scan_storage;
    }

    @Override
    public int getIvTorchId() {
        return 0;
    }


    @Override
    public int getViewfinderViewId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        storageCase = ScanStorageCase.getInstance();
        storageCase.init(this);

        initCapture();
        initView();
        initData();
    }

    private void initView() {
        tv_pickup_code = findViewById(R.id.tv_pickup_code);
        tv_bar_code = findViewById(R.id.tv_bar_code);
        tv_last_code_info = findViewById(R.id.tv_last_code_info);
        tv_total_number = findViewById(R.id.tv_total_number);


        findViewById(R.id.iv_pickup_code_modify).setOnClickListener(this);
        findViewById(R.id.tv_tip_edit_express).setOnClickListener(this);
    }

    private void initData() {
        PickupCode pickupCode = storageCase.getCurrentPickCode();
        tv_pickup_code.setText(pickupCode.getCurrentNumber());

        ScanImage scanImage = storageCase.getLastScanImage();

        if(scanImage!= null){
            //最后入库：取件码 A1-29-20000411 | 快递单号7238283772747737
            tv_last_code_info.setText(String.format("最后入库：取件码 %s | 快递单号 %s",
                    scanImage.getPickCode(),scanImage.getEId()));
        }else {
            tv_last_code_info.setVisibility(View.GONE);
        }

    }

    private void initCapture() {
        helper = getCaptureHelper();
        helper.playBeep(true)//播放音效
                .vibrate(true)//震动
                .fullScreenScan(true)
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .decodeFormats(EnumSet.of(BarcodeFormat.CODE_128))//设置只识别二维码会提升速度
                .frontLightMode(FrontLightMode.AUTO)//设置闪光灯模式
                .tooDarkLux(45f)//设置光线太暗时，自动触发开启闪光灯的照度值
                .brightEnoughLux(100f)//设置光线足够明亮时，自动触发关闭闪光灯的照度值
                .continuousScan(true);//是否连扫
    }


    /**
     * 扫码结果回调
     *
     * @param result 扫码结果
     * @return
     */
    @Override
    public boolean onResultCallback(String result) {
        ScanImage scanImage = storageCase.searchScanImageFromDb(result);
        if (scanImage != null) {
            ToastUtil.showShort("重复扫描：" + result);
//            return true;
        }
        takePicture(result);
        return true;
    }


    private void takePicture(String result) {
        if (camera == null) {
            camera = helper.getCameraManager().getOpenCamera().getCamera();
        }
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
//                camera.stopPreview();
                PickupCode pickupCode = storageCase.getCurrentPickCode();

                PickupCode nextCode = pickupCode.nextPickCode();

                updateUI(result, pickupCode.getCurrentNumber(), nextCode.getCurrentNumber());
                storageCase.saveScanImage(result, pickupCode.getCurrentNumber(),data);
                storageCase.updatePickCode(nextCode);

                camera.startPreview();

                helper.restartPreviewAndDecode();
            }
        });
    }

    private void updateUI(String barCode,String pickCode, String nextCode) {
        tv_bar_code.setText(barCode);
        if (tv_bar_code.getVisibility() != View.VISIBLE) {
            tv_bar_code.setVisibility(View.VISIBLE);
            tv_bar_code.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tv_bar_code != null) {
                        tv_bar_code.setVisibility(View.GONE);
                    }
                }
            }, 1500);
        }


        //最后入库：取件码 A1-29-20000411 | 快递单号7238283772747737
        tv_last_code_info.setText(String.format("最后入库：取件码 %s | 快递单号 %s",
                pickCode,barCode));

        tv_pickup_code.setText(nextCode);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_pickup_code_modify:
                PickupCode pickupCode = new PickupCode("12", PickupCode.Type.type_code.getDesc(),"A1",123,"A1-0001");
                SetPickupCodeTypeActivity.openActivity(this,REQUEST_CODE_SET_PICK_UP_NUMBER,pickupCode);
                break;
            case R.id.tv_tip_edit_express:

                break;
            case R.id.iv_capture:
                takePicture("123");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SET_PICK_UP_NUMBER:
                PickupCode pickupCode = (PickupCode) data.getSerializableExtra("pickupCodeRule");
                Log.d(TAG, pickupCode.toString());
                break;
                default:

        }
    }


}
