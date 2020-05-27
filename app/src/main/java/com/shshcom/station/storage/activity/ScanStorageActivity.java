package com.shshcom.station.storage.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.BarcodeFormat;
import com.king.zxing.CaptureActivity;
import com.king.zxing.CaptureHelper;
import com.king.zxing.camera.FrontLightMode;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.utls.UtilDialog;
import com.shshcom.station.storage.domain.ScanStorageCase;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.ExpressCompany;
import com.shshcom.station.util.AntiShakeUtils;

import java.util.EnumSet;
import java.util.List;

import io.reactivex.functions.Consumer;

import static com.lxj.xpopup.enums.PopupAnimation.ScaleAlphaFromCenter;

/**
 * desc:拍照入库
 * author: zhhli
 * 2020/5/18
 */
public class ScanStorageActivity extends CaptureActivity implements View.OnClickListener {
    private static final String TAG = "ScanStorageActivity";
    /*请求码-配置取件码*/
    private static final int REQUEST_CODE_SET_PICK_UP_NUMBER = 1;
    /*请求系统权限-摄像头*/
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 1;

    // 取件码
    private TextView tv_pickup_code;
    // 扫出的条码信息
    private TextView tv_bar_code;
    private TextView tv_last_code_info;
    private TextView tv_total_number;
    private TextView tv_capture_bar_code;

    private CaptureHelper helper;
    //private Camera camera;

    private Activity activity;

    private ScanStorageCase storageCase;

    private int count = 0;

    /*当前解码的条码或手动输入的条码-code*/
    private String currentBarCode;
    /*手动录入的手机号码*/
    private String currentPhone;

    private State state;

    private enum State{
        scanning,
        capturing,
        editing
    }

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

        state = State.scanning;

        initPermission();
        initCapture();
        initView();
        initData();

