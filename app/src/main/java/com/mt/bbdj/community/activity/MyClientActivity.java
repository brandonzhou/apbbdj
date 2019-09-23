package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.UserForCouponModel;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.CouponForUserAdapter;
import com.mt.bbdj.community.adapter.MyClientAdapter;
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

public class MyClientActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private String user_id="";

    private RelativeLayout rl_back;

    private XRecyclerView recyclerView;

    private final int REQUEST_GET_MY_CLIENT = 1001;      //获取我的客户

    private List<UserForCouponModel> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private MyClientAdapter mAdapter;
    private int mPage = 1;
    
    public static void actionTo(Context context, String user_id) {
        Intent intent = new Intent(context,MyClientActivity.class);
        intent.putExtra("user_id",user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_client);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(MyClientActivity.this);
        initParams();
        initView();
        initClickListener();
        initRecyclerView();
        requestData();
    }

    private void requestData() {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("page", mPage+"");
        Request<String> request = NoHttpRequest.getMyClient(params);
        mRequestQueue.add(REQUEST_GET_MY_CLIENT, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(MyClientActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MyClientActivity::" + what + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if (isFresh) {
                        recyclerView.refreshComplete();
                        mList.clear();
                        mAdapter.notifyDataSetChanged();
                    }else {
                        recyclerView.loadMoreComplete();
                    }

                    if ("5001".equals(code)) {
                        handleEvent(what, jsonObject);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    LoadDialogUtils.cannelLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                    ToastUtil.showShort(e.getMessage());
                }
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

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");

        JSONArray fans = dataObj.getJSONArray("fans");
        for (int i = 0; i < fans.length();i++) {
            JSONObject obj = fans.getJSONObject(i);
            UserForCouponModel model = new UserForCouponModel();
            model.setMember_id(obj.getString("member_id"));
            model.setLast_buy_time(obj.getString("last_buy_time"));
            model.setHeadImge(obj.getString("user_headimg"));
            model.setType(obj.getString("type"));
            model.setUser_name(obj.getString("user_name"));
            mList.add(model);
            model = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {

        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#f8f8f8"), 1));
        mAdapter = new MyClientAdapter(this,mList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(MyClientActivity.this).resumeRequests();//恢复Glide加载图片
                }else {
                    Glide.with(MyClientActivity.this).pauseRequests();//禁止Glide加载图片
                }
            }
        });
    }

    private void initClickListener() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        recyclerView = findViewById(R.id.recycler);
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    private boolean isFresh = true;

    @Override
    public void onRefresh() {
        isFresh = true;
        mPage = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        mPage ++;
        requestData();
    }
}
