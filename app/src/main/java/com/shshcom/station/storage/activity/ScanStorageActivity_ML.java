package com.shshcom.station.storage.activity;

import static com.lxj.xpopup.enums.PopupAnimation.ScaleAlphaFromCenter;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.barcode.Barcode;
import com.king.mlkit.vision.barcode.BarcodeCameraScanActivity;
import com.king.mlkit.vision.camera.AnalyzeResult;
import com.king.mlkit.vision.camera.CameraScan;
import com.king.mlkit.vision.camera.analyze.Analyzer;
import com.king.mlkit.vision.camera.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.lxj.xpopup.util.KeyboardUtils;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.utls.DialogUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.utls.UtilDialog;
import com.shshcom.camera.SHBaseCameraScan;
import com.shshcom.station.base.ICaseBack;
import com.shshcom.station.imageblurdetection.OpenCVData;
//import com.shshcom.station.print.PrintHelper;
import com.shshcom.station.storage.domain.SHCameraHelp;
import com.shshcom.station.storage.domain.ScanStorageCase;
import com.shshcom.station.storage.domain.StorageCase;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.ExpressCompany;
import com.shshcom.station.storage.ml.ExpressInfoPopView;
import com.shshcom.station.storage.ml.ImagePopView;
import com.shshcom.station.storage.ml.TzScanAnalyzer;
import com.shshcom.station.storage.ocrclip.BitmapUtil;
import com.shshcom.station.storage.ocrclip.ERectFindView;
import com.shshcom.station.storage.ocrclip.OcrTypeHelper;
import com.shshcom.station.storage.widget.CustomExpressCompanyPopup;
import com.shshcom.station.util.AntiShakeUtils;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.compress.Compress;

/**
 * desc:拍照入库
 * author: zhhli
 * 2020/5/18
 */
public class ScanStorageActivity_ML extends BarcodeCameraScanActivity implements View.OnClickListener, TzScanAnalyzer.OcrRecognizeCallback {
    private static final String TAG = "ScanStorageActivity";
    /*请求码-配置取件码*/
    private static final int REQUEST_CODE_SET_PICK_UP_NUMBER = 1;
    /*请求系统权限-摄像头*/
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 1;

//    private PrintHelper printHelper;

    // 取件码
    private TextView tv_pickup_code;
    // 扫出的条码信息
    private TextView tv_bar_code;
    private TextView tv_last_code_info;
    private TextView tv_total_number;
    private TextView tv_capture_bar_code;
    private EditText etExpressCode;
    private EditText etMobileValue;
    private TextView tvBtnOk;
    private LinearLayout llPhone;

    private TextView tv_tracking_company_value;

    private SHBaseCameraScan cameraScan;

    // ---------敏感区视图相关属性--------------------
    private RelativeLayout rootLayout;
    /*识别模板参数类，包括敏感区域位置等信息*/
    private OcrTypeHelper ocrTypeHelper;
    private ERectFindView rectFindView;
    private Animation verticalAnimation;
    private ImageView scanHorizontalLineImageView;
    private int scan_line_width;
    private int srcWidth, srcHeight, screenWidth, screenHeight;
    //private Camera camera;
    // 闪光灯
    private View flashlightView;

    private Activity activity;

    private ScanStorageCase storageCase;

    private int barCodeSanRepeatTime = 0;

    /*当前解码的条码或手动输入的条码-code*/
    private String currentBarCode;
    private ScanImage currentBarCodeScanImageFromDb;
    /*手动录入的手机号码*/
    private String currentPhone;
    /*手动录入的快递公司*/
    private ExpressCompany currentExpress;
    /*快递公司列表*/
    ArrayList<ExpressCompany> mExpressCompanies;

    private OpenCVData openCVData;

    private TzScanAnalyzer tzScanAnalyzer;

    private State state;


    @Override
    public void onScanResultFailure() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * Ocr识别手机号码 回调接口
     */
    @Override
    public void ocrCallback(String mobilePhone) {
        cameraScan.setAnalyzeImage(false);
        tzScanAnalyzer.setOpenRecog(false);

        if (!ocrHandlering) {
            currentPhone = mobilePhone;
            ocrHandlering = true;
            Message message = Message.obtain();
            message.what = 0;
            message.obj = currentPhone;
            ocrHandler.sendMessage(message);
        }

    }

    private OCRHandler ocrHandler;
    /*手机号码识别成功后，处理中*/
    boolean ocrHandlering = false;

    private static class OCRHandler extends Handler {
        WeakReference<ScanStorageActivity_ML> wr;

