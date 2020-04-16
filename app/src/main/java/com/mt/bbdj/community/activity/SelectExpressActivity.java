package com.mt.bbdj.community.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
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
    private int requestCode = -1;

    public static void actionTo(Context context) {
        Intent intent = new Intent(context, SelectExpressActivity.class);
        context.startActivity(intent);
    }

    public static void actionTo(Activity context, int requestCode) {
        Intent intent = new Intent(context, SelectExpressActivity.class);
        intent.putExtra("requestCode",requestCode);
        context.startActivityForResult(intent,requestCode);
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

//        findViewById(R.id.textview_serach).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ScannerPrintActivity.actionTo(SelectExpressActivity.this, "", "","","");
//            }
//        });
    }


    private void initListener() {
        //返回
        tv_back.setOnClickListener(view -> finish());

        //免运费限度
        expressLayout.setOnTagClickListener((view, position, parent) -> {
            String express_tag = listOne.get(position).get("express_id");
            String express_name = listOne.get(position).get("express_name");
            String code = listOne.get(position).get("code");
            String check_id = listOne.get(position).get("check_id");
            if (requestCode == -1) {
                //  ScannerActivity.actionTo(SelectExpressActivity.this, express_tag, express_name);
                 ScannerPrintActivity.actionTo(SelectExpressActivity.this, express_tag, express_name,code,check_id);
            } else {
                //修改进入
                Intent intent = new Intent();
                intent.putExtra("express_tag",express_tag);
                intent.putExtra("express_name",express_name);
                intent.putExtra("code",code);
                setResult(RESULT_OK,intent);
            }
            finish();
            return true;
        });

    }


    private void initParams() {
        Intent intent = getIntent();
        requestCode = intent.getIntExtra("requestCode",-1);
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
//        Request<String> request = NoHttpRequest.getExpressageRequest(user_id, "1");
//        mRequestQueue.add(REQUEST_EXPRESS, request, mResponseListener);
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);
        Request<String> request = NoHttpRequest.getExpressRequest(params);
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
        JSONArray dataArray = jsonObject.getJSONArray("data");
        setListOne(dataArray);
    }

    List<HashMap<String, String>> listOne = new ArrayList<>();

    private void setListOne(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject entity = data.getJSONObject(i);
            String express_id = entity.getString("express_id");
            String code = entity.getString("code");
            String express_name = entity.getString("express_name");
            String check_id = entity.getString("check_id");

            HashMap<String, String> map = new HashMap<>();
            map.put("express_id", express_id);
            map.put("code", code);
            map.put("express_name", express_name);
            map.put("check_id", check_id);
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
