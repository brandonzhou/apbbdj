package com.shshcom.station.storage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mt.bbdj.R;
import com.shshcom.station.storage.domain.ScanStorageCase;

public class ScanImageUploadingActivity extends AppCompatActivity {
    private TextView tv_upload_state;
    private TextView tv_upload_detail;
    private TextView tv_btn_upload;

    public static void openActivity(Activity activity){
        Intent intent = new Intent(activity, ScanImageUploadingActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scan_image_uploading);

        initView();
    }

    private void initView(){
        tv_upload_state = findViewById(R.id.tv_upload_state);
        tv_upload_detail = findViewById(R.id.tv_upload_detail);
        tv_btn_upload = findViewById(R.id.tv_btn_upload);

        tv_btn_upload.setOnClickListener(v -> {

        });
    }

    private void initData(){
        ScanStorageCase storageCase = ScanStorageCase.getInstance();

        boolean uploading = true;

        if(uploading){
            tv_upload_state.setText("上传中…");
            tv_upload_state.setText("已上传成功182张，剩余23张照片");
        }else {
            tv_upload_state.setText("上传失败，请重试");
            tv_upload_state.setText("已上传成功182张，剩余23张照片");
        }


    }
}
