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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.mt.bbdj.baseconfig.utls.CameraUtils;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
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
import com.mt.bbdj.community.adapter.EnterManagerAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.rxfeature.module.scaner.CameraConfigurationManager;
import com.rxfeature.module.scaner.CameraManager;
import com.rxfeature.module.scaner.OnRxScanerListener;
import com.rxfeature.module.scaner.PlanarYUVLuminanceSource;
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


public class EnterManager_new_Activity extends ActivityBase {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private RelativeLayout ivBack;    //返回
    private TextView tv_enter_number;     //入库数
    private TextView tv_enter;
    private LinearLayout ll_scan_phone_number, llFocus;
    private List<HashMap<String, String>> mList = new ArrayList<>();
    private List<HashMap<String, String>> mPrintList = new ArrayList<>();
    private List<String> mTempList = new ArrayList<>();//临时数据

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private int packageCode = 1060204;

    private MyPopuwindow popupWindow;

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private HashMap<String, String> currentMap = new HashMap<>();    //当前数据
    private List<HashMap<String, String>> mData = new ArrayList<>();
    private ExpressLogoDao mExpressLogoDao;

    private final int CHECK_WAY_BILL_STATE = 100;    //检测
    private final int ENTER_RECORDE_REQUEST = 200;    //入库
    private final int COMMIT_PICTURE_REQUEST = 300;    //上传图片
    private String resultNumber;
    private String expressName;
    private PrintTagModel printTagModel = new PrintTagModel();
    private String rootPaht = Environment.getExternalStorageDirectory() + "/bbdj/barcode/";

    private int tagNumber = 1;

    /**
     * 扫描结果监听
     */
    private static OnRxScanerListener mScanerListener;

    private InactivityTimer inactivityTimer;

    /**
     * 扫描处理
     */
    private CaptureActivityHandler handler;

    /**
     * 整体根布局
     */
    private RelativeLayout mContainer = null;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;

    /**
     * 扫描边界的宽度
     */
    private int mCropWidth = 0;

    /**
     * 扫描边界的高度
     */
    private int mCropHeight = 0;

    /**
     * 是否有预览
     */
    private boolean hasSurface;

    /**
     * 扫描成功后是否震动
     */
    private boolean vibrate = false;

    /**
     * 闪光灯开启状态
     */
    private boolean mFlashing = true;

    /**
     * 生成二维码 & 条形码 布局
     */
    private LinearLayout mLlScanHelp;

    /**
     * 闪光灯 按钮
     */
    private ImageView mIvLight;

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

    private HkDialogLoading dialogLoading;
    private SensorController sensorControler;

    private Camera mCamera;
    private Camera.Parameters mParams;
    private DisplayMetrics dm = new DisplayMetrics();
    private boolean mIsStop;
    private SurfaceHolder surfaceHolder;
    private ImageView testImage;
    private File testFile;
    private Camera.Size optimalPreviewSize;
    private ResizeAbleSurfaceView surfaceViewLayout;

