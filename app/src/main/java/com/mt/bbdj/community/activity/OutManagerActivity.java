package com.mt.bbdj.community.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.king.zxing.util.ResizeAbleSurfaceView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.ActivityBase;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.WaillMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.db.gen.WaillMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.RxConstants;
import com.mt.bbdj.baseconfig.utls.RxDataTool;
import com.mt.bbdj.baseconfig.utls.RxDialogSure;
import com.mt.bbdj.baseconfig.utls.RxSPTool;
import com.mt.bbdj.baseconfig.utls.RxToast;
import com.mt.bbdj.baseconfig.utls.SensorController;
import com.mt.bbdj.baseconfig.utls.SoftKeyBoardListener;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.baseconfig.view.RxAnimationTool;
import com.mt.bbdj.baseconfig.view.camera.CameraHelper;
import com.mt.bbdj.baseconfig.view.camera.CameraListener;
import com.mt.bbdj.baseconfig.view.camera.PlanarYUVLuminanceSource;
import com.mt.bbdj.baseconfig.view.glsurface.RoundTextureView;
import com.mt.bbdj.community.adapter.EnterManagerAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.rxfeature.module.scaner.CameraConfigurationManager;
import com.rxfeature.module.scaner.CameraManager;
import com.rxfeature.module.scaner.OnRxScanerListener;
import com.rxfeature.module.scaner.decoding.InactivityTimer;
import com.rxfeature.tool.RxQrBarTool;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.ContentValues.TAG;


public class OutManagerActivity extends ActivityBase implements ViewTreeObserver.OnGlobalLayoutListener, CameraListener {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private RelativeLayout ivBack;    //返回
    private TextView tv_enter_number;     //入库数
    private TextView tv_out_number;
    private List<HashMap<String, String>> mList = new ArrayList<>();
    private List<HashMap<String, String>> mPrintList = new ArrayList<>();
    private List<String> mTempList = new ArrayList<>();//临时数据

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;

    private ExpressLogoDao mExpressLogoDao;

    private final int CHECK_WAY_BILL_STATE = 100;    //检测
    private final int OUT_WAY_BILL_REQUEST = 400;    //出库

    private String resultNumber;
    private String expressName;
    private PrintTagModel printTagModel = new PrintTagModel();
    private String rootPaht = Environment.getExternalStorageDirectory() + "/bbdj/barcode/";

    private int tagNumber = 1;

    /**
     * 扫描结果监听
     */
    private static OnRxScanerListener mScanerListener;

    /**
     * 扫描处理
     */
    private CaptureActivityHandler handler;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;


    /**
     * 扫描结果显示框
     */
    private RxDialogSure rxDialogSure;

    private WaillMessageDao mWaillMessageDao;
    private String wayNumber = "";

    private String station_id = "";    //驿站id
    private String courier_id = "";    //快递员id
    private String express_id = "";    //快递公司id
    private RelativeLayout rl_scan;
    private TextView tv_phone;
    private TextView tv_yundan;
    private TextView tv_out;
    private boolean isAdd;
    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司


    private DisplayMetrics dm = new DisplayMetrics();
    private boolean mIsStop;
    private File testFile;
    private RoundTextureView textureView;
    private CameraHelper cameraHelper;
    private Camera.Size previewSize;
    private MyPopuwindow popupWindow;
    private int package_code = 0;