        public OCRHandler(ScanStorageActivity_ML activityMl) {
            wr = new WeakReference<ScanStorageActivity_ML>(activityMl);
        }

        public void handleMessage(Message msg) {
            if (wr.get() != null) {
                ScanStorageActivity_ML conext = wr.get();
                switch (msg.what) {
                    default:
                        String moiblephone = msg.obj.toString();
                        Vibrator mVibrator = (Vibrator) conext.getSystemService(Service.VIBRATOR_SERVICE);
                        mVibrator.vibrate(200);

                        conext.showOcrResultDialog(moiblephone);
                        conext.etMobileValue.setText(moiblephone);
                        conext.etMobileValue.setSelection(moiblephone.length());
//                        conext.llPhone.setVisibility(View.GONE);
                        conext.setViewShow(true, R.id.tv_tip_edit_express, R.id.tv_tip_capture);
                }
            }
        }
    }


    private enum State {
        scanning,
        capturing,
        editing
    }

    private ICaseBack<String> iCaseBack = new ICaseBack<String>() {
        @Override
        public void onSuccess(String result) {

        }

        @Override
        public void onError(@NotNull String error) {
            DialogUtil.promptDialog(activity, error);
            updateBottomCount();

        }
    };


    private ICaseBack<String> iCaseBackPickCode = new ICaseBack<String>() {
        @Override
        public void onSuccess(String result) {
            if (activity != null) {
                PickupCode pickupCode = storageCase.getCurrentPickCode();
                tv_pickup_code.setText(pickupCode.getCurrentNumber());
            }
        }

        @Override
        public void onError(@NotNull String error) {
            if (activity != null) {
                DialogUtil.promptDialog(activity, error, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }

        }
    };


    @Override
    public int getLayoutId() {
        return R.layout.act_scan_storage_ml;
    }


    public Analyzer<List<Barcode>> createAnalyzer() {
        tzScanAnalyzer = new TzScanAnalyzer();
        tzScanAnalyzer.setOcrRecognizeCallback(this);

        return tzScanAnalyzer;
    }


    @Override
    public CameraScan<List<Barcode>> createCameraScan(PreviewView previewView) {

        cameraScan = new SHBaseCameraScan(this, previewView);
        return cameraScan;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(this);

        activity = this;
        storageCase = ScanStorageCase.getInstance();
        storageCase.init(this);

        state = State.scanning;

        initPermission();
        initView();
        initCapture();
        initData();

//        printHelper = PrintHelper.INSTANCE;
//        printHelper.requestPermission(this, 1002);


//        printHelper.connectBluetooth();


        // 对未上传的图片进行上传
        List<ScanImage> list = storageCase.getScanImageList(ScanImage.State.uploading);
        storageCase.retryUploadImage(list);
    }

    //点击空白处，EditText隐藏
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描框及扫描线布局
     */
    private void layoutRectAndScanLineView() {
        if (rectFindView != null) {
            removeOcrFindView();
        }

        ocrTypeHelper = new OcrTypeHelper(OcrTypeHelper.OCR_TYPE_MOBILE, OcrTypeHelper.SCREENT_VERTICAL).getOcr();
        ocrTypeHelper.ocrTypeName = "请对准收件人手机号码";
        ocrTypeHelper.nameTextSize = 16;
        rectFindView = new ERectFindView(this, ocrTypeHelper);
        rectFindView.grayMarginTop = 55;//蒙层
        rectFindView.grayMarginBottom = 120;
        rootLayout.addView(rectFindView);

        scan_line_width = (int) (ocrTypeHelper.width * screenWidth);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scan_line_width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 0;
        scanHorizontalLineImageView.setLayoutParams(layoutParams);

        tzScanAnalyzer.setRectFindView(rectFindView);
    }

    /**
     * 动画销毁 移除取景框
     */
    private void removeOcrFindView() {
        if (rectFindView != null) {
            rectFindView.destroyDrawingCache();
            rootLayout.removeView(rectFindView);
            rectFindView = null;
            tzScanAnalyzer.setRectFindView(null);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        iCaseBack = null;
        iCaseBackPickCode = null;
    }

    private void initPermission() {
        //请求Camera权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.i(TAG, "onRequestPermissionsResult granted");

                } else {
                    LogUtil.i(TAG, "onRequestPermissionsResult denied");
                    UtilDialog.showDialog(this, "请前往设置中开启摄像头权限");
                }
                break;
            default:
        }
    }


