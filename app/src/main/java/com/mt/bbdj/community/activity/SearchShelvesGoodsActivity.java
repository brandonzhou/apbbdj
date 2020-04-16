package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
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

public class SearchShelvesGoodsActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private String user_id;
    private RelativeLayout rl_back;
    private TextView tv_cannel;
    private ImageView iv_delete;
    private XRecyclerView recycler;
    private AddShelvesAdapter mAdapter;
    private EditText textview_serach;
    private int mPage = 1;
    private String search = "";

    private List<HashMap<String,String>> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;

    private final int REQUEST_SHELVE_LIST = 100;      //货架列表

    private final int REQUEST_ADD_SHLVES = 101;    //添加货架

    private int currentPosition = 0;

    public static void actionTo(Context context,String user_id) {
        Intent intent = new Intent(context, SearchShelvesGoodsActivity.class);
        intent.putExtra("user_id",user_id);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shelves);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SearchShelvesGoodsActivity.this);
        initParams();
        initView();
        initRecycler();
        initListener();
    }

    private void initRecycler() {
        recycler = findViewById(R.id.recycler);
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        mAdapter = new AddShelvesAdapter(mList);
        recycler.setAdapter(mAdapter);
        recycler.setLoadingListener(this);
    }

    private void initListener() {

        mAdapter.setOnActionListener(new AddShelvesAdapter.OnActionListener() {
            @Override
            public void onItemClick(int position) {
                currentPosition = position;
                addShelves(mList.get(position));
            }
        });


        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_cannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textview_serach.setText("");
                mList.clear();
                mAdapter.notifyDataSetChanged();
                mPage = 1;
                isFresh =true;
            }
        });


        //键盘搜索
        textview_serach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    SystemUtil.hideKeyBoard(SearchShelvesGoodsActivity.this,textview_serach);
                    String content = textview_serach.getText().toString();
                    if ("".equals(content)) {
                        ToastUtil.showShort("搜索内容不可为空");
                        return true;
                    }
                    search = content;
                    requestData();    //开始搜索
                    return true;
                }
                return false;
            }
        });

    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        tv_cannel = findViewById(R.id.tv_cannel);
        iv_delete = findViewById(R.id.iv_delete);
        textview_serach = findViewById(R.id.textview_serach);
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private boolean isFresh = true;


    private void requestData() {
        if ("".equals(search)) {
            return;
        }
        mPage = 1;
        isFresh =true;
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("page", mPage+"");
        params.put("name", search);
        Request<String> request = NoHttpRequest.searchGetPreset(params);
        mRequestQueue.add(REQUEST_SHELVE_LIST, request, onResponseListener);
    }

    private void addShelves(HashMap<String, String> data) {
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("preset_id", data.get("preset_id"));
        Request<String> request = NoHttpRequest.addShelvesAndGoods(params);
        mRequestQueue.add(REQUEST_ADD_SHLVES, request, onResponseListener);
    }


    public OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(SearchShelvesGoodsActivity.this);
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
