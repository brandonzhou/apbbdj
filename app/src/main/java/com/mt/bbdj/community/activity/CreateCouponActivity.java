package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

 import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.wheel.BirthDateDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class CreateCouponActivity extends BaseActivity {

    private RelativeLayout rl_back;

    private TextView tv_start_time, tv_end_time;
    private TextView tv_manjian, tv_zhekou;
    private TextView tv_title_one, tv_title_two;
    private Button bt_create_coupon;
    private EditText et_money_one,et_money_two;
    private int mType = 2;
    private String user_id;
    private RequestQueue mRequestQueue;

    private final int REQUEST_CREATE_COUPON = 1001;    //生成优惠券

    public static void actionTo(Context context) {
        Intent intent = new Intent(context, CreateCouponActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coupon);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(CreateCouponActivity.this);

        initView();
        initParams();
        initListener();
    }

    private void initListener() {
        rl_back.setOnClickListener(onClickListener);
        tv_start_time.setOnClickListener(onClickListener);
        tv_end_time.setOnClickListener(onClickListener);
        tv_manjian.setOnClickListener(onClickListener);
        tv_zhekou.setOnClickListener(onClickListener);
        bt_create_coupon.setOnClickListener(onClickListener);
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();


    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_manjian = findViewById(R.id.tv_manjian);
        tv_zhekou = findViewById(R.id.tv_zhekou);
        tv_title_one = findViewById(R.id.tv_title_one);
        tv_title_two = findViewById(R.id.tv_title_two);
        bt_create_coupon = findViewById(R.id.bt_create_coupon);
        et_money_one = findViewById(R.id.tv_money_one);
        et_money_two = findViewById(R.id.tv_money_two);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rl_back:
                    finish();
                    break;
                case R.id.tv_start_time:     //开始时间
                    setData(1);
                    break;
                case R.id.tv_end_time:       //结束时间
                    setData(2);
                    break;
                case R.id.tv_manjian:        //满减
                    resetPannel(2);
                    break;
                case R.id.tv_zhekou:         //折扣
                    resetPannel(1);
                    break;
                case R.id.bt_create_coupon:  //创建优惠券
                    createCoupon();
                    break;
            }
        }
    };

    private void createCoupon() {
        String moneyOne = et_money_one.getText().toString();
        String moneyTwo = et_money_two.getText().toString();
        String startTime = tv_start_time.getText().toString();
        String endTime = tv_end_time.getText().toString();

        if ("".equals(moneyOne) || "".equals(moneyTwo) || "".equals(startTime) || "".equals(endTime)) {
            ToastUtil.showShort("请完善信息");
        } else if (mType==1 && (IntegerUtil.getStringChangeToFloat(moneyTwo) > 10 || IntegerUtil.getStringChangeToFloat(moneyTwo) < 0)) {
            ToastUtil.showShort("请设置合理的折扣范围");
        }else {
            commitData(moneyOne,moneyTwo,startTime,endTime);
        }
    }

    private void commitData(String moneyOne, String moneyTwo, String startTime, String endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("term_money", moneyOne);
        params.put("reduction_money", moneyTwo);
        params.put("types", mType+"");
        params.put("starttime", startTime+" "+"00:00:00");
        params.put("endtime", endTime+" "+"23:59:59");
        Request<String> request = NoHttpRequest.createCouponRequest(params);
        mRequestQueue.add(REQUEST_CREATE_COUPON, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(CreateCouponActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "CreateCouponActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("statusCode").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        finish();
                    }
                    ToastUtil.showShort(msg);
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

    private void resetPannel(int type) {
        if (type == 1) {
            tv_manjian.setBackgroundResource(R.drawable.shap_solid_grey_circle_1);
            tv_manjian.setTextColor(Color.parseColor("#777777"));
            tv_zhekou.setBackgroundResource(R.drawable.shap_solid_green_circle);
            tv_zhekou.setTextColor(Color.parseColor("#ffffff"));
            et_money_two.setHint("请设置折扣");
        } else {
            tv_zhekou.setBackgroundResource(R.drawable.shap_solid_grey_circle_1);
            tv_zhekou.setTextColor(Color.parseColor("#777777"));
            tv_manjian.setBackgroundResource(R.drawable.shap_solid_green_circle);
            tv_manjian.setTextColor(Color.parseColor("#ffffff"));
            et_money_two.setHint("请输入金额");
        }
        mType = type;
        tv_title_one.setText(type == 1 ? "当用户满足多少金额时，可以打折?" : "当用户满足多少金额时，可以满减？");
        tv_title_two.setText(type == 1 ? "满足金额后，打几折?" : "满足金额后，可以减多少？");
    }

    public void setData(final int flag) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        String curDate = DateUtil.getCurrentTimeFormat("yyyy-MM-dd");
        int[] date = getYMDArray(curDate, ".");
        BirthDateDialog birthDiolog = new BirthDateDialog(this,
                new BirthDateDialog.PriorityListener() {
                    @Override
                    public void refreshPriorityUI(String year, String month,
                                                  String day, String hours, String mins) {
                        if (flag == 1) {
                            tv_start_time.setText(year + "-" + month + "-" + day);
                        } else {
                            tv_end_time.setText(year + "-" + month + "-" + day);
                        }
                    }
                }, date[0], date[1], date[2], 23, 59, 59, width,
                height, "选择时间");
        Window window = birthDiolog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        //  window.setWindowAnimations(R.style.dialogstyle); // 添加动画
        birthDiolog.setCancelable(true);
        birthDiolog.show();
    }

    /**
     * 时间截取
     * @param datetime
     * @param splite
     * @return
     */
    public static int[] getYMDArray(String datetime, String splite) {
        int date[] = {0, 0, 0, 0, 0};
        if (datetime != null && datetime.length() > 0) {
            String[] dates = datetime.split(splite);
            int position = 0;
            for (String temp : dates) {
                date[position] = Integer.valueOf(temp);
                position++;
            }
        }
        return date;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
