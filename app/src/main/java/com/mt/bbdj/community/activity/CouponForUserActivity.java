package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.CouponModel;
import com.mt.bbdj.baseconfig.model.UserForCouponModel;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.CouponAdapter;
import com.mt.bbdj.community.adapter.CouponForUserAdapter;
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

public class CouponForUserActivity extends BaseActivity{

    private RelativeLayout rl_back;
    private XRecyclerView recyclerView;
    private CouponForUserAdapter mAdapter;
    private String user_id;
    private TextView tv_faquan_all;
    private TextView tv_title_have_dispath;
    private TextView tv_title_no_dispath;
    private RequestQueue mRequestQueue;

    private final int REQUEST_FOLLOW_STATION = 1002;    //关注人数
    private final int REQUEST_DISPATCH_COUPON = 1003;    //发放优惠券
    private CouponModel mCouponModel;

    private int mPage = 1;

    private List<UserForCouponModel> mList = new ArrayList<>();

    public static void actionTo(Context context, String user_id,CouponModel couponModel) {
        Intent intent = new Intent(context, CouponForUserActivity.class);
        intent.putExtra("data", couponModel);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_for_user);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(CouponForUserActivity.this);

        initParams();
        initView();
        initClickListener();
        requesData();     //请求关注人数
    }

    private void initClickListener() {

        //返回
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //一起发券
        tv_faquan_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提示全量发放代金券
                showDispathCouponDialog();
            }
        });

        //发券
        mAdapter.setOnClickManager(new CouponForUserAdapter.OnClickManager() {
            @Override
            public void onDispathCouponClick(int position) {
                dispathCoupon( mList.get(position).getMember_id());
            }
        });
    }

    private void showDispathCouponDialog() {
        String message = "给"+tv_title_no_dispath.getText().toString()+"发优惠券？";


        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setText("\n"+message+"\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String memberS = getMemberId();
                        dispathCoupon(memberS);
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    private String getMemberId() {
        StringBuilder sb = new StringBuilder();
        for(UserForCouponModel model : mList) {
            sb.append(model.getMember_id());
            sb.append(",");
        }
        String data = sb.toString();
        if (data.length() == 0) {
            return "";
        } else {
            return data.substring(0,data.lastIndexOf(","));
        }
    }

    private void requesData() {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("page", mPage+"");
        params.put("coupon_id", mCouponModel.getCoupon_id());
        Request<String> request = NoHttpRequest.getFollowStation(params);
        mRequestQueue.add(REQUEST_FOLLOW_STATION, request, onResponseListener);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        recyclerView = findViewById(R.id.recycler);
        tv_faquan_all = findViewById(R.id.tv_faquan);
        tv_title_have_dispath = findViewById(R.id.tv_title_have_dispath);
        tv_title_no_dispath = findViewById(R.id.tv_title_no_dispath);

        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#f8f8f8"), 1));
        mAdapter = new CouponForUserAdapter(this,mList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(CouponForUserActivity.this).resumeRequests();//恢复Glide加载图片
                }else {
                    Glide.with(CouponForUserActivity.this).pauseRequests();//禁止Glide加载图片
                }
            }
        });
    }

    private void initParams() {
        mCouponModel = (CouponModel) getIntent().getSerializableExtra("data");
        user_id = getIntent().getStringExtra("user_id");
        mRequestQueue = NoHttp.newRequestQueue();
    }

    public OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(CouponForUserActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "CouponForUserActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();

              /*  if (isFresh){
                    mList.clear();
                    mAdapter.notifyDataSetChanged();
                    recyclerView.refreshComplete();
                } else {
                    recyclerView.loadMoreComplete();
                }*/

                if ("5001".equals(code)) {
                    handleEvent(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
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

        }
    };

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_FOLLOW_STATION:   //查询
                followStation(jsonObject);
                break;
            case REQUEST_DISPATCH_COUPON:   //发放
                dispatchCoupon(jsonObject);
                break;
        }
    }

    private void dispatchCoupon(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.get("msg").toString();
        ToastUtil.showShort(msg);
        requesData();     //刷新列表
    }

    private void followStation(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String already_fans = StringUtil.handleNullResultForNumber(dataObj.getString("already_fans"));    //发放人数
        String not_fans = StringUtil.handleNullResultForNumber(dataObj.getString("not_fans"));    //剩余人数

        tv_title_have_dispath.setText("已经发放给"+already_fans+"个用户");
        tv_title_no_dispath.setText("剩余"+not_fans+"个用户");

        mList.clear();
        mAdapter.notifyDataSetChanged();

        JSONArray fans = dataObj.getJSONArray("fans");
        for (int i = 0; i < fans.length();i++) {
            JSONObject obj = fans.getJSONObject(i);
            UserForCouponModel model = new UserForCouponModel();
            model.setMember_id(obj.getString("member_id"));
            model.setLast_buy_time(obj.getString("last_buy_time"));
            model.setHeadImge(obj.getString("user_headimg"));
            model.setType(obj.getString("type"));
            model.setUser_name(obj.getString("user_name"));
            mList.add(model);
            model = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    //发放优惠券
    private void dispathCoupon(String member_data) {
        Map<String, String> params = new HashMap<>();
        params.put("distributor_id", user_id);
        params.put("coupon_id", mCouponModel.getCoupon_id());
        params.put("member_data", member_data);
        Request<String> request = NoHttpRequest.dispathCoupon(params);
        mRequestQueue.add(REQUEST_DISPATCH_COUPON, request, onResponseListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    private boolean isFresh = true;

}
