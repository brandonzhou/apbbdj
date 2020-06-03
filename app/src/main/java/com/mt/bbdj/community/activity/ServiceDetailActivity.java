package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TakeOutModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.OpenMapUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.ProducelistAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ServiceDetailActivity extends BaseActivity {


    private LinearLayout ll_gaode;
    private LinearLayout ll_baidu;
    private TextView tv_address;
    private TextView tv_name;
    private TextView tv_tag;
    private TextView tv_dispacth;
    private TextView tv_send_finish;
    private TextView tv_confirm_receive;
    private TextView tv_action_by_express;
    private TextView tv_send_by_me;
    private RelativeLayout rl_phone;
    private RelativeLayout rl_bottom_action;
    private RecyclerView recycler;
    private RelativeLayout rl_back;
    private TakeOutModel intentData;
    private String courier_id;
    private TextView tv_order_id;
    private TextView tv_create_time;
    private TextView tv_end_time;
    private TextView tv_dispath_type;
    private AppCompatTextView tv_remark;

    private String mLatitude = "0";
    private String mLongitude = "0";
    private RequestQueue mRequestQueue;

    private final int REQUEST_ORDER_DETAIL = 1001;    //请求订单详情
    private final int REQUEST_RECEIVE_ORDER = 1002;    //确认接单
    private final int REQUEST_SEND_ORDER = 1003;    //确认送达
    private final int REQUEST_SELECT_DISTRIBUTION = 1004;    //更改为自己配送
    private String user_id;

    private List<HashMap<String,String>> mList = new ArrayList<>();
    private ProducelistAdapter waterAdapter;


    public static void actionTo(Context context, String user_id, TakeOutModel takeOutModel) {
        Intent intent = new Intent(context, ServiceDetailActivity.class);
        intent.putExtra("data", takeOutModel);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ServiceDetailActivity.this);
        initParams();
        initView();
        initRecyclerView();
        initClickListener();

        requestData();     //请求数据
    }

    private void requestData() {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", intentData.getOrders_id());
        Request<String> request = NoHttpRequest.requestServiceDetail(map);
        mRequestQueue.add(REQUEST_ORDER_DETAIL, request, mResponseListener);

    }

    private void receiveOrder(String order_id) {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", order_id);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.receiveTakeOrders(signature, map);
        mRequestQueue.add(REQUEST_RECEIVE_ORDER, request, mResponseListener);
    }

    private void confirmOrderSend(String order_id) {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", order_id);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.completeTakeOrders(signature, map);
        mRequestQueue.add(REQUEST_SEND_ORDER, request, mResponseListener);
    }


    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ServiceDetailActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "LoginActivity::" + response.get());
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
                ToastUtil.showShort("连接失败！");
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

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_ORDER_DETAIL:    //订单详情
                setViewAndDataByStates(jsonObject);
                break;
            case REQUEST_RECEIVE_ORDER:   //确认接单
                confirmReceive(jsonObject);
                break;
            case REQUEST_SEND_ORDER:    //订单送达
                finishOrder(jsonObject);
                break;
            case REQUEST_SELECT_DISTRIBUTION:   //更改为自己配送
                changeSendType(jsonObject);
                break;
        }
    }

    private void changeSendType(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);

        //刷新
        requestData();
    }

    private void finishOrder(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);

        finish();
    }

    private void confirmReceive(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);

        //刷新
        requestData();
    }

    private void setViewAndDataByStates(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String states = data.getString("states");
        String courier_id = data.getString("courier_id");
        String order_number = data.getString("order_number");
        JSONArray detailedArray = data.getJSONArray("detailed");

        String create_time = DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",StringUtil.handleNullResultForString(data.getString("create_time")));
        String end_time = DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",StringUtil.handleNullResultForString(data.getString("end_time")));
        // 1.自提 2.商家配送 3.达达配送  4 驿站称重
        String distribution_mode = data.getString("distribution_mode");
        // distribution_mode 的文字显示
        String mode = data.getString("mode");
        String content = StringUtil.handleNullResultForString(data.getString("content"));

        tv_remark.setText(content);
        tv_order_id.setText(order_number);
        tv_create_time.setText(create_time);
        tv_end_time.setText("".equals(end_time)?"尽快送达":end_time);
        //tv_dispath_type.setText("2".equals(distribution_mode)?"快递员配送":"商家配送");
        tv_dispath_type.setText(mode);
        findViewById(R.id.ll_top_address).setVisibility("4".equals(distribution_mode)? View.GONE: View.VISIBLE);

        tv_confirm_receive.setVisibility(View.GONE);   //确认接单
        tv_dispacth.setVisibility(View.GONE);     //配送
        tv_send_finish.setVisibility(View.GONE);    //已送达
        tv_action_by_express.setVisibility(View.GONE);    //快递员操作
        tv_send_by_me.setVisibility(View.GONE);    //自己配送
        rl_bottom_action.setVisibility(View.VISIBLE);
        if ("1".equals(states)) {
            tv_confirm_receive.setVisibility(View.VISIBLE);   //确认接单
        } else if ("2".equals(states)) {
            if ("2".equals(distribution_mode) && "0".equals(courier_id)) {   //等待快递员接单 可以转换为自己配送
                tv_send_by_me.setVisibility(View.VISIBLE);
            } else if ("2".equals(distribution_mode) && !"0".equals(courier_id)) {  //等待快递员取件
                tv_action_by_express.setVisibility(View.VISIBLE);
                tv_action_by_express.setText("等待快递员取件");
            } else {
                tv_dispacth.setVisibility(View.VISIBLE);
            }
        } else if ("3".equals(states)) {
            if (!"0".equals(courier_id)) {
                tv_action_by_express.setVisibility(View.VISIBLE);
                tv_action_by_express.setText("快递员配送中");
            } else {
                tv_send_finish.setVisibility(View.VISIBLE);
            }
        } else if ("5".equals(states)) {
            rl_bottom_action.setVisibility(View.GONE);
        }
        //设置商品
        setGoodsList(detailedArray);
    }

    private void setGoodsList(JSONArray detailedArray) throws JSONException {
        for (int i = 0; i < detailedArray.length();i++) {
            JSONObject obj = detailedArray.getJSONObject(i);
            String product_title = obj.getString("product_title");
            String price = obj.getString("price");
            String number = obj.getString("number");
            String product_image = obj.getString("product_image");
            HashMap<String,String> map = new HashMap<>();
            map.put("product_title",product_title);
            map.put("price",price);
            map.put("number",number);
            map.put("product_image",product_image);
            mList.add(map);
            map = null;
        }
        waterAdapter.notifyDataSetChanged();
    }


    private void initView() {

        ll_gaode = findViewById(R.id.ll_gaode);
        ll_baidu = findViewById(R.id.ll_baidu);
        tv_confirm_receive = findViewById(R.id.tv_confirm_receive);
        tv_action_by_express = findViewById(R.id.tv_action_by_express);
        tv_address = findViewById(R.id.tv_address);
        tv_send_by_me = findViewById(R.id.tv_send_by_me);
        tv_name = findViewById(R.id.tv_name);
        rl_phone = findViewById(R.id.rl_phone);
        recycler = findViewById(R.id.recycler);
        rl_back = findViewById(R.id.rl_back);
        tv_dispacth = findViewById(R.id.tv_dispacth);
        tv_send_finish = findViewById(R.id.tv_send_finish);

        tv_order_id = findViewById(R.id.tv_order_id);
        tv_create_time = findViewById(R.id.tv_create_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_dispath_type = findViewById(R.id.tv_dispatch_type);
        tv_remark = findViewById(R.id.tv_remark);
        rl_bottom_action = findViewById(R.id.rl_bottom_action);

        tv_address.setText(intentData.getAddress());
        tv_name.setText(intentData.getName());
    }

    private void initParams() {
        Intent intent = getIntent();
        intentData = (TakeOutModel) intent.getSerializableExtra("data");
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        waterAdapter = new ProducelistAdapter(this, mList);
        recycler.setAdapter(waterAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    private void initClickListener() {
        //配送
        tv_dispacth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DispatchingTypeActivity.actionTo(ServiceDetailActivity.this, intentData);
                finish();
            }
        });

        //确认接单
        tv_confirm_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveOrder(intentData.getOrders_id());
            }
        });

        //送达
        tv_send_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrderSend(intentData.getOrders_id());
            }
        });

        //自己配送
        tv_send_by_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });


        ll_gaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMapUtil.openGaoDeMap(ServiceDetailActivity.this, intentData.getLatitude(), intentData.getLongitude(), intentData.getAddress());
               /* OpenExternalMapAppUtils.openMapMarker(ServiceDetailActivity.this, intentData.getLongitude(), intentData.getLatitude(),
                        intentData.getAddress(), intentData.getAddress(), "兵兵到家", 0);*/
            }
        });

        ll_baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMapUtil.openBaiduMap(ServiceDetailActivity.this, intentData.getLatitude(), intentData.getLongitude(), intentData.getAddress());
            /*    OpenExternalMapAppUtils.openMapMarker(ServiceDetailActivity.this, intentData.getLongitude(), intentData.getLatitude(),
                        intentData.getAddress(), intentData.getAddress(), "兵兵到家", 1);*/
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rl_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = intentData.getPhoneNumber();
                if (phone == null || "".equals(phone)) {
                    ToastUtil.showShort("用户未填写电话");
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + intentData.getPhoneNumber());
                    intent.setData(data);
                    startActivity(intent);
                }
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

    private void changeDistributionMode() {
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", intentData.getOrders_id());
        map.put("types", 1 + "");
        map.put("money", "0");
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.changeDistributionMode(signature, map);
        mRequestQueue.add(REQUEST_SELECT_DISTRIBUTION, request, mResponseListener);
    }
}
