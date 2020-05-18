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
import com.mt.bbdj.baseconfig.utls.BitmapUtil;
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


public class EnterManagerActivity extends ActivityBase implements ViewTreeObserver.OnGlobalLayoutListener, CameraListener {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private RelativeLayout ivBack;    //返回
    private TextView tv_enter_number;     //入库数
    private TextView tv_enter;
    SensorController sensorControler;
    private LinearLayout ll_scan_phone_number, llFocus;
    private List<HashMap<String, String>> mPrintList = new ArrayList<>();
    private List<String> mTempList = new ArrayList<>();//临时数据

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;

    private List<HashMap<String, String>> mData = new ArrayList<>();
    private ExpressLogoDao mExpressLogoDao;

    private final int CHECK_WAY_BILL_STATE = 100;    //检测
    private final int ENTER_RECORDE_REQUEST = 200;    //入库
    private final int COMMIT_PICTURE_REQUEST = 300;    //上传图片
    private final int REQUEST_CODE_REQUEST = 400;    //提货码
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
    private boolean isAdd;
    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司


    private DisplayMetrics dm = new DisplayMetrics();
    private boolean mIsStop;
    private File testFile;
    private RoundTextureView textureView;
    private CameraHelper cameraHelper;
    private Camera.Size previewSize;
    private MyPopuwindow popupWindow;
    private int package_number = 0;


    /**
     * 设置扫描信息回调
     */
    public static void setScanerListener(OnRxScanerListener scanerListener) {
        mScanerListener = scanerListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_manager);
        //界面控件初始化
        initDecode();
        initParams();
        initView();
        requestPackageCode();     //获取最新的提货码
        //权限初始化
        initPermission();
        //扫描动画初始化
        initScanerAnimation();
        initListener();
        initScanPhoneNumber();

        expressSelect.post(new Runnable() {
            @Override
            public void run() {
                selectExpressDialog(expressSelect);
            }
        });

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

