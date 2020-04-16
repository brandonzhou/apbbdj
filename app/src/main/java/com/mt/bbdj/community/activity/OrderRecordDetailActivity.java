package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.OrderRecordModel;

public class OrderRecordDetailActivity extends BaseActivity {

    private TextView tv_cut_money;
    private TextView tv_cut_time;
    private TextView tv_digndan;
    private TextView tv_yundan;
    private TextView tv_create_time;
    private TextView tv_order_state;
    private RelativeLayout iv_back;

    public static void actionTo(Context context, OrderRecordModel model) {
        Intent intent = new Intent(context,OrderRecordDetailActivity.class);
        intent.putExtra("data",model);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_record_detail);
        initView();
        initParams();
        initClickListener();
    }

    private void initParams() {
        OrderRecordModel model = (OrderRecordModel) getIntent().getSerializableExtra("data");
        if ("1".equals(model.getCallback_states())) {
            tv_cut_money.setText("等待快递公司返回重量（未扣款）");
            tv_cut_time.setVisibility(View.GONE);
        } else if (!"2".equals(model.getStates())){
            tv_cut_money.setText("无快递单号");
            tv_cut_time.setVisibility(View.GONE);
        } else {
            tv_cut_money.setText("-￥"+model.getSettle_money());
            tv_cut_time.setVisibility(View.VISIBLE);
        }

        tv_digndan.setText(model.getOrder_number());
        tv_create_time.setText(model.getCreate_time());
        tv_order_state.setText("1".equals(model.getFlag())?"正常":"已取消");
        tv_yundan.setText("2".equals(model.getStates())?model.getWaybill_number():"无");
    }

    private void initClickListener() {
        iv_back.setOnClickListener(view -> finish());
    }

    private void initView() {
        tv_cut_money = findViewById(R.id.tv_cut_money);
        tv_cut_time = findViewById(R.id.tv_cut_time);
        tv_digndan = findViewById(R.id.tv_digndan);
        tv_yundan = findViewById(R.id.tv_yundan);
        tv_create_time = findViewById(R.id.tv_create_time);
        tv_order_state = findViewById(R.id.tv_order_state);
        iv_back = findViewById(R.id.iv_back);
    }
}