    /**
     * 设置扫描信息回调
     */
    public static void setScanerListener(OnRxScanerListener scanerListener) {
        mScanerListener = scanerListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_manager);
        //界面控件初始化
        initDecode();
        initParams();
        initView();
        //权限初始化
        initPermission();
        //扫描动画初始化
        initScanerAnimation();
        initListener();
        initScanPhoneNumber();
        if (cameraHelper != null) {
            cameraHelper.start();
        }
    }

    private void initScanPhoneNumber() {
        //初始化相机参数
        textureView = findViewById(R.id.texture_preview);
        textureView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }


    private void initListener() {
        //返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                mList.remove(mList.get(position));
                mTempList.remove(mTempList.get(position));
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + mList.size()+")");
            }
        });

        tv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() == 0) {
                    ToastUtil.showShort("没有出库数据！");
                    return;
                }
                tv_out.setEnabled(false);
                outOfrepertory();
            }
        });
    }


    private void outOfrepertory() {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String,String> map : mList) {
            String pie_id = map.get("pie_id");
            sb.append(pie_id);
            sb.append(",");
        }
        String result = sb.toString();
        String effectionResult = result.substring(0,result.length()-1);
        Request<String> request = NoHttpRequest.outOfRepertoryRequest(user_id, effectionResult);
        mRequestQueue.add(OUT_WAY_BILL_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(OutManagerActivity.this);

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "OutManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    outofRepertoryResult(jsonObject);    //处理结果
                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_out.setEnabled(false);
                }
                LoadDialogUtils.cannelLoadingDialog();
                tv_out.setEnabled(false);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LoadDialogUtils.cannelLoadingDialog();
                tv_out.setEnabled(false);
            }

            @Override
            public void onFinish(int what) {
                LoadDialogUtils.cannelLoadingDialog();
                tv_out.setEnabled(false);
            }
        });
    }

    private void outofRepertoryResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();

        if ("5001".equals(code)) {
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }
        ToastUtil.showShort(msg);
        tv_out_number.setText("(" + mList.size() + ")");
    }


    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        mWaillMessageDao = daoSession.getWaillMessageDao();
        mExpressLogoDao = daoSession.getExpressLogoDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
        printTagModel = new PrintTagModel();

        String testFilePaht = Environment.getExternalStorageDirectory().getAbsolutePath() + "/abc/";
        testFile = new File(testFilePaht);
        if (!new File(testFilePaht).exists()) {
            testFile.mkdirs();
        }
    }

    private void initDecode() {
        multiFormatReader = new MultiFormatReader();
        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();

            Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector<BarcodeFormat>(5);
            PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
            PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
            PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
            PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
            // PRODUCT_FORMATS.add(BarcodeFormat.RSS14);
            Vector<BarcodeFormat> ONE_D_FORMATS = new Vector<BarcodeFormat>(PRODUCT_FORMATS.size() + 4);
            ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
            ONE_D_FORMATS.add(BarcodeFormat.ITF);
            Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector<BarcodeFormat>(1);
            QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
            Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<BarcodeFormat>(1);
            DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(ONE_D_FORMATS);
            decodeFormats.addAll(QR_CODE_FORMATS);
            decodeFormats.addAll(DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        multiFormatReader.setHints(hints);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraHelper != null) {
            cameraHelper.getmCamera().startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraHelper != null) {
            cameraHelper.stop();
        }
        if (handler != null) {
            handler.quitSynchronously();
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onDestroy() {
        //  inactivityTimer.shutdown();
        mScanerListener = null;
        if (cameraHelper != null) {
            cameraHelper.release();
        }
        super.onDestroy();
    }


    private void initView() {
        mCropLayout = findViewById(R.id.capture_crop_layout);
        rl_scan = findViewById(R.id.rl_scan);
        rl_scan.setVisibility(View.VISIBLE);
        tv_phone = findViewById(R.id.tv_phone);
        tv_yundan = findViewById(R.id.tv_yundan);
        tv_out = findViewById(R.id.tv_out);
        tvPackageCode = findViewById(R.id.tv_package_number);
        tvWailNumber = findViewById(R.id.tv_yundan);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);
        tv_enter_number = findViewById(R.id.tv_enter_number);
        tv_out_number = findViewById(R.id.tv_out_number);

        rl_scan.setVisibility(View.VISIBLE);
        ivBack = findViewById(R.id.iv_back);

        getWindowManager()
                .getDefaultDisplay().getMetrics(dm);

        initRecyclerView();    //初始化列表
    }

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        mAdapter = new EnterManagerAdapter(mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setAdapter(mAdapter);
    }

    private void initPermission() {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initScanerAnimation() {
        ImageView mQrLineView = findViewById(R.id.capture_scan_line);
        RxAnimationTool.ScaleUpDowm(mQrLineView);
    }


    private void initCamera() {
        cameraHelper = new CameraHelper.Builder()
                .cameraListener(this)
                .specificCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .previewOn(textureView)
                .setContext(this)
                .pictureSize(dm)
                .previewViewSize(new Point(textureView.getLayoutParams().width, textureView.getLayoutParams().height))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();
        cameraHelper.start();
        if (handler == null) {
            handler = new CaptureActivityHandler();
        }
    }

    //========================================打开本地图片识别二维码 end=================================

    public void handleDecode(Result result, String imagePath) {
        // inactivityTimer.onActivity();
        //扫描成功之后的振动与声音提示
        SoundHelper.getInstance().playNotifiSound();
        String result1 = result.getText();

        if (result1.length() < 7) {
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            return;
        }

        if (mList.size() == 30) {
            ToastUtil.showShort("超出数量限制！");
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            return;
        }

        if (isContain(result1)) {   //判断是否重复
            SoundHelper.getInstance().playNotifiRepeatSound();
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        } else {
            mTempList.add(result1);
            checkWaybillState(result1);  //获取运单号信息
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            tv_out_number.setText("(" + mList.size() + ")");
        }
    }

    private boolean isContain(String resultCode) {
        for (String data : mTempList) {
            if (resultCode.equals(data)) {
                return true;
            }
        }
        return false;
    }


    private void checkWaybillState(String number) {
//        Request<String> request = NoHttpRequest.checkOutWailnumberStateRequest(user_id, number);
//        mRequestQueue.add(CHECK_WAY_BILL_STATE, request, mOnresponseListener);
    }

    private OnResponseListener<String> mOnresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                handleResultForOut(what, jsonObject);    //处理结果
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void handleResultForOut(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case CHECK_WAY_BILL_STATE:    //运单状态
                CheckWaybillResult(jsonObject);
                break;
        }
    }

    private void CheckWaybillResult(JSONObject jsonObject) throws JSONException {

        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();

        if ("5001".equals(code)) {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            String package_code = dataObj.getString("code");
            String express = dataObj.getString("express");
            String pie_id = dataObj.getString("pie_id");
            String number = dataObj.getString("number");

            HashMap<String, String> map = new HashMap<>();
            map.put("package_code", package_code);
            map.put("wail_number", number);
            map.put("phone_number", express);
            map.put("pie_id", pie_id);
            mList.add(0, map);
            map = null;

            tvWailNumber.setText(number);
            tvPackageCode.setText(package_code);
            mAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showShort(msg);
        }
        if (handler != null) {
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
            handler.sendEmptyMessage(R.id.restart_preview);
        }
        tv_out_number.setText("(" + mList.size() + ")");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onGlobalLayout() {
        textureView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
        // int sideLength = Math.min(textureView.getWidth(), textureView.getHeight()) * 3 / 4;
        int sideLength = Math.min(textureView.getWidth(), textureView.getHeight());
        layoutParams.width = (int) (sideLength * 4 / 3);
        layoutParams.height = sideLength;
        textureView.setLayoutParams(layoutParams);
        textureView.turnRound();
        initCamera();
    }

    @Override
    public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
        previewSize = camera.getParameters().getPreviewSize();
        Log.i(TAG, "onCameraOpened:  previewSize = " + previewSize.width + "x" + previewSize.height);
        //在相机打开时，添加右上角的view用于显示原始数据和预览数据
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //将预览控件和预览尺寸比例保持一致，避免拉伸
                {
                    ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
                    //横屏
                    if (displayOrientation % 180 == 0) {
                        layoutParams.height = layoutParams.width * previewSize.height / previewSize.width;
                    }
                    //竖屏
                    else {
                        layoutParams.height = layoutParams.width * previewSize.width / previewSize.height;
                    }
                    textureView.setLayoutParams(layoutParams);
                }
            }
        });
    }

    @Override
    public void onPreview(byte[] data, Camera camera) {

    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onCameraError(Exception e) {

    }

    @Override
    public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

    }


    //==============================================================================================解析结果 及 后续处理 end

    final class CaptureActivityHandler extends Handler {

        DecodeThread decodeThread = null;
        public State state;

        public CaptureActivityHandler() {
            decodeThread = new DecodeThread();
            decodeThread.start();
            state = State.SUCCESS;
            restartPreviewAndDecode();
        }


        @Override
        public void handleMessage(Message message) {
            if (message.what == R.id.auto_focus) {
                if (state == State.PREVIEW) {
                    cameraHelper.requestAutoFocus(this, R.id.auto_focus);
                }
            } else if (message.what == R.id.restart_preview) {
                //显示扫描框
                rl_scan.setVisibility(View.VISIBLE);
                restartPreviewAndDecode();
            } else if (message.what == R.id.decode_succeeded) {
                state = State.SUCCESS;
                Result result = (Result) message.obj;
                Bitmap barcode_bitmap = (Bitmap) message.getData().get("barcode_bitmap");
                wayNumber = result.getText();
                boolean isRight = StringUtil.isDigit(wayNumber);

                if (isRight) {
                    handleDecode((Result) message.obj, "");// 解析成功，回调
                } else {
                    if (handler != null) {
                        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                        handler.sendEmptyMessage(R.id.restart_preview);
                    }
                }

            } else if (message.what == R.id.decode_failed) {
                state = State.PREVIEW;
                cameraHelper.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                //CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            }
        }

        public void quitSynchronously() {
            state = State.DONE;
            decodeThread.interrupt();
            //CameraManager.get().stopPreview();
            cameraHelper.stop();
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
            removeMessages(R.id.decode);
            removeMessages(R.id.auto_focus);
            removeMessages(R.id.phone_number);
        }

        private void restartPreviewAndDecode() {
            if (state == State.SUCCESS) {
                state = State.PREVIEW;
                cameraHelper.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                cameraHelper.requestAutoFocus(this, R.id.auto_focus);
            }
        }
    }

    final class DecodeThread extends Thread {

        private final CountDownLatch handlerInitLatch;
        private Handler handler;

        DecodeThread() {
            handlerInitLatch = new CountDownLatch(1);
        }

        Handler getHandler() {
            try {
                handlerInitLatch.await();
            } catch (InterruptedException ie) {
                // continue?
            }
            return handler;
        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new DecodeHandler();
            handlerInitLatch.countDown();
            Looper.loop();
        }
    }

    final class DecodeHandler extends Handler {
        DecodeHandler() {
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == R.id.decode) {
                decode((byte[]) message.obj, message.arg1, message.arg2);
            } else if (message.what == R.id.quit) {
                Looper.myLooper().quit();
            }
        }
    }

    private MultiFormatReader multiFormatReader;

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        //modify here
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        // Here we are swapping, that's the difference to #11
        int tmp = width;
        width = height;
        height = tmp;

        //    Bitmap phoneBitmap = getBitmap(data);

        PlanarYUVLuminanceSource source = cameraHelper.buildLuminanceSource(rotatedData, width, height);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException e) {
            // continue
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
            Bundle bundle = new Bundle();
            bundle.putParcelable("barcode_bitmap", source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            //Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget();
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }

    private enum State {
        //预览
        PREVIEW,
        //成功
        SUCCESS,
        //完成
        DONE,
        //手机
        PHONE
    }

}
