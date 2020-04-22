package com.mt.bbdj.community.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.BluetoothScanAdapter;
import com.mt.bbdj.community.adapter.OutExceptionAdapter;
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

public class OutExceptionActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private RelativeLayout iv_back;
    private Button bt_out;
    private OutExceptionAdapter mAdapter;
    private List<Map<String, String>> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private final int TYPE_GET = 100;
    private final int TYPE_COMMIT = 101;
    private String express_id,mNumber,unusual_type;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;


    public static void actionTo(Context context, String express_id,String number) {
        Intent intent = new Intent(context, OutExceptionActivity.class);
        intent.putExtra("express_id", express_id);
        intent.putExtra("number", number);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_exception);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(OutExceptionActivity.this);
        initView();
        initParams();
        initClickListener();
        requestException();
    }

    private void initClickListener() {
        bt_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(unusual_type)){
                    ToastUtil.showShort("请选择异常出库原因");
                } else{
                    outHourse();
                }
            }
        });
    }

    private void outHourse() {
        Map<String, String> map = new HashMap<>();
        map.put("unusual_type", unusual_type);
        map.put("station_id", user_id);
        map.put("number", mNumber);
        Request<String> request = NoHttpRequest.outHourse(map);
        mRequestQueue.add(TYPE_COMMIT, request, onResponseListener);
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        express_id = getIntent().getStringExtra("express_id");
        mNumber = getIntent().getStringExtra("number");
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void requestException() {
        Map<String, String> map = new HashMap<>();
        map.put("express_id", express_id);
        Request<String> request = NoHttpRequest.outException(map);
        mRequestQueue.add(TYPE_GET, request, onResponseListener);
    }


    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.d("outException==",response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    setData(what,jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
                LoadDialogUtils.cannelLoadingDialog();
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            ToastUtil.showShort(response.getException().getMessage());
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {
            LoadDialogUtils.cannelLoadingDialog();
        }
    };

    private void setData(int what, JSONObject jsonObject) throws JSONException {
        if (what == TYPE_GET){
            setList(jsonObject);
        } else {
            ToastUtil.showShort("异常出库成功");
            finish();
        }
    }

    private void setList(JSONObject jsonObject) throws JSONException {
        mList.clear();
        mAdapter.notifyDataSetChanged();
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            String tag = obj.getString("tag");
            String exception = StringUtil.handleNullResultForString(obj.getString("exception"));
            Map<String,String> map = new HashMap<>();
            map.put("tag",tag);
            map.put("exception",exception);
            mList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        recycler = findViewById(R.id.recycler);
        iv_back = findViewById(R.id.iv_back);
        bt_out = findViewById(R.id.bt_out);

        mAdapter = new OutExceptionAdapter(mList);
        recycler.setAdapter(mAdapter);
        recycler.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#f8f8f8"), 1));
        recycler.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(new OutExceptionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                unusual_type = mList.get(position).get("tag");
                mAdapter.setCurrentPosition(position);
            }
        });
    }
}
