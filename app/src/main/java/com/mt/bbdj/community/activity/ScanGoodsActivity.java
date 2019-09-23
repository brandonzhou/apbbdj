package com.mt.bbdj.community.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kdp.starbarcode.core.BarCodeScanConfig;
import com.kdp.starbarcode.core.BarCodeType;
import com.kdp.starbarcode.inter.OnBarCodeScanResultListener;
import com.kdp.starbarcode.view.BarCodePreview;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Goods;
import com.mt.bbdj.baseconfig.model.SearchGoodsModel;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.ScanView;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;

public class ScanGoodsActivity extends BaseActivity {

    private BarCodePreview barCodePreview;

    private ScanView scanView;
    private String user_id;
    private RequestQueue mRequestQueue;
    private Goods mGoods;

    public static void actionTo(Context context, String user_id, Goods goods) {
        Intent intent = new Intent(context, ScanGoodsActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("goods", goods);
        context.startActivity(intent);

    }

    public static void actionTo(Activity activity,int requestCode) {
        Intent intent = new Intent(activity, ScanGoodsActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_goods);

        initParams();
        initView();
        initScanView();
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initView() {
        mGoods = (Goods) getIntent().getSerializableExtra("goods");
        barCodePreview = findViewById(R.id.barcodepreview);
        scanView = findViewById(R.id.scanview);
        barCodePreview.setOnBarCodeScanResultListener(new OnBarCodeScanResultListener() {

            @Override
            public void onSuccess(String result) {
                vibrate();
                Intent intent = new Intent();
                intent.putExtra("code",result);
                setResult(RESULT_OK,intent);
                finish();
               // commitBarCode(result);     //提交
              // showSuccessDialog(result);
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void commitBarCode(String result) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("goods_code", result);
        Request<String> request = NoHttpRequest.commitGoodsCode(user_id, params);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ScanGoodsActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        setData(jsonObject);   //设置数据
                    } else {
                        ToastUtil.showShort(msg);
                    }
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
        });

    }

    private void setData(JSONObject jsonObject) throws JSONException {
        SearchGoodsModel searchGoodsModel = new SearchGoodsModel();
        List<SearchGoodsModel.SearchGoods> dataList = new ArrayList<>();
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String code_id = dataObj.getString("code_id");
        JSONArray jsonArray = dataObj.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String goods_id = obj.getString("goods_id");
            String img = obj.getString("img");
            SearchGoodsModel.SearchGoods goods = new SearchGoodsModel.SearchGoods();
            goods.setGoods_id(goods_id);
            goods.setImg(img);
            dataList.add(goods);
            goods = null;
        }
        searchGoodsModel.setData(dataList);
        searchGoodsModel.setCode_id(code_id);
        SelectGoodsPictureActivity.actionTo(ScanGoodsActivity.this,user_id,mGoods,searchGoodsModel);
        finish();
    }

    private void showSuccessDialog(String rawResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("识别结果")
                .setMessage(rawResult)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        barCodePreview.startRecognize();
                    }
                });

        builder.create().show();
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
                .setBarCodeType(BarCodeType.ONE_D_CODE)//仅识别所有的一维条形码
//                .setBarCodeType(BarCodeType.TWO_D_CODE)//仅识别所有的二维条形码
//                .setBarCodeType(BarCodeType.QR_CODE)//仅识别二维码
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
}
