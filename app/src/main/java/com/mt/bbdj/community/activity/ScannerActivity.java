package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huawei.hms.support.api.push.HandleTagsResult;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.ScannerMessageModel;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.ScannerMessageAdapter;
import com.zto.recognition.phonenumber.OCRListener;
import com.zto.recognition.phonenumber.tess.OCRResult;
import com.zto.scanner.BarcodeListener;
import com.zto.scanner.ZTOScannerFragment;

import java.util.ArrayList;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ScannerActivity extends BaseActivity implements OCRListener, BarcodeListener {

    ZTOScannerFragment scannerFragment;


    private TextView mPhone;

    private TextView mBarcode;

    private RecyclerView recyclerView;

    private ScannerMessageAdapter mAdapter;

    private List<ScannerMessageModel> mList = new ArrayList<>();
    private String express_name="";
    private String express_tag="";

    public static void actionTo(Context context,String express_tag,String express_name) {
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtra("express_tag",express_tag);
        intent.putExtra("express_name",express_name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ScannerActivity.this);
        initParams();
        initView();
        initScanner();
    }

    private void initParams() {
        Intent intent = getIntent();
        express_name = intent.getStringExtra("express_name");
        express_tag = intent.getStringExtra("express_tag");
    }


    @Override
    public void onOCRSuccess(OCRResult ocrResult) {
        String ocr = ocrResult.getText();
        if (!ocrResult.isNetResult()) {
            scannerFragment.reScan();
        }
        Message message = Message.obtain();
        message.obj = ocr;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScannerMessageModel model = mList.get(0);
            model.setPhone((String) msg.obj);
            model.setHavaPhone(true);
            model.setCode("020014");
            model.setHaveWayNumber(model.isHaveWayNumber());
            model.setExpressName(express_name);
            model.setExpressLogo(model.getExpressLogo());
            model.setWaybill(model.getWaybill());
            mAdapter.addData(0, model);
            recyclerView.scrollToPosition(0);
        }
    };

    @Override
    public void onGetBarcode(List<String> rawResult, Bitmap imageData) {
        System.out.println("barcode:" + rawResult.get(0));
        //mBarcode.setText(rawResult.get(0));
        //SoundUtil.play(R.raw.right);
        String barCode = rawResult.get(0);
        if (isRepeat(barCode)) {
            SoundHelper.getInstance().playNotifiRepeatSound();
        } else {
            ScannerMessageModel model = mList.get(0);
            model.setWaybill(barCode);
            model.setHaveWayNumber(true);
            model.setPhone(model.getPhone());
            model.setHavaPhone(model.isHavaPhone());
            model.setCode("020014");
            model.setExpressName(express_name);
            model.setExpressLogo(model.getExpressLogo());
            mAdapter.addData(0, model);
            recyclerView.scrollToPosition(0);
        }
    }

    private boolean isRepeat(String barCode) {
        if (null == barCode || "".equals(barCode)) {
            return false;
        }
        for (ScannerMessageModel model : mList) {
            if (barCode.equals(model.getWaybill())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onOCRFail(boolean isNet, String s) {
        System.out.println("ocr onOCRFail: " + s);
        if (!isNet) {
            scannerFragment.reScan();
        }
        scannerFragment.onStop();
    }

    private void initRecyclerView() {
        mList.add(new ScannerMessageModel("020014"));   //初始化第一条数据
        mAdapter = new ScannerMessageAdapter(this, mList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new MarginDecoration(this, 15));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        mPhone = findViewById(R.id.phone);
        mBarcode = findViewById(R.id.barcode);
        initRecyclerView();
    }

    private void initScanner() {
        scannerFragment = ZTOScannerFragment.newInstance(this, this);
        replaceFragment(R.id.container_scanner, scannerFragment);
        scannerFragment.onStop();
    }

    private void replaceFragment(int container_scanner, ZTOScannerFragment scannerFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (scannerFragment == null) {
            scannerFragment = ZTOScannerFragment.newInstance(this, this);
        }
        transaction.replace(container_scanner, scannerFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