    private void initView() {
        rootLayout = findViewById(R.id.root_layout);
        scanHorizontalLineImageView = findViewById(R.id.camera_scanHorizontalLineImageView);
        tv_pickup_code = findViewById(R.id.tv_pickup_code);
        tv_bar_code = findViewById(R.id.tv_bar_code);
        tv_last_code_info = findViewById(R.id.tv_last_code_info);
        tv_total_number = findViewById(R.id.tv_total_number);
        tv_capture_bar_code = findViewById(R.id.tv_capture_bar_code);
        llPhone = findViewById(R.id.ll_phone);
        tvBtnOk = findViewById(R.id.tv_btn_ok);
        etMobileValue = findViewById(R.id.et_mobile_value);
        etMobileValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString().trim();
                if (!temp.isEmpty()) {
                    if (temp.startsWith("95013") && temp.length() > 8 && temp.length() <= 17) {
                        tvBtnOk.setEnabled(true);
                    } else if (temp.startsWith("1") && temp.length() == 11) {
                        tvBtnOk.setEnabled(true);
                    } else {
                        tvBtnOk.setEnabled(false);
                    }
                } else {
                    tvBtnOk.setEnabled(false);
                }
            }
        });


        findViewById(R.id.iv_pickup_code_modify).setOnClickListener(this);
        findViewById(R.id.tv_tip_edit_express).setOnClickListener(this);
        findViewById(R.id.tv_btn_submit).setOnClickListener(this);
        findViewById(R.id.tv_btn_ok).setOnClickListener(this);
        findViewById(R.id.rl_back).setOnClickListener(this);
        findViewById(R.id.iv_set_ocr).setOnClickListener(v -> {
//            new XPopup.Builder(activity)
//            .asCustom(new SetOcrVerifyPopupView(activity)).show();
//TODO  SetScanPrintActivity
//            SetScanPrintActivity.Companion.openActivity(activity, true, false);

        });


