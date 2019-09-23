package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.mt.bbdj.community.adapter.AddShelvesAdapter;
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

public class SearchGoodsActivity extends BaseActivity implements XRecyclerView.LoadingListener, View.OnClickListener {

    private String user_id;
    private String type_id;
    private RelativeLayout rl_back;
    private TextView tv_cannel;
    private ImageView iv_delete;
    private XRecyclerView recycler;
    private SelectGoodsByStoreAdapter mAdapter;
    private EditText textview_serach;
    private int mPage = 1;
    private String search = "";
    private TagFlowLayout rl_goods_price;

    private List<StoreGoodsModel> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;

    private final int REQUEST_SEARCH_GOODS = 100;      //搜索

    private final int REQUEST_ADD_SHLVES = 101;    //添加货架

    private final int REQUEST_ADD_GOODS = 1002;    //添加商品
    private final int REQUEST_GET_GOODS_PRICE = 1003;    //获取商品建议价格

    private int currentPosition = 0;

    private LayoutInflater mInflater;
    List<HashMap<String, String>> listOne = new ArrayList<>();

    private PopupWindow popupWindow;
    private ImageView iv_goods_image;
    private TextView tv_goods_name;
    private EditText et_price_self;
    private String goodsPrice;

    public static void actionTo(Context context,String user_id,String class_id) {
        Intent intent = new Intent(context, SearchGoodsActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("class_id",class_id);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_goods);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SearchGoodsActivity.this);
        initParams();
        initView();
        initRecycler();
        initGoodsManagerDialog();
        initListener();
    }

    private void initRecycler() {
        recycler = findViewById(R.id.recycler);
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        mAdapter = new SelectGoodsByStoreAdapter(this,mList);
        recycler.setAdapter(mAdapter);
        recycler.setLoadingListener(this);
    }

    private void initListener() {

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


        //键盘搜索
        textview_serach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    SystemUtil.hideKeyBoard(SearchGoodsActivity.this,textview_serach);
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
        type_id = getIntent().getStringExtra("class_id");
        mRequestQueue = NoHttp.newRequestQueue();
        mInflater = getLayoutInflater();
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
        params.put("search", search);
        params.put("page", mPage+"");
        Request<String> request = NoHttpRequest.searchGoodsRequest(params);
        mRequestQueue.add(REQUEST_SEARCH_GOODS, request, onResponseListener);
    }

    private void addShelves(HashMap<String, String> data) {
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("shelves_id", data.get("shelves_id"));
        Request<String> request = NoHttpRequest.addShelves(params);
        mRequestQueue.add(REQUEST_ADD_SHLVES, request, onResponseListener);

    }

    public OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(SearchGoodsActivity.this);
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
            case REQUEST_SEARCH_GOODS:     //商品
                setGoodsList(jsonObject);
                break;
            case REQUEST_ADD_GOODS:    //添加商品
                refreshData(jsonObject);
                break;
            case REQUEST_GET_GOODS_PRICE:   //获取建议价格
                setGoodsPrice(jsonObject);
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

    private void refreshData(JSONObject jsonObject) throws JSONException {
        //reqeustGoodsStoreData();    //请求商品库数据
        //recyclerView.refresh();
        String msg = jsonObject.get("msg").toString();
        ToastUtil.showShort(msg);
        StoreGoodsModel storeGoodsModel = mList.get(currentPosition);
        storeGoodsModel.setFlag("2");
        mAdapter.notifyItemChanged(currentPosition+1,storeGoodsModel);
    }

    private void setGoodsList(JSONObject jsonObject) throws JSONException {
        if (isFresh) {
            recycler.refreshComplete();
            mList.clear();
            mAdapter.notifyDataSetChanged();
        } else {
            recycler.loadMoreComplete();
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

    private void requestGoodsPrice(int position) {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("goods_id", mList.get(position).getGoods_id());
        Request<String> request = NoHttpRequest.getGoodsPriceRequest(params);
        mRequestQueue.add(REQUEST_GET_GOODS_PRICE, request, onResponseListener);
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

    private void showMessageDialog(int position) {
        Glide.with(SearchGoodsActivity.this).load(mList.get(position).getGoods_img()).into(iv_goods_image);
        tv_goods_name.setText(mList.get(position).getGoods_name());
        et_price_self.setCursorVisible(false);
        et_price_self.setText("");
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(rl_back, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