        // 对未上传的图片进行上传
        List<ScanImage> list = storageCase.getScanImageList(ScanImage.State.uploading);
        storageCase.retryUploadImage(list);
    }


    private void initPermission() {
        //请求Camera权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG,"onRequestPermissionsResult granted");

                } else {
                    Log.i(TAG,"onRequestPermissionsResult denied");
                    UtilDialog.showDialog(this,"请前往设置中开启摄像头权限");
                }
                break;
                default:
        }
    }


    private void initView() {
        tv_pickup_code = findViewById(R.id.tv_pickup_code);
        tv_bar_code = findViewById(R.id.tv_bar_code);
        tv_last_code_info = findViewById(R.id.tv_last_code_info);
        tv_total_number = findViewById(R.id.tv_total_number);
        tv_capture_bar_code = findViewById(R.id.tv_capture_bar_code);


        findViewById(R.id.iv_pickup_code_modify).setOnClickListener(this);
        findViewById(R.id.tv_tip_edit_express).setOnClickListener(this);
        findViewById(R.id.tv_btn_submit).setOnClickListener(this);
        findViewById(R.id.rl_back).setOnClickListener(this);
    }

    private void initData() {
        PickupCode pickupCode = storageCase.getCurrentPickCode();
        tv_pickup_code.setText(pickupCode.getCurrentNumber());

        ScanImage scanImage = storageCase.getLastScanImage();

        if (scanImage != null) {
            //最后入库：取件码 A1-29-20000411 | 快递单号7238283772747737
            tv_last_code_info.setText(String.format("最后入库：取件码 %s | 快递单号 %s",
                    scanImage.getPickCode(), scanImage.getEId()));
        } else {
            tv_last_code_info.setVisibility(View.GONE);
        }

       // int size = storageCase.getScanImageSize();
        tv_total_number.setText(count + "");

    }

    private void initCapture() {
        helper = getCaptureHelper();
        helper
                //.playBeep(true)//播放音效
                .vibrate(true)//震动
                .fullScreenScan(true)
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .decodeFormats(EnumSet.of(BarcodeFormat.CODE_128))//设置只识别二维码会提升速度
                .frontLightMode(FrontLightMode.AUTO)//设置闪光灯模式
                .tooDarkLux(45f)//设置光线太暗时，自动触发开启闪光灯的照度值
                .brightEnoughLux(100f)//设置光线足够明亮时，自动触发关闭闪光灯的照度值
                .continuousScan(false);//是否连扫
    }


    /**
     * 扫码结果回调
     *
     * @param result 扫码结果
     * @return
     */
    @Override
    public boolean onResultCallback(String result) {
        if(State.editing.equals(state)){
            helper.restartPreviewAndDecode();
            return true;
        }


        if(result.equals(currentBarCode)){
            helper.restartPreviewAndDecode();
            return true;
        }
        currentBarCode = result;



        if(!StringUtil.isMatchExpressCode(result)){
            helper.restartPreviewAndDecode();
            return true;
        }

        ScanImage scanImage = storageCase.searchScanImageFromDb(result);
        if (scanImage != null) {
            helper.restartPreviewAndDecode();
            SoundHelper.getInstance().playNotifiRepeatSound();
            return true;
        }





        storageCase.httpQueryExpress(result).subscribe(new Consumer<BaseResult<ExpressCompany>>() {
            @Override
            public void accept(BaseResult<ExpressCompany> baseResult) throws Exception {
                ExpressCompany expressCompany = baseResult.getData();
                if (tv_bar_code != null && expressCompany!=null) {
                    takePicture(result, expressCompany.getExpress_id());

                }
            }
        });

        return true;
    }


    private void takePicture(String result,  int express_id) {
        // https://stackoverflow.com/questions/21723557/java-lang-runtimeexception-takepicture-failed
        // RuntimeException: Camera is being used after Camera.release() was called
        Camera  camera = helper.getCameraManager().getOpenCamera().getCamera();
        camera.startPreview();
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                SoundHelper.getInstance().playExpress(express_id);
//                camera.stopPreview();
                count ++;
                PickupCode pickupCode = storageCase.getCurrentPickCode();

                PickupCode nextCode = pickupCode.nextPickCode();

                updateUI(result, pickupCode.getCurrentNumber(), nextCode.getCurrentNumber());
                storageCase.saveScanImage(result, pickupCode, data, null);
                storageCase.updatePickCode(nextCode);

                tv_bar_code.setText(result);
                if (tv_bar_code.getVisibility() != View.VISIBLE) {
                    tv_bar_code.setVisibility(View.VISIBLE);
                    tv_bar_code.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (tv_bar_code != null) {
                                tv_bar_code.setVisibility(View.GONE);
                            }
                            currentBarCode ="";
                        }
                    }, 1500);
                }

                camera.startPreview();

                helper.restartPreviewAndDecode();
            }
        });
    }

    private void updateUI(String barCode, String pickCode, String nextCode) {

        //最后入库：取件码 A1-29-20000411 | 快递单号7238283772747737
        tv_last_code_info.setText(String.format("最后入库：取件码 %s | 快递单号 %s", pickCode, barCode));

        tv_pickup_code.setText(nextCode);

        //int size = storageCase.getScanImageSize()+1;
        tv_total_number.setText(count + "");

    }


    @Override
    public void onClick(View view) {
        if(AntiShakeUtils.isInvalidClick(view)){
            return;
        }
        switch (view.getId()) {
            case R.id.iv_pickup_code_modify:
                PickupCode pickupCode = storageCase.getCurrentPickCode();
                SetPickupCodeTypeActivity.openActivity(this, REQUEST_CODE_SET_PICK_UP_NUMBER, pickupCode);
                break;
            case R.id.tv_tip_edit_express:
                openEditDialog();
                break;
            case R.id.tv_btn_submit:
                if(storageCase.isAllImageUploaded()){
                    ScanOcrResultActivity.openActivity(this);
                }else {
                    ScanImageUploadingActivity.openActivity(this);
                }
                break;
            case R.id.iv_capture:
                saveEditExpress(currentBarCode, currentPhone);
                break;
            case R.id.iv_close_edit:
                closeEditDialog();
                break;
            case R.id.rl_back:
                finish();
                break;
                default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SET_PICK_UP_NUMBER:
                if (data != null) {
                    PickupCode pickupCode = (PickupCode) data.getSerializableExtra("pickupCodeRule");
                    Log.d(TAG, pickupCode.toString());
                    storageCase.updatePickCode(pickupCode);
                    tv_pickup_code.setText(pickupCode.getCurrentNumber());
                }
                break;
            default:

        }
    }


    private void openEditDialog(){
        state = State.editing;
        tv_capture_bar_code.setText("");
        EditDialogView dialog = new EditDialogView(this);
        BasePopupView popupView = new XPopup.Builder(this)
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onDismiss() {
                        super.onDismiss();
                        if (TextUtils.isEmpty(tv_capture_bar_code.getText().toString())) {
                            closeEditDialog();
                        }
                    }
                })
                .popupAnimation(ScaleAlphaFromCenter)
                .autoOpenSoftInput(true)
                .dismissOnTouchOutside(false)
                .asCustom(dialog)
                .show();
        setViewShow(true, R.id.rl_capture);
        setViewShow(false,R.id.tv_tip_edit_express,R.id.tv_tip_capture);
    }

    public void closeEditDialog(){
        if(activity.isDestroyed()){
            return;
        }
        setViewShow(false, R.id.rl_capture,R.id.rl_express_info_tips);
        setViewShow(true,R.id.tv_tip_edit_express,R.id.tv_tip_capture);
        state = State.scanning;

    }

    private void setViewShow(boolean show, int... viewIds){
        for(int id : viewIds){
            findViewById(id).setVisibility(show? View.VISIBLE: View.GONE);
        }
    }

    private void saveEditExpress(String barCode, String phone){
        Camera  camera = helper.getCameraManager().getOpenCamera().getCamera();
        camera.startPreview();
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                count ++;
                PickupCode pickupCode = storageCase.getCurrentPickCode();

                PickupCode nextCode = pickupCode.nextPickCode();

                updateUI(barCode, pickupCode.getCurrentNumber(), nextCode.getCurrentNumber());
                storageCase.saveScanImage(barCode, pickupCode, data,phone);

                storageCase.updatePickCode(nextCode);
                currentBarCode ="";

                camera.startPreview();


                helper.restartPreviewAndDecode();
            }
        });

        closeEditDialog();
    }

    class EditDialogView extends CenterPopupView{

        public EditDialogView(@NonNull Context context) {
            super(context);
        }

        @Override
        protected int getImplLayoutId() {
            return R.layout.layout_edit_pickup_code_dialog;
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            EditText etExpressCode = findViewById(R.id.et_tracking_number_value);
            EditText etPhone = findViewById(R.id.et_phone_value);

            findViewById(R.id.btn_cancel).setOnClickListener(v -> {
                        dismiss();
                        closeEditDialog();
            }
            );
            findViewById(R.id.btn_ok).setOnClickListener(v -> {
                        dismissOrHideSoftInput();
                        String expressCode = etExpressCode.getText().toString().trim();
                        if (TextUtils.isEmpty(expressCode)) {
                            ToastUtil.showShort(getResources().getString(R.string.input_express_code));
                            return;
                        }
                        String phone = etPhone.getText().toString().trim();
                        if (!StringUtil.isMobile(phone)) {
                            ToastUtil.showShort(getResources().getString(R.string.phone_format_error));
                            return;
                        }
                        currentBarCode = expressCode;
                        currentPhone = phone;
                        String showTips = currentBarCode+"\n手机号:"+currentPhone;
                        tv_capture_bar_code.setText(showTips);
                        setViewShow(true, R.id.rl_express_info_tips);
                        dismiss();
            }
            );

        }
    }


}
