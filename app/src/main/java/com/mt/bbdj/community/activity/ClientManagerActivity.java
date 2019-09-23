package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.ClientManagerAdapter;
import com.mt.bbdj.community.adapter.WaitPrintAdapter;
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

public class ClientManagerActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private RelativeLayout icBack;
    private TextView clientAddressManager;
    private XRecyclerView recyclerView;
    private ClientManagerAdapter mAdapter;
    private TextView tvNoAddress;
    private TextView tvClientManager;

    private List<HashMap<String, String>> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private String user_id;
    private boolean isFresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_manager);
        initView();
        initParams();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void initListener() {
        tvClientManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientManagerActivity.this, ClientMessageDetail.class);
                startActivity(intent);
            }
        });

        //列表点击
        mAdapter.setOnItemClickListener(new ClientManagerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                HashMap<String,String> map = mList.get(position);
                Intent intent = new Intent(ClientManagerActivity.this,OrderDetailActivity.class);
                intent.putExtra("customer_id",map.get("customer_id"));
                startActivity(intent);
            }
        });

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getClientListRequest(user_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                // dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ClientManagerActivity::" + response.get());
                try {
                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }
                    mList.clear();
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONArray data = jsonObject.getJSONArray("data");
                    if ("5001".equals(code)) {
                        if (data.length() == 0) {
                            tvNoAddress.setVisibility(View.VISIBLE);
                        } else {
                            tvNoAddress.setVisibility(View.GONE);
                            setData(data);
                        }
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                //   dialogLoading.cancel();
            }
        });
    }

    private void setData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String customer_id = jsonObject.getString("customer_id");
            String customer_realname = jsonObject.getString("customer_realname");
            String content = jsonObject.getString("content");
            String todaysum = jsonObject.getString("todaysum");
            String monthsum = jsonObject.getString("monthsum");
            String todaymoney = jsonObject.getString("todaymoney");
            String monthmoney = jsonObject.getString("monthmoney");

            todaysum = StringUtil.handleNullResultForNumber(todaysum);
            monthsum = StringUtil.handleNullResultForNumber(monthsum);
            todaymoney = StringUtil.handleNullResultForNumber(todaymoney);
            monthmoney = StringUtil.handleNullResultForNumber(monthmoney);
            HashMap<String,String> map = new HashMap<>();
            map.put("customer_id",customer_id);
            map.put("customer_realname",customer_realname);
            map.put("content",content);
            map.put("todaysum","寄件"+todaysum+"件");
            map.put("todaymoney",todaymoney+"元");
            map.put("monthsum","寄件"+monthsum+"件");
            map.put("monthmoney",monthmoney+"元");
            mList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        icBack = findViewById(R.id.iv_back);
        clientAddressManager = findViewById(R.id.bt_commit);
        recyclerView = findViewById(R.id.rl_client_manager);
        tvNoAddress = findViewById(R.id.tv_no_address);
        tvClientManager = findViewById(R.id.bt_client_manager);
        initRecycler();
    }

    private void initRecycler() {
        mAdapter = new ClientManagerAdapter(this, mList);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL,
                Color.parseColor("#f4f4f4"), 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        requestData();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
