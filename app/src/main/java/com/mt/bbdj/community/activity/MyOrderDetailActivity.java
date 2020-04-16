package com.mt.bbdj.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyOrderDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    AppCompatTextView tvAddress;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_goods_name)
    TextView tvGoodsName;
    @BindView(R.id.tv_goods_money)
    TextView tvGoodsMoney;
    @BindView(R.id.tv_goods_type)
    TextView tvGoodsType;
    @BindView(R.id.tv_tv_goods_number)
    TextView tvTvGoodsNumber;
    @BindView(R.id.tv_find_package_message)
    TextView tvFindPackageMessage;
    @BindView(R.id.tv_dingdan)
    TextView tvDingdan;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.tv_send_goods_time)
    TextView tvSendGoodsTime;
    @BindView(R.id.id_connact_service)
    TextView idConnactService;
    private String orders_id;
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao userBaseMessageDao;
    private UserBaseMessage userBaseMessage;
    private String user_id;
    private String express_id;
    private String express_name;
    private int type = 1;
    private String yundan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);
        ButterKnife.bind(this);

        initParams();
        requestData();
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getMyOrderDetailRequest(user_id, orders_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(MyOrderDetailActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MyOrderDetailActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject data = jsonObject.getJSONObject("data");
                    if ("5001".equals(code)) {
                        setData(data);
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
        });
    }

    private void setData(JSONObject jsonObject) throws JSONException {
        tvName.setText(jsonObject.getString("orders_realname"));
        //  String logoPath = jsonObject.getString("thumb");
        tvAddress.setText(jsonObject.getString("orders_region") + jsonObject.getString("orders_address"));
        tvGoodsName.setText(jsonObject.getString("product_name"));
        tvGoodsMoney.setText("￥" + jsonObject.getString("orders_money"));
        tvGoodsType.setText(jsonObject.getString("genre_name"));
        tvTvGoodsNumber.setText("×" + jsonObject.getString("orders_number"));
        tvDingdan.setText(jsonObject.getString("order_number"));
        express_id = jsonObject.getString("express_id");
        express_name = jsonObject.getString("express_name");
        yundan = jsonObject.getString("order_number");
        tvCreateTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss", jsonObject.getString("create_time")));
        tvSendGoodsTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss", jsonObject.getString("handle_time")));
    }


    private void initParams() {
        Intent intent = getIntent();
        orders_id = intent.getStringExtra("orders_id");
        type = intent.getIntExtra("type", 1);
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            user_id = userBaseMessage.getUser_id();
        }

    }

    @OnClick({R.id.iv_back, R.id.tv_find_package_message, R.id.id_call_service, R.id.id_connact_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_find_package_message:
                searchPackageMessage();     //查询物流信息
                break;
            case R.id.id_call_service:
            case R.id.id_connact_service:
                showConnectService();
                break;
        }
    }

    private void searchPackageMessage() {
        if (type == 1) {
            ToastUtil.showShort("暂无物流信息");
            return;
        }

        searPackageMessage();
    }

    private void searPackageMessage() {
        Request<String> request = NoHttpRequest.getSearchPackRequest(user_id, yundan, express_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(MyOrderDetailActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "SearchPackageActivity::" + response.get());
                try {
                    String result = response.get();
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.get("code").toString();
                    if ("5001".equals(code)) {
                        Intent intent = new Intent(MyOrderDetailActivity.this, ShowPackageMessageActivity.class);
                        intent.putExtra("express_id", express_id);
                        intent.putExtra("express", express_name);
                        intent.putExtra("result", result);
                        intent.putExtra("yundan", yundan);
                        startActivity(intent);
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
                LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }


    private void showConnectService() {
        new CircleDialog.Builder()
                .setTitle("客服热线")
                .setText("\n010-5838292\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("呼叫", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + "010-5838292");
                        intent.setData(data);
                        startActivity(intent);
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
