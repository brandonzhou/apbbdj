package com.mt.bbdj.community.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alipay.sdk.app.PayTask;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.model.PayResult;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.MD5Util;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class RechargeActivity extends BaseActivity {

    private RelativeLayout ivBack;
    private Button rechargeBt;   //充值
    private EditText etMoney;
    private LinearLayout weChatLayout, aliLayout;     //微信支付、支付宝支付
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao userBaseMessageDao;
    private UserBaseMessage userBaseMessage;
    private String user_id;

    private final int REQUEST_WECHAT_PAY = 100;   //微信支付请求
    private IWXAPI api;

    private ImageView checkWechat;   //微信
    private ImageView checkAli;    //支付宝
    private boolean isWechat = true;     //微信支付  false  :支付宝支付

    /**
     * 获取权限使用的 RequestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(RechargeActivity.this);
        EventBus.getDefault().register(this);
        requestPermission();
        initParams();
        initView();
        initListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.DESTORY_RECHAR) {
            finish();
        }
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            user_id = userBaseMessage.getUser_id();
        }
        api = WXAPIFactory.createWXAPI(RechargeActivity.this, null);    //注册到微信
        api.registerApp(Constant.appid);
    }

    private void initListener() {
        rechargeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交充值
                if (isWechat) {     //微信支付
                    payforByWechat();
                } else {    //支付宝支付
                    payforByAli();
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //微信支付
        weChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWechat.setBackgroundResource(R.drawable.ic_check_true);
                checkAli.setBackgroundResource(R.drawable.shap_circle_grey);
                isWechat = true;
            }
        });

        //支付宝支付
        aliLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToastUtil.showShort("暂不支持支付宝支付");
                checkWechat.setBackgroundResource(R.drawable.shap_circle_grey);
                checkAli.setBackgroundResource(R.drawable.ic_check_true);
                isWechat = false;
            }
        });
    }

    private static final int SDK_PAY_FLAG = 1;
    // String orderInfo = "app_id=2019031963597551&biz_content=%7B%22body%22%3A%22%5Cu4f59%5Cu989d%5Cu5145%5Cu503c%22%2C%22subject%22%3A%22%5Cu4f59%5Cu989d%5Cu5145%5Cu503c%22%2C%22out_trade_no%22%3A%2215536811439709%22%2C%22timeout_express%22%3A%221d%22%2C%22total_amount%22%3A0.01%2C%22seller_id%22%3A%222088431757095973%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22store_id%22%3A%22001%22%7D&charset=utf-8&method=alipay.trade.app.pay¬ify_url=http%3A%2F%2Fwww.81dja.com%2FPayment%2Fali_notify&sign_type=RSA2×tamp=2019-03-27+18%3A05%3A43&version=1.0";
    String orderInfo = "alipay_sdk=alipay-sdk-php-20180705&app_id=2019031963597551&biz_content=%7B%22body%22%3A%22%E6%88%91%E6%98%AF%E6%B5%8B%E8%AF%95%E6%95%B0%E6%8D%AE%22%2C%22subject%22%3A+%22App%E6%94%AF%E4%BB%98%E6%B5%8B%E8%AF%95%22%2C%22out_trade_no%22%3A+%2220170125test01%22%2C%22timeout_express%22%3A+%2230m%22%2C%22total_amount%22%3A+%220.01%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fwww.81dja.com%2FPayment%2Fali_notify&sign_type=RSA2&timestamp=2019-04-17+17%3A38%3A05&version=1.0&sign=fFmYgxHp0XVl8iIiOEaHWHQ3nBQ%2BnE572QERujk1CaMsCgFaL25KIx3ozkV9DiESFE776nxabyixyBbspiRXW3T0OtHGAK8E%2F6U4GnN94ulraTji%2B%2FZtp%2BzkIrr6IUshar0sCCSM39VKQ6k7lMu%2FLgv0Q8mHcxknvjfpQNHm1gVh01Stqj8t1Q8JLEj955UzsZVNfpWxp1ZGLAq9m8CqaC%2BP%2FAMpX684n4ZsZg256JAxRslBeWc%2FhcOu%2Fau4KmOZZ8NMy3bOYuo309NoyEpIexR4CfcKw7mKXpH7uNhByCoWbkFCroJtX8uLW09XNZJUtyofyxV1lg2MsvcAqf0QUw%3D%3D";

    private void payforByAli() {
        String money = etMoney.getText().toString();
        if ("".equals(money) || "0".equals(money)) {
            ToastUtil.showShort("金额不可为空！");
            return;
        }

        requestPayforByAlia(money);
    }

    //微信支付
    private void payforByWechat() {
        String money = etMoney.getText().toString();
        if ("".equals(money) || "0".equals(money)) {
            ToastUtil.showShort("金额不可为空！");
            return;
        }
        requestPayforByWechat(money);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                showAlert(RechargeActivity.this, "支付成功");
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                showAlert(RechargeActivity.this, "支付失败");
            }
        }

        ;
    };

    private void requestPayforByWechat(String money) {
        Request<String> request = NoHttpRequest.getWeiChartPayforRequest(user_id, money);
        //  Request<String> request = NoHttp.createStringRequest("https://wxpay.wxutil.com/pub_v2/app/app_pay.php", RequestMethod.GET);
        mRequestQueue.add(REQUEST_WECHAT_PAY, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(RechargeActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RechargeActivity::" + response.get());

                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        PayReq request = new PayReq();
                        request.prepayId = data.getString("prepayid");
                        request.appId = data.getString("appid");
                        request.packageValue = data.getString("package");
                        request.nonceStr = data.getString("noncestr");
                        request.timeStamp = data.getString("timestamp");
                        //request.sign = data.getString("sign");
                        request.partnerId = data.getString("partnerid");
                        SortedMap<String, Object> params = new TreeMap<String, Object>();
                        params.put("appid", request.appId);
                        params.put("partnerid", request.partnerId);
                        params.put("prepayid", request.prepayId);
                        params.put("package", request.packageValue);
                        params.put("noncestr", request.nonceStr);
                        params.put("timestamp", request.timeStamp);
                        String sign = createSign(params);
                        request.sign = sign;
                        boolean isSucceff = api.sendReq(request);

                    }
                    LoadDialogUtils.cannelLoadingDialog();
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


    private void requestPayforByAlia(String money) {
        Request<String> request = NoHttpRequest.getAliPayforRequest(user_id, money);
        //  Request<String> request = NoHttp.createStringRequest("https://wxpay.wxutil.com/pub_v2/app/app_pay.php", RequestMethod.GET);
        mRequestQueue.add(REQUEST_WECHAT_PAY, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(RechargeActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RechargeActivity::" + response.get());

                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONObject resultStr = jsonObject.getJSONObject("data");
                        String result = resultStr.getString("valurl");
                        String orderInfo = result.replaceAll("&amp;", "&");
                        LogUtil.d("photoFile::", orderInfo);
                        final Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(RechargeActivity.this);
                                Map<String, String> result = alipay.payV2(orderInfo, true);

                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };

                        // 必须异步调用
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    LoadDialogUtils.cannelLoadingDialog();
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

    public static String createSign(SortedMap<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        // String sign = MD5Utils.encode(sb.toString()).toUpperCase();
        String sign = MD5Util.toMD5(sb.toString()).toUpperCase();
        return sign;
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        rechargeBt = findViewById(R.id.bt_recharge);
        etMoney = findViewById(R.id.et_money);
        weChatLayout = findViewById(R.id.ll_wechat);
        aliLayout = findViewById(R.id.ll_ali);
        checkWechat = findViewById(R.id.iv_check_wechat);
        checkAli = findViewById(R.id.iv_check_ali);
        etMoney.setCursorVisible(false);
        etMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMoney.setCursorVisible(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new TargetEvent(TargetEvent.DESTORY_RECHAR));
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(onDismiss)
                .show();
    }

    /**
     * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
     * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
     * 如果接入支付宝 SDK 的应用 targetSdk 在 23 以下，可以省略这个步骤。
     */
    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSIONS_REQUEST_CODE);
        } else {

        }
    }

    /**
     * 权限获取回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                // 用户取消了权限弹窗
                if (grantResults.length == 0) {
                    ToastUtil.showShort("权限拒绝");
                    return;
                }

                // 用户拒绝了某些权限
                for (int x : grantResults) {
                    if (x == PackageManager.PERMISSION_DENIED) {
                        ToastUtil.showShort("权限拒绝");
                        return;
                    }
                }
            }
        }
    }

}
