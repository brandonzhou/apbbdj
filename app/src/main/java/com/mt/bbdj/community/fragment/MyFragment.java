package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.MywalletActivity;
import com.mt.bbdj.community.activity.SettingActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/9/16
 * @Description :
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id = "";
    private final int REQUEST_GET_MESSAGE = 1001;    //我的基本信息

    private ImageView head;
    private ImageView iv_manager_phone,iv_manager_head;
    private LinearLayout ll_pill,ll_setting,ll_money;
    private TextView tv_name,tv_code,tv_phone,tv_money,tv_manager_name,tv_manager_phone,tv_service_phone;

    public static MyFragment getInstance() {
        MyFragment comDataFragment = new MyFragment();
        return comDataFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,container, false);
        initView(view);
        initParams();
        initListener();
        //requestData();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_pill:    //账单
            case R.id.ll_money:
                showMyWalletPannel();
                break;
            case R.id.ll_setting:  //设置
                showSettingPannel();
                break;
            case R.id.iv_manager_phone:   //联系管家
                contractManager();
                break;
        }
    }

    private void contractManager() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + tv_manager_phone.getText().toString().trim());
        intent.setData(data);
        startActivity(intent);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            requestData();
        }
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void initListener() {
        ll_pill.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        iv_manager_phone.setOnClickListener(this);
        ll_money.setOnClickListener(this);

    }

    private void initView(View view) {
        head = view.findViewById(R.id.head);
        ll_pill = view.findViewById(R.id.ll_pill);
        ll_setting = view.findViewById(R.id.ll_setting);
        ll_money = view.findViewById(R.id.ll_money);
        tv_name = view.findViewById(R.id.tv_name);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_code = view.findViewById(R.id.tv_code);
        tv_money = view.findViewById(R.id.tv_money);
        tv_manager_name = view.findViewById(R.id.tv_manager_name);
        tv_manager_phone = view.findViewById(R.id.tv_manager_phone);
        iv_manager_head = view.findViewById(R.id.iv_manager_head);
        tv_service_phone = view.findViewById(R.id.tv_service_phone);
        iv_manager_phone = view.findViewById(R.id.iv_manager_phone);
    }

    private void requestData() {
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",user_id);
        Request<String> request = NoHttpRequest.getMyMessage(params);
        mRequestQueue.add(REQUEST_GET_MESSAGE, request, mResponseListener);
    }

    public OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
          //  LoadDialogUtils.getInstance().showLoadingDialog(getActivity());
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "MyFragment::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    setMessage(jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //  LoadDialogUtils.cannelLoadingDialog();
            }
           // LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            // LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void setMessage(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String headimg = data.getString("headimg");
        String username = data.getString("username");
        String balance = data.getString("balance");
        String min_balance = data.getString("min_balance");
        String prohibit = data.getString("prohibit");
        String is_close = data.getString("is_close");
        String latitude = data.getString("latitude");
        String longitude = data.getString("longitude");
        String region = data.getString("region");
        String address = data.getString("address");
        String station_number = data.getString("station_number");
        String account = data.getString("account");
        String service_phone = data.getString("service_phone");
        String market_headimg = data.getString("market_headimg");
        String market_telephone = data.getString("market_telephone");
        String market_realname = data.getString("market_realname");
        Glide.with(getActivity()).load(headimg).into(head);
        Glide.with(getActivity()).load(market_headimg).into(iv_manager_head);

        tv_name.setText(StringUtil.handleNullResultForString(username));
        tv_code.setText(StringUtil.handleNullResultForString(station_number));
        tv_phone.setText(StringUtil.handleNullResultForString(account));
        tv_money.setText(StringUtil.handleNullResultForString(balance));
        tv_manager_name.setText(StringUtil.handleNullResultForString(market_realname));
        tv_manager_phone.setText(StringUtil.handleNullResultForString(market_telephone));
        tv_service_phone.setText(StringUtil.handleNullResultForString(service_phone));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }



    private void showMyWalletPannel() {
        Intent intent = new Intent(getActivity(), MywalletActivity.class);
        startActivity(intent);
    }

    private void showSettingPannel() {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);

    }


}
