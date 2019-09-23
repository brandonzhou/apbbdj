package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.mt.bbdj.Manifest;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Goods;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.GoodsAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class AddGoodsNameActivity extends BaseActivity {

    private ImageView iv_picture;
    private RelativeLayout rl_back;
    private EditText tv_name;
    private Button bt_next;
    private RecyclerView rl_goods_name;

    private List<HashMap<String, String>> mGoodsData = new ArrayList<>();
    private String goods_id;
    private String imageUrl;
    private RequestQueue mRequestQueue;
    private String user_id;

    private final int REQUEST_NAME = 200;    //请求示例名称
    private GoodsAdapter goodsAdapter;
    private Goods mGoods;

    public static void actionTo(Context context, Goods goods) {
        Intent intent = new Intent(context, AddGoodsNameActivity.class);
        intent.putExtra("goods", goods);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.DESTORY) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_name);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(AddGoodsNameActivity.this);
        EventBus.getDefault().register(this);
        initView();
        initParams();    //初始化参数
        requestData();    //请求商品名称
        initListener();
    }

    private void initListener() {
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodsName = tv_name.getText().toString();
                if ("".equals(goodsName)) {
                    ToastUtil.showShort("请输入商品名称");
                } else {
                    mGoods.setGoods_name(goodsName);
                    AddGoodsPriceActivity.actionTo(AddGoodsNameActivity.this, mGoods);
                }
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //添加商品名称
        goodsAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String name = mGoodsData.get(position).get("name");
                mGoods.setGoods_name(name);
                AddGoodsPriceActivity.actionTo(AddGoodsNameActivity.this, mGoods);
            }
        });

    }

    private void requestData() {
        if ("".equals(goods_id) || null == goods_id) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("goods_id", goods_id);
        Request<String> request = NoHttpRequest.requestGoodsName(user_id, goods_id, params);
        mRequestQueue.add(REQUEST_NAME, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "AddGoodsNameActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            String name = dataArray.getJSONObject(i).getString("name");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("name", name);
                            mGoodsData.add(map);
                            map = null;
                        }
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goodsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    private void initView() {
        iv_picture = findViewById(R.id.iv_picture);
        rl_back = findViewById(R.id.rl_back);
        bt_next = findViewById(R.id.bt_next);
        tv_name = findViewById(R.id.tv_name);
        rl_goods_name = findViewById(R.id.rl_goods_name);
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
        mGoods = (Goods) getIntent().getSerializableExtra("goods");

        goods_id = mGoods.getGoods_id();
        Glide.with(this).load(mGoods.getImageUrl()).into(iv_picture);
        initRecycler();

    }

    private void initRecycler() {
        goodsAdapter = new GoodsAdapter(this, mGoodsData);
        rl_goods_name.setAdapter(goodsAdapter);
        rl_goods_name.addItemDecoration(new MarginDecoration(this));
        rl_goods_name.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