//        Point screenSize = new ScreenInfo().getScreenSize();
//        srcWidth = screenSize.x;
//        srcHeight = screenSize.y;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        flashlightView = findViewById(R.id.ivTorch);
        flashlightView.setOnClickListener(v -> toggleTorchState());

        findViewById(R.id.bt_private_pack).setOnClickListener(v -> {
            // 隐私号码入库
            submitExpressInfo("");


            restartScan();
            removeOcrFindView();


        });
        findViewById(R.id.bt_cancel).setOnClickListener(v -> {
            //取消OCR识别 重置状态

            currentPhone = "";
            currentBarCode = "";

            restartScan();
            removeOcrFindView();


        });


    }

    private void initData() {
        ocrHandler = new OCRHandler(this);
        int size = storageCase.getCurrentImageSize();
        if (size > 0) {
            // 当前批次未提交
            PickupCode pickupCode = storageCase.getCurrentPickCode();
            if (pickupCode != null) {
                tv_pickup_code.setText(pickupCode.getCurrentNumber());
            }
        } else {
            // 已提交，需要同步服务器
            StorageCase.INSTANCE.httpRestorePickCode(iCaseBackPickCode);
        }

        ScanImage scanImage = storageCase.getLastScanImage();

        if (scanImage != null) {
            //最后入库：取件码 A1-29-20000411 | 快递单号7238283772747737
            tv_last_code_info.setText(String.format("最后入库：取件码 %s | 快递单号 %s",
                    scanImage.getPickCode(), scanImage.getEId()));
        } else {
            tv_last_code_info.setVisibility(View.GONE);
        }
    }

    private void initCapture() {
        cameraScan.setPlayBeep(true);
//        helper.fullScreenScan(true)
//                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
//                .decodeFormats(EnumSet.of(BarcodeFormat.CODE_128))//设置只识别二维码会提升速度
//                .frontLightMode(FrontLightMode.AUTO)//设置闪光灯模式
//                .tooDarkLux(45f)//设置光线太暗时，自动触发开启闪光灯的照度值
//                .brightEnoughLux(100f)//设置光线足够明亮时，自动触发关闭闪光灯的照度值
//                .continuousScan(false);//是否连扫

        cameraScan.bindFlashlightView(flashlightView);
//        cameraScan.setBrightLightLux()
    }


    /**
     * 切换闪光灯状态（开启/关闭）
     */
    protected void toggleTorchState() {
        if (getCameraScan() != null) {
            boolean isTorch = getCameraScan().isTorchEnabled();
            getCameraScan().enableTorch(!isTorch);
            if (flashlightView != null) {
                flashlightView.setSelected(!isTorch);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateBottomCount();
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentBarCode = "";
    }

    /**
     * 扫码结果回调
     *
     * @param //result 扫码结果
     */
    @Override
    public void onScanResultCallback(@NonNull AnalyzeResult<List<Barcode>> analyzeResult) {
        // 暂停识别 条码
        cameraScan.setAnalyzeImage(false);

        List<Barcode> barcodeList = analyzeResult.getResult();
        String result = "";


        for (Barcode barcode : barcodeList) {
            String str = barcode.getDisplayValue();
            if (StringUtil.isMatchExpressCode(str)) {
                // 提取 条码
                result = str;
                break;
            }
        }

        if (!StringUtil.isMatchExpressCode(result)) {
            // 空串 重新识别
            cameraScan.setAnalyzeImage(true);
            return;
        }

//        if (barCodeSanRepeatTime < 3) {
//            barCodeSanRepeatTime++;
//            cameraScan.restartPreviewAndDecode();
//            return true;
//        }

        if (result.equals(currentBarCode)) {
            if (currentBarCodeScanImageFromDb == null) {
                currentBarCodeScanImageFromDb = storageCase.searchScanImageFromDb(result);
            }

            if (currentBarCodeScanImageFromDb != null) {

                tv_bar_code.setText("重复：" + result);
                if (tv_bar_code.getVisibility() != View.VISIBLE) {
                    tv_bar_code.setVisibility(View.VISIBLE);
                    SoundHelper.getInstance().playNotifiRepeatSound();
                    tv_bar_code.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (tv_bar_code != null) {
                                tv_bar_code.setVisibility(View.GONE);
                            }
                        }
                    }, 1500);
                }
            }

            cameraScan.setAnalyzeImage(true);
            return;
        }


        barCodeSanRepeatTime = 0;
        currentBarCode = result;

        currentBarCodeScanImageFromDb = storageCase.searchScanImageFromDb(result);
        if (currentBarCodeScanImageFromDb != null) {
            cameraScan.setAnalyzeImage(true);
            SoundHelper.getInstance().playNotifiRepeatSound();
            return;
        }

        Bitmap bitmap = analyzeResult.getBitmap();


        Disposable disposable = storageCase.httpQueryExpress(result).subscribe(new Consumer<BaseResult<ExpressCompany>>() {
            @Override
            public void accept(BaseResult<ExpressCompany> baseResult) throws Exception {
                ExpressCompany expressCompany = baseResult.getData();
                if (tv_bar_code != null && expressCompany != null) {
                    currentExpress = expressCompany;

                    Compress compress = Compress.Companion.with(activity, bitmap);

                    Disposable disposable = storageCase.getBitmap(compress)
                            .subscribe(cvData -> {
                                // 播报快递公司
                                SoundHelper.getInstance().playExpress(currentExpress.getExpress_id());
//                                if (BuildConfig.DEBUG) {
//                                    tv_pickup_code.setText(cvData.getScore() + "");
//                                    LogUtil.d("opencv", cvData.getScore() + "");
//                                    showImage(cvData.getBitmap(), currentBarCode);
//                                }
                                openCVData = cvData;
                                /*条码识别成功后，开启手机号码OCR识别*/

                                layoutRectAndScanLineView();
                                llPhone.setVisibility(View.VISIBLE);
                                setViewShow(false, R.id.tv_tip_edit_express, R.id.tv_tip_capture);
                                setViewShow(true, R.id.bt_private_pack, R.id.bt_cancel);
                                tzScanAnalyzer.setOpenRecog(true);
                                cameraScan.setAnalyzeImage(true);
                                /*if (cvData.isValid()) {
                                    httpSubmit(currentBarCode, currentExpress.getExpress_id(), cvData);

                                } else {
                                    //ToastUtil.showLong("请重拍");
                                    retryCaptureDialog(cvData.getScore(), false);
                                }*/

                            }, Throwable::printStackTrace);


                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ToastUtil.showShort(throwable.getMessage());
                // 重置状态
                if (tv_bar_code != null) {
                    currentBarCode = "";
//                    cameraScan.setAnalyzeImage(true);
                    restartScan();
                }
            }
        });

    }


    private void takePicture() {

        cameraScan.setAnalyzeImage(false);


        String imgFile = SHCameraHelp.createFileDir(this, currentBarCode);
        File file = new File(imgFile);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        cameraScan.getImageCapture().takePicture(outputFileOptions,
                ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri();

                        if (savedUri == null) {
                            savedUri = Uri.fromFile(file);
                        }

                        Compress compress = Compress.Companion.with(activity, file);

                        Disposable disposable = storageCase.getBitmap(compress)
                                .subscribe(cvData -> {
//                            SoundHelper.getInstance().playExpress(currentExpress.getExpress_id());
                                    /*if (BuildConfig.DEBUG) {
                                        tv_pickup_code.setText(cvData.getScore() + "");
                                        LogUtil.d("opencv", cvData.getScore() + "");

                                        showImage(cvData.getBitmap(), "");
                                    }*/

                                    if (cvData.isValid()) {
                                        httpSubmit(currentBarCode, currentExpress.getExpress_id(), cvData);
                                    } else {
                                        //ToastUtil.showLong("请重拍");
                                        retryCaptureDialog(cvData.getScore(), false);
                                    }

                                }, e -> {
                                });

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        LogUtils.e("Photo capture failed: " + exception.getMessage(), exception);
                    }
                });


    }

    private void httpSubmit(String eId, int express_id, OpenCVData cvData) {

        PickupCode pickupCode = storageCase.getCurrentPickCode();
        if (pickupCode == null) {
            DialogUtil.promptDialog(activity, "无取件码，操作失败");
            return;
        }

        // 根据规则，生成真正的取件码
        String codeStr = pickupCode.createRealPickCode(eId, currentPhone);

        PickupCode nextCode = pickupCode.nextPickCode();
        updateUI(eId, codeStr, nextCode.getCurrentNumber());
        storageCase.saveScanImage(eId, pickupCode, cvData, null, express_id + "", iCaseBack);

        storageCase.updatePickCode(nextCode);
        updateBottomCount();

        //打印
//        printHelper.checkPrintPickCode(eId, pickupCode.createRealPickCode(eId, currentPhone));

        tv_bar_code.setText(eId);
        if (tv_bar_code.getVisibility() != View.VISIBLE) {
            tv_bar_code.setVisibility(View.VISIBLE);
            tv_bar_code.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tv_bar_code != null) {
                        tv_bar_code.setVisibility(View.GONE);
                    }
                    barCodeSanRepeatTime = 0;
                    currentBarCode = "";
                }
            }, 1500);
        }

