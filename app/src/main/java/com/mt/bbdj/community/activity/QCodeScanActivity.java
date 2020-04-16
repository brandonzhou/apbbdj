package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.kdp.starbarcode.codec.QRCodeCodec;
import com.kdp.starbarcode.core.BarCodeScanConfig;
import com.kdp.starbarcode.core.BarCodeType;
import com.kdp.starbarcode.inter.OnBarCodeScanResultListener;
import com.kdp.starbarcode.view.BarCodePreview;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Goods;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.ScanView;
import com.mylhyl.circledialog.CircleDialog;
import com.wildma.idcardcamera.utils.ScreenUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class QCodeScanActivity extends BaseActivity {

    private BarCodePreview barCodePreview;

    private final int REQUEST_ORDER_DATA = 1001;
    private final int REQUEST_IS_OBSERVE = 1002;

    private ScanView scanView;
    private String user_id;
    private RequestQueue mRequestQueue;
    private Goods mGoods;
    private RelativeLayout iv_back;

    private QRCodeCodec qrCodeCodec;
    private View selectView;
    private ImageView observice_qcode;
    private AppCompatTextView tv_no_observe;
    private TextView tv_have_observe;

    public static void actionTo(Context context,String user_id) {
        Intent intent = new Intent(context, QCodeScanActivity.class);
        intent.putExtra("user_id",user_id);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        initParams();
        initView();
        initScanView();
        initPopuwindow();
    }



    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
        //实例化QRCode编解码器
        qrCodeCodec = new QRCodeCodec();
    }

    private void initView() {
        mGoods = (Goods) getIntent().getSerializableExtra("goods");
        barCodePreview = findViewById(R.id.barcodepreview);
        iv_back = findViewById(R.id.iv_back);
        scanView = findViewById(R.id.scanview);
        iv_back.setOnClickListener(view -> finish());
        barCodePreview.setOnBarCodeScanResultListener(new OnBarCodeScanResultListener() {

            @Override
            public void onSuccess(String result) {
                vibrate();
                //String effectResult = handleResult(result);
                showSuccessDialog(result);
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private String handleResult(String result) {
        if (null == result || "".equals(result)) {
            return "";
        }
        if (result.contains("BB")){
            return result.replace("BB","");
        }
        return "";
    }

    private void showSuccessDialog(String rawResult) {
        if ("".equals(rawResult)) {
            barCodePreview.startRecognize();
        } else {
            String effectiveResult = handleResult(rawResult);
            new CircleDialog.Builder()
                    .setTitle("温馨提示")
                    .setWidth(0.8f)
                    .setCanceledOnTouchOutside(false)
                    .setText("\n确定出库单号为" + effectiveResult + "的快件吗？\n")
                    .setPositive("确定", view -> {
                        barCodePreview.startRecognize();
                        outResporty(rawResult);
                    })
                    .setNegative("取消", view -> {
                        barCodePreview.startRecognize();
                    })
                    .show(getSupportFragmentManager());
        }
    }


    private void outResporty(String rawResult) {
        HashMap<String, String> params = new HashMap<>();
        params.put("code", rawResult);
        params.put("station_id", user_id);
        Request<String> request = NoHttpRequest.outRestoryByQCode(params);
        mRequestQueue.add(REQUEST_ORDER_DATA, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(QCodeScanActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "QCodeScanActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        handleRequestData(what,jsonObject);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                }
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "QCodeScanActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("statusCode").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleRequestData(what,jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {
            LoadDialogUtils.cannelLoadingDialog();
        }
    };

    private void handleRequestData(int what, JSONObject jsonObject) throws JSONException {
        if (what == REQUEST_ORDER_DATA){
            JSONObject obj = jsonObject.getJSONObject("data");
            String station_id = obj.getString("station_id");
            String pie_id = obj.getString("pie_id");
            requestObserver(station_id,pie_id);   //检测是否关注
        }

        if (what == REQUEST_IS_OBSERVE){
            JSONObject obj = jsonObject.getJSONObject("data");
            String state = obj.getString("state");
            String qrcode = obj.getString("qrcode");
            if ("0".equals(state)){   //未关注
                Bitmap bitmap = qrCodeCodec.encodeQRCode(qrcode, ScreenUtils.dip2px(QCodeScanActivity.this,120));
                showNoObservice(bitmap,1);
                SoundHelper.getInstance().outPromite();
            } else {   //已关注
                showNoObservice(null,2);
            }
        }
    }

    private void showNoObservice(Bitmap bitmap,int type) {
        if (type == 1){
            tv_have_observe.setVisibility(View.GONE);
            tv_no_observe.setVisibility(View.VISIBLE);
            observice_qcode.setVisibility(View.VISIBLE);
            observice_qcode.setImageBitmap(bitmap);
        } else {
            tv_have_observe.setVisibility(View.VISIBLE);
            tv_no_observe.setVisibility(View.GONE);
            observice_qcode.setVisibility(View.GONE);
        }

        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(iv_back, Gravity.CENTER, 0, 0);
        }
    }

    private void requestObserver(String station_id,String pie_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("pie_id", pie_id);
        params.put("station_id", station_id);
        Request<String> request = NoHttpRequest.isObservice(params);
        mRequestQueue.add(REQUEST_IS_OBSERVE, request,onResponseListener );
    }


    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    private void initScanView() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int scanWidth = screenWidth / 6 * 4;
        int scanHeight = (int) (screenHeight / 3);
        int left = (screenWidth - scanWidth) / 2;
        int top = (screenHeight - scanHeight) / 2;
        int right = scanWidth + left;
        int bottom = scanHeight + top;
        scanView.setBorder(new int[]{left, top, right, bottom});

        //识别区域
        Rect rect = new Rect(left, top, right, bottom);
        BarCodeScanConfig barCodeScanConfig = new BarCodeScanConfig.Builder()
                .setROI(rect)//识别区域
                .setAutofocus(true)//自动对焦，默认为true
                .setDisableContinuous(false)//使用连续对焦，必须在Autofocus为true的前提下，该参数才有效;默认为true
                //  .setBarCodeType(BarCodeType.values()[barcodeType])//识别所有的条形码
                // .setBarCodeType(BarCodeType.ONE_D_CODE)//仅识别所有的一维条形码
//                .setBarCodeType(BarCodeType.TWO_D_CODE)//仅识别所有的二维条形码
                .setBarCodeType(BarCodeType.QR_CODE)//仅识别二维码
//                .setBarCodeType(BarCodeType.CODE_128)//仅识别CODE 128码
//                .setBarCodeType(BarCodeType.CUSTOME)//自定义条码类型，必须指定自定义识别的条形码格式
//                .setBarcodeFormats(EnumSet.of(BarcodeFormat.QR_CODE,BarcodeFormat.EAN_13))//定义识别的条形码格式
                .setSupportAutoZoom(false)//当二维码图片较小时自动放大镜头(仅支持QR_CODE)
                .build();
        barCodePreview.setBarCodeScanConfig(barCodeScanConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        barCodePreview.openCamera();
        barCodePreview.startRecognize();
        //开启扫描动画
        scanView.startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        barCodePreview.stopRecognize();
        barCodePreview.closeCamera();
        scanView.stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
    private PopupWindow popupWindow;

    private void initPopuwindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.observice_dialog, null);
            observice_qcode = selectView.findViewById(R.id.observice_qcode);
            Button tv_confirm =selectView.findViewById(R.id.tv_confirm);
            tv_no_observe = selectView.findViewById(R.id.tv_no_observe);
            tv_have_observe = selectView.findViewById(R.id.tv_have_observe);
            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            LinearLayout layout_pop_close = selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(v -> popupWindow.dismiss());
            tv_confirm.setOnClickListener(v -> {
                barCodePreview.startRecognize();
                popupWindow.dismiss();
            });
        }
    }
}
