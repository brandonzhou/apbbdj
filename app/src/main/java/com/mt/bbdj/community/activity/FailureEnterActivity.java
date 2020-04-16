package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.ocr.sdk.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
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

public class FailureEnterActivity extends BaseActivity {

    private String user_id = "";
    private RequestQueue mRequestQueue;

    private ImageView iv_back;
    private LinearLayout ll_change_pannel;
    private Button bt_take_camera, bt_delete, bt_confirm;
    private ImageView iv_picture;
    private TextView tv_title;
    private EditText et_code, et_way_number, et_phone;

    private int mPage = 1;
    private int REQUEST_TAKE = 100;
    private int REQUEST_FAILURE = 1001;   //请求数据
    private int REQUEST_CHANGE = 1002;    //提交修改的数据
    private int REQUEST_DELETE = 1003;   //删除数据

    private List<String> pictureList = new ArrayList<>();

    private List<HashMap<String, String>> mList = new ArrayList<>();

    public static void actionTo(Context context, String user_id) {
        Intent intent = new Intent(context, FailureEnterActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure_enter);
        initParams();
        requestData();
        initView();
        initClickListener();
    }

    private void initClickListener() {
        iv_back.setOnClickListener(view -> finish());
        bt_delete.setOnClickListener(view -> deleteFromList());
        bt_confirm.setOnClickListener(view -> commitData());
        iv_picture.setOnClickListener(view -> setChangePannel());
    }

    private void commitData() {
        String code = et_code.getText().toString();
        String wayNumber = et_way_number.getText().toString();
        String phone = et_phone.getText().toString();
        if ("".equals(code)||"".equals(wayNumber)||"".equals(phone)){
            ToastUtil.showShort("数据不可为空");
            return;
        }
        if (!StringUtil.isMachinePhone(phone)){
            ToastUtil.showShort("手机号码不合法");
            return;
        }
        HashMap<String, String> dataMap = mList.get(0);
        HashMap<String, String> params = new HashMap<>();
        params.put("pie_id", dataMap.get("pie_id"));
        params.put("mobile", phone);
        params.put("waybill_number", wayNumber);
        params.put("pickup_code", code);
        params.put("station_id", user_id);
        Request<String> request = NoHttpRequest.commitDataRequest(params);
        mRequestQueue.add(REQUEST_CHANGE, request, onResponseListener);
    }


    private void deleteFromList() {
        if (isFinish) {
            ToastUtil.showShort("暂无数据");
            return;
        }
        String pie_id = mList.get(0).get("pie_id");
        HashMap<String, String> params = new HashMap<>();
        params.put("pie_id", pie_id);
        params.put("station_id", user_id);
        Request<String> request = NoHttpRequest.delFailureEnterData(params);
        mRequestQueue.add(REQUEST_DELETE, request, onResponseListener);
    }

    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", mPage + "");
        params.put("station_id", user_id);
        Request<String> request = NoHttpRequest.getFailureEnterData(params);
        mRequestQueue.add(REQUEST_FAILURE, request, onResponseListener);
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(FailureEnterActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "EnterDataActivity::" + response.get());
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
            ToastUtil.showShort("网络异常请重试！");
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        if (what == REQUEST_FAILURE) {    //获取失败的数据
            handleFailureData(jsonObject);
        }
        if (what == REQUEST_CHANGE) {    //修改数据
            handleChangeData(jsonObject);
        }
        if (what == REQUEST_DELETE) {    //删除数据
            handleDeleteData();
        }
    }

    private void handleDeleteData() {
        mPage++;
        requestData();
    }

    private void handleChangeData(JSONObject jsonObject) {
        mPage++;
        requestData();
    }

    private void handleFailureData(JSONObject jsonObject) throws JSONException {
        mList.clear();
        pictureList.clear();
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray dataArray = data.getJSONArray("data");
        tv_title.setText("");
        et_code.setText("");
        et_way_number.setText("");
        et_phone.setText("");
        if (dataArray.length() == 0) {
            showPromoteDialog();
        } else {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("pie_id", obj.getString("pie_id"));
                map.put("picture", obj.getString("picture"));
                map.put("pickup_code", obj.getString("pickup_code"));
                map.put("waybill_number", StringUtil.handleNullResultForString(obj.getString("waybill_number")));
                map.put("mobile", StringUtil.handleNullResultForString(obj.getString("mobile")));
                map.put("states", obj.getString("states"));
                map.put("baidu_mobile", obj.getString("baidu_mobile"));
                map.put("tencent_mobile", obj.getString("tencent_mobile"));
                map.put("types", obj.getString("types"));
                map.put("title", StringUtil.handleNullResultForString(obj.getString("title")));
                map.put("express_name", StringUtil.handleNullResultForString(StringUtil.handleNullResultForString(obj.getString("express_name"))));
                Glide.with(FailureEnterActivity.this).load(obj.getString("picture")).into(iv_picture);
                tv_title.setVisibility(View.VISIBLE);
                tv_title.setText(map.get("title"));
                et_code.setText(map.get("pickup_code"));
                et_way_number.setText(map.get("waybill_number"));
                et_phone.setText(map.get("mobile"));
                mList.add(map);
                pictureList.add(obj.getString("picture"));
                isFinish = false;
                map = null;
            }
        }
    }

    private void showPromoteDialog() {
        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setWidth(0.8f)
                .setText("\n数据处理中，请稍候再试\n")
                .setPositive("返回", null)
                .setOnCancelListener(dialogInterface -> {
                    finish();
                })
                .show(getSupportFragmentManager());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_TAKE) {
            String picture = data.getStringExtra("picture");
            mList.get(0).put("picture", picture);
            pictureList.set(0, picture);
        }
    }

    private void takePictureAgain() {
        HashMap<String, String> dataMap = mList.get(0);
        String pickup_code = dataMap.get("pickup_code");
        String pie_id = dataMap.get("pie_id");
    }


    private boolean isFinish = false;


    private void setPannelData(int position) {
        HashMap<String, String> dataMap = mList.get(position);
        tv_title.setText(dataMap.get("title"));
        et_code.setText(dataMap.get("pickup_code"));
        et_way_number.setText(dataMap.get("waybill_number"));
        et_phone.setText(dataMap.get("mobile"));
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        bt_delete = findViewById(R.id.bt_delete);
        bt_confirm = findViewById(R.id.bt_confirm);
        ll_change_pannel = findViewById(R.id.ll_change_pannel);
        bt_take_camera = findViewById(R.id.bt_take_camera);
        iv_picture = findViewById(R.id.iv_picture);
        tv_title = findViewById(R.id.tv_title);
        et_code = findViewById(R.id.et_code);
        et_way_number = findViewById(R.id.et_way_number);
        et_phone = findViewById(R.id.et_phone);
    }

    private void setChangePannel() {
        if (ll_change_pannel.getVisibility() == View.VISIBLE) {
            bt_take_camera.setVisibility(View.GONE);
            ll_change_pannel.setVisibility(View.GONE);
            tv_title.setVisibility(View.GONE);
        } else {
            ll_change_pannel.setVisibility(View.VISIBLE);
            // bt_take_camera.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.VISIBLE);
        }
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        user_id = getIntent().getStringExtra("user_id");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
