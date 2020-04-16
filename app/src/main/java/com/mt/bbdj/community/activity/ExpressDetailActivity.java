package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.baidu.ocr.sdk.utils.LogUtil;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ExpressDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.rl_address_title)
    RelativeLayout rlAddressTitle;
    @BindView(R.id.tv_wail_number)
    TextView tvWailNumber;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_express_name)
    TextView tvExpressName;
    @BindView(R.id.tv_tag_phone)
    TextView tvTagPhone;
    @BindView(R.id.tv_tag_number)
    TextView tvTagNumber;
    @BindView(R.id.tv_enter_time)
    TextView tvEnterTime;
    @BindView(R.id.tv_out_time)
    TextView tvOutTime;
    @BindView(R.id.ll_out_layout)
    LinearLayout llOutLayout;
    @BindView(R.id.bt_out)
    Button btOut;
    @BindView(R.id.tv_sms_time)
    TextView tvSmsTime;
    @BindView(R.id.tv_sms_content)
    AppCompatTextView tvSmsContent;
    private String pie_id;
    private RequestQueue mRequestQueue;

    private final int REQUEST_TYPE_MESSAGE = 100;    //快递信息

    public static void actionTo(Context context, String pie_id) {
        Intent intent = new Intent(context, ExpressDetailActivity.class);
        intent.putExtra("pie_id", pie_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_detail);
        ButterKnife.bind(this);
        initParams();
        requestData();
        initClickListener();
    }


    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("pie_id", pie_id);
        params.put("uuid", UUID.randomUUID().toString());
        Request<String> request = NoHttpRequest.expressDetail(params);
        mRequestQueue.add(REQUEST_TYPE_MESSAGE, request, onResponseListener);
    }

    private void initParams() {
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ExpressDetailActivity.this);

        mRequestQueue = NoHttp.newRequestQueue();
        pie_id = getIntent().getStringExtra("pie_id");
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ExpressDetailActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "EnterSelectLocationActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
                LoadDialogUtils.cannelLoadingDialog();
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
                ToastUtil.showShort("网络异常请重试！");
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            ToastUtil.showShort(response.getException().toString());
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {
            LoadDialogUtils.cannelLoadingDialog();
        }
    };

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        if (what == REQUEST_TYPE_MESSAGE) {
            handleExpressMessage(jsonObject);
        }
    }

    private void handleExpressMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String pie_id = dataObj.getString("pie_id");
        String types = dataObj.getString("types");
        if ("0".equals(types)){
            // TODO: 2020/4/14 入库失败 ： 显示入库时间  右上角提示入库失败
            tvState.setText("入库失败");
            llOutLayout.setVisibility(View.GONE);
        } else if ("1".equals(types)){
            // TODO: 2020/4/14  显示入库时间  右上角提示入库成功
            tvState.setText("已入库");
            llOutLayout.setVisibility(View.GONE);
        } else if ("2".equals(types)){
            // TODO: 2020/4/14 同时显示出库时间  右上角提示出库成功
            tvState.setText("已出库");
        } else if ("3".equals(types)){
            // TODO: 2020/4/14  显示出库时间   右上角显示出库失败
            tvState.setText("出库失败");
        }
        tvWailNumber.setText(StringUtil.handleNullResultForString(dataObj.getString("number")));
        tvExpressName.setText(StringUtil.handleNullResultForString(dataObj.getString("express_name")));
        tvTagPhone.setText(StringUtil.handleNullResultForString(dataObj.getString("mobile")));
        tvTagNumber.setText(StringUtil.handleNullResultForString(dataObj.getString("code")));
        tvEnterTime.setText(StringUtil.handleNullResultForString(dataObj.getString("warehousing_time")));
        tvOutTime.setText(StringUtil.handleNullResultForString(dataObj.getString("out_time")));
        tvSmsTime.setText(StringUtil.handleNullResultForString(dataObj.getString("sms_create_time")));
        tvSmsContent.setText(StringUtil.handleNullResultForString(dataObj.getString("sms_content")));
    }

    private void initClickListener() {
        ivBack.setOnClickListener(v -> finish());
    }

}