        //选择对话框
        expressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.size() != 0) {
                    ToastUtil.showShort("请先入库后再切换快递公司！");
                    return;
                }
                selectExpressDialog(v);
            }
        });

        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                HashMap<String, String> map = mData.get(position);
                String resultCode = map.get("wail_number");
                mWaillMessageDao.queryBuilder().where(WaillMessageDao.Properties.WailNumber.eq(resultCode)).buildDelete();
                mData.remove(mData.get(position));
                mTempList.remove(resultCode);
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + mData.size() + "/30)");
            }
        });

        //入库
        tv_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });


        //手动输入号码的监听
        tv_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = tv_phone.getText().toString();
                int legth = phoneNumber.length();
                if (legth == 11) {
                    String yundan = tv_yundan.getText().toString();
                    if (yundan.length() == 0) {
                        ToastUtil.showShort("运单号不可为空！");
                        return;
                    }

                    SystemUtil.hideKeyBoard(EnterManagerActivity.this, tv_yundan);
                    mData.get(mData.size() - 1).put("phone_number", phoneNumber);
                    tv_enter_number.setText("(" + mData.size() + "/30)");

                    tv_phone.setText("");
                    tvPackageCode.setText("");
                    tv_yundan.setText("");

                    mCropLayout.setVisibility(View.VISIBLE);
                    ll_scan_phone_number.setVisibility(View.GONE);
                    if (handler != null) {
                        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                        handler.sendEmptyMessage(R.id.restart_preview);
                    }
                }
            }
        });


       /* sensorControler.setCameraFocusListener(new SensorController.CameraFocusListener() {
            @Override
            public void onFocus() {
                Log.d(TAG, "onFocus");
                cameraHelper.start();
            }
        });*/
    }

    private void showConfirmDialog() {
        new CircleDialog.Builder()
                .setTitle("提示")
                .setText("\n请注意正式环境下的入库\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //入库
                        enterRecorde();
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }


    private void requestPackageCode() {
        Request<String> request = NoHttpRequest.getNewPackagerCodeFromHourse(user_id);
        mRequestQueue.add(REQUEST_CODE_REQUEST, request,mOnresponseListener);

    }


    private void enterRecorde() {
        if (mData.size() == 0) {
            ToastUtil.showShort("无可提交数据");
            return;
        }
        tv_enter.setEnabled(false);

        String data_json = getEnterrecordData();
        Request<String> request = NoHttpRequest.enterRecordeRequest(user_id, express_id, data_json);
        mRequestQueue.add(ENTER_RECORDE_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(EnterManagerActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        mData.clear();
                        tv_enter_number.setText("(" + 0 + "/30)");
                        tagNumber = 1;
                        mAdapter.notifyDataSetChanged();
                        LoadDialogUtils.cannelLoadingDialog();
                    }
                    ToastUtil.showShort(msg);
                } catch (JSONException e) {
                    ToastUtil.showShort("网络不稳定");
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                    tv_enter.setEnabled(true);
                }
                tv_enter.setEnabled(true);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LoadDialogUtils.cannelLoadingDialog();
                tv_enter.setEnabled(true);
            }

            @Override
            public void onFinish(int what) {
                LoadDialogUtils.cannelLoadingDialog();
                tv_enter.setEnabled(true);
            }
        });
    }

    private String getEnterrecordData() {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String, String> data : mData) {
            sb.append(data.get("package_code"));
            sb.append("|");
            sb.append(data.get("phone_number"));
            sb.append("|");
            sb.append(data.get("name"));
            sb.append("|");
            sb.append(data.get("wail_number"));
            sb.append("|");
            sb.append(data.get("type"));
            sb.append(",");
        }
        String sbb = sb.toString();
        String result = sbb.substring(0, sbb.length() - 1);
        return result;
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

       // sensorControler = SensorController.getInstance();    //聚焦控制
    }

    private void selectExpressDialog(View view) {
        popupWindow.showAsDropDown(view);
    }


    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.fast_layout_1, null);
            RecyclerView fastList = selectView.findViewById(R.id.tl_fast_list);
            initRecycler(fastList);
            popupWindow = new MyPopuwindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            selectView.findViewById(R.id.layout_left_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });
        }
    }


    private void initRecycler(RecyclerView fastList) {
        mFastData.clear();
        //查询快递公司的信息
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.States.eq(1)).list();
        if (expressLogoList != null && expressLogoList.size() != 0) {
            for (ExpressLogo expressLogo : expressLogoList) {
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("express", expressLogo.getExpress_name());
                map1.put("express_id", expressLogo.getExpress_id());
                mFastData.add(map1);
                map1 = null;
            }
        }

        SimpleStringAdapter goodsAdapter = new SimpleStringAdapter(this, mFastData);
        goodsAdapter.setOnItemClickListener(new SimpleStringAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //选中的快递公司id
                express_id = mFastData.get(position).get("express_id");
                expressName = mFastData.get(position).get("express");
                //sendExpressid(express_id);    //向对应的界面发送快递公司消息
                expressSelect.setText(mFastData.get(position).get("express"));
                popupWindow.dismiss();
            }
        });

        if (mFastData.size() != 0) {
            expressName = mFastData.get(0).get("express");
            express_id = mFastData.get(0).get("express_id");
        }

        fastList.setAdapter(goodsAdapter);
        fastList.addItemDecoration(new MarginDecoration(this));
        fastList.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter.notifyDataSetChanged();
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
        mIsStop = false;
      //  sensorControler.onStart();
        if (cameraHelper != null && cameraHelper.getmCamera()!=null) {
            cameraHelper.getmCamera().startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsStop = true;
        // sensorControler.onStop();
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

        mWaillMessageDao.deleteAll();
        List<WaillMessage> lists = new ArrayList<>();
        for (HashMap<String, String> map : mData) {
            String package_code = map.get("package_code");
            String wail_number = map.get("wail_number");
            String mobile = map.get("phone_number");
            String express_name = map.get("express_name");
            String name = map.get("name");

            WaillMessage waillMessage = new WaillMessage();
            waillMessage.setName(name);
            waillMessage.setExpressName(express_name);
            waillMessage.setTagCode(package_code);
            waillMessage.setMobile(mobile);
            waillMessage.setWailNumber(wail_number);
            waillMessage.setTagNumber(package_number);
            lists.add(waillMessage);
        }
        mWaillMessageDao.saveInTx(lists);

        super.onDestroy();
    }

    private ImageView image;


    private void initView() {
        mCropLayout = findViewById(R.id.capture_crop_layout);
        rl_scan = findViewById(R.id.rl_scan);
        rl_scan.setVisibility(View.VISIBLE);
        tv_phone = findViewById(R.id.tv_phone);
        tv_yundan = findViewById(R.id.tv_yundan);
        tv_enter = findViewById(R.id.tv_enter);
        image = findViewById(R.id.image);
        tvPackageCode = findViewById(R.id.tv_package_number);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);
        tv_enter_number = findViewById(R.id.tv_enter_number);

        ll_scan_phone_number = findViewById(R.id.ll_scan_phone_number);

        llFocus = findViewById(R.id.llFocus);
        rl_scan.setVisibility(View.VISIBLE);
        tv_enter = findViewById(R.id.tv_enter);
        ivBack = findViewById(R.id.iv_back);

        getWindowManager()
                .getDefaultDisplay().getMetrics(dm);

        initRecyclerView();    //初始化列表
        initSelectPop();     //选择框
    }

    private void initRecyclerView() {
        List<WaillMessage> dataList = mWaillMessageDao.queryBuilder().list();
        if (dataList != null && dataList.size() != 0) {
            for (WaillMessage waillMessage : dataList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("package_code", waillMessage.getTagCode());
                map.put("wail_number", waillMessage.getWailNumber());
                map.put("express_name", waillMessage.getExpressName());
                map.put("phone_number", waillMessage.getMobile());
                map.put("name", waillMessage.getName());
                map.put("type", "1");
                mData.add(map);
                mTempList.add(waillMessage.getWailNumber());
                map = null;
            }
        }
        tagNumber = dataList.size();
        recyclerView.setFocusable(false);
        mAdapter = new EnterManagerAdapter(mData);
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

    private Camera.Size getOptimalPictureSize(List<Camera.Size> pictureSizes) {
        Camera.Size pictureSize = null;
        for (int i = 0; i < pictureSizes.size(); i++) {
            pictureSize = pictureSizes.get(i);
            if (pictureSize.width == dm.widthPixels && pictureSize.height == dm.heightPixels) {
                return pictureSize;
            }
        }

        for (int i = 0; i < pictureSizes.size(); i++) {
            pictureSize = pictureSizes.get(i);
            if (pictureSize.width > dm.widthPixels && pictureSize.height > dm.heightPixels) {
                return pictureSize;
            }
        }
        return null;
    }

    //========================================打开本地图片识别二维码 end=================================


    private void initDialogResult(Result result) {
        BarcodeFormat type = result.getBarcodeFormat();
        String realContent = result.getText();

        if (rxDialogSure == null) {
            //提示弹窗
            rxDialogSure = new RxDialogSure(mContext);
        }

        if (BarcodeFormat.QR_CODE.equals(type)) {
            rxDialogSure.setTitle("二维码扫描结果");
        } else if (BarcodeFormat.EAN_13.equals(type)) {
            rxDialogSure.setTitle("条形码扫描结果");
        } else {
            rxDialogSure.setTitle("扫描结果");
        }

        rxDialogSure.setContent(realContent);
        rxDialogSure.setSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxDialogSure.cancel();
            }
        });

        rxDialogSure.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        });

        if (!rxDialogSure.isShowing()) {
            rxDialogSure.show();
        }

        RxSPTool.putContent(mContext, RxConstants.SP_SCAN_CODE, RxDataTool.stringToInt(RxSPTool.getContent(mContext, RxConstants.SP_SCAN_CODE)) + 1 + "");
    }

    public void handleDecode(Result result, String imagePath) {
        // inactivityTimer.onActivity();
        //扫描成功之后的振动与声音提示
        SoundHelper.getInstance().playNotifiSound();
        String result1 = result.getText();

        if (result1.length() < 8) {
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            return;
        }

        if (mData.size() == 30) {
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

            tv_yundan.setText(result1);

            mTempList.add(result1);
            HashMap<String, String> map = new HashMap<>();
            map.put("wail_number", result1);
            map.put("package_code", getPackage_code());
            map.put("express_name",expressName);
            map.put("name","李**");
            map.put("type", "1");
            mData.add(map);
            map = null;
            //上传图片
            // uploadImage(imagePath);
            //  checkWaybillState(wayNumber, "");
            mCropLayout.setVisibility(View.GONE);
            ll_scan_phone_number.setVisibility(View.VISIBLE);
            if (handler != null) {
                handler.sendEmptyMessage(R.id.phone_number);
            }
            tv_enter_number.setText("(" + mData.size() + "/30)");
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


    private OnResponseListener<String> mOnresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                handleResultForEnter(what, jsonObject);    //处理结果
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        }

        @Override
        public void onFinish(int what) {

        }
    };


    private void handleResultForEnter(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_CODE_REQUEST:     //获取最新提货码
                handleCodeResult(jsonObject);
                break;
        }
    }

    private void handleCodeResult(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String code = data.getString("code");
        package_number = IntegerUtil.getStringChangeToNumber(code);    //最新的数据库的提货码
    }

    private String getPackage_code() {
        package_number = package_number + tagNumber;
        String currentData = DateUtil.getCurrentDay();
        String effectCode = StringUtil.getEffectCode(package_number);
        String result = currentData +"0"+ effectCode;
        return result;
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
                } else if (state == State.PHONE) {
                    mCropLayout.setVisibility(View.GONE);
                    ll_scan_phone_number.setVisibility(View.VISIBLE);
                    //识别手机号码
                    imgCamera();
                }

            } else if (message.what == R.id.restart_preview) {
                //显示扫描框
                rl_scan.setVisibility(View.VISIBLE);
                restartPreviewAndDecode();

            } else if (message.what == R.id.decode_succeeded) {
                state = State.SUCCESS;
                Result result = (Result) message.obj;
                Bitmap barcode_bitmap = (Bitmap) message.getData().get("barcode_bitmap");
                String imagePath = saveBitmap(result.getText(), barcode_bitmap);
                wayNumber = result.getText();
                boolean isRight = StringUtil.isDigit(wayNumber);

                if (isRight) {
                    handleDecode((Result) message.obj, imagePath);// 解析成功，回调
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
            } else if (message.what == R.id.phone_number) {
                state = State.PHONE;
                cameraHelper.requestAutoFocus(this, R.id.auto_focus);
                //cameraHelper.requestPreviewFrame(decodeThread.getHandler(), R.id.auto_focus);
                //CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                // CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.auto_focus);
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
                //  mmhandler.sendEmptyMessageDelayed(1,5000L);
                //CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                //CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        }
    }

    Handler mmhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //cameraHelper.requestAutoFocus(this, R.id.auto_focus);
            Glide.with(EnterManagerActivity.this).load((Bitmap) msg.obj).into(image);
        }
    };

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
        Log.e("AAAAAAA_qian",width+"====="+height);
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

