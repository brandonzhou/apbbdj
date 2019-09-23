package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.CannelOrderAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
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

//取消订单原因
public class CannelOrderActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.rl_cause_order)
    RecyclerView rlCauseOrder;
    @BindView(R.id.bt_commit_next)
    TextView btCommitNext;
    @BindView(R.id.bt_commit_no)
    TextView bt_commit_no;
    @BindView(R.id.tv_refause_title)
    TextView tvRefauseTitle;

    private String user_id;    //用户id
    private String mail_id;     //订单id
    private String reason_id;    //取消原因id
    private String content;   //备注

    private List<HashMap<String, String>> mList = new ArrayList<>();

    private final int REQUEST_CAUSE_CANNEL = 101;    //请求快递取消订单原因
    private final int REQUEST_SERVICE_CANNEL = 105;    //请求服务取消订单原因
    private final int REQUEST_COMMIT_CAUSE = 102;   //提交取消订单
    private final int REQUEST_COMMIT_SERVICE_CAUSE = 106;   //提交服务取消订单
    private final int REQUEST_CANNEL_ORDER_FROM_SEN_MANAGER = 103;    //从寄件管理界面跳转过来取消订单

    public static final String STATE_CANNEL_FOR_EXPRESS = "100";      //快递取消
    public static final String STATE_CANNEL_FOR_SERVICE = "200";    //服务类型的取消

    private CannelOrderAdapter mAdapter;

    private RequestQueue mRequestQueue;
    private String cannel_type;   //0 ：快递  1：桶装水  2: 干洗
    private OkHttpClient okHttpClient;
    private String orders_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_cause__order);
        ButterKnife.bind(this);
        initParams();
        initData();
        initList();
        initClickListener();
        RequestCauseData();    //获取取消原因
    }

    @OnClick({R.id.iv_back, R.id.bt_commit_next, R.id.bt_commit_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_commit_next:
                commitCauseRequest();
                break;
            case R.id.bt_commit_no:
                finish();
                break;
        }
    }

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_CAUSE_CANNEL:
                handleCannelCause(jsonObject);    //获取取消消息
                break;
            case REQUEST_SERVICE_CANNEL:
                handleServiceCause(jsonObject);    //获取其他服务的取消原因
                break;
            case REQUEST_COMMIT_SERVICE_CAUSE:
                handleCannelServiceCause(jsonObject);    //取消服务类型的订单
                break;
            case REQUEST_COMMIT_CAUSE:     //取消
            case REQUEST_CANNEL_ORDER_FROM_SEN_MANAGER:    //取消从寄件管理来的订单
                ToastUtil.showShort("提交成功！");
                EventBus.getDefault().post(new TargetEvent(1));
                finish();
                break;
        }
    }

    private void handleCannelServiceCause(JSONObject jsonObject) {
        try {
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            if ("5001".equals(code)) {
                setResult(RESULT_OK);
                finish();
            }
            ToastUtil.showShort(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleServiceCause(JSONObject jsonObject) {
        try {
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");

            if ("5001".equals(code)) {
                JSONArray data = jsonObject.getJSONArray("data");
                mList.clear();
                int number = 1;
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject1 = data.getJSONObject(i);
                    String reason_id = jsonObject1.getString("id");
                    String reason_name = jsonObject1.getString("reason_name");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("reason_id", reason_id);
                    map.put("reason_name", reason_name);
                    map.put("reason_number", "0" + (number + i) + ".");
                    mList.add(map);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                ToastUtil.showShort(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCannelCause(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        mList.clear();
        int number = 1;
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            String reason_id = jsonObject1.getString("reason_id");
            String reason_name = jsonObject1.getString("reason_name");
            HashMap<String, String> map = new HashMap<>();
            map.put("reason_id", reason_id);
            map.put("reason_name", reason_name);
            map.put("reason_number", "0" + (number + i) + ".");
            mList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(CannelOrderActivity.this);

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "CauseCannelOrderActivity::" + response.get());
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

    private void initClickListener() {

        mAdapter.setOnItemClickListener(new CannelOrderAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                reason_id = mList.get(position).get("reason_id");
                HashMap<String, String> map = mList.get(position);
                mAdapter.setCurrentClickPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void RequestCauseData() {
        Request<String> request = null;
        if (STATE_CANNEL_FOR_EXPRESS.equals(cannel_type)) {   //快递
            request = NoHttpRequest.getCauseForCannelOrderRequest(user_id);
            mRequestQueue.add(REQUEST_CAUSE_CANNEL, request, onResponseListener);
        } else if (STATE_CANNEL_FOR_SERVICE.equals(cannel_type)) {   //桶装水和干洗
            request = NoHttpRequest.getCannelResult(user_id);
            mRequestQueue.add(REQUEST_SERVICE_CANNEL, request, onResponseListener);
        }
    }

    private void initList() {
        mAdapter = new CannelOrderAdapter(mList);
        rlCauseOrder.setFocusable(false);
        rlCauseOrder.setNestedScrollingEnabled(false);
        rlCauseOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        rlCauseOrder.setAdapter(mAdapter);
    }

    private void initParams() {
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        mail_id = intent.getStringExtra("mail_id");
        cannel_type = intent.getStringExtra("cannel_type");
        orders_id = intent.getStringExtra("orders_id");
    }

    private void commitCauseRequest() {

        if ("".equals(reason_id)) {
            ToastUtil.showShort("请选择取消原因！");
            return;
        }

        if (STATE_CANNEL_FOR_EXPRESS.equals(cannel_type)) {     //快递取消
            Request<String> request = NoHttpRequest.commitCannelOrderCauseRequest(user_id, mail_id, reason_id, content);
            mRequestQueue.add(REQUEST_COMMIT_CAUSE, request, onResponseListener);
        } else {   //取消服务类型的订单
            Request<String> request = NoHttpRequest.confirmServiceCannelOrder(user_id,orders_id, reason_id);
            mRequestQueue.add(REQUEST_COMMIT_SERVICE_CAUSE, request, onResponseListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
