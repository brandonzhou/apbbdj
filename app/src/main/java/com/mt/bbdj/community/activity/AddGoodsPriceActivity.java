package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
 import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Goods;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class AddGoodsPriceActivity extends BaseActivity {

    private ImageView iv_picture;
    private RelativeLayout rl_back;
    private EditText tv_price;
    private Button bt_next;
    private Goods mGoods;
    private String user_id;
    private RequestQueue mRequestQueue;

    private final int REQUEST_ADD_GOODS = 100;    //添加商品

    public static void actionTo(Context context, Goods goods) {
        Intent intent = new Intent(context, AddGoodsPriceActivity.class);
        intent.putExtra("goods", goods);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_price);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(AddGoodsPriceActivity.this);
        initView();
        initParams();

        initListener();
    }

    private void initListener() {
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = tv_price.getText().toString();
                if ("".equals(price)) {
                    ToastUtil.showShort("价格不能为空");
                } else {
                    mGoods.setPrice(price);
                    commitGoods();     //提交商品信息
                }
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void commitGoods() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("shelves_id", mGoods.getShelves_id());
        params.put("name", mGoods.getGoods_name());
        params.put("shelves_name", mGoods.getShelces_name());
        params.put("img", mGoods.getImageUrl());
        params.put("code_id", mGoods.getCode_id());
        params.put("price", mGoods.getPrice());
        params.put("class_name", mGoods.getShelces_name());
        params.put("class_id", mGoods.getShelves_id());
        params.put("lib_goods_id", mGoods.getGoods_id());
        Request<String> request = NoHttpRequest.commitGoodsRequest(params);
        mRequestQueue.add(REQUEST_ADD_GOODS, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                EventBus.getDefault().post(new TargetEvent(TargetEvent.DESTORY));
                LoadDialogUtils.getInstance().showLoadingDialog(AddGoodsPriceActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "AddGoodsNameActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        finish();
                    }
                    ToastUtil.showShort(msg);
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
                LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }

    private void initParams() {
        mGoods = (Goods) getIntent().getSerializableExtra("goods");
        Glide.with(this).load(mGoods.getImageUrl()).into(iv_picture);
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initView() {
        iv_picture = findViewById(R.id.iv_picture);
        rl_back = findViewById(R.id.rl_back);
        bt_next = findViewById(R.id.bt_next);
        tv_price = findViewById(R.id.tv_price);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
