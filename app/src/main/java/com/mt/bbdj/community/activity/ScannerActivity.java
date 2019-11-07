package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.king.zxing.Intents;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ScannerMessageModel;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ScannerMessageModelDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.EnterData;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.ScannerMessageAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.umeng.commonsdk.statistics.common.MLog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zto.recognition.phonenumber.OCRListener;
import com.zto.recognition.phonenumber.tess.OCRResult;
import com.zto.scanner.BarcodeListener;
import com.zto.scanner.ZTOScannerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ScannerActivity extends BaseActivity implements OCRListener, BarcodeListener {

    ZTOScannerFragment scannerFragment;

    private RecyclerView recyclerView;

    private RelativeLayout iv_back;

    private AppCompatTextView tv_express_change;

    private ScannerMessageModelDao scannerMessageModel;
    private TextView tv_pop_code;
    private EditText tv_pop_way_number;
    private EditText tv_pop_phone;
    private TextView tv_enter_title;
    private TextView tv_enter_number;


    private ScannerMessageAdapter mAdapter;

    private List<ScannerMessageModel> mList = new ArrayList<>();
    private String express_name = "";
    private String express_tag = "";

    private int currentPosition = 0;

    private PopupWindow popupWindow;

    private final int REQUEST_ENTER_REAPORTY = 1001;   //入库
    private final int REQUEST_START = 1002;
    private final int REQUEST_PHONE = 1003;
    private final int REQUEST_CODE_REQUEST = 1004;   //取件码
    private final int CHANGE_EXPRESS = 1005;   //修改快递公司

    private RequestQueue mRequestQueue;
    private String user_id;
    private int package_number = 0;
    private int current_local_package_number = 0;

    //用于识别手机号去重处理
    private int comparePhoneCount = 0;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REQUEST_START) {   //重新开始识别
                scannerFragment.onResume();
            } else if (msg.what == REQUEST_PHONE) {   //获取识别的手机号
                getOCRphone(msg);
            }
        }
    };


    private void getOCRphone(Message msg) {
        String phone = (String) msg.obj;
        ScannerMessageModel model = mList.get(0);
        if (phone.equals(model.getPhone())) {
            return;
        }
        model.setPhone((String) msg.obj);
        model.setIsHavaPhone(1);
        model.setCode(IntegerUtil.getEffectiveCode(package_number));
        model.setIsHaveWayNumber(model.getIsHaveWayNumber());
        model.setExpressName(express_name);
        model.setMAXTAG(package_number);
        model.setExpressLogo(model.getExpressLogo());
        model.setWaybill(model.getWaybill());
        isCompliteMessage(model);   //判断当前信息若是齐全、就暂停扫描
        mAdapter.addData(0, model);
        recyclerView.scrollToPosition(0);
        scannerFragment.setOCRHint("请请扫描条形码");
    }


    public static void actionTo(Context context, String express_tag, String express_name) {
        Intent intent = new Intent(context, ScannerActivity.class);
        intent.putExtra("express_tag", express_tag);
        intent.putExtra("express_name", express_name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ScannerActivity.this);
        initParams();
        initView();
        requestCode();    //获取取件码
        initScanner();
        initClickListener();
        initPopuStyle();
    }

    @Override
    public void onOCRSuccess(OCRResult ocrResult) {
        String ocr = ocrResult.getText();
        //scannerFragment.onPause();
        if (!ocrResult.isNetResult()) {
            scannerFragment.reScan();
        }
        Message message = Message.obtain();
        message.obj = ocr;
        message.what = REQUEST_PHONE;
        handler.sendMessage(message);
    }


    @Override
    public void onGetBarcode(List<String> rawResult, Bitmap imageData) {
        System.out.println("barcode:" + rawResult.get(0));
        String barCode = rawResult.get(0);
        if (isRepeat(barCode)) {
            SoundHelper.getInstance().playNotifiRepeatSound();
        } else {
            ScannerMessageModel model = mList.get(0);
            model.setWaybill(barCode);
            model.setIsHaveWayNumber(1);
            model.setPhone(model.getPhone());
            model.setIsHavaPhone(model.getIsHavaPhone());
            model.setCode(IntegerUtil.getEffectiveCode(package_number));
            model.setMAXTAG(package_number);
            model.setExpressName(express_name);
            model.setExpressLogo(model.getExpressLogo());
            isCompliteMessage(model);  //判断当前信息完全、延迟扫描
            mAdapter.addData(0, model);
            recyclerView.scrollToPosition(0);
        }
        scannerFragment.setOCRHint("请扫描手机号");
    }


    @Override
    public void onOCRFail(boolean isNet, String s) {
        System.out.println("ocr onOCRFail: " + s);
        if (!isNet) {
            scannerFragment.reScan();
        }
        scannerFragment.onStop();
    }

    private boolean isRepeat(String barCode) {
        if (null == barCode || "".equals(barCode)) {
            return false;
        }
        //完整数据的提示
        if (mList.size() > 1) {
            for (int i = 1; i < mList.size(); i++) {
                ScannerMessageModel entity = mList.get(i);
                if (barCode.equals(entity.getWaybill())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getData() {
        List<EnterData> data = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 1; i < mList.size(); i++) {
            ScannerMessageModel model = mList.get(i);
            EnterData enterData = new EnterData();
            enterData.setCode(model.getCode());
            enterData.setExpress_id(express_tag);
            enterData.setMobile(model.getPhone());
            enterData.setNumber(model.getWaybill());
            data.add(enterData);
        }
        return gson.toJson(data);
    }

    private void showEditPopuwindow(int position) {
        ScannerMessageModel model = mList.get(position);
        tv_pop_code.setText(model.getCode());
        tv_pop_phone.setText(model.getPhone());
        tv_pop_way_number.setText(model.getWaybill());
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(iv_back, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void requestCode() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        Request<String> request = NoHttpRequest.getExpressCode(params);
        mRequestQueue.add(REQUEST_CODE_REQUEST, request, onResponseListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CHANGE_EXPRESS) {
            express_name = data.getStringExtra("express_name");
            express_tag = data.getStringExtra("express_tag");
        }
    }

    private void enterResotry() {
        if (mList.size() == 1) {
            ToastUtil.showShort("无可提交数据");
            return;
        }
        ScannerMessageModel model = mList.get(0);

        if (!(0 == model.getIsHavaPhone() && 0 == model.getIsHaveWayNumber())) {
            ToastUtil.showShort("有未完善信息");
            return;
        }
        String str_data = getData();   //获取数据
        HashMap<String, String> params = new HashMap<>();
        params.put("str_data", str_data);
        params.put("user_id", user_id);
        params.put("type", "1");

        Request<String> request = NoHttpRequest.managerRestory(params);
        mRequestQueue.add(REQUEST_ENTER_REAPORTY, request, onResponseListener);
    }

    private void handleEnterEvent(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            requestCode();    //提交完成之后重新请求一下取货码
            mList.clear();
            mAdapter.notifyDataSetChanged();
            mList.add(new ScannerMessageModel());
            mAdapter.notifyDataSetChanged();
            scannerMessageModel.deleteAll();
        }
        tv_enter_number.setText("(" + (mList.size() - 1) + ")");
        ToastUtil.showShort(msg);
    }

    private void handleCodeEvent(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            JSONObject data = jsonObject.getJSONObject("data");
            String packageCode = data.getString("code");
            int serviceCode = IntegerUtil.getStringChangeToNumber(packageCode);  //最新的取货码
            package_number = serviceCode >= current_local_package_number ? serviceCode : current_local_package_number;  //作比较保证最新的取货码
            package_number += 1;
        } else {
            scannerFragment.onPause();
            ToastUtil.showShort(msg);
        }
    }


    private void initParams() {
        Intent intent = getIntent();
        express_name = intent.getStringExtra("express_name");
        express_tag = intent.getStringExtra("express_tag");
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        scannerMessageModel = daoSession.getScannerMessageModelDao();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void initRecyclerView() {
        //mList.add(new ScannerMessageModel());   //初始化第一条数据
        mAdapter = new ScannerMessageAdapter(this, mList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new MarginDecoration(this, 15));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        iv_back = findViewById(R.id.iv_back);
        tv_express_change = findViewById(R.id.tv_express_change);
        tv_enter_title = findViewById(R.id.tv_enter_title);
        tv_enter_number = findViewById(R.id.tv_enter_number);
        resetData();    //复现数据
        initRecyclerView();
    }

    private void initScanner() {
        scannerFragment = ZTOScannerFragment.newInstance(this, this);
        replaceFragment(R.id.container_scanner, scannerFragment);
        ScannerMessageModel model = mList.get(0);
        int isHavePhone = model.getIsHavaPhone();
        if (isHavePhone == 0) {
            scannerFragment.setOCRHint("请扫描手机号");
        } else {
            scannerFragment.setOCRHint("请扫描条形码");
        }
    }

    private void initClickListener() {
        mAdapter.setOnClickManager(new ScannerMessageAdapter.OnClickManager() {
            @Override
            public void onEditMessage(int position) {
                currentPosition = position;
                showEditPopuwindow(position);   //编辑信息框
            }

            @Override
            public void onCompliteMessage() {
                SoundHelper.getInstance().playNotifiSuccessSound();
                mList.add(0, new ScannerMessageModel());
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + (mList.size() - 1) + ")");
            }

            @Override
            public void onRemoveMessage(int position) {
                if (position == 0) {
                    mList.remove(0);
                    mList.add(0, new ScannerMessageModel());
                } else {
                    mList.remove(position);
                }
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + (mList.size() - 1) + ")");
            }


            @Override
            public void onUpdateMessage(int position) {
                if (position == 0) {
                    mList.add(0, new ScannerMessageModel());
                }
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + (mList.size() - 1) + ")");
            }
        });

        iv_back.setOnClickListener(view -> finish());

        tv_express_change.setOnClickListener(view -> handleChangeExpress());  //转换快递公司

        //入库
        tv_enter_title.setOnClickListener(view -> {
            enterResotry();    //入库提交
        });
    }

    private void replaceFragment(int container_scanner, ZTOScannerFragment scannerFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (scannerFragment == null) {
            scannerFragment = ZTOScannerFragment.newInstance(this, this);
        }
        transaction.replace(container_scanner, scannerFragment);
        transaction.commit();
    }

    private void resetData() {
        List<ScannerMessageModel> resetList = scannerMessageModel.queryBuilder().list();
        if (resetList != null && resetList.size() != 0) {
            mList = resetList;
            current_local_package_number = getLocalPackageCode(mList);   //获取本地的最新取货码序列
        } else {
            mList.add(new ScannerMessageModel());    //防止销毁时保存数据失败 故此初始化一条数据
        }
        tv_enter_number.setText("(" + (mList.size() - 1) + ")");
    }

    private void saveMessage() {
        scannerMessageModel.deleteAll();
        scannerMessageModel.saveInTx(mList);
    }

    //判断信息是否完全
    private void isCompliteMessage(ScannerMessageModel model) {
        if (1 == model.getIsHaveWayNumber() && 1 == model.getIsHavaPhone()) {
            scannerFragment.onPause();
            handler.sendEmptyMessageDelayed(REQUEST_START, 1200);
            package_number++;
        }
    }

    private int getLocalPackageCode(List<ScannerMessageModel> list) {
        /**
         * 存储到数据库中 数据有两种
         * 1、只有一条空数据：此时返回0
         * 2、第二条为残数据或者全数据 返回MAXTAG
         */
        ScannerMessageModel model = list.get(0);
        if (1 == model.getIsHaveWayNumber() || 1 == model.getIsHavaPhone()) {
            return model.getMAXTAG();
        } else {
            if (list.size() > 1) {
                return list.get(1).getMAXTAG();
            }
        }
        return 0;
    }


    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ScannerActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ScannerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                if (what == REQUEST_ENTER_REAPORTY) {   //入库信息
                    handleEnterEvent(jsonObject);
                } else if (what == REQUEST_CODE_REQUEST) {  //获取取件码
                    handleCodeEvent(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
                if (what == REQUEST_CODE_REQUEST) {
                    scannerFragment.onPause();
                }
                ToastUtil.showShort("网络异常请重试");
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            if (what == REQUEST_CODE_REQUEST) {
                scannerFragment.onPause();
            }
            ToastUtil.showShort("网络异常请重试！");
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };

    /************************************************ 对话框 **************************************************/
    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.edit_scanner_message, null);
            View layout_left_close = selectView.findViewById(R.id.layout_left_close);
            tv_pop_code = selectView.findViewById(R.id.tv_pop_code);
            tv_pop_way_number = selectView.findViewById(R.id.tv_pop_way_number);
            tv_pop_phone = selectView.findViewById(R.id.tv_pop_phone);
            TextView tv_pop_cannel = selectView.findViewById(R.id.tv_pop_cannel);
            TextView tv_pop_save = selectView.findViewById(R.id.tv_pop_save);


            layout_left_close.setOnClickListener(viewClicklistener);
            tv_pop_cannel.setOnClickListener(viewClicklistener);
            tv_pop_save.setOnClickListener(viewClicklistener);

            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
        }
    }

    private View.OnClickListener viewClicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_pop_save:   //保存
                    saveChangeMessage();
                    break;
                case R.id.layout_left_close:
                case R.id.tv_pop_cannel:    //取消
                    popupWindow.dismiss();
                    break;
            }
        }
    };

    private void handleChangeExpress() {
        ScannerMessageModel model = mList.get(0);
        if (0 == model.getIsHavaPhone() && 0 == model.getIsHaveWayNumber()) {// 空数据
            if (mList.size() == 1) {
                SelectExpressActivity.actionTo(ScannerActivity.this, CHANGE_EXPRESS);
            } else {
                showChangeExpressDialog();   //提示先入库
            }
        } else {
            ToastUtil.showShort("有未完善数据");
        }
    }

    private void showChangeExpressDialog() {
        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(false)
                .setText("\n当前有未提交数据，请先提交后更换快递公司\n")
                .setPositive("提交", null)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        enterResotry(); //提交
                    }
                })
                .show(getSupportFragmentManager());
    }


    private void saveChangeMessage() {
        String code = tv_pop_code.getText().toString();   //取货码
        String way_number = tv_pop_way_number.getText().toString();    //运单号
        String phone = tv_pop_phone.getText().toString();
        if ("".equals(code)) {
            ToastUtil.showShort("取货码不可为空");
        } else if ("".equals(way_number)) {
            ToastUtil.showShort("运单号不可为空");
        } else if ("".equals(phone)) {
            ToastUtil.showShort("手机号不可为空");
        } else {
            ScannerMessageModel model = mList.get(currentPosition);
            model.setWaybill(way_number);
            model.setPhone(phone);
            model.setIsHavaPhone(1);
            model.setIsHaveWayNumber(1);
            mAdapter.changeData(currentPosition, model);
        }
        popupWindow.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveMessage();   //保存信息

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRequestQueue == null) {
            mRequestQueue.cancelAll();
            mRequestQueue.stop();
        }
    }

}
