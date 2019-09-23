package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TakeOutModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

//快递员派送
public class SendByExpressActivity extends BaseActivity {

    private RelativeLayout iv_back;
    private TextView tv_confirm_change;
    private TakeOutModel intentData;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private String orders_id;
    private final int REQUEST_SELECT_DISTRIBUTION = 101;
    private String money;

    public static void actionTo(Context context, String money,TakeOutModel takeOutModel) {
        Intent intent = new Intent(context, SendByExpressActivity.class);
        intent.putExtra("data",takeOutModel);
        intent.putExtra("money",money);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_by_express);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SendByExpressActivity.this);
        initParams();
        initView();
        initListener();
    }

    private void initListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //更换配送方式
        tv_confirm_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

    }

    private void showConfirmDialog() {
        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setText("\n确认更改配送方式吗?\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //更改
                        changeDistributionMode();
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    private void initParams() {
        Intent intent = getIntent();
        intentData = (TakeOutModel) intent.getSerializableExtra("data");
        money = getIntent().getStringExtra("money");

        orders_id = intentData.getOrders_id();
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void initView() {
        iv_back = findViewById(R.id.rl_back);
        tv_confirm_change = findViewById(R.id.tv_confirm_change);
    }

    private void changeDistributionMode() {
        Map<String,String> map = new HashMap<>();
        map.put("distributor_id",user_id);
        map.put("orders_id", orders_id);
        map.put("types",1+"");
        map.put("money",money);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.changeDistributionMode(signature,map);
        mRequestQueue.add(REQUEST_SELECT_DISTRIBUTION, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(SendByExpressActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "SendByExpressActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if ("5001".equals(code)) {
                        SendBymeActivity.actionTo(SendByExpressActivity.this,intentData);
                        finish();
                    }
                    LoadDialogUtils.cannelLoadingDialog();
                    ToastUtil.showShort(msg);
                } catch (JSONException e) {
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
