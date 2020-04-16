package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginByCodeActivity;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.PackageUtils;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class PrintLabelSettingActivity extends BaseActivity implements View.OnClickListener {

    private EditText tv_print_single;
    private EditText et_chart;
    private EditText et_number;
    private EditText tv_print_code;
    private Button bt_print;
    private RelativeLayout rl_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_label);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(PrintLabelSettingActivity.this);
        initView();
    }

    public static void actionTo(Context context) {
        Intent intent = new Intent(context, PrintLabelSettingActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        tv_print_single = findViewById(R.id.tv_print_single);
        et_chart = findViewById(R.id.et_chart);
        et_number = findViewById(R.id.et_number);
        bt_print = findViewById(R.id.bt_print);
        tv_print_code = findViewById(R.id.tv_print_code);
        rl_back = findViewById(R.id.rl_back);
        bt_print.setOnClickListener(this);
        rl_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.bt_print:   //打印界面
                printLabel();
                break;
        }
    }

    private void printLabel() {
        PrintTagModel printTagModel = new PrintTagModel();
        List<HashMap<String,String>> list = new ArrayList<>();
        String singleLabel = tv_print_single.getText().toString();
        String printCode = tv_print_code.getText().toString();
        String chart = et_chart.getText().toString();
        String number = et_number.getText().toString();
        if ("".equals(singleLabel) && ("".equals(chart) || "".equals(number))&&"".equals(printCode)){
            ToastUtil.showShort("请填写需要打印的标签");
            return;
        }
        if (!"".equals(singleLabel)){
            HashMap<String,String> map = new HashMap<>();
            map.put("tag",singleLabel);
            list.add(map);
        } else if (!"".equals(printCode)){
            String[] chart1 = printCode.split(",");
            for (String str : chart1){
                HashMap<String,String> map = new HashMap<>();
                map.put("tag",str);
                list.add(map);
                map = null;
            }
        }else {
            String[] charts = chart.split(",");
            String[] numbers = number.split(",");
            for (String num:numbers){
                for (String str :charts){
                    HashMap map = new HashMap();
                    map.put("tag",str+num);
                    list.add(map);
                    map = null;
                }
            }
        }
        printTagModel.setData(list);
        PrintLabelActivity.actionTo(this,printTagModel);
    }

}
