package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.widget.DateChooseWheelViewDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class SelectExpressActivity extends BaseActivity {

    private TagFlowLayout expressLayout;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private final int REQUEST_EXPRESS = 100;
    private final int REQUEST_REQUEST_COMMIT_SETTING = 101;
    private LayoutInflater mInflater;
    private RelativeLayout tv_back;

    public static void actionTo(Context context) {
        Intent intent = new Intent(context, SelectExpressActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_express);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SelectExpressActivity.this);
        initView();
        initParams();
        requestData();   //请求数据
        initListener();
    }



    private void initListener() {
        //返回
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //免运费限度
        expressLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String express_tag = listOne.get(position).get("express_id");
                String express_name = listOne.get(position).get("express_name");
                ScannerActivity.actionTo(SelectExpressActivity.this,express_tag,express_name);
                return true;
            }
        });
        
    }


    private void initParams() {

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        mInflater = getLayoutInflater();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();

        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }


    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getExpressageRequest(user_id, "1");
        mRequestQueue.add(REQUEST_EXPRESS, request, mResponseListener);
    }

    private void initView() {
        expressLayout = findViewById(R.id.id_free_one);
        tv_back = findViewById(R.id.tv_back);
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(SelectExpressActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "LoginActivity::" + response.get());
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
                ToastUtil.showShort("请求失败！");
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            LoadDialogUtils.cannelLoadingDialog();
            ToastUtil.showShort("请求失败！");
        }

        @Override
        public void onFinish(int what) {
            //  LoadDialogUtils.cannelLoadingDialog();
        }
    };

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        if (what == REQUEST_EXPRESS) {
            setExpressData(jsonObject);
        }
    }

    private void setExpressData(JSONObject jsonObject) throws JSONException {
       JSONArray data = jsonObject.getJSONArray("data");
        setListOne(data);
    }

    List<HashMap<String, String>> listOne = new ArrayList<>();

    private void setListOne(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject entity = data.getJSONObject(i);
            String express_id = entity.getString("express_id");
            String express_logo = entity.getString("express_logo");
            String express_name = entity.getString("express_name");

            HashMap<String, String> map = new HashMap<>();
            map.put("express_id", express_id);
            map.put("express_logo", express_logo);
            map.put("express_name", express_name);
            listOne.add(map);
            map = null;
        }

        TagAdapter oneTagAdapter = new TagAdapter<HashMap<String, String>>(listOne) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_flow_express, expressLayout, false);
                tv.setText(map.get("express_name"));
                return tv;
            }
        };

       // oneTagAdapter.setSelectedList(position);
        expressLayout.setAdapter(oneTagAdapter);
    }
}
