package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.model.OrderRecordModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.OrderRecordAdapter;
import com.mt.bbdj.community.adapter.PaymentAdapter;
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

public class OrderRecordActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private XRecyclerView recycler;
    private RelativeLayout iv_back;

    private OrderRecordAdapter mAdapter;

    private String user_id = "";

    private int mPages = 1;
    private RequestQueue mRequestQueue;

    private final int REQUEST_GET_ORDER = 1001;   //订单记录

    private List<OrderRecordModel> mList = new ArrayList<>();

    public static void actionTo(Context context, String user_id) {
        Intent intent = new Intent(context, OrderRecordActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_record);
        initView();
        initParams();
        initListener();
        recycler.refresh();
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        recycler = findViewById(R.id.recycler);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        recycler.addItemDecoration(new MarginDecoration(this, 10));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setLoadingListener(this);
        mAdapter = new OrderRecordAdapter(mList);
        recycler.setAdapter(mAdapter);
    }

    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("page", mPages + "");
        Request<String> request = NoHttpRequest.getOrderRecorde(params);
        mRequestQueue.add(REQUEST_GET_ORDER, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "OrderRecordActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONArray data = jsonObject.getJSONArray("data");

                    if (isFresh) {
                        recycler.refreshComplete();
                    } else {
                        recycler.loadMoreComplete();
                    }
                    if ("5001".equals(code)) {
                        if (isFresh) {
                            mList.clear();
                            mAdapter.notifyDataSetChanged();
                            setData(data);
                        }
                    } else {
                        ToastUtil.showShort(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void setData(JSONArray data) throws JSONException {

        for (int i = 0; i < data.length(); i++) {
            JSONObject object = data.getJSONObject(i);
            OrderRecordModel model = new OrderRecordModel();
            model.setOrder_number(StringUtil.handleNullResultForString(object.getString("order_number")));
            model.setWaybill_number(StringUtil.handleNullResultForString(object.getString("waybill_number")));
            model.setSettle_money(StringUtil.handleNullResultForString(object.getString("settle_money")));
            model.setFlag(StringUtil.handleNullResultForNumber(object.getString("flag")));
            model.setStates(StringUtil.handleNullResultForNumber(object.getString("states")));
            String create_time = object.getString("create_time");
            String callback_time = object.getString("callback_time");
            model.setCreate_time(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",create_time));
            model.setCallback_states(StringUtil.handleNullResultForNumber(object.getString("callback_states")));
            model.setCallback_time(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",callback_time));
            mList.add(model);
            model = null;
        }
        mAdapter.notifyDataSetChanged();
    }


    private void initListener() {
        iv_back.setOnClickListener(view -> finish());

        mAdapter.setOnClickListener(position -> {
            OrderRecordDetailActivity.actionTo(OrderRecordActivity.this,mList.get(position));
        });
    }

    private boolean isFresh = true;

    @Override
    public void onRefresh() {
        isFresh = true;
        mPages = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        mPages++;
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll();
            mRequestQueue.stop();
        }
    }
}
