package com.mt.bbdj.community.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.utls.camera.CameraActivity;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.SimpleExpressAdapter;
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

public class SelectEnterExpressActivity extends BaseActivity {

    private RecyclerView rl_express;
    private Button bt_next;
    private RelativeLayout iv_back;
    private SimpleExpressAdapter expressAdapter;

    List<HashMap<String, String>> mExpress = new ArrayList<>();
    private String express_id;
    private String express_name;
    private RequestQueue mRequestQueue;
    private String user_id;

    private final int REQUEST_EXPRESS = 1001;

    public static void actionTo(Context context, String user_id) {
        Intent intent = new Intent(context, SelectEnterExpressActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_enter_express);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SelectEnterExpressActivity.this);
        initParams();
        initView();
        requestData();
        initClickListener();
    }

    private void initClickListener() {
        bt_next.setOnClickListener(view->{
            if ("".equals(express_id)){
                ToastUtil.showShort("请选择快递公司");
            } else {
                TakePictureActivity.actionTo(SelectEnterExpressActivity.this,"12",express_id,express_name);
            }
        });
    }


    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        user_id = getIntent().getStringExtra("user_id");

    }

    private void initView() {
        rl_express = findViewById(R.id.rl_express);
        bt_next = findViewById(R.id.bt_next);
        iv_back = findViewById(R.id.iv_back);
        expressAdapter = new SimpleExpressAdapter(this, mExpress);
        rl_express.setAdapter(expressAdapter);
        rl_express.addItemDecoration(new MarginDecoration(this));
        rl_express.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        expressAdapter.setOnItemClickListener(position -> {
            express_id = mExpress.get(position).get("express_id");
            express_name = mExpress.get(position).get("name");
            expressAdapter.setPosition(position);
            expressAdapter.notifyDataSetChanged();
        });
    }

    private void requestData() {
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);
        Request<String> request = NoHttpRequest.getExpressRequest(params);
        mRequestQueue.add(REQUEST_EXPRESS, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(SelectEnterExpressActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "SelectEnterExpressActivity::" + response.get());
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

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject entity = dataArray.getJSONObject(i);
            String express_id = entity.getString("express_id");
            String code = entity.getString("code");
            String express_name = entity.getString("express_name");
            String check_id = entity.getString("check_id");

            HashMap<String, String> map = new HashMap<>();
            map.put("express_id", express_id);
            map.put("code", code);
            map.put("name", express_name);
            map.put("check_id", check_id);
            mExpress.add(map);
            map = null;
        }
        expressAdapter.notifyDataSetChanged();
    }

}
