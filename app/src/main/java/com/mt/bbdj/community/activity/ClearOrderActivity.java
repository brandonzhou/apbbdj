package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.ClearOrderAdapter;
import com.mt.bbdj.community.adapter.OrderAdapter;
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

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ClearOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;

    private ClearOrderAdapter mAdapter;

    private TextView tv_no_data;

    private RelativeLayout rl_back;


    private boolean isFresh = true;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private final int REQUEST_GET_ORDER = 100;   //干洗订单
    private final int REQUEST_CONFIRM_ORDER = 200;    //确认订单送达
    private final int REQUEST_RECEIVE_ORDER = 300;    //接单

    private List<ProductModel> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_order);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ClearOrderActivity.this);

        initParams();
        initView();
        initListener();
    }

    private void initListener() {
        mAdapter.setExpressInterfaceManager(new ClearOrderAdapter.ExpressInterfaceManager() {
            @Override
            public void OnCheckClearOrderClick(int position) {
                // 有按钮的时候跳转 ClearDetailActivity  否则显示的是状态界面
                //查看干洗详情
                Intent intent = new Intent();
                ProductModel productModel = mList.get(position);
                int clearState = productModel.getClearState();
                if (clearState == 5 || clearState == 6 || clearState == 8) {    //状态为等待干洗店取件、送件、
                    intent.setClass(ClearOrderActivity.this, ClearStateActivity.class);
                } else {
                    intent.setClass(ClearOrderActivity.this, ClearDetailActivity.class);
                }
                intent.putExtra("productModel", productModel);
                startActivity(intent);
            }

            @Override
            public void OnConfirmClearSendClick(int position) {
                //确认干洗送达
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                confirmClearSend(order_id);
            }

            @Override
            public void OnConfirmRefauseClick(int position) {
                //干洗拒绝接单
                String mail_id = mList.get(position).getMail_id();
                String orders_id = mList.get(position).getOrders_id();
                //取消订单
                Intent intent = new Intent(ClearOrderActivity.this, CannelOrderActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                intent.putExtra("orders_id", orders_id);
                intent.putExtra("cannel_type", CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
                startActivity(intent);
            }

            @Override
            public void OnCallClick(int position) {
                String phone = mList.get(position).getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                startActivity(intent);
            }

            @Override
            public void OnCommitPrice(int position) {
                //干洗提交报价
                ProductModel productModel = mList.get(position);
                Intent intent = new Intent(ClearOrderActivity.this, QuotedPriceActivity.class);
                intent.putExtra("productModel",productModel);
                startActivity(intent);
            }

            @Override
            public void OnClearReceiveClick(int position) {
                //干洗接单
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                receiveClearOrder(order_id);
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void receiveClearOrder(String order_id) {
        Request<String> request = NoHttpRequest.receiveClearOrder(user_id, order_id);
        mRequestQueue.add(REQUEST_RECEIVE_ORDER, request, mResponseListener);
    }

    private void confirmClearSend(String order_id) {
        Request<String> request = NoHttpRequest.confirmClearSend(user_id, order_id);
        mRequestQueue.add(REQUEST_CONFIRM_ORDER, request, mResponseListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

    }

    private void initView() {
        mAdapter = new ClearOrderAdapter(this, mList);
        recyclerView = findViewById(R.id.rl_product);
        tv_no_data = findViewById(R.id.tv_no_data);
        rl_back = findViewById(R.id.rl_back);
        recyclerView.setRefreshHeader(new ArrowRefreshHeader(this));
        recyclerView.setLoadingListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getExpressDetailRequest(user_id);
        mRequestQueue.add(REQUEST_GET_ORDER, request, mResponseListener);
    }

    OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

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
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.showShort("网络不稳定！");
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_GET_ORDER:    //请求订单
                handleOrder(jsonObject);
                break;
            case REQUEST_CONFIRM_ORDER:  //干洗送达
                handleConfirmOrder(jsonObject);
                break;
            case REQUEST_RECEIVE_ORDER:   //干洗接单
                handleReceiveOrder(jsonObject);
                break;
        }
    }

    private void handleReceiveOrder(JSONObject jsonObject) {
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

    private void handleConfirmOrder(JSONObject jsonObject) {
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

    private void handleOrder(JSONObject jsonObject) throws JSONException {
        if (isFresh) {
            recyclerView.refreshComplete();
            mList.clear();
            mAdapter.notifyDataSetChanged();
        } else {
            recyclerView.loadMoreComplete();
        }
        JSONObject dataObj = jsonObject.getJSONObject("data");
        JSONObject cleaning_order = dataObj.getJSONObject("cleaning_order");
        JSONArray cleaninglist = cleaning_order.getJSONArray("cleaninglist");
        setClearList(cleaninglist);   //干洗
    }

    private void setClearList(JSONArray cleaninglist) throws JSONException {
        int length = cleaninglist.length();
        for (int i = 0; i < cleaninglist.length(); i++) {
            List<HashMap<String, String>> list = new ArrayList<>();
            JSONObject jsonObject = cleaninglist.getJSONObject(i);
            String id = jsonObject.getString("id");
            String user_name = jsonObject.getString("user_name");
            String user_mobile = jsonObject.getString("user_mobile");
            String region = jsonObject.getString("region");
            String address = jsonObject.getString("address");
            String time_appointment = jsonObject.getString("time_appointment");
            String pay_states = jsonObject.getString("pay_states");
            String states = jsonObject.getString("states");
            String order_number = jsonObject.getString("order_number");
            String create_time = jsonObject.getString("create_time");
            String zongjia = jsonObject.getString("zongjia");
            String overtime = jsonObject.getString("overtime");
            String juli_time = jsonObject.getString("juli_time");
            JSONArray detailed = jsonObject.getJSONArray("detailed");
            ProductModel productModel = new ProductModel();
            productModel.setContext(time_appointment);

            for (int j = 0; j < detailed.length(); j++) {
                JSONObject data = detailed.getJSONObject(j);
                HashMap<String, String> map = new HashMap<>();
                map.put("id", data.getString("id"));
                map.put("commodity_id", data.getString("commodity_id"));
                map.put("orders_id", data.getString("orders_id"));
                map.put("commodity_name", data.getString("commodity_name"));
                map.put("money", data.getString("money"));
                map.put("number", data.getString("number"));
                map.put("total", data.getString("total"));
                map.put("create_time", data.getString("create_time"));
                map.put("update_time", data.getString("update_time"));
                list.add(map);
                map = null;
            }
            productModel.setClearMessageList(list);

            int stateTag = Integer.parseInt(states);


            if (stateTag == 8 && pay_states.equals("1")) {
                productModel.setClearStateName("等待用户支付");
            } else if (stateTag == 5) {
                productModel.setClearStateName("等待干洗店取件");
            } else if (stateTag == 6) {
                productModel.setClearStateName("等待干洗店送件");
            } else if (stateTag == 7) {
                productModel.setClearStateName("等待用户取件");
            }

            if ("2".equals(overtime)) {
                juli_time = "已超时" + juli_time;
            } else {
                juli_time = "剩余" + juli_time;
            }

            productModel.setShowType(i == 0);
            productModel.setAccountPrice(zongjia);
            productModel.setCreateTime(create_time);
            productModel.setJuli_time(juli_time);
            productModel.setOrderNumber(order_number);
            productModel.setClearState(stateTag);
            productModel.setOrders_id(id);
            productModel.setType(2);
            productModel.setPayfor(pay_states);
            productModel.setProductName(user_name);
            productModel.setPhone(user_mobile);
            productModel.setAddress(region + address);
            productModel.setShowBottom(i == cleaninglist.length() - 1);
            mList.add(productModel);
            productModel = null;
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
