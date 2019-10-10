package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ExpressMoney;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.ExpressMoneyAdapter;
import com.mylhyl.circledialog.CircleDialog;
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

public class SaveManagerMoneyActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private RecyclerView recyclerView;
    private ExpressMoneyAdapter mAdapter;
    private List<ExpressMoney> mList = new ArrayList<>();
    private String user_id="";
    private RequestQueue mRequestQueue;
    private final int REQUEST_EXPRESS_MONEY = 100;   //获取快递公司信息
    private final int REQUEST_SET = 200;   //设置价格

    public static void actionTo(Context context,String user_id) {
        Intent intent = new Intent(context,SaveManagerMoneyActivity.class);
        intent.putExtra("user_id",user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_manager_money);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SaveManagerMoneyActivity.this);
        initParams();
        initView();
        initListener();
        requestData();   //请求数据
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getExpressMoney(user_id);
        mRequestQueue.add(REQUEST_EXPRESS_MONEY,request,onResponseListener);
    }

    private OnResponseListener<String> onResponseListener= new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(SaveManagerMoneyActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "SaveManagerMoneyActivity::" + what + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleResult(what,jsonObject);
                    if (what == REQUEST_SET) {
                        ToastUtil.showShort(msg);
                    }
                }
                else{
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
    };

    private void handleResult(int what,JSONObject jsonObject) throws JSONException {
        if (what == REQUEST_EXPRESS_MONEY) {
            setExpressMoney(jsonObject);
        }
        if (what == REQUEST_SET) {

        }
    }

    private void setExpressMoney(JSONObject jsonObject) throws JSONException {
        mList.clear();
        mAdapter.notifyDataSetChanged();
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0;i < data.length();i++) {
            ExpressMoney expressMoney = new ExpressMoney();
            JSONObject obj = data.getJSONObject(i);
            expressMoney.setExpress_id(obj.getString("express_id"));
            expressMoney.setMoney_id(obj.getString("id"));
            expressMoney.setLogo(obj.getString("express_logo"));
            expressMoney.setPrice(StringUtil.handleNullResultForNumber(obj.getString("money"))+"元");
            expressMoney.setName(obj.getString("express_name"));
            mList.add(expressMoney);
        }
        mAdapter.notifyDataSetChanged();
    }


    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        recyclerView = findViewById(R.id.recycler);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#f8f8f8"), 1));
        mAdapter = new ExpressMoneyAdapter(this,mList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(SaveManagerMoneyActivity.this).resumeRequests();//恢复Glide加载图片
                }else {
                    Glide.with(SaveManagerMoneyActivity.this).pauseRequests();//禁止Glide加载图片
                }
            }
        });
    }

    private void initListener() {

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAdapter.setOnItemClickListener(new ExpressMoneyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                changeMoney(position);
            }
        });
    }

    private void changeMoney(int position) {
        ExpressMoney expressMoney = mList.get(position);
        new CircleDialog.Builder()
                .setCanceledOnTouchOutside(false)
                .setCancelable(true)
                .setTitle("提示")
                .setInputText(expressMoney.getPrice())
                .setInputHint("请输入寄存费用")
                .setInputHeight(120)
                .setInputShowKeyboard(true)
                .setInputEmoji(false)
                .configInput(params -> {
                    params.styleText = Typeface.BOLD;
                })
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        ToastUtil.showShort("内容不可为空");
                        return false;
                    } else if (!StringUtil.isDigit(getEffectiveNumber(text))){
                        ToastUtil.showShort("价格必须为数字");
                        return false;
                    }else {
                        expressMoney.setPrice(getEffectiveNumber(text)+"元");
                        mAdapter.notifyItemChanged(position);
                        commitMoney(expressMoney);
                        return true;
                    }
                })
                .show(getSupportFragmentManager());

    }

    private void commitMoney(ExpressMoney expressMoney) {
        Request<String> request = NoHttpRequest.changeExpressMoney(expressMoney,user_id);
        mRequestQueue.add(REQUEST_SET,request,onResponseListener);
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

    private String getEffectiveNumber(String data){
        if (data != null) {
            if (data.contains("元")){
                int end = data.lastIndexOf("元");
                String result = data.substring(0,end);
                return result;
            }
            else {
                return data;
            }
        }
        return "";
    }


}
