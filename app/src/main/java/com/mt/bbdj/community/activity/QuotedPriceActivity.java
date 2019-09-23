package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ClearGoodsModel;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.ClearGoodsAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

public class QuotedPriceActivity extends BaseActivity {

    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_place_order)
    ImageView ivPlaceOrder;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.bt_commit_no)
    TextView btCommitNo;
    @BindView(R.id.bt_commit_price)
    TextView btCommitPrice;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private OkHttpClient okHttpClient;
    private String orders_id;
    private List<ClearGoodsModel> modelList = new ArrayList<>();
    private ClearGoodsAdapter messageAdapter;
    private RequestQueue mRequestQueue;
    private final int REQUEST_RECEIVE_ORDER = 100;    //类目
    private final int REQUEST_RECEIVE_PRICE = 200;    //报价
    private ProductModel productModel;
    private final int REQUEST_CANNEL = 100; //取消
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_quoted_price);
        ButterKnife.bind(this);
        initParams();
        initRecyclerView();
        requestClearType();    //获取清洗衣服
    }

    private void initRecyclerView() {
        messageAdapter = new ClearGoodsAdapter(modelList);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        messageAdapter.setOnValueChanage(new ClearGoodsAdapter.OnValueChanage() {
            @Override
            public void onValueChanage(int position, int value) {
                ClearGoodsModel data = modelList.get(position);
                data.setNumber(value);
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void requestClearType() {
        Request<String> request = NoHttpRequest.requestClearType(user_id,orders_id);
        mRequestQueue.add(REQUEST_RECEIVE_ORDER, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    String json = response.get();
                    LogUtil.d("QuotedPriceActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    if ("5001".equals(code)) {
                        setClearType(dataArray);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    messageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                ToastUtil.showShort(response.getException().getMessage());
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void setClearType(JSONArray dataArray) throws JSONException {

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            String title = dataObj.getString("title");
            JSONArray goodsArray = dataObj.getJSONArray("goods");
            for (int j = 0;j < goodsArray.length() ;j++) {
                ClearGoodsModel clearGoodsModel = new ClearGoodsModel();
                JSONObject goodsObj = goodsArray.getJSONObject(j);
                String id = goodsObj.getString("id");
                String goodsTitle = goodsObj.getString("title");
                String price = goodsObj.getString("price");
                String states = goodsObj.getString("states");
                String flag = goodsObj.getString("flag");
                if (j == 0) {
                    clearGoodsModel.setType(title);
                } else {
                    clearGoodsModel.setType("");
                }

                clearGoodsModel.setId(id);
                clearGoodsModel.setTitle(goodsTitle);
                clearGoodsModel.setPrice(price);
                clearGoodsModel.setStates(states);
                clearGoodsModel.setFlag(flag);
                clearGoodsModel.setNumber(0);
                modelList.add(clearGoodsModel);
            }
        }
    }

    private void initParams() {
        Intent intent = getIntent();
        productModel = (ProductModel) intent.getSerializableExtra("productModel");
        orders_id = productModel.getOrders_id();

        tvAddress.setText(productModel.getAddress());
        tvPhone.setText(productModel.getPhone());
        tvOrderNumber.setText(productModel.getOrderNumber());
        tvCreateTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm", productModel.getCreateTime()));

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();

    }

    @OnClick({R.id.bt_commit_no, R.id.bt_commit_price, R.id.iv_place_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_commit_no:
                finish();
                CannelOrder();
                break;
            case R.id.bt_commit_price:
                commitPrice();
                break;
            case R.id.iv_place_order:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPhone.getText().toString().trim());
                intent.setData(data);
                startActivity(intent);
                break;
        }
    }

    private void CannelOrder() {
        Intent intent1 = new Intent(this,CannelOrderActivity.class);
        String orders_id = productModel.getOrders_id();
        intent1.putExtra("user_id",user_id);
        intent1.putExtra("cannel_type",CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
        intent1.putExtra("orders_id",orders_id);
        startActivityForResult(intent1,REQUEST_CANNEL);
    }

    private void commitPrice() {
        String commodity_id = getGoodsId();
        String goods_number = getGoodsNumber();

        if ("".equals(commodity_id) || "".equals(goods_number)) {
            ToastUtil.showShort("请选择干洗类目");
            return ;
        }
        Request<String> request = NoHttpRequest.commitPrice(user_id,orders_id,commodity_id,goods_number);
        mRequestQueue.add(REQUEST_RECEIVE_ORDER, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(QuotedPriceActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    String json = response.get();
                    LogUtil.d("QuotedPriceActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        setResult(RESULT_OK);
                        finish();
                    }
                    LoadDialogUtils.cannelLoadingDialog();
                    ToastUtil.showShort(msg);

                } catch (Exception e) {
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
        });
    }

    private String getGoodsNumber() {
        StringBuilder sb = new StringBuilder();
        for (ClearGoodsModel model : modelList) {
            if (model.getNumber() != 0) {
                sb.append(model.getNumber());
                sb.append(",");
            }
        }
        String result = sb.toString();
        String realResult = "";
        if (result.length() != 0) {
           realResult = result.substring(0,result.lastIndexOf(","));
        }
        return realResult;
    }

    private String getGoodsId() {
        StringBuilder sb = new StringBuilder();
        for (ClearGoodsModel model : modelList) {
            if (model.getNumber() != 0) {
                sb.append(model.getId());
                sb.append(",");
            }
        }
        String result = sb.toString();
        String realResult = "";
        if (result.length() != 0) {
           realResult = result.substring(0,result.lastIndexOf(","));
        }

        return realResult;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
