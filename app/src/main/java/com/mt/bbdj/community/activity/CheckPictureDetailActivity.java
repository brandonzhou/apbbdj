package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.StringUtil;

public class CheckPictureDetailActivity extends AppCompatActivity {

    private TextView tv_code;
    private ImageView mImage, iv_back;
    private String mCode;
    private String picture;

    public static void actionTo(Context context, String code, String picture) {
        Intent intent = new Intent(context, CheckPictureDetailActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("picture", picture);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_picture_detail);
        initParams();
        initView();
        initClickListener();
    }

    private void initClickListener() {

        iv_back.setOnClickListener(view -> finish());
    }

    private void initView() {
        tv_code = findViewById(R.id.tv_code);
        mImage = findViewById(R.id.image);
        iv_back = findViewById(R.id.iv_back);
        tv_code.setText(mCode);
        Glide.with(this).load(picture).into(mImage);
    }

    private void initParams() {
        mCode = StringUtil.handleNullResultForString(getIntent().getStringExtra("code"));
        picture = StringUtil.handleNullResultForString(getIntent().getStringExtra("picture"));
    }
}
