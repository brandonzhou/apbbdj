package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.SimpleTypeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class PrintCodeSettingActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_chart_start;
    private EditText et_chart_end;
    private EditText et_chart_repeat;
    private Button bt_print;
    private RelativeLayout rl_back;
    private RecyclerView rl_chart;
    private int chartPosition = 0;
    String[] mLocalChart = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","P","Q","R","S","T","U","V","W","X","Y","Z"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_code);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(PrintCodeSettingActivity.this);
        initView();
        initRecycler();
    }

    private void initRecycler() {
        SimpleTypeAdapter goodsAdapter = new SimpleTypeAdapter(this, mLocalChart);
        rl_chart.setAdapter(goodsAdapter);
        rl_chart.addItemDecoration(new MarginDecoration(this));
        rl_chart.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setPosition(0);
        goodsAdapter.setOnItemClickListener(position -> {
            chartPosition = position;
            goodsAdapter.setPosition(position);
            goodsAdapter.notifyDataSetChanged();
        });
    }

    public static void actionTo(Context context) {
        Intent intent = new Intent(context, PrintCodeSettingActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        et_chart_start = findViewById(R.id.et_chart_start);
        et_chart_end = findViewById(R.id.et_chart_end);
        et_chart_repeat = findViewById(R.id.et_chart_repeat);
        bt_print = findViewById(R.id.bt_print);
        rl_back = findViewById(R.id.rl_back);
        rl_chart = findViewById(R.id.rl_chart);
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

        String charCode = mLocalChart[chartPosition];
        String startNumber = et_chart_start.getText().toString();
        String endNumber = et_chart_end.getText().toString();
        String repeatNumber = et_chart_repeat.getText().toString();

        if ("".equals(startNumber)) {
            ToastUtil.showShort("请输入起始编号");
        } else if ("".equals(endNumber)){
            ToastUtil.showShort("请输入终止编号");
        } else if ("".equals(repeatNumber) || "0".equals(repeatNumber)){
            ToastUtil.showShort("打印数量不可为0");
        } else {
            printCode(charCode, startNumber, endNumber, repeatNumber);
        }
    }

    private void printCode(String charCode, String startNumber, String endNumber, String repeatNumber) {
        PrintTagModel printTagModel = new PrintTagModel();
        List<HashMap<String,String>> list = new ArrayList<>();
        int number = IntegerUtil.changeStrToInteger(repeatNumber);
        int start = IntegerUtil.changeStrToInteger(startNumber);
        int end = IntegerUtil.changeStrToInteger(endNumber);
        for (int m = start;m<=end;m++){
            String tagNumber = StringUtil.formatCode(m);
            for (int i = 0;i < number;i++){
                HashMap<String,String> map = new HashMap<>();
                map.put("code",charCode+tagNumber);
                map.put("qrcode","BB:"+charCode+tagNumber);
                list.add(map);
                map = null;
            }
            tagNumber = null;
        }
        printTagModel.setData(list);
        PrintTakeNumber1Activity.actionTo(this,printTagModel);
    }

}
