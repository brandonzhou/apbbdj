package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.AddShelvesAdapter;
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

public class AddShelvesAndGoodsActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private XRecyclerView recycler;
    private TextView textview_serach;
    private RelativeLayout rl_back;

    private List<HashMap<String,String>> mList = new ArrayList<>();
    private AddShelvesAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;

    private final int REQUEST_SHELVE_LIST = 100;      //货架列表

    private final int REQUEST_ADD_SHLVES = 101;    //添加货架

    private int mPage = 1;

    private String search = "";

    private int currentPosition = 0;

    public static void actionTo(Context context,String user_id) {
        Intent intent = new Intent(context, AddShelvesAndGoodsActivity.class);
        intent.putExtra("user_id",user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shelves_and_goods);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(AddShelvesAndGoodsActivity.this);
        initView();
        initParams();
        requestData();   //请求货架数据
        initListerner();
    }

    private void initListerner() {

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAdapter.setOnActionListener(new AddShelvesAdapter.OnActionListener() {
            @Override
            public void onItemClick(int position) {
                currentPosition = position;
                addShelves(mList.get(position));
            }
        });

        //搜素界面
        textview_serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchShelvesGoodsActivity.actionTo(AddShelvesAndGoodsActivity.this,user_id);
            }
        });


    }

    private void addShelves(HashMap<String, String> data) {
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("preset_id", data.get("preset_id"));
        Request<String> request = NoHttpRequest.addShelvesAndGoods(params);
        mRequestQueue.add(REQUEST_ADD_SHLVES, request, onResponseListener);
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void requestData() {
        mPage = 1;
        isFresh =true;
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("page", mPage+"");
        params.put("name", search);
        Request<String> request = NoHttpRequest.searchGetPreset(params);
        mRequestQueue.add(REQUEST_SHELVE_LIST, request, onResponseListener);
    }

    public OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(AddShelvesAndGoodsActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "AddShelvesActivity::" + response.get());
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
            case REQUEST_SHELVE_LIST:     //货架列表
                setShelvesList(jsonObject);
                break;
            case REQUEST_ADD_SHLVES:    //添加货架
                addShelvesData(jsonObject);
                break;
        }

    }

    private void addShelvesData(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);
        HashMap<String,String> map =  mList.get(currentPosition);
        map.put("type","2");
        mAdapter.notifyItemChanged(currentPosition+1,map);
    }

    private void setShelvesList(JSONObject jsonObject) throws JSONException {
        if (isFresh) {
            recycler.refreshComplete();
            mList.clear();
            mAdapter.notifyDataSetChanged();
        } else {
            recycler.loadMoreComplete();
        }

        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0;i < dataArray.length();i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            HashMap<String,String> map = new HashMap<>();
            map.put("name",obj.getString("name"));
            map.put("type",obj.getString("flag"));
            map.put("preset_id",obj.getString("preset_id"));
            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        textview_serach = findViewById(R.id.textview_serach);
        rl_back = findViewById(R.id.rl_back);
        recycler = findViewById(R.id.recycler);
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        mAdapter = new AddShelvesAdapter(mList);
        recycler.setAdapter(mAdapter);
        recycler.setLoadingListener(this);
    }

    private boolean isFresh = true;
    @Override
    public void onRefresh() {
        mPage = 1;
        isFresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        mPage ++;
        isFresh = false;
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mList.clear();
        mList = null;
    }
}
