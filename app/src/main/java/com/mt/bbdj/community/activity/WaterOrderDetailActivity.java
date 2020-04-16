package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.ProductListAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

public class WaterOrderDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_address_title)
    TextView tvAddressTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_phone_title)
    TextView tvPhoneTitle;
    @BindView(R.id.tv_call)
    TextView tvCall;
    @BindView(R.id.tv_cannel_order)
    TextView tvCannelOrder;
    @BindView(R.id.tv_confirm_receive)
    TextView tvConfirmReceive;
    @BindView(R.id.tv_confirm_send)
    TextView tvConfirmSend;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_call_number)
    ImageView ivCallNumber;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.tv_account_money)
    TextView tvAccountMoney;
    @BindView(R.id.rl_product)
    RecyclerView rlProduct;
    private ProductListAdapter mAdapter;

    private List<HashMap<String, String>> mList = new ArrayList<>();
    private OkHttpClient okHttpClient;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private String order_id;
    private ProductModel productModel;
    private int REQUEST_CANNEL = 100;
    private RequestQueue mRequestQueue;
    private final int REQUEST_RECEIVE = 100;   //接单
    private final int REQUEST_SEND = 200;   //送达

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_water_order_detail);
        ButterKnife.bind(this);
        initParams();
        initView();
    }

    private void initView() {
        rlProduct.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new ProductListAdapter(mList);
        rlProduct.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rlProduct.setLayoutManager(linearLayoutManager);
        rlProduct.setAdapter(mAdapter);
    }

    private void initParams() {
        Intent intent = getIntent();
        productModel = (ProductModel) intent.getSerializableExtra("orderDetail");
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        mList = productModel.getWaterMessageList();
        order_id = productModel.getOrders_id();
        String states = productModel.getStates();

        if ("1".equals(states)) {
            tvConfirmReceive.setVisibility(View.VISIBLE);
            tvConfirmSend.setVisibility(View.GONE);
        } else if ("2".equals(states)) {
            tvConfirmReceive.setVisibility(View.GONE);
            tvConfirmSend.setVisibility(View.VISIBLE);
        }

        tvAddressTitle.setText(productModel.getAddress());
        tvPhoneTitle.setText(productModel.getPhone());
        tvContent.setText((DateUtil.changeStampToStandrdTime("HH:mm", productModel.getContext()) + "上门"));
        tvAddress.setText(productModel.getAddress());
        tvPhone.setText(productModel.getPhone());
        tvOrderNumber.setText(productModel.getOrderNumber());
        String createTime = productModel.getCreateTime();
        createTime = DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm", createTime);
        tvOrderTime.setText(createTime);
        tvAccountMoney.setText("￥" + productModel.getAccountPrice());


        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }


    }

    @OnClick({R.id.iv_back, R.id.tv_call, R.id.tv_cannel_order, R.id.tv_confirm_receive, R.id.tv_confirm_send, R.id.iv_call_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_cannel_order:
                cannelWaterOlder();
                break;
            case R.id.tv_confirm_receive:
                confirmWaterReive();
                break;
            case R.id.tv_confirm_send:
                confirmWaterSend();
                break;
            case R.id.tv_call:
            case R.id.iv_call_number:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPhone.getText().toString());
                intent.setData(data);
                startActivity(intent);
                break;
        }
    }

    private void cannelWaterOlder() {
        //桶装水取消订单
        Intent intent = new Intent(this, CannelOrderActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("mail_id", productModel.getMail_id());
        intent.putExtra("orders_id", productModel.getOrders_id());
        intent.putExtra("cannel_type", CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
        startActivityForResult(intent, REQUEST_CANNEL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        finish();
    }

    private void confirmWaterSend() {

        Request<String> request = NoHttpRequest.confirmWaterSend(user_id, order_id);
        mRequestQueue.add(REQUEST_SEND, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(WaterOrderDetailActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    String json = response.get();
                    LogUtil.d("WaterOrderDetailActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        tvConfirmSend.setEnabled(false);
                        tvCannelOrder.setEnabled(false);
                        tvConfirmSend.setBackgroundResource(R.drawable.shap_ll_bg);
                        tvCannelOrder.setBackgroundResource(R.drawable.shap_ll_bg);
                        tvCannelOrder.setTextColor(Color.parseColor("#DDDDDD"));
                        tvConfirmSend.setTextColor(Color.parseColor("#DDDDDD"));
                        tvConfirmSend.setText("已送达");
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    LoadDialogUtils.cannelLoadingDialog();
                    e.printStackTrace();
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

    private void confirmWaterReive() {

        Request<String> request = NoHttpRequest.confirmWaterReive(user_id, order_id);
        mRequestQueue.add(REQUEST_SEND, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(WaterOrderDetailActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    String json = response.get();
                    LogUtil.d("WaterOrderDetailActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {
                        tvConfirmReceive.setVisibility(View.GONE);
                        tvConfirmSend.setVisibility(View.VISIBLE);
                    }
                    ToastUtil.showShort(msg);
                    LoadDialogUtils.cannelLoadingDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