/*
        Bitmap phoneBitmap = BitmapUtil.byteToBitmap(data,width,height);
        Message message = new Message();
        message.what = 1;
        message.obj = phoneBitmap;
        mmhandler.sendMessage(message);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (handler != null) {
            Message message1 = Message.obtain(handler, R.id.decode_failed);
            message1.sendToTarget();

       /* PlanarYUVLuminanceSource source = cameraHelper.buildLuminanceSource(rotatedData, width, height);
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
        }*/
    }


    private String saveBitmap(String imageName, Bitmap bitmap) {
        File rootPath = new File(rootPaht);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        String filePaht = "";
        File saveFile = new File(rootPath, imageName + ".jpg");
        FileOutputStream os = null;
        try {
            Log.d("FileCache", "Saving File To Cache " + saveFile.getPath());
            os = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            filePaht = saveFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePaht;
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


    Rect mRect = new Rect();
    Rect mPreviewRect = new Rect();


    /**
     * 点击拍照
     *
     * @param
     */
    public void imgCamera() {
        try {
            mIsStop = true;
            //将正方形的大小映射到mRect上,为了截取大小
            llFocus.getGlobalVisibleRect(mRect);
            cameraHelper.takePicture(new CropPictureCallback());
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                handler.sendEmptyMessage(R.id.auto_focus);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 拍照完成的回调
     */
    private final class CropPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            new SavePicTask(data).execute();
            try {
                mIsStop = false;
                // 拍完照后，重新开始预览
                cameraHelper.getmCamera().startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class SavePicTask extends AsyncTask<Void, Void, String> {
        long currentTime = 0;
        private byte[] data;

        SavePicTask(byte[] data) {
            this.data = data;
        }

        protected void onPreExecute() {
            // showProgressDialog("处理中");
        }

        @Override
        protected String doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            String filePath = testFile.getPath() + UUID.randomUUID() + ".jpg";

            //获取宽高比
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //saveImage(filePath, bitmap);
            if (bitmap != null) {
                imageCrop(filePath, bitmap, mRect);
                recognnizeImageView(filePath);
            }
            return filePath;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            //Glide.with(EnterManagerActivity.this).load(result).into(image);
        }
    }


    /**
     * 按正方形裁切图片
     */
    public void imageCrop(String filePath, Bitmap bitmap, Rect focusRect) {
        Log.d(TAG, "imageCrop 屏幕尺寸 宽：" + dm.widthPixels + " 高=" + dm.heightPixels);
        Log.d(TAG, "imageCrop 预览控件 宽：" + textureView.getLayoutParams().width + " 高=" + textureView.getLayoutParams().height);
        Log.d(TAG, "imageCrop 裁剪区域 左边距：" + focusRect.left + "上边距：" + focusRect.top);
        Log.d(TAG, "imageCrop 裁剪区域 宽：" + (focusRect.right - focusRect.left) + "高：" + (focusRect.bottom - focusRect.top));
        Log.d(TAG, "imageCrop 图片 宽：" + bitmap.getWidth() + " 高：" + bitmap.getHeight());
        int heightLayout = cameraHelper.getPreviewViewSize().height;
        int widthLayout = cameraHelper.getPreviewViewSize().width;
        Log.d(TAG, "imageCrop 分辨率 宽：" + widthLayout + " 高：" + heightLayout);
        // 下面这句是关键
        float hScale = (float) bitmap.getHeight() / dm.heightPixels;
        float wScale = (float) bitmap.getWidth() / dm.widthPixels;
        Log.d(TAG, "imageCrop 比例 宽：" + wScale + " 高：" + hScale);

        int x = (int) (focusRect.left * wScale);
        int y = (int) (focusRect.top * hScale * 3 / 4);

        /*  x += x * 0.3;
        y += y * 0.6;*/
        Log.d(TAG, "imageCrop 裁剪区域 左边宽：" + x + " 上边高：" + y);
        int width = (int) (focusRect.width() * wScale);
        int height = (int) (focusRect.height() * hScale * 3 / 4);
        Log.d(TAG, "imageCrop 裁剪区域 宽" + width + " 高：" + height);
        //Camera.Size size = mCamera.getParameters().getPreviewSize();
        Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, x, y, width, height);
        //Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, 800, 900, (int)(1000),(int)(400));
        //saveImage(filePath, toGrayscale(bitmapTemp));
        saveImage(filePath, bitmapTemp);
    }

    private static void saveImage(String path, Bitmap bitmap) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ((bitmap != null) && (!bitmap.isRecycled())) {
            bitmap.recycle();
        }
    }


    private void recognnizeImageView(String imgPath) {
        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(imgPath));
        // 调用通用文字识别服务
        OCR.getInstance(this).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                int resultNumber = result.getWordsResultNumber();
                if (resultNumber == 0) {
                    if (handler != null) {
                        // 继续扫描
                        handler.sendEmptyMessage(R.id.phone_number);
                    }
                } else {
                    setData(result);
                }

            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                LogUtil.d("AAAAAAAAAAA", error.getMessage());
                if (handler != null) {
                    // 继续扫描
                    handler.sendEmptyMessage(R.id.phone_number);
                }
            }
        });
    }

    private void setData(GeneralResult result) {
        LogUtil.d("AAAAAAAAAAA", result.getJsonRes());
        StringBuilder sb = new StringBuilder();
        // 调用成功，返回GeneralResult对象
        for (WordSimple wordSimple : result.getWordList()) {
            sb.append(wordSimple.getWords());
        }
        String phoneNumber = StringUtil.isPhone(sb.toString());
        if (!"".equals(phoneNumber)) {
            tv_phone.setText("");
            tv_yundan.setText("");
            mData.get(mData.size() - 1).put("phone_number", phoneNumber);
            mCropLayout.setVisibility(View.VISIBLE);
            ll_scan_phone_number.setVisibility(View.GONE);
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
                handler.state = State.SUCCESS;
            }
            mAdapter.notifyDataSetChanged();
        } else {
            if (handler != null) {
                handler.sendEmptyMessage(R.id.phone_number);
            }
        }

    }
}
