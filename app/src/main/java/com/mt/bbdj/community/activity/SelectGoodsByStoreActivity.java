package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.FontRequest;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.StoreGoodsModel;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.CouponForUserAdapter;
import com.mt.bbdj.community.adapter.SelectGoodsByStoreAdapter;
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

public class SelectGoodsByStoreActivity extends BaseActivity implements XRecyclerView.LoadingListener, View.OnClickListener {

    private RelativeLayout rl_back;
    private XRecyclerView recyclerView;
    private String user_id;
    private String type_id;
    private RequestQueue mRequestQueue;
    private PopupWindow popupWindow;
    private TextView textview_serach;

    private final int REQUEST_GOODS_STORE = 1001;    //请求商品库数据
    private final int REQUEST_ADD_GOODS = 1002;    //添加商品
    private final int REQUEST_GET_GOODS_PRICE = 1003;    //获取商品建议价格
    private final int REQUEST_SEARCH_GOODS = 1004;    //搜索价格
    private SelectGoodsByStoreAdapter mAdapter;

    private List<StoreGoodsModel> mList = new ArrayList<>();
    private String warehouse_shelves_id;
    private LayoutInflater mInflater;
    List<HashMap<String, String>> listOne = new ArrayList<>();
    private ImageView iv_goods_image;
    private TextView tv_goods_name;
    private EditText et_price_self;
    private TagFlowLayout rl_goods_price;
    private int currentPosition = 0;
    private String goodsPrice;

