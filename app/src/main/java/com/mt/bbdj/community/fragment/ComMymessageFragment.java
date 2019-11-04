package com.mt.bbdj.community.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginActivity;
import com.mt.bbdj.baseconfig.activity.LoginByCodeActivity;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.AboutAppActivity;
import com.mt.bbdj.community.activity.BaseMessageActivity;
import com.mt.bbdj.community.activity.ConsumeRecordActivity;
import com.mt.bbdj.community.activity.MyAddressActivity;
import com.mt.bbdj.community.activity.MyOrderActivity;
import com.mt.bbdj.community.activity.MywalletActivity;
import com.mt.bbdj.community.activity.SettingCenterActivity;
import com.mylhyl.circledialog.CircleDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description :  社区版我的
 */
public class ComMymessageFragment extends BaseFragment {

    @BindView(R.id.ll_base_message)
    LinearLayout llBaseMessage;
    @BindView(R.id.ll_my_wallet)
    LinearLayout llMyWallet;
    @BindView(R.id.ll_center_sitting)
    LinearLayout llCenterSitting;
    @BindView(R.id.ll_about_app)
    LinearLayout llAboutApp;
    @BindView(R.id.ll_connect_service)
    LinearLayout llConnectService;
    @BindView(R.id.ll_connect_manager)
    LinearLayout llConnectManager;
    @BindView(R.id.ll_my_consume)
    LinearLayout ll_my_consume;
    @BindView(R.id.bt_cannel)
    Button btCannel;
    Unbinder unbinder;
    @BindView(R.id.tv_shop_local)
    TextView tvShopLocal;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_ming_money)
    TextView tvMingMoney;    //警戒余额

    private SharedPreferences.Editor editor;
    private String user_id;
    private RequestQueue mRequestQueue;
    private final int REQUEST_GET_USER_MESSAGE = 100;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor1;
    private UserBaseMessageDao userBaseMessageDao;
    private IWXAPI api;
    private UserBaseMessage mUserBaseMessage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_com_my_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initParams();
        initData();
        return view;
    }

    private void initParams() {
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = userBaseMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            mUserBaseMessage = list.get(0);
            user_id = mUserBaseMessage.getUser_id();

            String address = mUserBaseMessage.getAddress();
            if (address != null && address.contains("泉州市")) {
                tvMingMoney.setVisibility(View.GONE);
            } else{
                tvMingMoney.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            requestBaseMessage();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick({R.id.ll_base_message, R.id.ll_my_wallet, R.id.ll_center_sitting,
            R.id.ll_about_app, R.id.ll_connect_service, R.id.ll_connect_manager,
            R.id.bt_cannel, R.id.ll_my_order, R.id.ll_address_manager,R.id.ll_my_consume})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_base_message:
                showBaseMessagePannel();    //基础信息面板
                break;
            case R.id.ll_my_wallet:
                showMyWalletPannel();     //我的钱包面板
                break;
            case R.id.ll_my_order:
                showOrderPannel();        //我的订单
                break;
            case R.id.ll_address_manager:
                showAddressManagerPannel();   //地址管理
                break;
            case R.id.ll_center_sitting:
                showSettingPannel();     //设置中心
                break;
            case R.id.ll_about_app:
                showAboutAppPannel();    //关于app
                break;
            case R.id.ll_connect_service:
                showConnectService();     //联系客服
                break;
            case R.id.ll_connect_manager:
                showConnectMaster();     //联系管家
                break;
            case R.id.ll_my_consume:
                showConsumeRecordPannel();
                break;
            case R.id.bt_cannel:
                takeoutLogin();     //退出登录
                break;
        }
    }

    private void showConnectMaster() {

        new CircleDialog.Builder()
                .setTitle("联系管家")
                .setText("\n400-775-0008\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("呼叫", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + "400-775-0008");
                        intent.setData(data);
                        startActivity(intent);
                    }
                })
                .setNegative("取消", null)
                .show(getFragmentManager());
    }

    private void showConnectService() {
        new CircleDialog.Builder()
                .setTitle("联系管家")
                .setText("\n400-775-0008\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("呼叫", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + "400-775-0008");
                        intent.setData(data);
                        startActivity(intent);
                    }
                })
                .setNegative("取消", null)
                .show(getFragmentManager());
    }

    private void showConsumeRecordPannel() {
        Intent intent = new Intent(getActivity(), ConsumeRecordActivity.class);
        startActivity(intent);
    }

    private void showAddressManagerPannel() {
        Intent intent = new Intent(getActivity(), MyAddressActivity.class);
        startActivity(intent);
    }

    private void showAboutAppPannel() {
        Intent intent = new Intent(getActivity(), AboutAppActivity.class);
        startActivity(intent);
    }

    private void showSettingPannel() {
        Intent intent = new Intent(getActivity(), SettingCenterActivity.class);
        startActivity(intent);
    }

    private void showOrderPannel() {
        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
        startActivity(intent);
    }

    private void showMyWalletPannel() {
        Intent intent = new Intent(getActivity(), MywalletActivity.class);
        startActivity(intent);
    }

    private void showBaseMessagePannel() {
        Intent intent = new Intent(getActivity(), BaseMessageActivity.class);
        startActivity(intent);
    }

    private void takeoutLogin() {
        //editor.putBoolean("firstStart",true);
        editor.putString("userName", "");
        editor.putString("password", "");
        editor.putBoolean("update", false);
        editor.commit();
        EventBus.getDefault().post(new TargetEvent(111));
        Intent intent = new Intent(getActivity(), LoginByCodeActivity.class);
        startActivity(intent);
        getActivity().onBackPressed();//销毁自己
    }

    private void initData() {
        editor = SharedPreferencesUtil.getEditor();
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
    }

    public static ComMymessageFragment getInstance() {
        ComMymessageFragment comMymessageFragment = new ComMymessageFragment();
        return comMymessageFragment;
    }


    private void requestBaseMessage() {
        Request<String> request = NoHttpRequest.getUserBaseMessageRequest(user_id);
        mRequestQueue.add(REQUEST_GET_USER_MESSAGE, request, onResponseListener);
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ComMymessageFragment::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                JSONObject dataObj = jsonObject.getJSONObject("data");
                if ("5001".equals(code)) {
                    userBaseMessageDao.deleteAll();
                    String user_id = dataObj.getString("user_id");
                    String headimg = dataObj.getString("headimg");
                    String mingcheng = dataObj.getString("mingcheng");
                    String contacts = dataObj.getString("contacts");
                    String contact_number = dataObj.getString("contact_number");
                    String contact_email = dataObj.getString("contact_email");
                    String contact_account = dataObj.getString("contact_account");
                    String address = dataObj.getString("address");
                    String latitude = dataObj.getString("latitude");
                    String longitude = dataObj.getString("longitude");
                    String birthday = StringUtil.handleNullResultForNumber(dataObj.getString("birthday"));
                    String balance = StringUtil.handleNullResultForNumber(dataObj.getString("balance"));
                    String min_balance = StringUtil.handleNullResultForNumber(dataObj.getString("min_balance"));    //境界余额
                    UserBaseMessage userBaseMessage = new UserBaseMessage();
                    userBaseMessage.setUser_id(user_id);
                    userBaseMessage.setHeadimg(headimg);
                    userBaseMessage.setMingcheng(mingcheng);
                    userBaseMessage.setContacts(contacts);
                    userBaseMessage.setContact_number(contact_number);
                    userBaseMessage.setContact_email(contact_email);
                    userBaseMessage.setBirthday(birthday);
                    userBaseMessage.setBalance(balance);
                    userBaseMessage.setAddress(address);
                    userBaseMessage.setLatitude(latitude);
                    userBaseMessage.setLongitude(longitude);
                    userBaseMessage.setContact_account(contact_account);
                    tvShopLocal.setText(mingcheng);

                    double balanceNumber =  StringUtil.changeStringToDouble(balance);
                    double min_balanceNumber  =  StringUtil.changeStringToDouble(min_balance);
                    double userMoney = balanceNumber - min_balanceNumber;
                    tvMoney.setText("可用余额  " + StringUtil.formatDouble(userMoney)+"元");
                    tvBirthday.setText("入驻天数  " + birthday + "天");
                    userBaseMessageDao.save(userBaseMessage);
                    tvMingMoney.setText("保证金 : " + min_balance+"元");
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                ToastUtil.showShort("登录失效，请重新登录");
            }

        }

        @Override
        public void onFailed(int what, Response<String> response) {
            loginOut();
        }

        @Override
        public void onFinish(int what) {

        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    private void loginOut() {
        ToastUtil.showShort("登录状态失效，请重新登录！");
        editor.putString("userName", "");
        editor.putString("password", "");
        editor.putBoolean("update", false);
        editor.commit();
        EventBus.getDefault().post(new TargetEvent(111));
        Intent intent = new Intent(getActivity(), LoginByCodeActivity.class);
        startActivity(intent);
        getActivity().onBackPressed();//销毁自己
    }


}
