package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.CouponModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.ComplainAdapter;
import com.mt.bbdj.community.adapter.CouponAdapter;
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
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class CouponActivity extends BaseActivity {
    private LinearLayout ll_create_coupon;
    private RelativeLayout rl_back;
    private String user_id;
    private RequestQueue mRequestQueue;
    private final int REQUEST_SEARCH_COUPON = 1001;    //查询优惠券

    private List<CouponModel> mList = new ArrayList<>();
    private CouponAdapter mAdapter;
    private RecyclerView recycler;

    public static void actionTo(Context context, String user_id) {
        Intent intent = new Intent(context, CouponActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(CouponActivity.this);

        initView();
        initParams();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestCoupon();    //请求代金券
    }

    private void requestCoupon() {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        Request<String> request = NoHttpRequest.checkCouponData(params);
        mRequestQueue.add(REQUEST_SEARCH_COUPON, request, onResponseListener);
    }

    public OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(CouponActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "CouponActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("statusCode").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleEvent(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
    };

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_SEARCH_COUPON:    //查询优惠券
                searchCoupon(jsonObject);
                break;
        }
    }

    private void searchCoupon(JSONObject jsonObject) throws JSONException {
        mList.clear();
        mAdapter.notifyDataSetChanged();
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            CouponModel couponModel = new CouponModel();
            couponModel.setCoupon_id(obj.getString("coupon_id"));
            couponModel.setTerm_money(obj.getString("term_money"));
            couponModel.setReduction_money(obj.getString("reduction_money"));
            couponModel.setTypes(obj.getString("types"));
            couponModel.setEffection(obj.getString("flag"));
            String startTime = getStartTime(obj.getString("starttime"));
            String endTime = getEndTime(obj.getString("endtime"));
            couponModel.setStarttime(startTime);
            couponModel.setEndtime(endTime);
            mList.add(couponModel);
            couponModel = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    private String getEndTime(String endtime) {
        if ("".equals(endtime)) {
            return "";
        }

        String[] times = endtime.split(" ");
        String[] dates = times[0].split("-");
        return dates[1] + "-" + dates[2] + " 23:59";
    }

    private String getStartTime(String starttime) {
        if ("".equals(starttime)) {
            return "";
        }
        String[] times = starttime.split(" ");
        String[] dates = times[0].split("-");
        return dates[1] + "-" + dates[2] + " " + times[1];
    }

    private void initListener() {
        //创建优惠券
        ll_create_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCouponActivity.actionTo(CouponActivity.this);
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //发券
        mAdapter.setOnClickManager(new CouponAdapter.OnClickManager() {
            @Override
            public void onSendCouponClick(int position) {
                //发放优惠券
                CouponForUserActivity.actionTo(CouponActivity.this,user_id,mList.get(position));
            }

            @Override
            public void onItemClick(int position) {
                //优惠券详情

                CouponUseDetailActivity.actionTo(CouponActivity.this,user_id,mList.get(position).getCoupon_id());
            }
        });
    }

    private int currrentPosition = 0;

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initView() {
        ll_create_coupon = findViewById(R.id.ll_create_coupon);
        rl_back = findViewById(R.id.rl_back);
        recycler = findViewById(R.id.recycler);
        initRecycler();
    }


    private void initRecycler() {
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        mAdapter = new CouponAdapter(mList);
        recycler.setAdapter(mAdapter);
    }
}
