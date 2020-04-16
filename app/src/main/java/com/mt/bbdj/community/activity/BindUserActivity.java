package com.mt.bbdj.community.activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.BindUserAdapter;
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

public class BindUserActivity extends BaseActivity
        implements XRecyclerView.LoadingListener {
    private int ACTION_REQUEST_BIND_USER = 100;
    private RelativeLayout ic_back;
    private boolean isFresh = true;
    private BindUserAdapter mAdapter;
    private List<HashMap<String, String>> mList = new ArrayList();
    private int mPage = 1;
    private RequestQueue mRequestQueue;
    private XRecyclerView recyclerView;
    private String user_id;

    private void initListener() {
        this.ic_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                BindUserActivity.this.finish();
            }
        });
    }

    private void initParams() {
        this.mRequestQueue = NoHttp.newRequestQueue();
        List localList = GreenDaoManager.getInstance().getSession().getUserBaseMessageDao().queryBuilder().list();
        if ((localList != null) && (localList.size() != 0))
            this.user_id = ((UserBaseMessage) localList.get(0)).getUser_id();
    }

    private void initRecyclerView() {
        this.mAdapter = new BindUserAdapter(this.mList);
        this.recyclerView.setFocusable(false);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLoadingListener(this);
        this.recyclerView.addItemDecoration(new MyDecoration(this, 1, Color.parseColor("#f4f4f4"), 1));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.recyclerView.setAdapter(this.mAdapter);
    }

    private void initView() {
        this.ic_back = ((RelativeLayout) findViewById(R.id.iv_back));
        this.recyclerView = ((XRecyclerView) findViewById(R.id.recycler));
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_bind_user);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(BindUserActivity.this);
        initParams();
        initView();
        initRecyclerView();
        initListener();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mRequestQueue.cancelAll();
        this.mRequestQueue.stop();
        this.mRequestQueue = null;
    }

    public void onLoadMore() {
        this.isFresh = false;
        this.mPage += 1;
        requestData();
    }

    public void onRefresh() {
        this.isFresh = true;
        this.mPage = 1;
        requestData();
    }

    protected void onResume() {
        super.onResume();
        this.recyclerView.refresh();
    }

    public void requestData() {
        Request<String> localRequest = NoHttpRequest.getStationUser(this.user_id, this.mPage);
        this.mRequestQueue.add(this.ACTION_REQUEST_BIND_USER, localRequest, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    JSONObject dataJson = new JSONObject(response.get());
                    String localObject = dataJson.getString("code");
                    String msg = dataJson.getString("msg");
                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }

                    if ("5001".equals(localObject)) {
                        if (isFresh) {
                            mList.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                        JSONArray dataArray = dataJson.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject jsonObject = dataArray.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("username", StringUtil.handleNullResultForString(jsonObject.getString("username")));
                            map.put("create_time", StringUtil.handleNullResultForString(jsonObject.getString("create_time")));
                            mList.add(map);
                            map = null;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException paramAnonymousResponse) {
                    paramAnonymousResponse.printStackTrace();
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
}