//        Camera camera = (Camera) cameraScan.getCamera();
//        camera.startPreview();
//
//        cameraScan.restartPreviewAndDecode();
        cameraScan.setAnalyzeImage(true);


    }

    private void httpSubmit(String result, int express_id, String mobile, OpenCVData cvData) {

        PickupCode pickupCode = storageCase.getCurrentPickCode();
        if (pickupCode == null) {
            DialogUtil.promptDialog(activity, "无取件码，操作失败");
            return;
        }

        // 根据规则，生成真正的取件码
        String curCode = pickupCode.createRealPickCode(result, mobile);

        PickupCode nextCode = pickupCode.nextPickCode();
        updateUI(result, curCode, nextCode.getCurrentNumber());
        storageCase.saveScanImage(result, pickupCode, cvData, mobile, express_id + "", iCaseBack);


        //打印
//        printHelper.checkPrintPickCode(result, pickupCode.createRealPickCode(result, mobile));

        storageCase.updatePickCode(nextCode);
        updateBottomCount();

        tv_bar_code.setText(result);
        if (tv_bar_code.getVisibility() != View.VISIBLE) {
            tv_bar_code.setVisibility(View.VISIBLE);
            tv_bar_code.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tv_bar_code != null) {
                        tv_bar_code.setVisibility(View.GONE);
                    }
                    barCodeSanRepeatTime = 0;
                    /*解决在连续快速识别过程中，运单号为空的问题。
                    原因：接口请求回调是异步，且接口回调完成时间可能
                    在下一次扫码成功之后，在此置空，会覆盖扫码结果*/
//                    currentBarCode = "";
                }
            }, 1500);
        }

