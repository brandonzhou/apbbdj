package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
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

/**
 *  选择配送方式
 */
public class DispatchingTypeActivity extends BaseActivity {

    private RelativeLayout rl_Back;

    private RadioGroup radioGroup;

    private TextView rg_send_by_me;
    private TextView rg_send_by_express;
    private TakeOutModel intentData;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private String orders_id;

    private final int  REQUEST_SELECT_DISTRIBUTION = 1001;   //配送方式

    private final int REQUEST_GET_SEND_MONEY = 1002;   //快递员配送金额

    public static void actionTo(Context context, TakeOutModel data) {
        Intent intent = new Intent(context, DispatchingTypeActivity.class);
        intent.putExtra("data",data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatching_type);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(DispatchingTypeActivity.this);
        initParams();
        initView();
        initClickListener();


    }



    private void initParams() {
        Intent intent = getIntent();
        intentData = (TakeOutModel) intent.getSerializableExtra("data");
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

    private void initClickListener() {
        rl_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //自己配送
        rg_send_by_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetState();
                rg_send_by_me.setBackgroundResource(R.drawable.shape_rb_check);
                rg_send_by_me.setTextColor(Color.parseColor("#ffffff"));
                selectByme();
            }
        });

        //快递员配送
        rg_send_by_express.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetState();
                rg_send_by_express.setBackgroundResource(R.drawable.shape_rb_check);
                rg_send_by_express.setTextColor(Color.parseColor("#ffffff"));

                requestExpressMoney();     //请求快递员配送的价格

            }
        });
    }

    private void resetState() {
        rg_send_by_me.setBackgroundResource(R.drawable.shape_rb_normal);
        rg_send_by_express.setBackgroundResource(R.drawable.shape_rb_normal);
        rg_send_by_me.setTextColor(Color.parseColor("#777777"));
        rg_send_by_express.setTextColor(Color.parseColor("#777777"));
    }

    private void selectByExpress(String money) {
        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setText("\n快递员配送需要配送费"+money+"元，确定选择吗？\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("我确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  SendByExpressActivity.actionTo(DispatchingTypeActivity.this,intentData);
                        selectDistributionMode(2,money);
                    }
                })
                .setNegative("换种方式", null)
                .show(getSupportFragmentManager());
    }

    private void selectDistributionMode(int mode,String money) {
        Map<String,String> map = new HashMap<>();
        map.put("distributor_id",user_id);
        map.put("orders_id", orders_id);
        map.put("types",mode+"");
        map.put("money",money);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.selectDistributionMode(signature,map);
        mRequestQueue.add(REQUEST_SELECT_DISTRIBUTION, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(DispatchingTypeActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "DispatchingTypeActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if ("5001".equals(code)) {
                        if (mode == 1) {
                            SendBymeActivity.actionTo(DispatchingTypeActivity.this,intentData);
                        } else {
                            SendByExpressActivity.actionTo(DispatchingTypeActivity.this,money,intentData);
                        }
                        finish();
                    } else {
                        ToastUtil.showShort(msg);
                        LoadDialogUtils.cannelLoadingDialog();
                    }
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

    private void selectByme() {
        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setText("\n您确定选择自己派送方式吗？选定之后不可更改\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("我确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  SendBymeActivity.actionTo(DispatchingTypeActivity.this,intentData);
                        selectDistributionMode(1,"0");
                    }
                })
                .setNegative("换种方式", null)
                .show(getSupportFragmentManager());
    }

    private void requestExpressMoney() {
        Map<String,String> map = new HashMap<>();
        map.put("distributor_id",user_id);
        map.put("orders_id", orders_id);
        Request<String> request = NoHttpRequest.getSendByExpressMoney(map);
        mRequestQueue.add(REQUEST_GET_SEND_MONEY, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(DispatchingTypeActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "DispatchingTypeActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        String money = jsonObject.get("data").toString();
                        selectByExpress(money);
                    } else {
                        ToastUtil.showShort(msg);
                        LoadDialogUtils.cannelLoadingDialog();
                    }
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


    private void initView() {
        rl_Back = findViewById(R.id.rl_back);
        rg_send_by_me = findViewById(R.id.rg_send_by_me);
        rg_send_by_express = findViewById(R.id.rg_send_by_express);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
