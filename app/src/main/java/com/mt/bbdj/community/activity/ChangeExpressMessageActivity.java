package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.EnterDetailModel;
import com.mt.bbdj.baseconfig.model.EventModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
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
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ChangeExpressMessageActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    RelativeLayout iv_back;
    @BindView(R.id.item_express_code)
    EditText itemExpressCode;
    @BindView(R.id.item_express)
    TextView itemExpress;
    @BindView(R.id.item_change_yundan)
    TextView itemChangeYundan;
    @BindView(R.id.item_change_phone)
    TextView itemChangePhone;
    @BindView(R.id.item_confirm_enter)
    TextView itemConfirmEnter;
    private EnterDetailModel mExpressMessage;
    private RequestQueue mRequestQueue;

    private final int REQUEST_COMMIT = 1001;
    private String courier_id,express_id,mCode,mPhone,mNumber,express_name;

    public static void actionTo(Context context, EnterDetailModel model) {
        Intent intent = new Intent(context, ChangeExpressMessageActivity.class);
        intent.putExtra("model", model);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_express_message);
        ButterKnife.bind(this);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ChangeExpressMessageActivity.this);
        initParams();
        initClickListener();
    }

    private void initClickListener() {
        iv_back.setOnClickListener(v -> finish());
        itemConfirmEnter.setOnClickListener(v -> {
            commitRequest();
        });
        itemExpress.setOnClickListener(v -> {
            if ("1".equals(mExpressMessage.getIsEnter())){
                ToastUtil.showShort(mExpressMessage.getMessage());
            } else {
                SelectExpressActivity.actionTo(ChangeExpressMessageActivity.this,100);
            }
        });
        itemChangeYundan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if ("1".equals(mExpressMessage.getIsEnter())){
                        ToastUtil.showShort(mExpressMessage.getMessage());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void commitRequest() {
        mCode = itemExpressCode.getText().toString().trim();
        String express = itemExpress.getText().toString().trim();
        mNumber = itemChangeYundan.getText().toString().trim();
        mPhone = itemChangePhone.getText().toString().trim();
        if ("".equals(mCode)||"".equals(express)||"".equals(mPhone)||"".equals(mNumber)||"".equals(express_id)){
            ToastUtil.showShort("请完善信息");
            return;
        }
        commit();
    }

    private void commit() {
        HashMap<String, String> params = new HashMap<>();
        params.put("station_id", courier_id);
        params.put("pie_id",mExpressMessage.getPie_id());
        params.put("express_id",express_id);
        params.put("code",mCode);
        params.put("mobile",mPhone);
        params.put("number",mNumber);
        params.put("uuid", UUID.randomUUID().toString());
        Request<String> request = NoHttpRequest.commitChange(params);
        mRequestQueue.add(REQUEST_COMMIT, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(ChangeExpressMessageActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    Log.d("====++",response.get());
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        EventBus.getDefault().post(new EventModel(EventModel.TYPE_FRESH));
                        finish();
                    }
                    ToastUtil.showShort(msg);
                    LoadDialogUtils.cannelLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                    ToastUtil.showShort("网络异常请重试！");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                ToastUtil.showShort("网络异常请重试！");
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        UserBaseMessageDao userMessageDao = GreenDaoManager.getInstance().getSession().getUserBaseMessageDao();
        List<UserBaseMessage> list = userMessageDao.queryBuilder().list();
        if (list.size() != 0) {
            courier_id = list.get(0).getUser_id();
        }
        mExpressMessage = (EnterDetailModel) getIntent().getSerializableExtra("model");
        itemExpressCode.setText(mExpressMessage.getCode());
        itemExpress.setText(mExpressMessage.getExpress_name());
        itemChangeYundan.setText(mExpressMessage.getExpress_yundan());
        itemChangePhone.setText(mExpressMessage.getPhone());

        express_id = mExpressMessage.getExpress_id();
        mCode = mExpressMessage.getCode();
        mPhone = mExpressMessage.getPhone();
        mNumber = mExpressMessage.getExpress_yundan();
        express_name = mExpressMessage.getExpress_name();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        express_id =  data.getStringExtra("express_id");
        express_name =  data.getStringExtra("express_name");
        itemExpress.setText(express_name);
    }
}
