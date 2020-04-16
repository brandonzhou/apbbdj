package com.mt.bbdj.community.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ScannerMessageModel;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ScannerMessageModelDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.EnterData;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.EnterManagerAdapter;
import com.mt.bbdj.community.adapter.ScannerMessageAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zto.recognition.phonenumber.OCRListener;
import com.zto.recognition.phonenumber.tess.OCRResult;
import com.zto.scanner.BarcodeListener;
import com.zto.scanner.ZTOScannerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ScannerOutActivity extends BaseActivity implements OCRListener, BarcodeListener {

    ZTOScannerFragment scannerFragment;

    private RecyclerView recyclerView;

    private RelativeLayout iv_back;


    private TextView tv_out_title;
    private TextView tv_out_number;


    private EnterManagerAdapter mAdapter;

    private List<HashMap<String, String>> mList = new ArrayList<>();

    private final int REQUEST_OUT_REAPORTY = 1001;   //出库
    private final int CHECK_WAY_BILL_STATE = 1002;   //检测运单号
    private final int MESSAGE_DELAY = 1003;
    private final int REQUEST_PHONE = 1004;

    private RequestQueue mRequestQueue;
    private String user_id;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_DELAY) {   //重新开始识别
                scannerFragment.onResume();
            }
        }
    };


    public static void actionTo(Context context) {
        Intent intent = new Intent(context, ScannerOutActivity.class);
        context.startActivity(intent);
    }

    public static void actionTo(Activity context, int requestCode) {
        Intent intent = new Intent(context, SelectExpressActivity.class);
        intent.putExtra("requestCode",requestCode);
        context.startActivityForResult(intent,requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_out);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ScannerOutActivity.this);
        initParams();
        initView();
        initScanner();
        initClickListener();

    }


    @Override
    public void onOCRSuccess(OCRResult ocrResult) {
        String ocr = ocrResult.getText();
        //scannerFragment.onPause();
        if (!ocrResult.isNetResult()) {
            scannerFragment.reScan();
        }
    }

    @Override
    public void onGetBarcode(List<String> rawResult, Bitmap imageData) {
        System.out.println("barcode:" + rawResult.get(0));
        String barCode = rawResult.get(0);
        if (isRepeat(barCode)) {
            SoundHelper.getInstance().playNotifiRepeatSound();
        } else {
            SoundHelper.getInstance().playNotifiSuccessSound();
            scannerFragment.onPause();
            handler.sendEmptyMessageDelayed(MESSAGE_DELAY, 500);
            checkWaybillState(barCode);  //获取运单号信息
        }
    }


    @Override
    public void onOCRFail(boolean isNet, String s) {
        System.out.println("ocr onOCRFail: " + s);
        if (!isNet) {
            scannerFragment.reScan();
        }
        scannerFragment.onStop();
    }

    private boolean isRepeat(String barCode) {
        if (null == barCode || "".equals(barCode)) {
            return false;
        }
        for (HashMap<String, String> map : mList) {
            String wail_number = map.get("wail_number");
            if (barCode.equals(wail_number)) {
                return true;
            }
        }
        return false;
    }


    private void checkWaybillState(String number) {
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);
        params.put("number",number);
        Request<String> request = NoHttpRequest.checkOutWailnumberStateRequest(params);
        mRequestQueue.add(CHECK_WAY_BILL_STATE, request, onResponseListener);
    }

    private void enterResotry() {
        if (mList.size() == 0) {
            ToastUtil.showShort("无可提交数据");
            return;
        }
        String str_data = getData();
        HashMap<String, String> params = new HashMap<>();
        params.put("pie_data", str_data);
        params.put("user_id", user_id);
        Request<String> request = NoHttpRequest.outRestory(params);
        mRequestQueue.add(REQUEST_OUT_REAPORTY, request, onResponseListener);
    }

    private void handleEnterEvent(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }
        tv_out_number.setText("(" + mList.size() + ")");
        ToastUtil.showShort(msg);
    }

    private void handleCodeEvent(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();

        if ("5001".equals(code)) {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            String package_code = dataObj.getString("code");
            String express = dataObj.getString("express");
            String express_id = dataObj.getString("express_id");
            String pie_id = dataObj.getString("pie_id");
            String number = dataObj.getString("number");
            String mobile = dataObj.getString("mobile");

            HashMap<String, String> map = new HashMap<>();
            map.put("package_code", package_code);
            map.put("wail_number", number);
            map.put("phone_number", mobile);
            map.put("express_id", express_id);
            map.put("pie_id", pie_id);
            mList.add(0, map);
            map = null;
            mAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showShort(msg);
        }
        tv_out_number.setText("(" + mList.size() + ")");
    }

    private String getData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mList.size(); i++) {
            HashMap<String,String> model = mList.get(i);
            sb.append(model.get("wail_number"));
            sb.append(",");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    private void initParams() {
        Intent intent = getIntent();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void initRecyclerView() {
        //mList.add(new ScannerMessageModel());   //初始化第一条数据
        mAdapter = new EnterManagerAdapter(mList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new MarginDecoration(this, 5));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        iv_back = findViewById(R.id.iv_back);
        tv_out_title = findViewById(R.id.tv_out_title);
        tv_out_number = findViewById(R.id.tv_out_number);
        initRecyclerView();
    }

    private void initScanner() {
        scannerFragment = ZTOScannerFragment.newInstance(this, this);
        replaceFragment(R.id.container_scanner, scannerFragment);
        scannerFragment.setOCRHint("请扫描条形码");
    }

    private void initClickListener() {

        iv_back.setOnClickListener(view -> finish());

        //入库
        tv_out_title.setOnClickListener(view -> {
            enterResotry();    //入库提交
        });

        mAdapter.setDeleteClickListener(position -> {
            mList.remove(position);
            mAdapter.notifyDataSetChanged();
            tv_out_number.setText("(" + mList.size() + ")");
        });
    }

    private void replaceFragment(int container_scanner, ZTOScannerFragment scannerFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (scannerFragment == null) {
            scannerFragment = ZTOScannerFragment.newInstance(this, this);
        }
        transaction.replace(container_scanner, scannerFragment);
        transaction.commit();
    }


    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ScannerOutActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ScannerOutActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                if (what == REQUEST_OUT_REAPORTY) {   //出库信息
                    handleEnterEvent(jsonObject);
                } else if (what == CHECK_WAY_BILL_STATE) {  //获取运单号
                    handleCodeEvent(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                scannerFragment.reScan();
                LoadDialogUtils.cannelLoadingDialog();
                ToastUtil.showShort("网络异常请重试");
            }
            LoadDialogUtils.cannelLoadingDialog();
            scannerFragment.reScan();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            scannerFragment.reScan();
            ToastUtil.showShort("网络异常请重试！");
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };


    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.cancelAll();
        mRequestQueue = null;
    }


}
