package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ScannerMessageModel;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ScannerMessageModelDao;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
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

    private RecyclerView recyclerView;

    private ScannerMessageAdapter mAdapter;

    private List<ScannerMessageModel> mList = new ArrayList<>();
    private String express_name="";
    private String express_tag="";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScannerMessageModel model = mList.get(0);
            model.setPhone((String) msg.obj);
            model.setIsHavaPhone(1);
            model.setCode("020014");
            model.setIsHaveWayNumber(model.getIsHaveWayNumber());
            model.setExpressName(express_name);
            model.setExpressLogo(model.getExpressLogo());
            model.setWaybill(model.getWaybill());
            mAdapter.addData(0, model);
            recyclerView.scrollToPosition(0);
        }
    };
    private ScannerMessageModelDao scannerMessageModel;

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
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        scannerMessageModel = daoSession.getScannerMessageModelDao();
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
            model.setIsHaveWayNumber(1);
            model.setPhone(model.getPhone());
            model.setIsHavaPhone(model.getIsHavaPhone());
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
        //mList.add(new ScannerMessageModel());   //初始化第一条数据
        mAdapter = new ScannerMessageAdapter(this, mList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new MarginDecoration(this, 15));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        resetData();    //复现数据
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

        saveMessage();   //保存信息
    }

    private void resetData() {

        List<ScannerMessageModel> resetList = scannerMessageModel.queryBuilder().list();
        if (resetList != null && resetList.size() != 0) {
            mList = resetList;
        } else {
            mList.add(new ScannerMessageModel());   //初始化第一条数据
        }
    }

    private void saveMessage() {
        scannerMessageModel.deleteAll();
        scannerMessageModel.saveInTx(mList);
    }
}