    /**
     * 设置扫描信息回调
     */
    public static void setScanerListener(OnRxScanerListener scanerListener) {
        mScanerListener = scanerListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_manager_new);
        //界面控件初始化
        initDecode();
        initParams();
        initView();
        initSelectPop();
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
        //初始化 CameraManager
        CameraManager.init(mContext, mCropLayout);
        hasSurface = false;
        //  inactivityTimer = new InactivityTimer(this);
    }

    private void initScanPhoneNumber() {
        //初始化相机参数
        surfaceViewLayout = findViewById(R.id.capture_preview);
       /* int width = ScreenUtils.getScreenWidth(this);
        int height = ScreenUtils.dip2px(this, 280);
        surfaceView.resize(width, height);*/

        surfaceHolder = surfaceViewLayout.getHolder();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        llFocus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                autoFocus();
                return false;
            }
        });
      /*  sensorControler = SensorController.getInstance();
        sensorControler.setCameraFocusListener(new SensorController.CameraFocusListener() {
            @Override
            public void onFocus() {
                Log.d(TAG, "onFocus");
                autoFocus();
            }
        });*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        surfaceViewLayout.getGlobalVisibleRect(mPreviewRect);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager()
                .getDefaultDisplay().getMetrics(dm);
        LogUtil.e("AAAAAAAAA", mPreviewRect.left + " " + mPreviewRect.top + " " + mPreviewRect.right + " " + mPreviewRect.bottom);
        LogUtil.e("AAAAAAAAABBB", dm.widthPixels + " " + dm.heightPixels);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        mPrintList.clear();
        if (hasSurface) {
            //Camera初始化
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    if (mCamera != null) {
                        mParams = mCamera.getParameters();
                        mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
                        //设置PreviewSize和PictureSize
                        List<Camera.Size> pictureSizes = mParams.getSupportedPictureSizes();
                        Camera.Size size = getOptimalPictureSize(pictureSizes);
                        if (size == null) {
                            Toast.makeText(getApplication(), "相机出错,请尝试换一台手机!", Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println("surfaceChanged picture size width=" + size.width + " height=" + size.height);
                            mParams.setPictureSize(size.width, size.height);
                        }

                        if (mParams.getSupportedFocusModes().contains(
                                mParams.FOCUS_MODE_FIXED)) {
                            mParams.setFocusMode(mParams.FOCUS_MODE_FIXED);
                        }

                        Log.d("surfaceChanged", "widthPixels=" + dm.widthPixels + " heightPixels=" + dm.heightPixels);
                        optimalPreviewSize = getOptimalPreviewSize(EnterManager_new_Activity.this,
                                mParams.getSupportedPreviewSizes(),
                                (float) dm.widthPixels / dm.heightPixels);
                        Point screenPoint = CameraConfigurationManager.getCameraResolution();
                        mParams.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                        try {
                            mCamera.setPreviewDisplay(surfaceHolder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCamera.setDisplayOrientation(90);
                        mCamera.setParameters(mParams);
                        try {
                            //mCamera.setParameters(mParams);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mCamera.startPreview();
                        Log.d(TAG, "mParams heightPixels=" + mParams.getPictureSize().height + " widthPixels=" + mParams.getPictureSize().width);
                    }
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (!hasSurface) {
                        hasSurface = true;
                        initCamera(holder);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    hasSurface = false;

                }
            });
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    private void initListener() {
        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                HashMap<String, String> map = mList.get(position);
                String resultCode = map.get("wail_number");
                mWaillMessageDao.queryBuilder().where(WaillMessageDao.Properties.WailNumber.eq(resultCode)).buildDelete();
                mList.remove(position);
                mTempList.remove(resultCode);
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + mList.size() + "/30)");
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        });


        //选择对话框
        expressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() != 0) {
                    ToastUtil.showShort("请先入库后再切换快递公司！");
                    return;
                }
                selectExpressDialog(v);
            }
        });


        //返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

                    SystemUtil.hideKeyBoard(EnterManager_new_Activity.this, tv_yundan);
                    mData.get(mData.size() - 1).put("phone_number", phoneNumber);
                    tv_enter_number.setText("(" + mList.size() + "/30)");

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

        //监听软键盘的弹出隐藏
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                rl_scan.setVisibility(View.GONE);
            }

            @Override
            public void keyBoardHide(int height) {
                rl_scan.setVisibility(View.VISIBLE);
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        });
    }

    private void showConfirmDialog() {
        new CircleDialog.Builder()
                .setTitle("提示")
                .setText("\n非正式环境不可入库\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //入库
                        //enterRecorde();
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());

    }

    private void enterRecorde() {
        if (mList.size() == 0) {
            ToastUtil.showShort("无可提交数据");
            return;
        }
        tv_enter.setEnabled(false);

        String data_json = getEnterrecordData();
        Request<String> request = NoHttpRequest.enterRecordeRequest(user_id, express_id, data_json);
        mRequestQueue.add(ENTER_RECORDE_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(EnterManager_new_Activity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    enterRecorderResult(jsonObject);
                } catch (JSONException e) {
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

    private void enterRecorderResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            mList.clear();
            tv_enter_number.setText("(" + 0 + "/30)");
            tagNumber = 1;
            mAdapter.notifyDataSetChanged();
            JSONArray data = jsonObject.getJSONArray("data");
            printNumber(data);    //打印取件码
            LoadDialogUtils.cannelLoadingDialog();
        } else {
            ToastUtil.showShort(msg);
        }
    }

    private void printNumber(JSONArray data) throws JSONException {
        mWaillMessageDao.deleteAll();    //删除数据库中临时数据
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String code = jsonObject.getString("code");
            String qrcode = jsonObject.getString("qrcode");
            String pie_number = jsonObject.getString("pie_number");
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("pie_number", pie_number);
            map.put("qrcode", qrcode);
            mPrintList.add(map);
            map = null;
        }
        printTagModel.setData(mPrintList);
        //  LoadDialogUtils.cannelLoadingDialog();
        Intent intent = new Intent(EnterManager_new_Activity.this, BluetoothNumberActivity.class);
        intent.putExtra("printData", printTagModel);
        startActivity(intent);
    }


    private String getEnterrecordData() {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String, String> data : mList) {
            sb.append(data.get("package_code"));
            sb.append("|");
            sb.append(data.get("express_name"));
            sb.append("|");
            sb.append("李**");
            sb.append("|");
            sb.append(data.get("wail_number"));
            sb.append(",");
        }
        String sbb = sb.toString();
        String result = sbb.substring(0, sbb.length() - 1);
        return result;
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

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        List<WaillMessage> dataList = mWaillMessageDao.queryBuilder().list();
        if (dataList != null && dataList.size() != 0) {
            for (WaillMessage waillMessage : dataList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("package_code", waillMessage.getTagCode());
                map.put("wail_number", waillMessage.getWailNumber());
                map.put("express_name", waillMessage.getExpressName());
                map.put("mobile", waillMessage.getMobile());
                map.put("name", waillMessage.expressName);
                mList.add(map);
                map = null;
                tagNumber = waillMessage.getTagNumber();
            }
        }

        mAdapter = new EnterManagerAdapter(mData);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setAdapter(mAdapter);
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

    private void initParams() {
        dialogLoading = new HkDialogLoading(this, "入库中...");
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
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        CameraManager.get().closeDriver();
        mIsStop = true;
    }

    @Override
    protected void onDestroy() {
        //  inactivityTimer.shutdown();
        mScanerListener = null;
        super.onDestroy();
        mWaillMessageDao.deleteAll();
        List<WaillMessage> lists = new ArrayList<>();
        for (HashMap<String, String> map : mList) {
            String package_code = map.get("package_code");
            String wail_number = map.get("wail_number");
            String mobile = map.get("mobile");
            String express_name = map.get("express_name");
            String name = map.get("name");
            WaillMessage waillMessage = new WaillMessage();
            waillMessage.setName(name);
            waillMessage.setExpressName(express_name);
            waillMessage.setTagCode(package_code);
            waillMessage.setMobile(mobile);
            waillMessage.setWailNumber(wail_number);
            waillMessage.setTagNumber(tagNumber);
            lists.add(waillMessage);
        }
        mWaillMessageDao.saveInTx(lists);
        closeCamera(mCamera);
    }

    private void initView() {
        mIvLight = findViewById(R.id.top_mask);
        mContainer = findViewById(R.id.capture_containter);
        mCropLayout = findViewById(R.id.capture_crop_layout);
        rl_scan = findViewById(R.id.rl_scan);
        rl_scan.setVisibility(View.VISIBLE);
        tv_phone = findViewById(R.id.tv_phone);
        tv_yundan = findViewById(R.id.tv_yundan);
        tv_enter = findViewById(R.id.tv_enter);
        tvPackageCode = findViewById(R.id.tv_package_number);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);
        tv_enter_number = findViewById(R.id.tv_enter_number);

        ll_scan_phone_number = findViewById(R.id.ll_scan_phone_number);

        llFocus = findViewById(R.id.llFocus);
        rl_scan.setVisibility(View.VISIBLE);
        tv_enter = findViewById(R.id.tv_enter);
        ivBack = findViewById(R.id.iv_back);
        testImage = findViewById(R.id.image);

        initRecyclerView();    //初始化列表

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

    public int getCropWidth() {
        return mCropWidth;
    }

    public void setCropWidth(int cropWidth) {
        mCropWidth = cropWidth;
        CameraManager.FRAME_WIDTH = mCropWidth;
    }

    public int getCropHeight() {
        return mCropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.mCropHeight = cropHeight;
        CameraManager.FRAME_HEIGHT = mCropHeight;
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            surfaceHolder.addCallback(new SurfaceHolderCallBack());
            CameraManager.get().openDriver(surfaceHolder);
            mCamera = CameraManager.get().getCamera();
            Point point = CameraManager.get().getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = mCropLayout.getWidth() * width.get() / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height.get() / mContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler();
        }
    }


    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver resolver = getContentResolver();
            // 照片的原始资源地址
            Uri originalUri = data.getData();
            try {
                // 使用ContentProvider通过URI获取原始图片
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                // 开始对图像资源解码
                Result rawResult = RxQrBarTool.decodeFromPhoto(photo);
                if (rawResult != null) {
                    if (mScanerListener == null) {
                        initDialogResult(rawResult);
                    } else {
                        mScanerListener.onSuccess("From to Picture", rawResult);
                    }
                } else {
                    if (mScanerListener == null) {
                        RxToast.error("图片识别失败.");
                    } else {
                        mScanerListener.onFail("From to Picture", "图片识别失败");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        if (result1.length() < 7) {
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
            HashMap<String, String> map = new HashMap<>();
            map.put("wail_number", result1);
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
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
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
            case CHECK_WAY_BILL_STATE:     //检测运单号

                break;
            case COMMIT_PICTURE_REQUEST:    //上传图片

                break;
        }
    }


    //==============================================================================================解析结果 及 后续处理 end

    final class CaptureActivityHandler extends Handler {

        DecodeThread decodeThread = null;
        private State state;

        public CaptureActivityHandler() {
            decodeThread = new DecodeThread();
            decodeThread.start();
            state = State.SUCCESS;
            CameraManager.get().startPreview();
            restartPreviewAndDecode();
        }


        @Override
        public void handleMessage(Message message) {
            if (message.what == R.id.auto_focus) {
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
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
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            } else if (message.what == R.id.phone_number) {
                state = State.PHONE;
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                // CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.auto_focus);
            }
        }

        public void quitSynchronously() {
            state = State.DONE;
            decodeThread.interrupt();
            CameraManager.get().stopPreview();
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
            removeMessages(R.id.decode);
            removeMessages(R.id.auto_focus);
            removeMessages(R.id.phone_number);
        }

        private void restartPreviewAndDecode() {
            if (state == State.SUCCESS) {
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
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

        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
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


    //#########################################################################################
    public void autoFocus() {
        if (mCamera != null) {
            try {
                if (mCamera.getParameters().getSupportedFocusModes() != null && mCamera.getParameters()
                        .getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                            mIsStop = success;
                            imgCamera();
                        }
                    });
                } else {
//                    Log.e(TAG, getString(R.string.unsupport_auto_focus));
                }
            } catch (Exception e) {
                e.printStackTrace();
                mCamera.stopPreview();
                mCamera.startPreview();
//                Log.e(TAG, getString(R.string.toast_autofocus_failure));
            }
        }
    }

    /**
     * @return ${return_type} 返回类型
     * @throws
     * @Title: 关闭相机
     * @Description: 释放相机资源
     */
    public Camera closeCamera(Camera camera) {
        try {
            if (camera != null) {
                mParams = null;
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            Log.i("TAG", e.getMessage());
        }
        return camera;
    }

    public class SurfaceHolderCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                if (null == mCamera) {
                    mCamera = CameraManager.get().getCamera();
                    setDisplayOrientation();
                }
                //handler.sendEmptyMessageDelayed(R.id.auto_focus,1000);
            } catch (Exception e) {
                //Toast.makeText(MyApplication.getInstance(), "暂未获取到拍照权限", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            if (mCamera != null) {
                mParams = mCamera.getParameters();
                mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
                //设置PreviewSize和PictureSize
                List<Camera.Size> pictureSizes = mParams.getSupportedPictureSizes();
                Camera.Size size = getOptimalPictureSize(pictureSizes);
                if (size == null) {
                    Toast.makeText(getApplication(), "相机出错,请尝试换一台手机!", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("surfaceChanged picture size width=" + size.width + " height=" + size.height);
                    mParams.setPictureSize(size.width, size.height);
                }

                if (mParams.getSupportedFocusModes().contains(
                        mParams.FOCUS_MODE_FIXED)) {
                    mParams.setFocusMode(mParams.FOCUS_MODE_FIXED);
                }

                optimalPreviewSize = getOptimalPreviewSize(EnterManager_new_Activity.this,
                        mParams.getSupportedPreviewSizes(),
                        (float) dm.widthPixels / dm.heightPixels);

                //Point screenPoint = CameraConfigurationManager.getCameraResolution();
                mParams.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mCamera.setDisplayOrientation(90);
                mCamera.setParameters(mParams);
                try {
//                mCamera.setParameters(mParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
                Log.d(TAG, "mParams heightPixels=" + mParams.getPictureSize().height + " widthPixels=" + mParams.getPictureSize().width);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }

    private void setDisplayOrientation() {
        int rotation = getWindowManager()
                .getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
        }
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degree) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degree + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
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


    /**
     * @param currentActivity
     * @param sizes           最理想的预览分辨率的宽和高
     * @param targetRatio
     * @return 获得最理想的预览尺寸
     */
    public Camera.Size getOptimalPreviewSize(Activity currentActivity,
                                             List<Camera.Size> sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size
        DisplayMetrics dm = new DisplayMetrics();
        currentActivity.getWindowManager()
                .getDefaultDisplay().getMetrics(dm);


        int screenWidth = 1080;
        int screenHeight = 1055;

        int targetHeight = Math.min(screenHeight, screenWidth);
        if (targetHeight <= 0) {
            // We don't know the size of SurfaceView, use screen height
            targetHeight = screenHeight;
        }

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }


        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSize == null) {
            System.out.println("No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
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
            mCamera.takePicture(null, null, null, new CropPictureCallback());
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
            new SavePicTask(data).execute();
            try {
                mIsStop = false;
                camera.startPreview(); // 拍完照后，重新开始预览
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
            Glide.with(EnterManager_new_Activity.this).load(result).skipMemoryCache(true).into(testImage);
        }
    }


    /**
     * 按正方形裁切图片
     */
    public void imageCrop(String filePath, Bitmap bitmap, Rect focusRect) {
        Log.d(TAG, "imageCrop heightPixels=" + dm.heightPixels + " widthPixels=" + dm.widthPixels);
        Log.d(TAG, "imageCrop bitmap w=" + bitmap.getWidth() + " h=" + bitmap.getHeight());
        Log.d(TAG, "imageCrop focusRect left=" + focusRect.left + " top=" + focusRect.top);
        // 下面这句是关键
        float hScale = (float) bitmap.getHeight() / optimalPreviewSize.height;
        float wScale = (float) bitmap.getWidth() / optimalPreviewSize.width;
        Log.d(TAG, "imageCrop wScale=" + wScale + " hScale=" + hScale);
        int x = (int) (focusRect.left * wScale);
        int y = (int) (focusRect.top * hScale) + 20;
       /*x += x * 0.3;
        y += y * 0.6;*/
        Log.d(TAG, "imageCrop x=" + x + " y=" + y);
        int width = focusRect.width();
        int height = focusRect.height();
        Log.d(TAG, "imageCrop width=" + width + " height=" + height);
        //Camera.Size size = mCamera.getParameters().getPreviewSize();
        //Bitmap bitmapTemp = Bitmap.createBitmap(bitmap,  focusRect.left, focusRect.top, width, height);
        Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, x, y, (int) (width * hScale), (int) (height * wScale));
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
                setData(result);
               /* if (resultNumber == 0) {
                    mCamera.startPreview();
                } else {
                    setData(result);
                }*/

            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                LogUtil.d("AAAAAAAAAAA", error.getMessage());
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
            mData.get(mData.size() - 1).put("phone_number", phoneNumber);
            mCropLayout.setVisibility(View.VISIBLE);
            ll_scan_phone_number.setVisibility(View.GONE);
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        } else {
            if (handler != null) {
                handler.sendEmptyMessage(R.id.phone_number);
            }
        }
        mAdapter.notifyDataSetChanged();
        //String phone = getPhoneNumber(sb.toString());
    }
}