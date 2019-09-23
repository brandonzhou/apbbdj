package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
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
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
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
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import okhttp3.ResponseBody;

public class WaterOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;

    private OrderAdapter mAdapter;

    private TextView tv_no_data;

    private RelativeLayout rl_back;

    private boolean isFresh = true;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private final int REQUEST_GET_ORDER = 100;    //桶装水订单
    private final int REQUEST_RECEIVE_ORDER = 200;     //接单
    private final int REQUEST_SEND_ORDER = 300;     //送达

    private List<ProductModel> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_order);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(WaterOrderActivity.this);

        initParams();
        initView();
        initListener();
    }

    private void initListener() {
        mAdapter.setOnCheckDetailListener(new OrderAdapter.ExpressInterfaceManager() {
            @Override
            public void OnCheckWaterOrderClick(int position) {
                ProductModel productModel = mList.get(position);
                int cannelType = productModel.getType();
                //查看桶装水的详情
                Intent intent = new Intent(WaterOrderActivity.this, WaterOrderDetailActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("cannel_type", cannelType);
                intent.putExtra("orderDetail", productModel);
                startActivity(intent);
            }

            @Override
            public void OnConfirmWaterReceiveClick(int position) {
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                //确认桶装水接单
                confirmWaterReive(order_id);
            }

            @Override
            public void OnConfirmWaterSendClick(int position) {
                //确认桶装水送达
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                confirmWaterSend(order_id);
            }

            @Override
            public void OnConfirWaterCannelClick(int position) {
                String mail_id = mList.get(position).getMail_id();
                String orders_id = mList.get(position).getOrders_id();
                //桶装水取消订单
                Intent intent = new Intent(WaterOrderActivity.this, CannelOrderActivity.class);
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
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void confirmWaterSend(String order_id) {
        Request<String> request = NoHttpRequest.confirmWaterSend(user_id,order_id);
        mRequestQueue.add(REQUEST_SEND_ORDER, request, mResponseListener);

    }

    private void confirmWaterReive(String order_id) {
        Request<String> request = NoHttpRequest.confirmWaterReive(user_id,order_id);
        mRequestQueue.add(REQUEST_RECEIVE_ORDER, request, mResponseListener);
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
        mAdapter = new OrderAdapter(this,mList);
        recyclerView = findViewById(R.id.rl_product);
        rl_back = findViewById(R.id.rl_back);
        tv_no_data = findViewById(R.id.tv_no_data);
        recyclerView.setRefreshHeader(new ArrowRefreshHeader(this));
        recyclerView.setLoadingListener(this);
        recyclerView.setItemViewCacheSize(10);
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
            case REQUEST_RECEIVE_ORDER:    //接单
                handleReceiveOrder(jsonObject);
                break;
            case REQUEST_SEND_ORDER:    //送达
                handleSendOrder(jsonObject);
                break;

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
                // ToastUtil.showShort(msg);s
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
        JSONObject water_order = dataObj.getJSONObject("water_order");
        JSONArray waterlist = water_order.getJSONArray("waterlist");
        setWaterList(waterlist);    //桶装水
    }

    private void setWaterList(JSONArray waterlist) throws JSONException {
        int length = waterlist.length();
        for (int i = 0; i < length; i++) {
            List<HashMap<String, String>> list = new ArrayList<>();
            ProductModel productModel = new ProductModel();
            JSONObject jsonObject = waterlist.getJSONObject(i);
            String id = jsonObject.getString("id");
            String user_name = jsonObject.getString("user_name");
            String user_mobile = jsonObject.getString("user_mobile");
            String region = jsonObject.getString("region");
            String address = jsonObject.getString("address");
            String create_time = jsonObject.getString("create_time");
            String order_number = jsonObject.getString("order_number");
            String accountPrice = jsonObject.getString("zongjia");
            String overtime = jsonObject.getString("overtime");
            String juli_time = jsonObject.getString("juli_time");
            String states = jsonObject.getString("states");
            String time_appointment = jsonObject.getString("time_appointment");
            JSONArray detailed = jsonObject.getJSONArray("detailed");

            for (int j = 0; j < detailed.length(); j++) {
                JSONObject jsonObject1 = detailed.getJSONObject(j);
                HashMap<String, String> map = new HashMap<>();
                map.put("id", jsonObject1.getString("id"));
                map.put("commodity_id", jsonObject1.getString("commodity_id"));
                map.put("commodity_name", jsonObject1.getString("commodity_name"));
                map.put("money", jsonObject1.getString("money"));
                map.put("number", jsonObject1.getString("number"));
                map.put("total", jsonObject1.getString("total"));
                map.put("create_time", jsonObject1.getString("create_time"));
                map.put("update_time", jsonObject1.getString("update_time"));
                list.add(map);
                map = null;
            }

            if("2".equals(overtime)) {
                juli_time = "已超时"+juli_time;
            } else {
                juli_time = "剩余"+juli_time;
            }

            productModel.setWaterMessageList(list);
            productModel.setShowType(i == 0);
            productModel.setJuli_time(juli_time);
            productModel.setType(1);
            productModel.setStates(states);
            productModel.setProductName(user_name);
            productModel.setContext(time_appointment);
            productModel.setOrders_id(id);
            productModel.setPhone(user_mobile);
            productModel.setCreateTime(create_time);
            productModel.setAccountPrice(accountPrice);
            productModel.setOrderNumber(order_number);
            productModel.setAddress(region + address);
            productModel.setShowBottom(i == waterlist.length() - 1);
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