    public static void actionTo(Context context, String user_id, String type_id, String warehouse_shelves_id) {
        Intent intent = new Intent(context, SelectGoodsByStoreActivity.class);
        intent.putExtra("type_id", type_id);
        intent.putExtra("user_id", user_id);
        intent.putExtra("warehouse_shelves_id", warehouse_shelves_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_goods_by_store);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SelectGoodsByStoreActivity.this);
        initParams();
        initView();
        initGoodsManagerDialog();
        initClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void initClickListener() {
        //返回
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //添加
        mAdapter.setOnClickManager(new SelectGoodsByStoreAdapter.OnClickManager() {
            @Override
            public void onAddClick(int position) {
                currentPosition = position;
                // addGoods(position);
                requestGoodsPrice(position);    //请求商品价格
                showMessageDialog(position);
            }
        });

        //添加商品
        rl_goods_price.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                goodsPrice = listOne.get(position).get("price");
                et_price_self.setText("");
                return true;
            }
        });

        //焦点变换
        et_price_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_price_self.setCursorVisible(true);
            }
        });

        textview_serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchGoodsActivity.actionTo(SelectGoodsByStoreActivity.this,user_id,type_id);
            }
        });
    }


    private void showMessageDialog(int position) {
        Glide.with(SelectGoodsByStoreActivity.this).load(mList.get(position).getGoods_img()).into(iv_goods_image);
        tv_goods_name.setText(mList.get(position).getGoods_name());
        et_price_self.setCursorVisible(false);
        et_price_self.setText("");
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(rl_back, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void searData(String content) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("search", content);
        params.put("page", mPage+"");
        Request<String> request = NoHttpRequest.searchGoodsRequest(params);
        mRequestQueue.add(REQUEST_SEARCH_GOODS, request, onResponseListener);
    }


    private void addGoods(int position) {
        String price  = et_price_self.getText().toString();
        if (!"".equals(price)) {
            goodsPrice = price;
        }
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("class_id", type_id);
        params.put("title", mList.get(position).getGoods_name());
        params.put("thumb", mList.get(position).getGoods_img());
        params.put("price",goodsPrice);
        params.put("house_id", mList.get(position).getGoods_id());
        Request<String> request = NoHttpRequest.addGoodsRequest(params);
        mRequestQueue.add(REQUEST_ADD_GOODS, request, onResponseListener);
    }

    private void requestGoodsPrice(int position) {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("goods_id", mList.get(position).getGoods_id());
        Request<String> request = NoHttpRequest.getGoodsPriceRequest(params);
        mRequestQueue.add(REQUEST_GET_GOODS_PRICE, request, onResponseListener);
    }


    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        type_id = getIntent().getStringExtra("type_id");
        warehouse_shelves_id = getIntent().getStringExtra("warehouse_shelves_id");

        mRequestQueue = NoHttp.newRequestQueue();
        mInflater = getLayoutInflater();
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        recyclerView = findViewById(R.id.recycler);
        textview_serach = findViewById(R.id.textview_serach);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#f8f8f8"), 1));
        mAdapter = new SelectGoodsByStoreAdapter(this, mList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLoadingListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(SelectGoodsByStoreActivity.this).resumeRequests();//恢复Glide加载图片
                } else {
                    Glide.with(SelectGoodsByStoreActivity.this).pauseRequests();//禁止Glide加载图片
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    private boolean isFresh = true;

    private int mPage = 1;

    @Override
    public void onRefresh() {

        mPage = 1;
        isFresh = true;

        String content = textview_serach.getText().toString();
        if ("".equals(content)) {
            reqeustGoodsStoreData();    //请求商品库数据
        } else {
            searData(content);
        }

    }


    @Override
    public void onLoadMore() {
        mPage++;
        isFresh = false;
        String content = textview_serach.getText().toString();
        if ("".equals(content)) {
            reqeustGoodsStoreData();    //请求商品库数据
        } else {
            searData(content);
        }
    }

    private void reqeustGoodsStoreData() {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("type_id", warehouse_shelves_id);
        params.put("page", mPage + "");
        Request<String> request = NoHttpRequest.getGoodsStore(params);
        mRequestQueue.add(REQUEST_GOODS_STORE, request, onResponseListener);
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(SelectGoodsByStoreActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "SelectGoodsByStoreActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if (isFresh) {
                    recyclerView.refreshComplete();
                } else {
                    recyclerView.loadMoreComplete();
                }
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
            case REQUEST_GOODS_STORE:    //请求商品库
                setGoodsData(jsonObject);
                break;
            case REQUEST_ADD_GOODS:    //添加商品
                refreshData(jsonObject);
                break;
            case REQUEST_GET_GOODS_PRICE:   //获取建议价格
                setGoodsPrice(jsonObject);
                break;
            case REQUEST_SEARCH_GOODS:   //搜索商品
                setGoodsData(jsonObject);
                break;
        }
    }

    private void setGoodsPrice(JSONObject jsonObject) throws JSONException {
        listOne.clear();
        int position = 0;
        JSONObject dataObj = jsonObject.getJSONObject("data");
        JSONArray priceArray = dataObj.getJSONArray("price");
        for (int i = 0; i < priceArray.length(); i++) {
            JSONObject obj = priceArray.getJSONObject(i);
            HashMap<String, String> map = new HashMap<>();
            map.put("price", obj.getString("price"));
            map.put("price_number", "￥"+obj.getString("price"));
            map.put("type", obj.getString("type"));
            String type = obj.getString("type");
            if ("1".equals(type)) {
                position = i;
            }
            listOne.add(map);
            map = null;
        }


        TagAdapter oneTagAdapter = new TagAdapter<HashMap<String, String>>(listOne) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_goods_price, rl_goods_price, false);
                tv.setText(map.get("price_number"));
                return tv;
            }
        };

        if (listOne.size() !=0) {
            oneTagAdapter.setSelectedList(position);
            goodsPrice = listOne.get(position).get("price");
        }

        rl_goods_price.setAdapter(oneTagAdapter);
    }

    private void refreshData(JSONObject jsonObject) {
        //reqeustGoodsStoreData();    //请求商品库数据
        //recyclerView.refresh();
        StoreGoodsModel storeGoodsModel = mList.get(currentPosition);
        storeGoodsModel.setFlag("2");
        mAdapter.notifyItemChanged(currentPosition+1,storeGoodsModel);
    }

    private void setGoodsData(JSONObject jsonObject) throws JSONException {
        if (isFresh) {
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }

        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            StoreGoodsModel storeGoodsModel = new StoreGoodsModel();
            JSONObject obj = dataArray.getJSONObject(i);
            storeGoodsModel.setGoods_id(obj.getString("goods_id"));
            storeGoodsModel.setFlag(obj.getString("flag"));
            storeGoodsModel.setGoods_img(obj.getString("goods_img"));
            String goods_name = getEffectiveName(obj.getString("goods_name"));
            storeGoodsModel.setGoods_name(goods_name);
            storeGoodsModel.setGoods_price(obj.getString("goods_price"));
            mList.add(storeGoodsModel);
            storeGoodsModel = null;
        }
        mAdapter.setData(mList);
        mAdapter.notifyDataSetChanged();
    }

    private String getEffectiveName(String goods_name) {

        if ("".equals(goods_name) || goods_name.length() == 0) {
            return "";
        }

        if (goods_name.length() > 30) {
            return goods_name.substring(0, 30) + "...";
        } else {
            return goods_name;
        }
    }


    private void initGoodsManagerDialog() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.add_goods, null);
            RelativeLayout iv_delete = selectView.findViewById(R.id.iv_delete);
            iv_goods_image = selectView.findViewById(R.id.iv_goods_image);
            tv_goods_name = selectView.findViewById(R.id.tv_goods_name);
            et_price_self = selectView.findViewById(R.id.et_price_self);
            LinearLayout layout_left_close = selectView.findViewById(R.id.layout_left_close);
            TextView tv_add_confirm = selectView.findViewById(R.id.tv_add_confirm);
            rl_goods_price = selectView.findViewById(R.id.rl_goods_price);
            tv_add_confirm.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
            layout_left_close.setOnClickListener(this);

            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_delete:
                popupWindow.dismiss();
                break;
            case R.id.tv_add_confirm:
                popupWindow.dismiss();
                addGoods(currentPosition);
                break;
            case R.id.layout_left_close:
                popupWindow.dismiss();
                break;
        }
    }
}
