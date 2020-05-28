package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.ProducelDetailAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 收支明细详情
 */
public class PaymentDetailActivity extends BaseActivity{

    private RecyclerView recycler;
    private RelativeLayout iv_back;

    private ProducelDetailAdapter mAdapter;

    private LinearLayout ll_send_layout,ll_goods_weight_layout,ll_goods_name_layout,ll_title_layout,ll_way_number_layout,ll_time_layout,ll_balance_layout;
    private LinearLayout ll_takeout_layout,ll_takeout_end_layout,ll_distribution_layout,ll_content_layout,ll_order_layout,ll_service_layout;

    private TextView tv_goods_name,tv_goods_weight,tv_title,tv_way_number,tv_balance,tv_time,tv_first_title;
    private TextView tv_send,tv_send_phone,tv_send_address,tv_receive,tv_receive_phone,tv_receive_address,tv_cut_money;
    private TextView tv_takeout_create_time,tv_takeout_end_time,tv_content,tv_distribution_type,tv_order_number,tv_user_name,tv_user_phone;

    private AppCompatTextView tv_user_address;

    private List<HashMap<String,String>> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;

    private final int REQUEST_DETAIL_REQUEST = 1001;
    private String user_id = "";
    private String recored_id = "";

    public static void actionTo(Context context,String user_id,String id) {
        Intent intent = new Intent(context, PaymentDetailActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        initView();
        initParams();
        requestData();
    }

    private void initParams() {
        iv_back.setOnClickListener(view -> finish());
        mRequestQueue = NoHttp.newRequestQueue();
        user_id = getIntent().getStringExtra("user_id");
        recored_id = getIntent().getStringExtra("id");
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getConsumeDetailRequest(user_id, recored_id);
        mRequestQueue.add(REQUEST_DETAIL_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(PaymentDetailActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "PaymentDetailActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject data = jsonObject.getJSONObject("data");
                    String types = data.getString("types");
                    if ("5001".equals(code)) {  //寄件信息
                        if ("1".equals(types) || "2".equals(types)){
                            setSendMessage(data);
                        } else if ("5".equals(types)){  //服务订单
                            setServiceMessage(data);
                        }else if ("6".equals(types)){    //外卖订单
                            setTakeOutMessage(data);
                        } else {     //派件服务、余额充值
                            setOtherMessage(data);
                        }
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    ToastUtil.showShort(e.getMessage());
                    LoadDialogUtils.cannelLoadingDialog();
                }
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void setOtherMessage(JSONObject data) throws JSONException {
        showOtherPannelView();
        String budget = data.getString("budget");
        if ("1".equals(budget)){
            tv_cut_money.setText("-￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        } else {
            tv_cut_money.setText("+￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        }
        tv_first_title.setText(StringUtil.handleNullResultForString(data.getString("title")));
        tv_title.setText(StringUtil.handleNullResultForString(data.getString("title")));
        tv_time.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",data.getString("create_time")));
        tv_balance.setText(StringUtil.handleNullResultForString("￥"+data.getString("con_balance")));
    }




    private void setServiceMessage(JSONObject data) throws JSONException {
        showServicePannelView();    //展示相应的界面
        JSONObject people = data.getJSONObject("people");
        String budget = data.getString("budget");
        if ("1".equals(budget)){
            tv_cut_money.setText("-￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        } else {
            tv_cut_money.setText("+￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        }
        tv_first_title.setText(StringUtil.handleNullResultForString(data.getString("title")));
        tv_balance.setText(StringUtil.handleNullResultForString("￥"+data.getString("con_balance")));
        tv_takeout_create_time.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",data.getString("create_time")));
        tv_order_number.setText(StringUtil.handleNullResultForString(data.getString("order_number")));
        tv_user_name.setText(StringUtil.handleNullResultForString(people.getString("user_name")));
        tv_user_phone.setText(StringUtil.handleNullResultForString(people.getString("user_mobile")));
        tv_user_address.setText(StringUtil.handleNullResultForString(people.getString("address")));
    }



    private void setTakeOutMessage(JSONObject data) throws JSONException {
        showTakeOutPannelView();    //展示相应的界面
        JSONObject people = data.getJSONObject("people");
        String budget = data.getString("budget");
        JSONArray detailed = people.getJSONArray("detailed");
        if ("1".equals(budget)){
            tv_cut_money.setText("-￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        } else {
            tv_cut_money.setText("+￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        }
        tv_first_title.setText(StringUtil.handleNullResultForString(data.getString("title")));
        tv_takeout_create_time.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",data.getString("create_time")));
        tv_takeout_end_time.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",people.getString("end_time")));
        tv_content.setText(StringUtil.handleNullResultForString(people.getString("content")));
        tv_distribution_type.setText("1".equals(people.getString("distribution_mode"))?"商家配送":"快递员配送");

        for (int i = 0;i < detailed.length();i++) {
            JSONObject obj = detailed.getJSONObject(i);
            HashMap<String,String> map = new HashMap<>();
            map.put("product_title",obj.getString("product_title"));
            map.put("number",obj.getString("number"));
            map.put("price",obj.getString("price"));
            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }


    private void setSendMessage(JSONObject data) throws JSONException {
        JSONObject people = data.getJSONObject("people");
        showSendMessagePannelView();   //展示相应的面板
        tv_goods_name.setText(StringUtil.handleNullResultForString(people.getString("goods_name")));
        tv_goods_weight.setText(StringUtil.handleNullResultForString(people.getString("goods_weight")+"kg"));
        tv_title.setText(StringUtil.handleNullResultForString(data.getString("title")));
        tv_way_number.setText(StringUtil.handleNullResultForString(people.getString("waybill_number")));
        tv_balance.setText(StringUtil.handleNullResultForString("￥"+data.getString("con_balance")));
        tv_time.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",data.getString("create_time")));
        tv_first_title.setText(StringUtil.handleNullResultForString(data.getString("title")));
        String budget = data.getString("budget");
        if ("1".equals(budget)){
            tv_cut_money.setText("-￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        } else {
            tv_cut_money.setText("+￥"+StringUtil.handleNullResultForNumber(data.getString("con_amount")));
        }
        tv_send.setText(StringUtil.handleNullResultForString(people.getString("send_name")));
        tv_send_phone.setText(StringUtil.handleNullResultForString(people.getString("send_phone")));
        tv_send_address.setText(StringUtil.handleNullResultForString(people.getString("send_region"))
                +StringUtil.handleNullResultForString(people.getString("send_address")));

        tv_receive.setText(StringUtil.handleNullResultForString(people.getString("collect_name")));
        tv_receive_phone.setText(StringUtil.handleNullResultForString(people.getString("collect_phone")));
        tv_receive_address.setText(StringUtil.handleNullResultForString(people.getString("collect_region"))
                +StringUtil.handleNullResultForString(people.getString("collect_address")));
    }

    private void showOtherPannelView() {
        ll_title_layout.setVisibility(View.VISIBLE);
        ll_time_layout.setVisibility(View.VISIBLE);
        //ll_balance_layout.setVisibility(View.VISIBLE);
    }

    private void showServicePannelView() {
        ll_takeout_layout.setVisibility(View.VISIBLE);
        //ll_balance_layout.setVisibility(View.VISIBLE);
        ll_order_layout.setVisibility(View.VISIBLE);
        ll_service_layout.setVisibility(View.VISIBLE);
    }

    private void showTakeOutPannelView() {
        recycler.setVisibility(View.VISIBLE);
        ll_takeout_layout.setVisibility(View.VISIBLE);
        ll_takeout_end_layout.setVisibility(View.VISIBLE);
        ll_distribution_layout.setVisibility(View.VISIBLE);
        ll_content_layout.setVisibility(View.VISIBLE);
    }

    private void showSendMessagePannelView() {
        ll_send_layout.setVisibility(View.VISIBLE);
        ll_goods_name_layout.setVisibility(View.VISIBLE);
        ll_goods_weight_layout.setVisibility(View.VISIBLE);
        ll_title_layout.setVisibility(View.VISIBLE);
        ll_way_number_layout.setVisibility(View.VISIBLE);
        ll_time_layout.setVisibility(View.VISIBLE);
        //ll_balance_layout.setVisibility(View.VISIBLE);
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        recycler = findViewById(R.id.recycler);
        ll_send_layout = findViewById(R.id.ll_send_layout);
        tv_goods_name = findViewById(R.id.tv_goods_name);
        tv_goods_weight = findViewById(R.id.tv_goods_weight);
        tv_title = findViewById(R.id.tv_title);
        tv_way_number = findViewById(R.id.tv_way_number);
        tv_balance = findViewById(R.id.tv_balance);
        tv_time = findViewById(R.id.tv_time);
        tv_first_title = findViewById(R.id.tv_first_title);
        tv_send = findViewById(R.id.tv_send);
        tv_send_phone = findViewById(R.id.tv_send_phone);
        tv_send_address = findViewById(R.id.tv_send_address);
        tv_receive = findViewById(R.id.tv_receive);
        tv_receive_phone = findViewById(R.id.tv_receive_phone);
        tv_receive_address = findViewById(R.id.tv_receive_address);
        tv_cut_money = findViewById(R.id.tv_cut_money);


        ll_goods_weight_layout = findViewById(R.id.ll_goods_weight_layout);
        ll_goods_name_layout = findViewById(R.id.ll_goods_name_layout);
        ll_title_layout = findViewById(R.id.ll_title_layout);
        ll_way_number_layout = findViewById(R.id.ll_way_number_layout);
        ll_time_layout = findViewById(R.id.ll_time_layout);
        ll_balance_layout = findViewById(R.id.ll_balance_layout);
        ll_takeout_layout = findViewById(R.id.ll_takeout_layout);
        ll_takeout_end_layout = findViewById(R.id.ll_takeout_end_layout);
        ll_distribution_layout = findViewById(R.id.ll_distribution_layout);
        ll_content_layout = findViewById(R.id.ll_content_layout);
        tv_takeout_create_time = findViewById(R.id.tv_takeout_create_time);
        tv_takeout_end_time = findViewById(R.id.tv_takeout_end_time);
        tv_content = findViewById(R.id.tv_content);
        tv_distribution_type = findViewById(R.id.tv_distribution_type);
        ll_order_layout = findViewById(R.id.ll_order_layout);
        tv_order_number = findViewById(R.id.tv_order_number);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_phone = findViewById(R.id.tv_user_phone);
        tv_user_address = findViewById(R.id.tv_user_address);
        ll_order_layout = findViewById(R.id.ll_order_layout);
        ll_service_layout = findViewById(R.id.ll_service_layout);
        initRecyclerView();
    }
    private void initRecyclerView() {
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        mAdapter = new ProducelDetailAdapter(this,mList);
        recycler.setAdapter(mAdapter);
    }

}