//        Camera camera = (Camera) cameraScan.getCamera();
//        camera.startPreview();
//
//        cameraScan.restartPreviewAndDecode();

        currentPhone = "";
        etMobileValue.setText("");
        cameraScan.setAnalyzeImage(true);


    }

    private void retryCaptureDialog(double score, boolean isEdit) {
        new XPopup.Builder(this)
                .asConfirm("提示", "照片不清晰，请重拍(" + score + ")", "取消", "重拍", () -> {
                    // 重拍
                    String showTips = currentExpress.getExpress_name() + "\n" + currentBarCode;
                    if (isEdit) {
                        showTips = currentBarCode + "\n手机号:" + currentPhone;
                    }
                    tv_capture_bar_code.setText(showTips);

//                    Camera camera = cameraScan.getCameraManager().getOpenCamera().getCamera();
//                    camera.startPreview();

                    cameraScan.setAnalyzeImage(true);

                    setViewShow(true, R.id.rl_capture);
                    setViewShow(false, R.id.tv_tip_edit_express, R.id.tv_tip_capture);
                }, () -> {
                    // 取消
                    currentBarCode = "";
                    barCodeSanRepeatTime = 0;
//                    Camera camera = cameraScan.getCameraManager().getOpenCamera().getCamera();
//                    camera.startPreview();
//
//                    cameraScan.restartPreviewAndDecode();

                    cameraScan.setAnalyzeImage(true);

                }, false).show();
    }

    private void updateUI(String barCode, String pickCode, String nextCode) {
        //最后入库：取件码 A1-29-20000411 | 快递单号7238283772747737
        tv_last_code_info.setText(String.format("最后入库：取件码 %s | 快递单号 %s", pickCode, barCode));

        tv_pickup_code.setText(nextCode);
    }

    private void updateBottomCount() {
        int size = storageCase.getCurrentImageSize();
        tv_total_number.setText(size + "");
    }


    @Override
    public void onClick(View view) {
        if (AntiShakeUtils.isInvalidClick(view)) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_pickup_code_modify:
                // 设置取件码
                PickupCode pickupCode = storageCase.getCurrentPickCode();
                if (pickupCode != null) {
                    //SetPickupCodeTypeActivity.openActivity(this, REQUEST_CODE_SET_PICK_UP_NUMBER, pickupCode);
                    SetPickCodeActivity.Companion.openActivity(this, REQUEST_CODE_SET_PICK_UP_NUMBER, pickupCode);
                }
                break;
            case R.id.tv_tip_edit_express:
                // 手动输入入库
                cameraScan.setAnalyzeImage(false);
                openEditDialog();
                break;
            case R.id.tv_btn_submit:
                // 底部提交入库
                if (storageCase.isAllImageUploaded()) {
                    ScanOcrResultActivity.openActivity(this);
                } else {
                    ScanImageUploadingActivity.openActivity(this);
                }
                break;
            case R.id.tv_btn_ok:
                // OCR 识别，底部手动输入手机号，提交
                restartScan();
                removeOcrFindView();
                llPhone.setVisibility(View.GONE);
                submitExpressInfo(etMobileValue.getText().toString().trim());

                break;
            case R.id.iv_capture:
                saveEditExpress(currentBarCode, currentPhone, currentExpress.getExpress_id() + "");
                break;
            case R.id.iv_capture_phone:
                takePicture();
                break;
            case R.id.iv_close_edit:
                closeEditDialog();
                resetScan();
                break;
            case R.id.rl_back:
                finish();
                break;
            default:
        }
    }

    private void submitExpressInfo(String phone) {
        if (openCVData.isValid()) {
            httpSubmit(currentBarCode, currentExpress.getExpress_id(), phone, openCVData);

        } else {
            //ToastUtil.showLong("请重拍");
            retryCaptureDialog(openCVData.getScore(), false);
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


    private void openEditDialog() {
        state = State.editing;
        tv_capture_bar_code.setText("");
        EditDialogView dialog = new EditDialogView(this);
        BasePopupView popupView = new XPopup.Builder(this)
                .setPopupCallback(new SimpleCallback() {
//                    @Override
//                    public void onDismiss(BasePopupView basePopupView) {
//                        if (TextUtils.isEmpty(tv_capture_bar_code.getText().toString())) {
//                            closeEditDialog();
//                        }
//                    }


                    @Override
                    public void onDismiss() {
                        if (TextUtils.isEmpty(tv_capture_bar_code.getText().toString())) {
                            closeEditDialog();
                        }
                    }
                })
                .popupAnimation(ScaleAlphaFromCenter)
                .autoOpenSoftInput(false)
                .dismissOnTouchOutside(false)
                .autoFocusEditText(false)
                .asCustom(dialog)
                .show();
        setViewShow(true, R.id.rl_capture);
        setViewShow(false, R.id.tv_tip_edit_express, R.id.tv_tip_capture);
    }

    public void closeEditDialog() {
        if (activity.isDestroyed()) {
            return;
        }
        // setViewShow(false, R.id.rl_capture, R.id.rl_express_info_tips);
        setViewShow(false, R.id.rl_capture);
        setViewShow(true, R.id.tv_tip_edit_express, R.id.tv_tip_capture);
        state = State.scanning;

    }

    private void resetScan() {
//        currentBarCode = "";
        barCodeSanRepeatTime = 0;
        state = State.scanning;
//        Camera camera = cameraScan.getCameraManager().getOpenCamera().getCamera();
//        camera.startPreview();
//        cameraScan.restartPreviewAndDecode();
        cameraScan.setAnalyzeImage(true);
    }

    private void setViewShow(boolean show, int... viewIds) {
        for (int id : viewIds) {
            findViewById(id).setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 手动录入快递信息 保存
     *
     * @param barCode          运单号
     * @param phone            手机号
     * @param expressCompanyId 快递公司Id
     */
    private void saveEditExpress(String barCode, String phone, String expressCompanyId) {
        ScanImage scanImage = storageCase.searchScanImageFromDb(barCode);
        if (scanImage != null) {
            SoundHelper.getInstance().playNotifiRepeatSound();
            return;
        }


        String imgFile = SHCameraHelp.createFileDir(this, currentBarCode);
        File file = new File(imgFile);

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file)
                .build();

        cameraScan.getImageCapture().takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();

                Compress compress = Compress.Companion.with(activity, file);

                Disposable disposable = storageCase.getBitmap(compress)
                        .subscribe(cvData -> {
                            SoundHelper.getInstance().playExpress(currentExpress.getExpress_id());
                            /*if (BuildConfig.DEBUG) {
                                tv_pickup_code.setText(cvData.getScore() + "");
                                LogUtil.d("opencv", cvData.getScore() + "");

                                showImage(cvData.getBitmap(), "");
                            }*/

                            if (cvData.isValid()) {
                                PickupCode pickupCode = storageCase.getCurrentPickCode();
                                if (pickupCode == null) {
                                    DialogUtil.promptDialog(activity, "无取件码，操作失败");
                                    return;
                                }
                                String codeStr = pickupCode.createRealPickCode(barCode, phone);
                                PickupCode nextCode = pickupCode.nextPickCode();
                                storageCase.saveScanImage(barCode, pickupCode, cvData, phone, expressCompanyId, iCaseBack);
                                //打印
//                                printHelper.checkPrintPickCode(barCode, pickupCode.createRealPickCode(barCode, phone));
                                storageCase.updatePickCode(nextCode);
                                updateUI(barCode, codeStr, nextCode.getCurrentNumber());
                                updateBottomCount();
                                currentBarCode = "";
                                cameraScan.setAnalyzeImage(true);
                            } else {
                                retryCaptureDialog(cvData.getScore(), true);
                            }


                        }, e -> {
                        });

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                LogUtils.e("Photo capture failed: " + exception.getMessage(), exception);
            }
        });

        closeEditDialog();
    }

    class EditDialogView extends CenterPopupView {

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
            etExpressCode = findViewById(R.id.et_tracking_number_value);
            EditText etPhone = findViewById(R.id.et_phone_value);

            tv_tracking_company_value = findViewById(R.id.tv_tracking_company_value);
            tv_tracking_company_value.setOnClickListener(v -> {
                KeyboardUtils.hideSoftInput(v);
                if (mExpressCompanies == null) {
                    getExpressCompany();
                } else {
                    showExpressCompanies();
                }
            });

            findViewById(R.id.btn_cancel).setOnClickListener(v -> {
                        KeyboardUtils.hideSoftInput(v);
                        dismiss();
                        closeEditDialog();
                        resetScan();
                    }
            );
            findViewById(R.id.btn_ok).setOnClickListener(v -> {


                        if (currentExpress == null) {
                            ToastUtil.showShort("请选择快递公司");
                            return;
                        }

                        String expressCode = etExpressCode.getText().toString().trim();
                        if (TextUtils.isEmpty(expressCode)) {
                            ToastUtil.showShort(getResources().getString(R.string.input_express_code));
                            return;
                        }
                        String phone = etPhone.getText().toString().trim();
                        if (!StringUtil.isMobile(phone) && !StringUtil.is95013Num(phone)) {
                            ToastUtil.showShort(getResources().getString(R.string.phone_format_error));
                            return;
                        }

                        KeyboardUtils.hideSoftInput(EditDialogView.this);

                        currentBarCode = expressCode;
                        currentPhone = phone;
                        String showTips = currentBarCode + "\n号码:" + currentPhone;
                        tv_capture_bar_code.setText(showTips);
                        setViewShow(true, R.id.rl_express_info_tips);
                        dismiss();
                    }
            );

        }
    }


    /**
     * 显示快递公司列表
     */
    private void showExpressCompanies() {


        new XPopup.Builder(this)
                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                .asCustom(getCustomExpressCompanyPopup(mExpressCompanies)/*.enableDrag(false)*/)
                .show();
    }

    private CustomExpressCompanyPopup getCustomExpressCompanyPopup(ArrayList<ExpressCompany> list) {
        CustomExpressCompanyPopup popup = new CustomExpressCompanyPopup(this, list);
        popup.setOnItemClickListener(new CustomExpressCompanyPopup.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                currentExpress = list.get(position);
                tv_tracking_company_value.setText(currentExpress.getExpress_name());


            }
        });
        return popup;
    }


    /**
     * 获取快递公司列表
     */
    private void getExpressCompany() {
        storageCase.httpGetExpressCompany().subscribe(new Observer<BaseResult<ArrayList<ExpressCompany>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                LoadDialogUtils.showLoadingDialog(ScanStorageActivity_ML.this);
            }

            @Override
            public void onNext(BaseResult<ArrayList<ExpressCompany>> baseResult) {
                LogUtil.d("stringBaseResult", baseResult.getData().toString());
                mExpressCompanies = baseResult.getData();
                if (tv_tracking_company_value != null) {
                    showExpressCompanies();
                }
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showShort(e.getMessage());
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onComplete() {
                LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }


    BaseLoaderCallback loaderCallback;

    @Override
    public void onResume() {
        super.onResume();

        if (loaderCallback == null) {
            loaderCallback = new BaseLoaderCallback(this) {

            };
        }

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, loaderCallback);
        } else {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public byte[] convertImageToByte(Uri uri) {
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void restartScan() {
        setViewShow(true, R.id.tv_tip_edit_express, R.id.tv_tip_capture);
        setViewShow(false, R.id.bt_private_pack, R.id.bt_cancel);
        llPhone.setVisibility(View.GONE);
        ocrHandlering = false;
        cameraScan.setAnalyzeImage(true);
        tzScanAnalyzer.setOpenRecog(false);
    }


    /**
     * 弹窗显示ocr识别结果
     */
    private void showOcrResultDialog(String mobilePhone) {
//        boolean needVerify = SharedPreferencesUtil.getBoolean("needVerify", true);
//        if(!needVerify){
//            // 不需要弹窗进行手机号码校验,直接提交
//            submitExpressInfo(mobilePhone);
//            restartScan();
//            removeOcrFindView();
//
//            return;
//        }
//
//
//        ExpressInfoPopView expressInfoPopView = new ExpressInfoPopView(this, currentExpress.getExpress_name(), currentBarCode, mobilePhone);
//        expressInfoPopView.setOnDisMiss(() -> {
//            ocrHandlering = false;
//            etMobileValue.setText("");
//            currentPhone = "";
//            llPhone.setVisibility(View.VISIBLE);
//            setViewShow(false, R.id.tv_tip_edit_express, R.id.tv_tip_capture);
//            // ocr重新识别
//            tzScanAnalyzer.setOpenRecog(true);
//            cameraScan.setAnalyzeImage(true);
//        });
//        expressInfoPopView.setOnConfirm((phone) -> {
//            submitExpressInfo(phone);
//            restartScan();
//            removeOcrFindView();
//
//        });
//        new XPopup.Builder(this)
//                .dismissOnTouchOutside(false)
//                .asCustom(expressInfoPopView)
//                .show();
    }

    /**
     * 显示 开源框架OCR识别结果
     *
     * @param bitmap
     * @param info
     */
    private void showImage(Bitmap bitmap, String info) {
        Bitmap clipBitmap = getClipBitmap(bitmap);
        ImagePopView imagePopView = new ImagePopView(this, clipBitmap, currentExpress.getExpress_name(), currentBarCode);
        imagePopView.setOnDisMiss(() -> {
        });
        imagePopView.setOnConfirm(() -> {
            currentBarCode = "";
            cameraScan.setAnalyzeImage(true);
        });
        BasePopupView popupView = new XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .asCustom(imagePopView)
                .show();
    }

    /**
     * 裁剪出 包含手机号码的敏感区域图片
     *
     * @param bitmap
     * @return
     */
    private Bitmap getClipBitmap(Bitmap bitmap) {
        float densityX = (float) bitmap.getWidth() / previewView.getWidth();
        float densityY = (float) bitmap.getHeight() / previewView.getHeight();

        int x = (int) (ocrTypeHelper.leftPointX * screenWidth * densityX);
        int y = (int) (ocrTypeHelper.leftPointY * screenHeight * densityY);
        int w = (int) ((ocrTypeHelper.width) * screenWidth * densityX);
        int h = (int) ((ocrTypeHelper.height) * screenHeight * densityY);
        return BitmapUtil.cropBitmap(bitmap, x, y, w, h);
    }
}
