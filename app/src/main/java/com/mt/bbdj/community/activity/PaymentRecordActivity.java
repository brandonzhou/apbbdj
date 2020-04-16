package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.PaymentRecordModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
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
import java.util.List;

public class PaymentRecordActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private XRecyclerView recycler;
    private RelativeLayout iv_back;

    private PaymentAdapter mAdapter;

    private int mPage = 1;

    private boolean isFresh = true;

    private List<PaymentRecordModel> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;

    private final int REQUEST_CONSUME_REQUEST = 1001;
    private String user_id = "";

    public static void actionTo(Context context, String user_id) {
        Intent intent = new Intent(context, PaymentRecordActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_record);
        initView();
        initParams();
        initClickListener();
        recycler.refresh();
    }

    private void initClickListener() {
        iv_back.setOnClickListener(view -> finish());
        mAdapter.setOnItemClickListener(position -> {
            PaymentDetailActivity.actionTo(PaymentRecordActivity.this,user_id,mList.get(position).getId());
        });
    }

    private void initParams() {

        mRequestQueue = NoHttp.newRequestQueue();
        user_id = getIntent().getStringExtra("user_id");
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getConsumeRecordRequest(user_id, mPage);
        mRequestQueue.add(REQUEST_CONSUME_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "PaymentRecordActivity::" + response.get());
                if (isFresh) {
                    recycler.refreshComplete();
                } else {
                    recycler.loadMoreComplete();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    if ("5001".equals(code)) {
                        setData(dataObj);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    ToastUtil.showShort(e.getMessage());
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

    private void setData(JSONObject dataObj) throws JSONException {
        if (isFresh) {
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }
        JSONArray list = dataObj.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject obj = list.getJSONObject(i);
            PaymentRecordModel model = new PaymentRecordModel();
            model.setId(obj.getString("id"));
            model.setTitle(obj.getString("title"));
            model.setCon_amount(StringUtil.handleNullResultForNumber(obj.getString("con_amount")));
            model.setCon_balance(StringUtil.handleNullResultForNumber(obj.getString("con_balance")));
            model.setTime(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",obj.getString("time")));
            model.setBudget(obj.getString("budget"));
            model.setTypes(obj.getString("types"));
            model.setWaybill_number(obj.getString("waybill_number"));
            mList.add(model);
            model = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        recycler = findViewById(R.id.recycler);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.addItemDecoration(new MarginDecoration(this, 10));
        recycler.setLayoutManager(mLayoutManager);
        mAdapter = new PaymentAdapter(mList);
        recycler.setLoadingListener(this);
        recycler.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        mPage = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        mPage++;
        requestData();
    }

}
