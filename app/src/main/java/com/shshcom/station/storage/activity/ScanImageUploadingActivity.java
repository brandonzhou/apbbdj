package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.shshcom.station.storage.domain.ScanStorageCase;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ScanImageUploadingActivity extends AppCompatActivity {
    private TextView tv_upload_state;
    private TextView tv_upload_detail;
    private TextView tv_btn_upload;
    private ImageView imageView;

    private ScanStorageCase storageCase;
    private Disposable disposable;

    public static void openActivity(Activity activity){
        Intent intent = new Intent(activity, ScanImageUploadingActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scan_image_uploading);
        storageCase = ScanStorageCase.getInstance();

        List<ScanImage> list = storageCase.getScanImageList(ScanImage.State.uploading);
        storageCase.retryUploadImage(list);


        initView();
        refresh();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable!= null){
            disposable.dispose();
        }
    }

    private void initView(){
        tv_upload_state = findViewById(R.id.tv_upload_state);
        tv_upload_detail = findViewById(R.id.tv_upload_detail);
        tv_btn_upload = findViewById(R.id.tv_btn_upload);

        imageView = findViewById(R.id.imageView);

        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

    }

    private void refresh(){
        disposable = Observable.interval(0,2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    LogUtil.v("zhhli", "initData");
                    initData();
                });
    }

    private void initData(){
        int successSize = storageCase.getScanImageList(ScanImage.State.upload_success).size();
        int uploadingSize = storageCase.getScanImageList(ScanImage.State.uploading).size();


        if(uploadingSize>0){
            tv_upload_state.setText("上传中…");
            tv_upload_detail.setText(String.format("已上传成功%d张，剩余%d张照片", successSize, uploadingSize));

            imageView.setVisibility(View.INVISIBLE);

            tv_btn_upload.setText("下一步…");
            tv_btn_upload.setOnClickListener(null);
            return;
        }


        disposable.dispose();
        List<ScanImage> failList = storageCase.getScanImageList(ScanImage.State.upload_fail);

        if(failList.isEmpty()){
            imageView.setVisibility(View.INVISIBLE);
            tv_upload_state.setText("上传完成");
            tv_upload_detail.setText(String.format("已上传成功%d张，剩余0张照片", successSize));

            tv_btn_upload.setText("下一步");
            tv_btn_upload.setOnClickListener(v -> {
                ScanOcrResultActivity.openActivity(this);
                finish();
            });

        }else {
            imageView.setVisibility(View.VISIBLE);
            tv_upload_state.setText("上传失败，请重试…");
            tv_upload_detail.setText(String.format("已上传成功%d张，剩余%d张照片", successSize, failList.size()));


            tv_btn_upload.setText("重试");
            tv_btn_upload.setOnClickListener(v -> {
                storageCase.retryUploadImage(failList);
                refresh();
            });
        }


    }
}
