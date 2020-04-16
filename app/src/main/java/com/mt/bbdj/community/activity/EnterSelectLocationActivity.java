package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.utls.camera.CameraActivity;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.SimpleTypeAdapter;
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

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class EnterSelectLocationActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rl_shelf,rl_shelf_number,rl_shelf_account;
    private TagFlowLayout rl_express;
    private RelativeLayout rl_back;
    private TextView tv_next;

    String[] mLocalShelf = new String[]{"A","B","C","D","E","F","G","H","I","K","L","P","Q","R","S","T","U","V","W","X","Y","Z"};
    String[] mLocalNumber = new String[]{"1","2","3","4","5","6","7","8"};
    String[] mLocalAccount = new String[]{"1个","2个","3个","4个","5个","6个","7个","8个","9个","10个","11个","12个","13个","14个","15个","16个","17个","18个","19个","20个"};

    private String mShelf = "";
    private String mShelfNumber = "";
    private String mShelfAccount = "";
    private String express_id = "";
    private String user_id;
    private RequestQueue mRequestQueue;
    private final int REQUEST_EXPRESS = 1001;

    private List<HashMap<String,String>> mList = new ArrayList<>();
    private LayoutInflater mInflater;

    public static void actionTo(Context context,String user_id) {
        Intent intent = new Intent(context, EnterSelectLocationActivity.class);
        intent.putExtra("user_id",user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_select_location);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(EnterSelectLocationActivity.this);
        initParams();
        initView();
        requestData();
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        mInflater = getLayoutInflater();
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_next:   //下一步
                handleNextEvent();
                break;
        }
    }

    private void handleNextEvent() {
        if ("".equals(mShelf)){
            ToastUtil.showShort("请选择货架号");
        } else if ("".equals(mShelfNumber)){
            ToastUtil.showShort("请选择格子号");
        } else if ("".equals(mShelfAccount)) {
            ToastUtil.showShort("请选择放置数量");
        } else if ("".equals(express_id)){
            ToastUtil.showShort("请选择快递公司");
        }else {
          //  CameraActivity.actionTo(this,101,null,mShelf+mShelfNumber,mShelfNumber);
            mShelfAccount = mShelfAccount.replace("个","");
            CameraActivity.actionTo(this,101,null,mShelf,mShelfNumber,mShelfAccount,express_id);
        }
    }

    private void initView() {
        rl_shelf = findViewById(R.id.rl_shelf);
        rl_shelf_number = findViewById(R.id.rl_shelf_number);
        rl_shelf_account = findViewById(R.id.rl_shelf_account);
        rl_express = findViewById(R.id.rl_express);
        tv_next = findViewById(R.id.tv_next);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_next.setOnClickListener(this);

        initRecyclerShelf();
        initRecyclerShelfNumber();
        initRecyclerShelfAccount();
        initRecyclerExpress();
    }

    private void initRecyclerExpress() {
        rl_express.setOnTagClickListener((view, position, parent) -> {
            express_id = mList.get(position).get("express_id");
            return true;
        });
    }

    private void initRecyclerShelfAccount() {
        SimpleTypeAdapter goodsAdapter = new SimpleTypeAdapter(this, mLocalAccount);
        rl_shelf_account.setAdapter(goodsAdapter);
        rl_shelf_account.addItemDecoration(new MarginDecoration(this));
        rl_shelf_account.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setOnItemClickListener(position -> {
            mShelfAccount = mLocalAccount[position];
            goodsAdapter.setPosition(position);
            goodsAdapter.notifyDataSetChanged();
        });
    }

    private void initRecyclerShelfNumber() {
        SimpleTypeAdapter goodsAdapter = new SimpleTypeAdapter(this, mLocalNumber);
        rl_shelf_number.setAdapter(goodsAdapter);
        rl_shelf_number.addItemDecoration(new MarginDecoration(this));
        rl_shelf_number.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setOnItemClickListener(position -> {
            mShelfNumber = mLocalNumber[position];
            goodsAdapter.setPosition(position);
            goodsAdapter.notifyDataSetChanged();
        });
    }

    private void initRecyclerShelf() {
        SimpleTypeAdapter goodsAdapter = new SimpleTypeAdapter(this, mLocalShelf);
        rl_shelf.setAdapter(goodsAdapter);
        rl_shelf.addItemDecoration(new MarginDecoration(this));
        rl_shelf.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setOnItemClickListener(position -> {
            mShelf = mLocalShelf[position];
            goodsAdapter.setPosition(position);
            goodsAdapter.notifyDataSetChanged();
        });
    }


    private void requestData() {
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);
        Request<String> request = NoHttpRequest.getExpressList(params);
        mRequestQueue.add(REQUEST_EXPRESS, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(EnterSelectLocationActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "EnterSelectLocationActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        handleExpress(jsonObject);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    LoadDialogUtils.cannelLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                    ToastUtil.showShort("网络异常请重试！");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                ToastUtil.showShort("网络异常请重试！");
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void handleExpress(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i= 0;i < data.length();i++) {
            JSONObject obj= data.getJSONObject(i);
            String express_id = obj.getString("express_id");
            String express_name = obj.getString("express_name");
            HashMap<String,String> map = new HashMap<>();
            map.put("express_id",express_id);
            map.put("express_name",express_name);
            mList.add(map);
            map = null;
        }

        TagAdapter oneTagAdapter = new TagAdapter<HashMap<String, String>>(mList) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_flow_express, rl_express, false);
                tv.setText(map.get("express_name"));
                return tv;
            }
        };
        rl_express.setAdapter(oneTagAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
