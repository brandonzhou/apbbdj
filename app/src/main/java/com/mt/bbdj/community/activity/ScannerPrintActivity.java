package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ScannerMessageModel;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.GoodsAdapter;
import com.mt.bbdj.community.adapter.ScannerMessageAdapter;
import com.mt.bbdj.community.adapter.SimpleTypeAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zto.recognition.phonenumber.OCRListener;
import com.zto.recognition.phonenumber.tess.OCRResult;
import com.zto.scanner.BarcodeListener;
import com.zto.scanner.ZTOScannerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ScannerPrintActivity extends BaseActivity implements OCRListener, BarcodeListener {

    ZTOScannerFragment scannerFragment;

    private RecyclerView recyclerView;

    private RelativeLayout iv_back;

    private AppCompatTextView tv_express_change;

    private EditText tv_pop_code;
    private EditText tv_pop_way_number;
    private EditText tv_pop_phone;
    private TextView tv_enter_title;
    private TextView tv_enter_number;
    private ImageView id_edit_code;   //修改取货码


    private ScannerMessageAdapter mAdapter;

    private List<ScannerMessageModel> mList = new ArrayList<>();
    private String express_name = "";
    private String express_tag = "";
    private String mCode = "";    //取件码
    private String mCheck_id = "";    //


    private int currentPosition = 0;

    private ScannerMessageModel currentModel;

    private PopupWindow popupWindow, codePopWindow;

    private final int REQUEST_ENTER_REAPORTY = 1001;   //入库
    private final int REQUEST_COMMIT_REAPORTY = 1008;   //单个提交
    private final int REQUEST_DELETE_REAPORTY = 1009;   //单个删除
    private final int REQUEST_GET_HISTORY = 1010;   //历史数据
    private final int REQUEST_START = 1002;
    private final int REQUEST_PHONE = 1003;
    private final int CHANGE_EXPRESS = 1005;   //修改快递公司
    private final int CHANGE_CODE = 1006;   //修改取件码
    private final int REQUEST_CHECK_CODE = 1007;   //检测是否重复
    private final int REQUEST_CHANGE_TAKECODE = 1012;   //切换货架

    private RequestQueue mRequestQueue;
    private String user_id;
    private TextView tv_pop_code_title;

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
    private TextView tv_local;


    private void getOCRphone(Message msg) {
        String phone = (String) msg.obj;
        ScannerMessageModel model = mList.get(0);
        SoundHelper.getInstance().playNotifiSuccessSound();
        if (!isEffective(phone)) {
            return;
        }
        model.setPhone((String) msg.obj);
        model.setIsHavaPhone(1);
        model.setExpressName(express_name);
        model.setExpressLogo(model.getExpressLogo());
        model.setTimestamp(model.getTimestamp());
        model.setWaybill(model.getWaybill());
        model.setCode(model.getCode());
        isCompliteMessage(model);   //判断当前信息若是齐全、就暂停扫描
        mAdapter.addData(0, model);
        recyclerView.scrollToPosition(0);
        scannerFragment.setOCRHint("请请扫描条形码");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(ScannerPrintActivity.this);
        initParams();
        initView();
        initScanner();
        initClickListener();
        initPopuStyle();
        initChangeCodePop();   //修改取件码对话框
        increaseVoice();   //加大音量

        findViewById(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //printNumber();
                //PrintCodeActivity.actionTo(ScannerPrintActivity.this,"");
            }
        });
    }


    @Override
    public void onOCRSuccess(OCRResult ocrResult) {
        String ocr = ocrResult.getText();
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
        int state = isRepeat(barCode);
        if (1 == state) {
        } else if (2 == state) {
            SoundHelper.getInstance().playNotifiRepeatSound();
        } else {
            ScannerMessageModel model = mList.get(0);
            model.setWaybill(barCode);
            model.setIsHaveWayNumber(1);
            model.setIsSync(0);   //未同步
            model.setPhone(model.getPhone());
            model.setIsHavaPhone(model.getIsHavaPhone());
            model.setExpressName(express_name);
            model.setCode(model.getCode());
            model.setTimestamp(model.getTimestamp());
            model.setExpressLogo(model.getExpressLogo());
            isCompliteMessage(model);  //判断当前信息完全、延迟扫描
            checkEnterNumber(model);    //检测是否重复
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
        // scannerFragment.onStop();
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(ScannerPrintActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ScannerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                if (what == REQUEST_ENTER_REAPORTY) {   //入库信息
                    handleEnterEvent(jsonObject);
                } else if (what == REQUEST_CHECK_CODE) {   //检测是否重复
                    handlerCheckCode(jsonObject);
                } else if (what == REQUEST_COMMIT_REAPORTY) {  //单个提交数据
                    handleCommitData(jsonObject);
                } else if (what == REQUEST_DELETE_REAPORTY) {  //单个删除
                    handleDeleteData(jsonObject);
                } else if (what == REQUEST_GET_HISTORY) {  //历史数据
                    handleHistoryData(jsonObject);
                } else if (what == REQUEST_CHANGE_TAKECODE){  //切换货架
                    handleChangeTakeCode(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
                ToastUtil.showShort("网络异常，请切换后重试");
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            if (what == REQUEST_CHECK_CODE || what ==REQUEST_COMMIT_REAPORTY || what == REQUEST_GET_HISTORY) {
                showBackDialog("网络不稳定请切换重试");
            } else if (what == REQUEST_CHANGE_TAKECODE || what == REQUEST_DELETE_REAPORTY || what ==REQUEST_ENTER_REAPORTY){
                ToastUtil.showShort("网络不稳定请切换重试");
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };


    /********************************************************** 网络请求 **********************************************************/
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
        params.put("check_data", str_data);
        params.put("user_id", user_id);
        Request<String> request = NoHttpRequest.managerRestory(params);
        mRequestQueue.add(REQUEST_ENTER_REAPORTY, request, onResponseListener);
    }

    private void commitData(ScannerMessageModel data) {
        String check_id = data.getTimestamp();
        if (null == check_id || "".equals(check_id)) {
            check_id = "0";
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", data.getPhone());
        params.put("user_id", user_id);
        params.put("code", data.getCode());
        params.put("number", data.getWaybill());
        params.put("express_id", express_tag);
        params.put("check_id", check_id);
        params.put("uuid", data.getUuid());
        Request<String> request = NoHttpRequest.commitSubgleRestory(params);
        mRequestQueue.add(REQUEST_COMMIT_REAPORTY, request, onResponseListener);
    }

    private void changeTakeCode() {
        String code = tv_local.getText().toString();
        if (code.length() != 2){
            ToastUtil.showShort("请完善取件码");
            return;
        }
        codePopWindow.dismiss();
        HashMap<String, String> params = new HashMap<>();
        params.put("shelves_code", code);
        params.put("user_id", user_id);
        params.put("uuid", currentModel.getUuid());
        params.put("check_id", currentModel.getTimestamp());
        Request<String> request = NoHttpRequest.changeTakeCode(params);
        mRequestQueue.add(REQUEST_CHANGE_TAKECODE, request, onResponseListener);
    }

    private void checkEnterNumber(ScannerMessageModel data) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("number", data.getWaybill());
        params.put("check_id", data.getTimestamp());
        Request<String> request = NoHttpRequest.checkEnterNumber(params);
        mRequestQueue.add(REQUEST_CHECK_CODE, request, onResponseListener);
    }

    private void deleteDataFromService(ScannerMessageModel data) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("check_id", data.getTimestamp());
        params.put("uuid", data.getUuid());
        Request<String> request = NoHttpRequest.deleteRestory(params);
        mRequestQueue.add(REQUEST_DELETE_REAPORTY, request, onResponseListener);
    }

    /************************************************************ 处理请求结果 ****************************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CHANGE_EXPRESS) {   //快递公司
            express_name = data.getStringExtra("express_name");
            express_tag = data.getStringExtra("express_tag");
        }
    }

    private void handleEnterEvent(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        JSONArray data = jsonObject.getJSONArray("data");
        if ("5001".equals(code)) {
            changeLocalDataState();   //更改本地数据的状态
            PrintTagModel printTagModel = new PrintTagModel();
            List<HashMap<String,String>> list = new ArrayList<>();

            for (int i = 0;i < data.length();i++){
                HashMap<String,String> map = new HashMap<>();
                JSONObject obj = data.getJSONObject(i);
                map.put("pie_number",obj.getString("number"));   //运单号
                map.put("qrcode",obj.getString("url"));   //二维码
                String[] tagCode = StringUtil.spliteStrFormat(obj.getString("code"));
                map.put("code_head",tagCode[0]);
                map.put("code",tagCode[1]);
                list.add(map);
                map = null;
            }
            printTagModel.setData(list);
            PrintTakeNumberActivity.actionTo(ScannerPrintActivity.this,printTagModel);
        }
        ToastUtil.showShort(msg);
    }

    private void changeLocalDataState() {
        ScannerMessageModel mode = mList.get(0);
        mList.clear();
        mAdapter.notifyDataSetChanged();
        mList.add(mode);
        mAdapter.notifyDataSetChanged();
        tv_enter_number.setText("(" + (mList.size() - 1) + ")");
    }

    private void resetData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        Request<String> request = NoHttpRequest.getHistoryData(params);
        mRequestQueue.add(REQUEST_GET_HISTORY, request, onResponseListener);

    }


    private void handleChangeTakeCode(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        JSONObject obj = jsonObject.getJSONObject("data");
        if ("5001".equals(code)) {
            String uuid = obj.getString("uuid");
            JSONObject newObj = obj.getJSONObject("new_code");
            String newCode  = newObj.getString("code");
            String new_check_id  = newObj.getString("check_id");
            Iterator<ScannerMessageModel> iterator = mList.iterator();
            while (iterator.hasNext()) {
                ScannerMessageModel model = iterator.next();
                if (uuid.equals(model.getUuid())) {
                    model.setTimestamp(new_check_id);
                    model.setCode(newCode);
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showShort("删除失败，请稍后再试");
        }
    }

    private void handleHistoryData(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            JSONArray data = jsonObject.getJSONArray("data");
            mList.clear();
            mAdapter.notifyDataSetChanged();
            for (int i = 0; i < data.length(); i++) {
                ScannerMessageModel model = new ScannerMessageModel();
                JSONObject obj = data.getJSONObject(i);
                String number = StringUtil.handleNullResultForString(obj.getString("number"));
                String codeNumber = StringUtil.handleNullResultForString(obj.getString("code"));
                String mobile = StringUtil.handleNullResultForString(obj.getString("mobile"));
                String express_name = StringUtil.handleNullResultForString(obj.getString("express_name"));
                String express_id = StringUtil.handleNullResultForString(obj.getString("express_id"));
                String check_id = obj.getString("check_id");
                model.setPhone(mobile);
                model.setWaybill(number);
                model.setCode(codeNumber);
                model.setTimestamp(check_id);
                model.setExpressName(express_name);
                model.setIsHaveWayNumber("".equals(number) ? 0 : 1);
                model.setIsHavaPhone("".equals(mobile) ? 0 : 1);
                mList.add(model);
                model = null;
            }
            if (mList.size() == 0) {
                ScannerMessageModel model = new ScannerMessageModel();
                model.setTimestamp(mCheck_id);
                model.setCode(mCode);
                mList.add(0,model);
                tv_enter_number.setText("(0)");
            } else {
                ScannerMessageModel model = mList.get(0);
                if (1 == model.getIsHaveWayNumber() && 1 == model.getIsHavaPhone() && !"".equals(model.getCode())) {  //表示信息全
                    ScannerMessageModel model1 = new ScannerMessageModel();
                    model1.setTimestamp(mCheck_id);
                    model1.setCode(mCode);
                    mList.add(0,model1);
                }
                tv_enter_number.setText("(" + (mList.size() - 1) + ")");
            }

            ScannerMessageModel model = mList.get(0);
            int isHavePhone = model.getIsHavaPhone();
            if (isHavePhone == 0) {
                scannerFragment.setOCRHint("请扫描手机号");
            } else {
                scannerFragment.setOCRHint("请扫描条形码");
            }
            mAdapter.notifyDataSetChanged();
        } else {
            showBackDialog("网络不稳定请切换网络重试");
        }
    }

    private void handleDeleteData(JSONObject jsonObject) throws JSONException {
        LogUtil.i("photoFile", "ScannerActivity::删除" + jsonObject.toString());
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        JSONObject obj = jsonObject.getJSONObject("data");
        if ("5001".equals(code)) {
            String uuid = obj.getString("uuid");
            Iterator<ScannerMessageModel> iterator = mList.iterator();
            while (iterator.hasNext()) {
                ScannerMessageModel model = iterator.next();
                if (uuid.equals(model.getUuid())) {
                    iterator.remove();
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showShort("删除失败，请稍后再试");
        }
    }

    private void handleCommitData(JSONObject jsonObject) throws JSONException {
        LogUtil.i("photoFile", "ScannerActivity::提交" + jsonObject.toString());
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        JSONObject data = jsonObject.getJSONObject("data");
        if ("5001".equals(code)) { //成功
            setHandHandleData(data);
        } else if ("5002".equals(code)) {  //修改失败
            resetChangeErrorData(data);
            ToastUtil.showShort(msg);
        } else if ("5003".equals(code)) {   //取件码重复
            resetRepeatErrorData(data);
            showRepeatErrorDialog();
        } else if ("5004".equals(code)) { //取件码长度不一 手动输入类型
            setHandHandleData(data);
            ToastUtil.showShort(msg);
        } else {
            showCommitErrorDialog();
        }
    }


    private void setHandHandleData(JSONObject data) throws JSONException {
        String uuid = data.getString("uuid");
        String check_id = data.getString("check_id");
        String number = data.getString("number");
        String codeNumber = data.getString("code");
        String mobile = data.getString("mobile");
        JSONObject newObj = data.getJSONObject("new_code");
        String new_check_id = newObj.getString("check_id");
        String new_code = newObj.getString("code");

        for (int i = 0; i < mList.size(); i++) {
            ScannerMessageModel model = mList.get(i);
            if (uuid.equals(model.getUuid())) {
                model.setTimestamp(check_id);
                model.setWaybill(number);
                model.setPhone(mobile);
                model.setCode(codeNumber);
            }
        }

        if (new_code != null && !"".equals(new_code)){   //表示的是修改
            mCheck_id = new_check_id;
            mCode = new_code;
            ScannerMessageModel model = new ScannerMessageModel();
            model.setTimestamp(new_check_id);
            model.setCode(new_code);
            mList.add(0,model);
            tv_enter_number.setText("(" + (mList.size() - 1) + ")");
        }
        mAdapter.notifyDataSetChanged();
    }

    private void resetRepeatErrorData(JSONObject data) throws JSONException {
        String uuid = data.getString("uuid");
        String number = data.getString("number");
        String codeNumber = data.getString("code");
        String mobile = data.getString("mobile");
        for (int i = 0; i < mList.size(); i++) {
            ScannerMessageModel model = mList.get(i);
            if (uuid.equals(model.getUuid())) {
                model.setWaybill(number);
                model.setPhone(mobile);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void resetChangeErrorData(JSONObject data) throws JSONException {
        String check_id = data.getString("check_id");
        String express_id = data.getString("express_id");
        String uuid = data.getString("uuid");
        String number = data.getString("number");
        String codeNumber = data.getString("code");
        String mobile = data.getString("mobile");
        for (int i = 0; i < mList.size(); i++) {
            ScannerMessageModel model = mList.get(i);
            if (uuid.equals(model.getUuid())) {
                model.setCode(codeNumber);
                model.setWaybill(number);
                model.setPhone(mobile);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void handlerCheckCode(JSONObject jsonObject) throws JSONException {
        LogUtil.i("photoFile", "ScannerActivity::运单号" + jsonObject.toString());
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            JSONObject data = jsonObject.getJSONObject("data");
            String codeNumber = data.getString("code");
            String check_id = data.getString("check_id");
            String code_take = data.getString("code");
            ScannerMessageModel model = mList.get(0);
            model.setWaybill(model.getWaybill());
            model.setIsHaveWayNumber(1);
            model.setIsSync(0);   //未同步
            model.setCode(codeNumber);
            model.setPhone(model.getPhone());
            model.setIsHavaPhone(model.getIsHavaPhone());
            model.setTimestamp(check_id);   //记录的id
            model.setExpressName(express_name);
            model.setCode(code_take);
            model.setExpressLogo(model.getExpressLogo());
            isCompliteMessage(model);  //判断当前信息完全、延迟扫描
            mAdapter.addData(0, model);
        } else if ("5002".equals(code)) {
            SoundHelper.getInstance().playNotifiRepeatSound();
            JSONObject data = jsonObject.getJSONObject("data");
            String number = data.getString("number");
            deleteLocalData(number);   //重复的就删除该条
        }
    }

    private void deleteLocalData(String number) {
        Iterator<ScannerMessageModel> iterator = mList.iterator();
        while (iterator.hasNext()) {
            ScannerMessageModel model = iterator.next();
            if (number.equals(model.getWaybill())) {
                model.setWaybill("");
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /************************************************ 初始化 **************************************************/
    private void initParams() {
        Intent intent = getIntent();
        express_name = intent.getStringExtra("express_name");
        express_tag = intent.getStringExtra("express_tag");
        mCode = intent.getStringExtra("code");
        mCheck_id = intent.getStringExtra("check_id");
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void initRecyclerView() {
        ScannerMessageModel model = new ScannerMessageModel();
        model.setCode(mCode);
        model.setTimestamp(mCheck_id);
        mList.add(model);   //初始化第一条数据
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
        id_edit_code = findViewById(R.id.id_edit_code);
        initRecyclerView();
        resetData();    //复现数据
    }

    private void increaseVoice() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        max = max / 2;
        //设置媒体音量为最大值，当然也可以设置媒体音量为其他给定的值
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
    }

    private void initScanner() {
        scannerFragment = ZTOScannerFragment.newInstance(this, this);
        replaceFragment(R.id.container_scanner, scannerFragment);
    }

    private void initClickListener() {
        mAdapter.setOnClickManager(new ScannerMessageAdapter.OnClickManager() {
            @Override
            public void onEditMessage(int position, ScannerMessageModel data) {
                currentPosition = position;
                currentModel = data;
                showEditPopuwindow(position);   //编辑信息框
            }

            @Override
            public void onCompliteMessage(int position, ScannerMessageModel data) {
                currentModel = data;
                SoundHelper.getInstance().playNotifiSuccessSound();
                commitData(data);    //入库单个数据
            }

            @Override
            public void onRemoveMessage(int position, ScannerMessageModel data) {
                currentModel = data;
                if (position == 0) {
                    ToastUtil.showShort("不可删除");
                } else {
                    if (data.getCode() != null && !"".equals(data.getCode()) && !"null".equals(data.getCode())) {
                        deleteDataFromService(data);   //删除服务器上的数据
                    }
                }
            }

            @Override
            public void onUpdateMessage(int position, ScannerMessageModel data) {
                if (position == 0) {
                    mList.add(0, new ScannerMessageModel());
                }
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + (mList.size() - 1) + ")");
            }

            @Override
            public void onChangeCode(int position, ScannerMessageModel model) {
                currentModel = model;
                showChangeDialogPop();   //修改取件码
            }
        });

        iv_back.setOnClickListener(view -> finish());

        tv_express_change.setOnClickListener(view -> handleChangeExpress());  //转换快递公司

        //入库
        tv_enter_title.setOnClickListener(view -> {
            enterResotry();    //入库提交
        });

        //编辑取货码
        id_edit_code.setOnClickListener(view -> {
            EditCodeActivity.actionTo(ScannerPrintActivity.this, CHANGE_CODE);
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

    /*********************************************** 打印标签 *************************************************/
    private List<HashMap<String, String>> mPrintList = new ArrayList<>();
    private PrintTagModel printTagModel = new PrintTagModel();

    private void printNumber() {
        HashMap<String, String> map = new HashMap<>();
        map.put("code", "0548");
        map.put("code_head", "A3");
        map.put("pie_number", "75486459521325");
        map.put("qrcode", "http://meng.81dja.com/Express/Express/deletCheckData?id=12&name=78&ud=45622");
        mPrintList.add(map);

        printTagModel.setData(mPrintList);
        PrintTakeNumberActivity.actionTo(this,printTagModel);
    }

    /********************************************** 工具函数 *************************************************/

    public static void actionTo(Context context, String express_tag, String express_name, String code, String check_id) {
        Intent intent = new Intent(context, ScannerPrintActivity.class);
        intent.putExtra("express_tag", express_tag);
        intent.putExtra("express_name", express_name);
        intent.putExtra("code", code);
        intent.putExtra("check_id", check_id);
        context.startActivity(intent);
    }

    private int isRepeat(String barCode) {
        int lenth = mList.size();
        if (lenth == 0) {
            return 0;  //不重复
        }
        for (ScannerMessageModel model : mList) {
            if (barCode.equals(model.getWaybill())) {
                if (1 == model.getIsHavaPhone()) {
                    return 2;    //播放声音
                } else {
                    return 1;    //不播放声音
                }
            }
        }
        return 0;
    }

    private String getData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < mList.size(); i++) {
            ScannerMessageModel model = mList.get(i);
            sb.append(model.getWaybill());
            sb.append(",");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    //去除由于识别灵敏带来的误差
    private boolean isEffective(String phone) {
        ScannerMessageModel model = mList.get(0);
        if (phone.equals(model.getPhone())) {
            return false;
        }
        if (mList.size() > 1) {
            ScannerMessageModel entity = mList.get(1);
            if (phone.equals(entity.getPhone())) {
                return false;
            }
        }
        return true;
    }

    //判断信息是否完全
    private void isCompliteMessage(ScannerMessageModel model) {
        if (1 == model.getIsHaveWayNumber() && 1 == model.getIsHavaPhone() && !"".equals(model.getCode())) {
            handler.sendEmptyMessageDelayed(REQUEST_START, 1200);
        }
    }

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
            tv_pop_code_title = selectView.findViewById(R.id.tv_pop_code_title);

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


    private void initChangeCodePop() {
        if (codePopWindow != null && codePopWindow.isShowing()) {
            codePopWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.edit_code_pop, null);
            View layout_local_close = selectView.findViewById(R.id.layout_local_close);

            RecyclerView rl_chart = selectView.findViewById(R.id.rl_chart);
            RecyclerView rl_number = selectView.findViewById(R.id.rl_number);

            initRecycler(rl_chart,rl_number);   //初始化

            tv_local = selectView.findViewById(R.id.tv_local);

            TextView tv_local_cannel = selectView.findViewById(R.id.tv_local_cannel);
            TextView tv_local_save = selectView.findViewById(R.id.tv_local_save);

            layout_local_close.setOnClickListener(viewClicklistener);
            tv_local_cannel.setOnClickListener(viewClicklistener);
            tv_local_save.setOnClickListener(viewClicklistener);

            codePopWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            codePopWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            codePopWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            codePopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            codePopWindow.setTouchable(true); // 设置popupwindow可点击
            codePopWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            codePopWindow.setFocusable(true); // 获取焦点
        }

    }

    private void showChangeDialogPop() {
        if (codePopWindow != null && !codePopWindow.isShowing()) {
            codePopWindow.showAtLocation(iv_back, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private int chartPosition = -1;
    private int numberPosition = -1;

    private void initRecycler(RecyclerView rl_chart, RecyclerView rl_number) {
        String[] mLocalChart = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String[] mLocalNumber = new String[]{"1","2","3","4","5","6","7","8"};
        SimpleTypeAdapter goodsAdapter = new SimpleTypeAdapter(this, mLocalChart);
        rl_chart.setAdapter(goodsAdapter);
        rl_chart.addItemDecoration(new MarginDecoration(this));
        rl_chart.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setOnItemClickListener(position -> {
            chartPosition = position;
            if (numberPosition != -1) {
                tv_local.setText(mLocalChart[position]+mLocalNumber[numberPosition]);
            } else {
                tv_local.setText(mLocalChart[position]);
            }
            goodsAdapter.setPosition(position);
            goodsAdapter.notifyDataSetChanged();
        });

        SimpleTypeAdapter goodsAdapter1 = new SimpleTypeAdapter(this, mLocalNumber);
        rl_number.setAdapter(goodsAdapter1);
        rl_number.addItemDecoration(new MarginDecoration(this));
        rl_number.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter1.setOnItemClickListener(new SimpleTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                numberPosition = position;
                if (chartPosition != -1) {
                    tv_local.setText(mLocalChart[chartPosition]+mLocalNumber[position]);
                } else {
                    tv_local.setText(mLocalNumber[position]);
                }
                goodsAdapter1.setPosition(position);
                // popupWindow.dismiss();
                goodsAdapter1.notifyDataSetChanged();
            }
        });
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
                case R.id.layout_local_close:
                case R.id.tv_local_cannel:
                    codePopWindow.dismiss();
                    break;
                case R.id.tv_local_save:  //保存取件码
                    changeTakeCode();
                    break;
            }
        }
    };

    private void showEditPopuwindow(int position) {
        ScannerMessageModel model = mList.get(position);
        String code = model.getCode();
        String[] codeS = StringUtil.handleCode(code);
        tv_pop_code_title.setText(codeS[0]);
        tv_pop_code.setText(codeS[1]);
        tv_pop_phone.setText(model.getPhone());
        tv_pop_way_number.setText(model.getWaybill());
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(iv_back, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void handleChangeExpress() {
        ScannerMessageModel model = mList.get(0);
        if (0 == model.getIsHavaPhone() && 0 == model.getIsHaveWayNumber()) {// 空数据
            if (mList.size() == 1) {
                SelectExpressActivity.actionTo(ScannerPrintActivity.this, CHANGE_EXPRESS);
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

    private void showBackDialog(String title) {
        scannerFragment.onPause();
        new CircleDialog.Builder()
                .setTitle("标题")
                .setWidth(0.8f)
                .setText("\n"+title+"\n")
                .setPositive("退出重试", null)
                .setOnCancelListener(dialogInterface -> ScannerPrintActivity.this.finish())
                .show(getSupportFragmentManager());
    }

    private void showRepeatErrorDialog() {
        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setWidth(0.8f)
                .setText("\n取件码重复，请重新设置\n")
                .setPositive("确定", null)
                .show(getSupportFragmentManager());
    }

    private void showCommitErrorDialog() {
        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(false)
                .setText("\n数据同步失败，请重新提交\n")
                .setPositive("提交", view -> {
                    commitData(mList.get(currentPosition));
                })
                .setNegative("删除", view -> {
                    deleteDataFromService(currentModel);
                })
                .show(getSupportFragmentManager());
    }

    private void saveChangeMessage() {
        String code_title = tv_pop_code_title.getText().toString();
        String way_number = tv_pop_way_number.getText().toString();    //运单号
        String phone = tv_pop_phone.getText().toString();
        if ("".equals(way_number)) {
            ToastUtil.showShort("运单号不可为空");
        } else if ("".equals(phone)) {
            ToastUtil.showShort("手机号不可为空");
        } else if (isRightAboutChange("", way_number)) {    //判断修改的信息是否正确
            ScannerMessageModel model = mList.get(currentPosition);
            model.setWaybill(way_number);
            model.setCode(model.getCode());
            model.setTimestamp(model.getTimestamp());
            model.setPhone(phone);
            model.setExpressName(express_name);
            model.setIsHavaPhone(1);
            model.setIsHaveWayNumber(1);
            mAdapter.addData(currentPosition, model);
           // commitData(model);   //修改提交
        }
        popupWindow.dismiss();
    }

    private boolean isRightAboutChange(String code, String way_number) {
        int codeNumber = IntegerUtil.changeStrToInteger(code);
        if (codeNumber > 9999) {
            ToastUtil.showShort("取件码不可超过9999");
            return false;
        }
        for (int i = 0; i < mList.size(); i++) {
            if (i == currentPosition) {
                continue;
            }
            ScannerMessageModel model = mList.get(i);
            if (code.equals(model.getCode())) {
                ToastUtil.showShort("取件码重复");
                return false;
            }
            if (way_number.equals(model.getWaybill())) {
                ToastUtil.showShort("运单号重复");
                return false;
            }
        }
        return true;
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
