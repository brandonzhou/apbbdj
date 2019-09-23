package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

public class ClearDetailActivity extends BaseActivity {

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
    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_confirm_price)
    TextView tvConfirmPrice;
    private ProductModel productModel;
    private OkHttpClient okHttpClient;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;

    private final int COMMIT_PRICE = 100;   //提交报价
    private final int REQUEST_CANNEL = 200;    //取消原因
    private final int REQUEST_RECEIVE_ORDER = 300;    //接单
    private Request<String> request;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_detail);
        ButterKnife.bind(this);
        initParams();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == COMMIT_PRICE || requestCode == REQUEST_CANNEL) {
            finish();
        }
    }

    private void initParams() {
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mRequestQueue = NoHttp.newRequestQueue();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Intent intent = getIntent();
        productModel = (ProductModel) intent.getSerializableExtra("productModel");
        int clearState = productModel.getClearState();
        //设置视图状态
        initView(clearState);

        tvAddressTitle.setText(productModel.getAddress());
        tvContent.setText((DateUtil.changeStampToStandrdTime("HH:mm",productModel.getContext())+"上门"));
        tvPhoneTitle.setText(productModel.getPhone());
    }

    private void initView(int clearState) {
        switch (clearState) {
            case 1:     //显示电话联系、取消、确认接单
                tvCall.setVisibility(View.VISIBLE);
                tvCannelOrder.setVisibility(View.VISIBLE);
                tvConfirmReceive.setVisibility(View.VISIBLE);
                tvConfirmSend.setVisibility(View.GONE);
                tvConfirmPrice.setVisibility(View.GONE);
                break;
            case 2:      //显示确认报价
                tvCall.setVisibility(View.VISIBLE);
                tvCannelOrder.setVisibility(View.VISIBLE);
                tvConfirmReceive.setVisibility(View.GONE);
                tvConfirmSend.setVisibility(View.GONE);
                tvConfirmPrice.setVisibility(View.VISIBLE);
                break;
            case 7:
                tvCall.setVisibility(View.VISIBLE);
                tvCannelOrder.setVisibility(View.GONE);
                tvConfirmReceive.setVisibility(View.GONE);
                tvConfirmSend.setVisibility(View.VISIBLE);
                tvConfirmPrice.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick({R.id.tv_call, R.id.tv_cannel_order, R.id.tv_confirm_receive, R.id.tv_confirm_send, R.id.tv_confirm_price})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPhoneTitle.getText().toString().trim());
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.tv_cannel_order:
                Intent intent1 = new Intent(this,CannelOrderActivity.class);
                String orders_id = productModel.getOrders_id();
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("cannel_type",CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
                intent1.putExtra("orders_id",orders_id);
                startActivityForResult(intent1,REQUEST_CANNEL);
                break;
            case R.id.tv_confirm_receive:
                receiveClearOrder();
                break;
            case R.id.tv_confirm_send:

                break;
            case R.id.tv_confirm_price:
                //干洗提交报价
                Intent intent2 = new Intent(this, QuotedPriceActivity.class);
                intent2.putExtra("productModel",productModel);
                startActivityForResult(intent2,COMMIT_PRICE);
                break;
        }
    }

    private void receiveClearOrder() {
        String order_id = productModel.getOrders_id();
        request = NoHttpRequest.receiveClearOrder(user_id,order_id);
        mRequestQueue.add(REQUEST_RECEIVE_ORDER, request, onResponseListener);
    }


    OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ClearDetailActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ClearDetailActiviyt::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
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
    };

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what){
            case REQUEST_RECEIVE_ORDER:   //接单
                handleReceiveOrder(jsonObject);
                break;
        }

    }

    private void handleReceiveOrder(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.get("msg").toString();
        ToastUtil.showShort(msg);
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
