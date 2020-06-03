package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.core.DbUserUtil;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TakeOutModel;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.WaitHandleOrderAdapter;
import com.mylhyl.circledialog.CircleDialog;
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

public class WaitHandleOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;

    private WaitHandleOrderAdapter mAdapter;

    private TextView tv_no_data;

    private RelativeLayout rl_back;

    private TextView tv_title;

    private boolean isFresh = true;
    private RequestQueue mRequestQueue;
    private String user_id;
    private final int REQUEST_GET_ORDER = 100;
    private final int REQUEST_RECEIVE_ORDER = 200;     //接单
    private final int REQUEST_SEND_ORDER = 300;     //送达
    private final int REQUEST_CANNEL_ORDER = 400;     //取消
    private final int REQUEST_CHANGE_TYPE = 500;    //自己配送

    private List<TakeOutModel> mList = new ArrayList<>();
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_wait_order);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(WaitHandleOrderActivity.this);
        initView();
        initParams();
        initListener();
    }


    private void initListener() {
        mAdapter.setOnClickManagerListener(new WaitHandleOrderAdapter.WaitHandleOrderManager() {
            @Override
            public void OnCheckDetailClick(int position) {
                TakeOutModel takeOutModel = mList.get(position);
                ServiceDetailActivity.actionTo(WaitHandleOrderActivity.this, user_id, takeOutModel);
            }

            @Override
            public void OnReceiveOrderClick(int position) {
                //确认接单
                TakeOutModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                receiveOrder(order_id);
            }

            @Override
            public void OnConfirmSendClick(int position) {
                //确认送达
                TakeOutModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                confirmOrderSend(order_id);
            }

            @Override
            public void OnCallPhoneClick(int position) {
                String phone = mList.get(position).getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                startActivity(intent);
            }

            @Override
            public void OnCannelOrderClick(int position) {

                showProitDialog(position);

            }

            @Override
            public void OnDispathingClick(int position) {
                //配送
                DispatchingTypeActivity.actionTo(WaitHandleOrderActivity.this, mList.get(position));
            }

            @Override
            public void OnChangeSendType(int position) {
                //更改配送方式
                changeSendType(position);
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showProitDialog(int position) {
        new CircleDialog.Builder()
                .setTitle("提示")
                .setText("\n确定要删除此订单吗?\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //取消
                        TakeOutModel productModel = mList.get(position);
                        String order_id = productModel.getOrders_id();
                        cannelOrder(order_id);
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    private void changeSendType(int position) {

        showChangeTypeDialog(position);
    }

    private void showChangeTypeDialog(int position) {

        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setText("\n确认更改配送方式吗?\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //更改
                        changeDistributionMode(position);
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    private void changeDistributionMode(int position) {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", mList.get(position).getOrders_id());
        map.put("types", 1 + "");
        map.put("money", "0");
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.changeDistributionMode(signature, map);
        mRequestQueue.add(REQUEST_CHANGE_TYPE, request, mResponseListener);
    }

    private void cannelOrder(String order_id) {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", order_id);
        map.put("reason_id", "1");
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.cannelTakeOrders(signature, map);
        mRequestQueue.add(REQUEST_CANNEL_ORDER, request, mResponseListener);

    }

    private void receiveOrder(String order_id) {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", order_id);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.receiveTakeOrders(signature, map);
        mRequestQueue.add(REQUEST_RECEIVE_ORDER, request, mResponseListener);
    }

    private void confirmOrderSend(String order_id) {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", order_id);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.completeTakeOrders(signature, map);
        mRequestQueue.add(REQUEST_SEND_ORDER, request, mResponseListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        user_id = DbUserUtil.getStationId();

        mType = getIntent().getStringExtra("type");
        tv_title.setText("1".equals(mType) ? "待处理订单" : "已完成订单");
    }

    private void initView() {
        mAdapter = new WaitHandleOrderAdapter(this, mList);
        recyclerView = findViewById(R.id.rl_product);
        rl_back = findViewById(R.id.rl_back);
        tv_title = findViewById(R.id.tv_title);
        tv_no_data = findViewById(R.id.tv_no_data);
        recyclerView.setRefreshHeader(new ArrowRefreshHeader(this));
        recyclerView.setLoadingListener(this);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    private void requestData() {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("type", mType);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.getTakeOrders(signature, user_id, mType);
        mRequestQueue.add(REQUEST_GET_ORDER, request, mResponseListener);
    }

    OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(WaitHandleOrderActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "WaterOrderActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleEvent(what, jsonObject);
                } else {
                    if (what == REQUEST_GET_ORDER) {
                        if (isFresh) {
                            recyclerView.refreshComplete();
                        } else {
                            recyclerView.loadMoreComplete();
                        }
                    }
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.showShort("网络不稳定！");
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
            LoadDialogUtils.cannelLoadingDialog();
        }
    };

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_GET_ORDER:    //请求订单
                handleOrder(jsonObject);
                break;
            case REQUEST_RECEIVE_ORDER:    //接单
                handleReceiveOrder(jsonObject);
                break;
            case REQUEST_SEND_ORDER:    //送达
                handleSendOrder(jsonObject);
                break;
            case REQUEST_CANNEL_ORDER:    //取消
                handleCannelOrder(jsonObject);
                break;
            case REQUEST_CHANGE_TYPE:    //自己配送
                handleChangeType(jsonObject);
                break;
        }
    }

    private void handleChangeType(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);
        recyclerView.refresh();
    }

    private void handleCannelOrder(JSONObject jsonObject) {
        try {
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            if ("5001".equals(code)) {
                recyclerView.refresh();
            }
            ToastUtil.showShort(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSendOrder(JSONObject jsonObject) {
        try {
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            if ("5001".equals(code)) {
                recyclerView.refresh();
            }
            ToastUtil.showShort(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleReceiveOrder(JSONObject jsonObject) {
        try {
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");

            if ("5001".equals(code)) {
                recyclerView.refresh();
            } else {
                // ToastUtil.showShort(msg);
            }
            ToastUtil.showShort(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOrder(JSONObject jsonObject) throws JSONException {
        if (isFresh) {
            recyclerView.refreshComplete();
            mList.clear();
            mAdapter.notifyDataSetChanged();
        } else {
            recyclerView.loadMoreComplete();
        }
        JSONArray dataArray = jsonObject.getJSONArray("data");
        setWaterList(dataArray);
    }

    private void setWaterList(JSONArray dataArray) throws JSONException {
        int length = dataArray.length();
        for (int i = 0; i < length; i++) {
            List<HashMap<String, String>> list = new ArrayList<>();
            TakeOutModel takeOutModel = new TakeOutModel();
            JSONObject jsonObject = dataArray.getJSONObject(i);
            String id = jsonObject.getString("orders_id");
            takeOutModel.setOrder_number(jsonObject.getString("order_number"));
            takeOutModel.setMode(jsonObject.getString("mode"));
            String user_name = jsonObject.getString("member_name");
            String user_mobile = jsonObject.getString("member_mobile");
            String region = jsonObject.getString("member_region");
            String address = jsonObject.getString("member_address");
            String create_time = jsonObject.getString("create_time");
            String total = jsonObject.getString("total");
            // String juli_time = jsonObject.getString("juli_time");
            String states = jsonObject.getString("states");
            String pay_states = jsonObject.getString("pay_states");
            String courier_id = jsonObject.getString("courier_id");
            String member_latitude = jsonObject.getString("member_latitude");
            String member_longitude = jsonObject.getString("member_longitude");
            String distribution_mode = jsonObject.getString("distribution_mode");
            // String time_appointment = jsonObject.getString("time_appointment");
            JSONArray detailed = jsonObject.getJSONArray("detailed");

            for (int j = 0; j < detailed.length(); j++) {
                JSONObject jsonObject1 = detailed.getJSONObject(j);
                HashMap<String, String> map = new HashMap<>();
                map.put("price", jsonObject1.getString("price"));
                map.put("product_title", jsonObject1.getString("product_title"));
                map.put("number", jsonObject1.getString("number"));
                list.add(map);
                map = null;
            }
            //  time_appointment = DateUtil.changeStampToStandrdTime("HH:mm", time_appointment) + "上门 ";

            if ("2".equals(states)) {    //已结单的 快递员配送 或者 自己配送
                if (("2".equals(distribution_mode) ||"3".equals(distribution_mode)) && "0".equals(courier_id)) {
                    takeOutModel.setOrderState("10");    //等待快递员接单
                } else if (("2".equals(distribution_mode) ||"3".equals(distribution_mode)) && !"0".equals(courier_id)){
                    takeOutModel.setOrderState("12");     //等待快递员取件
                } else {
                    takeOutModel.setOrderState(states);
                }
            } else if ("3".equals(states) && !"0".equals(courier_id)) {
                takeOutModel.setOrderState("11");    //快递员配送中
            } else {
                takeOutModel.setOrderState(states);
            }

            takeOutModel.setCurrentTimeState("");
            takeOutModel.setAddress(region + address);
            takeOutModel.setPayStates(pay_states);
            takeOutModel.setTotal(total);
            takeOutModel.setLatitude(member_latitude);
            takeOutModel.setLongitude(member_longitude);
            takeOutModel.setEstimatedTime("");
            takeOutModel.setName(user_name);
            takeOutModel.setOrders_id(id);

            takeOutModel.setPhoneNumber(user_mobile);
            takeOutModel.setTakeOutList(list);
            mList.add(takeOutModel);
        }

        if (mList.size() == 0) {
            tv_no_data.setVisibility(View.VISIBLE);
        } else {
            tv_no_data.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        recyclerView.loadMoreComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
