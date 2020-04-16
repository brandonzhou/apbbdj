package com.mt.bbdj.community.activity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;

public class EditCodeActivity extends BaseActivity {

    private RelativeLayout iv_back;
    private TextView tv_standard_title;
    private TextView tv_cannel;
    private TextView tv_confirm;
    private EditText et_code;

    public static void actionTo(Activity activity,int requestCode){
        Intent intent = new Intent(activity,EditCodeActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_code);
        initView();
        initClickListener();
    }

    private void initClickListener() {
        tv_cannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEvent();    //处理点击事件
            }
        });

    }

    private void handleEvent() {
       String code = et_code.getText().toString();
       if ("".equals(code)) {
           ToastUtil.showShort("取件码不可为空");
           return;
       }
       Intent intent = new Intent();
       intent.putExtra("packageCode",code);
       setResult(RESULT_OK,intent);
       finish();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        tv_standard_title = findViewById(R.id.tv_standard_title);
        tv_cannel = findViewById(R.id.tv_cannel);
        tv_confirm = findViewById(R.id.tv_confirm);
        et_code = findViewById(R.id.et_code);
        tv_standard_title.setText(DateUtil.getCurrentDay());
    }
}
