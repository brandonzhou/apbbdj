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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.mt.bbdj.baseconfig.utls.OpenMapUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.ProducelistAdapter;
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

//店主自己送
public class SendBymeActivity extends BaseActivity {

    private TextView tv_confirm_send;
    private LinearLayout ll_gaode;
    private LinearLayout ll_baidu;
    private TextView tv_address;
    private TextView tv_name;
    private RelativeLayout rl_phone;
    private RecyclerView recycler;
    private RelativeLayout rl_back;
    private TakeOutModel intentData;
    private String orders_id;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;

    public static void actionTo(Context context, TakeOutModel takeOutModel) {
        Intent intent = new Intent(context, SendBymeActivity.class);
        intent.putExtra("data",takeOutModel);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_byme);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SendBymeActivity.this);
        initParams();
        initView();
        initRecyclerView();
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
        tv_confirm_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeOrders();   //确认送达
            }
        });

        ll_gaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMapUtil.openGaoDeMap(SendBymeActivity.this,intentData.getLatitude(),intentData.getLongitude(),intentData.getAddress());
              /*  OpenExternalMapAppUtils.openMapMarker(SendBymeActivity.this,intentData.getLongitude(),intentData.getLatitude(),
                        intentData.getAddress(),  intentData.getAddress(),"A栈",0);*/
            }
        });

        ll_baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMapUtil.openBaiduMap(SendBymeActivity.this,intentData.getLatitude(),intentData.getLongitude(),intentData.getAddress());
              /*  OpenExternalMapAppUtils.openMapMarker(SendBymeActivity.this,intentData.getLongitude(),intentData.getLatitude(),
                        intentData.getAddress(),  intentData.getAddress(),"A栈",1);*/
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

    private void completeOrders() {
        Map<String,String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("orders_id", orders_id);
        String signature = StringUtil.getsignature(map);
        Request<String> request = NoHttpRequest.completeTakeOrders(signature,map);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(SendBymeActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "SendBymeActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if ("5001".equals(code)) {
                        CompleteServiceActivity.actionTo(SendBymeActivity.this);
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

    private void initView() {
        tv_confirm_send = findViewById(R.id.tv_confirm_send);
        ll_gaode = findViewById(R.id.ll_gaode);
        ll_baidu = findViewById(R.id.ll_baidu);
        tv_address = findViewById(R.id.tv_address);
        tv_name = findViewById(R.id.tv_name);
        rl_phone = findViewById(R.id.rl_phone);
        recycler = findViewById(R.id.recycler);
        rl_back = findViewById(R.id.rl_back);

        tv_address.setText(intentData.getAddress());
        tv_name.setText(intentData.getName());
    }


    private void initRecyclerView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setFocusable(false);
        recycler.setNestedScrollingEnabled(false);
        ProducelistAdapter waterAdapter = new ProducelistAdapter(this, intentData.getTakeOutList());
        recycler.setAdapter(waterAdapter);
    }

}
