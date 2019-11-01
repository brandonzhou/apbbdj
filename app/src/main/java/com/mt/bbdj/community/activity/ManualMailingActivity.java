package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.ExpressAdapter;
import com.mt.bbdj.community.adapter.GoodsAdapter;
import com.mylhyl.circledialog.CircleDialog;
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

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ManualMailingActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_go_add_send_message, ll_go_add_receive_message;   //去添加地址
    private LinearLayout ll_send_layout, ll_receive_layout;   //地址信息
    private TextView tv_send_name, tv_send_phone, tv_send_address;     //寄件信息
    private TextView tv_receive_name, tv_receive_phone, tv_receive_address;     //收件信息
    private TextView tv_send_book, tv_receive_book;     //寄件、收件地址簿
    private LinearLayout ll_select_goods;    //选择物品重量
    private TextView tv_good_weigth;    //物品、重量
    private TextView tv_place_order;   //立刻下单
    private EditText et_money;     //实收金额
    private RelativeLayout iv_back;
    private ImageView iv_change_message;    //转换地址
    private TextView tv_forecast_price;    //预测价格

    private RecyclerView rl_express;

    private final int SELECT_SEND_MESSAGE_BY_BOOK = 1;    //选择收贱人
    private final int SELECT_RECEIVE_MESSAGE_BY_BOOK = 2;    //选择寄件人

    private final int ADD_SEND_ADDRESS = 1001;    //添加寄件信息
    private final int ADD_RECEIVE_ADDRESS = 1002;     //添加收件信心
    private final int CHANGE_SEND_ADDRESS = 1003;     //修改寄件信息
    private final int CHANGE_RECEIVE_ADDRESS = 1004;    //修改收件信息


    private final int REQUEST_GOODS_REQUEST = 2001;    //请求商品类型
    private final int REQUEST_EXPRESS_REQUEST = 2002;    //获取快递公司
    private final int REQUEST_COMMIT_ORDER = 2003;    //下单
    private final int REQUEST_IS_IDENTIFY_REQUEST = 2004;    //验证是否实名
    private final int REQUEST_ESTIMATE = 2005;    //预估价格
    private final int REQUEST_GET_DATA = 2006;    //获取运单号

    private String start_province = "", start_city = "", start_country = "", start_detial_address = "";   //寄件人的省市县
    private String end_province = "", end_city = "", end_country = "", end_detail_address = "";    //收件人省市县
    private String send_id = "";   //寄件人地址id
    private String receive_id = "";    //收件人地址id
    private String type_id = "";     //物品类型id
    private String user_id = "";     //用户id
    private String express_id = "";    //选择的快递公司id
    private String mail_id = "";   //运单号


    private PopupWindow popupWindow;      //物品类型对话框
    private View selectView;
    private RecyclerView myGridView;
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao mUserMessageDao;
    private ExpressLogoDao mExpressLogoDao;
    private ExpressAdapter expressAdapter;

    private int templeWeightSelect = 0;


    public static void actionTo(Context context) {
        Intent intent = new Intent(context, ManualMailingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mailing);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ManualMailingActivity.this);
        initParams();
        initView();
        initPopuStyle();
        requestExpressData();     //获取快递公司
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_go_add_send_message:   //去添加寄件人
                actionToAddSendMessage();
                break;
            case R.id.ll_go_add_receive_message:  //去添加收件人
                actionToAddReceiveMessage();
                break;
            case R.id.tv_send_book:     //寄件地址博选择
                actionToSendBook();
                break;
            case R.id.tv_receive_book:    //收件地址簿选择
                actionToReceiveBook();
                break;
            case R.id.ll_select_goods:    //选择物品、重量
                actionToSelectGoods();
                break;
            case R.id.ll_send_layout:    //修改 寄件信息
                actionToChangeSendMessage();
                break;
            case R.id.ll_receive_layout:   //修改收件信息
                actionToChangeReceiveMessage();
                break;
            case R.id.iv_change_message:  //转换信息
                actionToChangeMessage();
                break;
            case R.id.tv_place_order:    //立刻下单
                // PrintPannelActivity.actionTo(this);
                actionToPlaceOrder();
                break;
            case R.id.iv_back:    //返回
                actionToFinish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ADD_SEND_ADDRESS:    //寄件人地址
            case CHANGE_SEND_ADDRESS:    //修改寄件人地址
            case SELECT_SEND_MESSAGE_BY_BOOK:   //地址簿选择寄件人地址
                handleSendMessage(data);
                break;
            case ADD_RECEIVE_ADDRESS:  //收件人地址
            case CHANGE_RECEIVE_ADDRESS:  //修改收件人地址
            case SELECT_RECEIVE_MESSAGE_BY_BOOK:    //地址簿选择收件人地址
                handleReceiveMessage(data);
                break;
        }
    }

    private void handleRequestData(int what, JSONObject data) throws JSONException {
        switch (what) {
            case REQUEST_GOODS_REQUEST:    //设置商品数据
                setGoodsData(data);
                break;
            case REQUEST_EXPRESS_REQUEST:   //获取快递公司
                setExpressData(data);
                break;
            case REQUEST_IS_IDENTIFY_REQUEST: //是否认证
                setIdentifyData(data);
                break;
            case REQUEST_COMMIT_ORDER:     //下单成功立刻打印
                actionToGetOrderMessage(data);
                //showPromptDialog();
                break;
            case REQUEST_ESTIMATE:   //预估价格
                handleEstimate(data);
                break;
            case REQUEST_GET_DATA:   //获取运单号
                getWayNumber(data);
                break;
        }
    }

    private void getWayNumber(JSONObject data) throws JSONException {
        JSONObject dataObj = data.getJSONObject("data");
        String mail_id = dataObj.getString("mail_id");
        PrintPannelActivity.actionTo(this, user_id, mail_id, selectGoodsName, mWeight, et_money.getText().toString());
        finish();
    }

    private void actionToGetOrderMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String mail_id = dataObj.getString("mail_id");
        Request<String> request = NoHttpRequest.waitMimeographRequest(user_id, mail_id, selectGoodsName
                , mWeight, et_money.getText().toString(), "");
        mRequestQueue.add(REQUEST_GET_DATA, request, mOnPlaceOrderResponseListener);
    }

    private void setIdentifyData(JSONObject data) {
        //下单
        Request<String> request = NoHttpRequest.commitOrderRequest(user_id, express_id,
                send_id, receive_id, type_id, mWeight, "");
        mRequestQueue.add(REQUEST_COMMIT_ORDER, request, mOnPlaceOrderResponseListener);
    }

    private void handleEstimate(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String price = dataObj.getString("price");
        //String costTime = dataObj.getString("estimated_time");
        price = StringUtil.handleNullResultForString(price);
        if ("0".equals(price)) {
            tv_forecast_price.setText("预估计费用：暂无预估");
        } else {
            tv_forecast_price.setText("预估计费用：" + price + "元");
        }
    }

    private void setExpressData(JSONObject jsonObject) throws JSONException {
        mExpressData.clear();
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            String express_id = jsonObject1.getString("express_id");
            String express_name = jsonObject1.getString("express_name");
            String states = jsonObject1.getString("states");
            String express_logo = jsonObject1.getString("express_logo");
            HashMap<String, String> map = new HashMap<>();
            map.put("express_logo", express_logo);
            map.put("express_id", express_id);
            map.put("name", express_name);
            map.put("states", states);
            mExpressData.add(map);
        }
        expressAdapter.notifyDataSetChanged();
    }

    private void setGoodsData(JSONObject jsonObject) throws JSONException {
        JSONObject goodaArray = jsonObject.getJSONObject("data");
        //商品类型
        JSONArray goodsArray = goodaArray.getJSONArray("goods");
        mGoodsData.clear();
        for (int i = 0; i < goodsArray.length(); i++) {
            JSONObject jsonObject1 = goodsArray.getJSONObject(i);
            String goods_id = jsonObject1.getString("goods_id");
            String name = jsonObject1.getString("name");
            String states = jsonObject1.getString("states");
            HashMap<String, String> map = new HashMap<>();
            map.put("goods_id", goods_id);
            map.put("name", name);
            mGoodsData.add(map);
        }
        goodsAdapter.notifyDataSetChanged();
    }

    //接收收件人信息 ：类型包含 地址簿选择、直接添加、修改
    private void handleReceiveMessage(Intent data) {
        if (data == null) {
            return;
        }
        end_province = data.getStringExtra("book_province");
        end_city = data.getStringExtra("book_city");
        end_country = data.getStringExtra("book_area");
        receive_id = data.getStringExtra("book_id");
        end_detail_address = data.getStringExtra("book_address");

        ll_go_add_receive_message.setVisibility(View.GONE);
        ll_receive_layout.setVisibility(View.VISIBLE);

        tv_receive_name.setText(data.getStringExtra("book_name"));
        tv_receive_phone.setText(data.getStringExtra("book_telephone"));
        tv_receive_address.setText(data.getStringExtra("book_region") + data.getStringExtra("book_address"));

        isReceiveDetailShow = true;
        requestPredictMoney();   //获取预估价格
    }


    //接收寄件人信息 ：类型包含 地址簿选择、直接添加、修改
    private void handleSendMessage(Intent data) {
        if (data == null) {
            return;
        }
        start_province = data.getStringExtra("book_province");
        start_city = data.getStringExtra("book_city");
        start_country = data.getStringExtra("book_area");
        start_detial_address = data.getStringExtra("book_address");
        send_id = data.getStringExtra("book_id");

        ll_go_add_send_message.setVisibility(View.GONE);
        ll_send_layout.setVisibility(View.VISIBLE);

        tv_send_name.setText(data.getStringExtra("book_name"));
        tv_send_phone.setText(data.getStringExtra("book_telephone"));
        tv_send_address.setText(data.getStringExtra("book_region") + data.getStringExtra("book_address"));
        requestPredictMoney();   //获取预估价格

        isSendDetailShow = true;
    }

    private boolean isSendDetailShow = false;
    private boolean isReceiveDetailShow = false;

    private void actionToChangeMessage() {

        String send_name = tv_send_name.getText().toString();
        String send_phone = tv_send_phone.getText().toString();

        String receive_name = tv_receive_name.getText().toString();
        String receive_phone = tv_receive_phone.getText().toString();

        String[] provinces = StringUtil.changeStr1ToStr2(start_province, end_province);
        String[] citys = StringUtil.changeStr1ToStr2(start_city, end_city);
        String[] countrys = StringUtil.changeStr1ToStr2(start_country, end_country);
        String[] detial_addresss = StringUtil.changeStr1ToStr2(start_detial_address, end_detail_address);
        String[] addressId = StringUtil.changeStr1ToStr2(send_id, receive_id);

        String[] names = StringUtil.changeStr1ToStr2(send_name, receive_name);
        String[] phones = StringUtil.changeStr1ToStr2(send_phone, receive_phone);


        start_province = provinces[0];
        end_province = provinces[1];

        start_city = citys[0];
        end_city = citys[0];

        start_country = countrys[0];
        end_country = countrys[1];

        start_detial_address = detial_addresss[0];
        end_detail_address = detial_addresss[1];

        send_id = addressId[0];
        receive_id = addressId[1];

        send_name = names[0];
        receive_name = names[1];

        send_phone = phones[0];
        receive_phone = phones[1];

        tv_send_name.setText(send_name);
        tv_send_phone.setText(send_phone);
        tv_send_address.setText(start_province + start_city + start_country + start_detial_address);

        tv_receive_name.setText(receive_name);
        tv_receive_phone.setText(receive_phone);
        tv_receive_address.setText(end_province + end_city + end_country + end_detail_address);


        //表示寄件有信息、收件没有信息
        if (isSendDetailShow && !isReceiveDetailShow) {
            isSendDetailShow = false;
            isReceiveDetailShow = true;

            ll_send_layout.setVisibility(View.GONE);
            ll_go_add_send_message.setVisibility(View.VISIBLE);

            ll_receive_layout.setVisibility(View.VISIBLE);
            ll_go_add_receive_message.setVisibility(View.GONE);

        } else if (!isSendDetailShow && isReceiveDetailShow) {

            isSendDetailShow = true;
            isReceiveDetailShow = false;

            ll_send_layout.setVisibility(View.VISIBLE);
            ll_go_add_send_message.setVisibility(View.GONE);

            ll_receive_layout.setVisibility(View.GONE);
            ll_go_add_receive_message.setVisibility(View.VISIBLE);

        }
        requestPredictMoney();  //请求预估价格
    }

    private void actionToChangeSendMessage() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        intent.putExtra("address_id", send_id);
        intent.putExtra("book_name", tv_send_name.getText().toString());
        intent.putExtra("book_telephone", tv_send_phone.getText().toString());
        intent.putExtra("book_region", start_province + start_city + start_country);
        intent.putExtra("book_address", start_detial_address);
        intent.putExtra("book_province", start_province);
        intent.putExtra("book_city", start_city);
        intent.putExtra("book_area", start_country);
        intent.putExtra("type", 1);     //寄件
        startActivityForResult(intent, CHANGE_SEND_ADDRESS);
    }

    private void actionToChangeReceiveMessage() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        intent.putExtra("address_id", receive_id);
        intent.putExtra("book_name", tv_receive_name.getText().toString());
        intent.putExtra("book_telephone", tv_receive_phone.getText().toString());
        intent.putExtra("book_region", end_province + end_city + end_country);
        intent.putExtra("book_address", end_detail_address);
        intent.putExtra("book_province", end_province);
        intent.putExtra("book_city", start_city);
        intent.putExtra("book_area", end_country);
        intent.putExtra("type", 2);    //收件
        startActivityForResult(intent, CHANGE_RECEIVE_ADDRESS);
    }

    private void actionToPlaceOrder() {
        //验证信息是否完善
        if (!isRightAboutOrderMessage()) {
            return;
        }
        Request<String> request = NoHttpRequest.isIdentifyRequest(user_id, send_id);
        mRequestQueue.add(REQUEST_IS_IDENTIFY_REQUEST, request, mOnPlaceOrderResponseListener);
    }

    private boolean isRightAboutOrderMessage() {
        if ("".equals(send_id)) {
            ToastUtil.showShort("请填写寄件地址");
            return false;
        } else if ("".equals(receive_id)) {
            ToastUtil.showShort("请填写收件地址");
            return false;
        } else if ("".equals(type_id)) {
            ToastUtil.showShort("请填写商品信息");
            return false;
        } else if ("".equals(express_id)) {
            ToastUtil.showShort("请选择快递公司");
            return false;
        } else if ("".equals(mWeight)) {
            ToastUtil.showShort("请填写物品和重量");
            return false;
        } else if ("".equals(et_money.getText().toString())) {
            ToastUtil.showShort("请填写收取的金额");
            return false;
        }
        return true;
    }

    private void actionToAddReceiveMessage() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        intent.putExtra("type", 2);
        startActivityForResult(intent, ADD_RECEIVE_ADDRESS);
    }

    private void actionToAddSendMessage() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        intent.putExtra("type", 1);
        startActivityForResult(intent, ADD_SEND_ADDRESS);
    }

    private void actionToSendBook() {
        Intent intent = new Intent(this, FastmailMessageActivity.class);
        intent.putExtra("type", SELECT_SEND_MESSAGE_BY_BOOK);
        startActivityForResult(intent, SELECT_SEND_MESSAGE_BY_BOOK);
    }

    private void actionToIndentify() {
        Intent intent = new Intent(this, IdentificationActivity.class);
        intent.putExtra("come_type", true);
        intent.putExtra("book_id", send_id);
        startActivity(intent);
    }

    private void actionToReceiveBook() {
        Intent intent = new Intent(this, FastmailMessageActivity.class);
        intent.putExtra("type", SELECT_RECEIVE_MESSAGE_BY_BOOK);
        startActivityForResult(intent, SELECT_RECEIVE_MESSAGE_BY_BOOK);
    }

    private void actionToSelectGoods() {
        requestGoodsData();    //请求商品数据
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(iv_back, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void requestExpressData() {
        Request<String> request = NoHttpRequest.getExpressageRequest(user_id, "1");
        mRequestQueue.add(REQUEST_EXPRESS_REQUEST, request, mOnResponseListener);
    }

    private void requestGoodsData() {
        Request<String> request = NoHttpRequest.getGoodsTypeRequest(user_id);
        mRequestQueue.add(REQUEST_GOODS_REQUEST, request, mOnResponseListener);
    }

    private void actionToFinish() {
        finish();
    }


    private OnResponseListener<String> mOnPlaceOrderResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ManualMailingActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ManualMailingActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleRequestData(what, jsonObject);    //处理各种请求结果
                } else {
                    //提示未认证
                    if (what == REQUEST_IS_IDENTIFY_REQUEST) {
                        //认证
                        showIndentifyDialog();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            ToastUtil.showShort("当前网络不佳");
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };

    private OnResponseListener<String> mOnResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ManualMailingActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ManualMailingActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleRequestData(what, jsonObject);    //处理各种请求结果
                } else {
                    //提示未认证
                    if (what == REQUEST_IS_IDENTIFY_REQUEST) {
                        //认证
                        showIndentifyDialog();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            ToastUtil.showShort("当前网络不佳");
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void showIndentifyDialog() {
        new CircleDialog.Builder()
                .setTitle("标题")
                .setWidth(0.8f)
                .setText("\n根据国家邮政局要求，寄件需实名认证\n")
                .setPositive("去认证", null)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        actionToIndentify();
                    }
                })
                .show(getSupportFragmentManager());

    }


    private void showPromptDialog() {

        new CircleDialog.Builder()
                .setTitle("标题")
                .setWidth(0.8f)
                .setText("\n下单成功!\n")
                .setPositive("知道了", null)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        ManualMailingActivity.this.finish();
                    }
                })
                .show(getSupportFragmentManager());
    }


    //获取预估价格
    private void requestPredictMoney() {
        if ("".equals(user_id) || "".equals(express_id) || "".equals(start_province)
                || "".equals(start_city) || "".equals(end_province) || "".equals(end_city) || "".equals(mWeight)) {
            return;
        }
        Request<String> request = NoHttpRequest.getPredictMoneyRequest(user_id, express_id, start_province, start_city, end_province, end_city, mWeight);
        mRequestQueue.add(REQUEST_ESTIMATE, request, mOnResponseListener);
    }


    /******************************************************* 物品类型 初始化 *****************************************************/

    private TextView[] weights;
    private String mWeight = "";   //重量
    private TextView weithOne;
    private TextView weithTwo;
    private TextView weithThree;
    private TextView weightOther;
    private TextView etWeightOther;
    private int mGoodsPosition = -1;
    private GoodsAdapter goodsAdapter;
    private TextView weihtTag;
    private String selectGoodsName = "";

    private List<HashMap<String, String>> mGoodsData = new ArrayList<>();    //商品类型数据
    private List<HashMap<String, String>> mExpressData = new ArrayList<>();    //快递公司类型

    private void initView() {
        ll_go_add_send_message = findViewById(R.id.ll_go_add_send_message);
        ll_go_add_receive_message = findViewById(R.id.ll_go_add_receive_message);
        ll_send_layout = findViewById(R.id.ll_send_layout);
        ll_receive_layout = findViewById(R.id.ll_receive_layout);

        tv_send_name = findViewById(R.id.tv_send_name);
        tv_send_phone = findViewById(R.id.tv_send_phone);
        tv_send_address = findViewById(R.id.tv_send_address);

        tv_receive_name = findViewById(R.id.tv_receive_name);
        tv_receive_phone = findViewById(R.id.tv_receive_phone);
        tv_receive_address = findViewById(R.id.tv_receive_address);

        ll_select_goods = findViewById(R.id.ll_select_goods);
        tv_good_weigth = findViewById(R.id.tv_good_weigth);
        et_money = findViewById(R.id.et_money);

        rl_express = findViewById(R.id.rl_express);

        tv_send_book = findViewById(R.id.tv_send_book);
        tv_receive_book = findViewById(R.id.tv_receive_book);

        iv_back = findViewById(R.id.iv_back);

        iv_change_message = findViewById(R.id.iv_change_message);
        tv_place_order = findViewById(R.id.tv_place_order);
        tv_forecast_price = findViewById(R.id.tv_forecast_price);


        ll_go_add_send_message.setOnClickListener(this);
        ll_go_add_receive_message.setOnClickListener(this);
        tv_send_book.setOnClickListener(this);
        tv_receive_book.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        ll_select_goods.setOnClickListener(this);
        ll_send_layout.setOnClickListener(this);
        ll_receive_layout.setOnClickListener(this);
        tv_place_order.setOnClickListener(this);
        iv_change_message.setOnClickListener(this);

        ll_go_add_receive_message.setVisibility(View.VISIBLE);
        ll_go_add_send_message.setVisibility(View.VISIBLE);
        ll_send_layout.setVisibility(View.GONE);
        ll_receive_layout.setVisibility(View.GONE);

        initExpressList();    //初始化快递公司列表
    }

    private void initExpressList() {
        expressAdapter = new ExpressAdapter(this, mExpressData);
        rl_express.setAdapter(expressAdapter);
        rl_express.addItemDecoration(new MarginDecoration(this));
        rl_express.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        expressAdapter.setOnItemClickListener(new ExpressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HashMap<String, String> map = mExpressData.get(position);
                express_id = map.get("express_id");
                expressAdapter.setPosition(position);
                expressAdapter.notifyDataSetChanged();
                requestPredictMoney();
            }
        });

    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        mExpressLogoDao = daoSession.getExpressLogoDao();

        //获取用户id
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.pop_goods_type_layout, null);
            myGridView = selectView.findViewById(R.id.gv_goods);
            TextView tvSure = selectView.findViewById(R.id.sure);
            TextView tvcannel = selectView.findViewById(R.id.cannel);
            initWeightListenre(selectView);   //重量选择

            tvcannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (templeWeightSelect == 3) {
                        mWeight = etWeightOther.getText().toString();
                        if ("".equals(mWeight)) {
                            ToastUtil.showShort("请选择重量！");
                            return;
                        }
                    }

                    if (mGoodsPosition == -1) {
                        ToastUtil.showShort("请选择物品类型！");
                        return;
                    }

                    tv_good_weigth.setText(selectGoodsName + " " + mWeight + " kg");
                    requestPredictMoney();
                    popupWindow.dismiss();
                }
            });

            initRecycler();
            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            View layout_pop_close = selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
    }

    private void initWeightListenre(View selectView) {
        weithOne = selectView.findViewById(R.id.tv_weight_one);
        weithTwo = selectView.findViewById(R.id.tv_weight_two);
        weithThree = selectView.findViewById(R.id.tv_weight_three);
        weightOther = selectView.findViewById(R.id.tv_other);
        etWeightOther = selectView.findViewById(R.id.id_other_weight);
        weights = new TextView[]{weithOne, weithTwo, weithThree, weightOther};
        weihtTag = selectView.findViewById(R.id.tv_weiht_tag);

        weithOne.setOnClickListener(mWeightClickListenr);
        weithTwo.setOnClickListener(mWeightClickListenr);
        weithThree.setOnClickListener(mWeightClickListenr);
        weightOther.setOnClickListener(mWeightClickListenr);
    }

    private View.OnClickListener mWeightClickListenr = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_weight_one:
                    mWeight = "1";
                    setWightState(0);
                    break;
                case R.id.tv_weight_two:
                    mWeight = "2";
                    setWightState(1);
                    break;
                case R.id.tv_weight_three:
                    mWeight = "3";
                    setWightState(2);
                    break;
                case R.id.tv_other:
                    setWightState(3);
                    mWeight = "";
                    break;
            }
        }
    };

    //设置重量选择按钮的状态
    public void setWightState(int position) {
        templeWeightSelect = position;
        restoreWeithState();    //重置选择状态
        TextView weightTextView = weights[position];
        weightTextView.setBackgroundResource(R.drawable.bg_green_circle);
        weightTextView.setTextColor(Color.parseColor("#ffffff"));
        if (position == 3) {
            etWeightOther.setVisibility(View.VISIBLE);
            weihtTag.setVisibility(View.VISIBLE);
        }
    }

    private void restoreWeithState() {
        for (TextView weitht : weights) {
            weitht.setBackgroundResource(R.drawable.tv_bg_grey_circle);
            weitht.setTextColor(Color.parseColor("#353535"));
        }
        etWeightOther.setVisibility(View.INVISIBLE);
        weihtTag.setVisibility(View.INVISIBLE);
    }

    private void initRecycler() {
        goodsAdapter = new GoodsAdapter(this, mGoodsData);
        myGridView.setAdapter(goodsAdapter);
        myGridView.addItemDecoration(new MarginDecoration(this));
        myGridView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mGoodsPosition = position;
                HashMap<String, String> map = mGoodsData.get(position);
                String goodsName = map.get("name");
                selectGoodsName = goodsName;
                type_id = map.get("goods_id");
                goodsAdapter.setPosition(position);
                // popupWindow.dismiss();
                goodsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
