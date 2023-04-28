package com.mt.bbdj.community.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.BluetoothScanAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import HPRTAndroidSDKA300.HPRTPrinterHelper;
import HPRTAndroidSDKA300.PublicFunction;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class PrintPannelActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout iv_back;
    private ProgressBar pb_search;    //刷新
    private TextView tv_refresh;    //刷新
    private RecyclerView rl_bluetooth;

    private List<HashMap<String, String>> mList = new ArrayList<>();    //存储蓝牙名称
    private List<String> addressTooth = new ArrayList<>();    //存储蓝牙地址
    private BluetoothScanAdapter mAdapter;
    private Thread printThread;     //新添加的线程
    private PublicFunction PFun;
    private Context thisCon = null;
    private BluetoothAdapter mBtAdapter;

    private int connectPosition = 0; //蓝牙连接位置

    private final int REQUEST_GET_DATA = 1001;

    @SuppressLint("HandlerLeak")
    Handler mPrintHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LoadDialogUtils.cannelLoadingDialog();
            if (msg.what != 0) {
                ToastUtil.showShort("连接失败，请重试！");
            } else {
                printPanel();    //打印面单
                //打印完成之后刷新任务列表
                refreshDataList();
            }
        }
    };

    private SharedPreferences mShared;
    private String user_id;
    private RequestQueue mRequestQueue;


    public static void actionTo(Activity context, String user_id, String mail_id, String goods_name, String weight, String money) {
        Intent intent = new Intent(context, PrintPannelActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("mail_id", mail_id);
        intent.putExtra("goods_name", goods_name);
        intent.putExtra("weight", weight);
        intent.putExtra("money", money);
        context.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pannel);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(PrintPannelActivity.this);
        initParams();
        initView();
        initBlueToothParam();    //初始化蓝牙设备
        initPrintListener();     //初始化打印监听
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (EnableBluetooth()) {
            requestBluetoothPermission();
        } else {
            ToastUtil.showShort("确保打开蓝牙后，刷新列表");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_refresh:   //刷新蓝牙设备
                actionToRefreshList();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //EnableBluetooth
    private boolean EnableBluetooth() {
        boolean bRet = false;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null) {
            if (mBtAdapter.isEnabled())
                return true;
            mBtAdapter.enable();
        }
        return bRet;
    }

    private void scanBluetooth() {
        startProgress();
        mList.clear();
        mAdapter.notifyDataSetChanged();
        mList.addAll(getPairedData());
        addressTooth.clear();

        if (!mBtAdapter.startDiscovery()) {
            LogUtil.e("BlueTooth", "扫描尝试失败,请重试");
            stopProgress();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<HashMap<String, String>> getPairedData() {
        List<HashMap<String, String>> list = new ArrayList<>();
        //获取蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        //得到当前的已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", device.getName());
                map.put("address", device.getAddress());
                list.add(map);
                map = null;
            }
        }

        return list;
    }

    private void initPrintListener() {

        //连接打印
        mAdapter.setConnectClickListener(new BluetoothScanAdapter.OnItemConnectClickListener() {
            @Override
            public void onConnect(int position) {
                connectPosition = position;
                try {
                    if (mBtAdapter.isDiscovering()) {
                        mBtAdapter.cancelDiscovery();
                    }
                    getPannelData();    //获取面单内容
                } catch (Exception e) {
                    LoadDialogUtils.cannelLoadingDialog();
                    e.printStackTrace();
                }
            }
        });

    }

    private void connectBluetooth() {
        LoadDialogUtils.getInstance().showLoadingDialog(this);
        //获取蓝牙mvc地址
        String toothAddress = mList.get(connectPosition).get("address");

        if (!toothAddress.contains(":")) {
            return;
        }
        printThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new HPRTPrinterHelper(thisCon, HPRTPrinterHelper.PRINT_NAME_A300);
                    int portOpen = HPRTPrinterHelper.PortOpen("Bluetooth," + toothAddress);
                    HPRTPrinterHelper.logcat("portOpen:" + portOpen);
                    Message message = Message.obtain();
                    message.what = portOpen;
                    mPrintHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                }
            }
        });
        printThread.start();
    }


    private void actionToRefreshList() {
        if (!mBtAdapter.isDiscovering()) {
            scanBluetooth();
        }
    }


    //开始动画
    public void startProgress() {
        pb_search.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.shape_progressbar));
        pb_search.setProgressDrawable(getResources().getDrawable(
                R.drawable.shape_progressbar));
    }

    //结束动画
    public void stopProgress() {
        pb_search.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.progress_icon));
        pb_search.setProgressDrawable(getResources().getDrawable(
                R.drawable.progress_icon));
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        pb_search = findViewById(R.id.pb_search);
        tv_refresh = findViewById(R.id.tv_refresh);
        rl_bluetooth = findViewById(R.id.rl_bluetooth);
        iv_back.setOnClickListener(this);
        tv_refresh.setOnClickListener(this);

        initBlueToothList();    //初始化蓝牙列表
    }

    private void initBlueToothList() {
        mAdapter = new BluetoothScanAdapter(mList);
        rl_bluetooth.setAdapter(mAdapter);
        rl_bluetooth.addItemDecoration(new MarginDecoration(this, 22));
        rl_bluetooth.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initParams() {

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        mail_id = intent.getStringExtra("mail_id");
        goods_name = intent.getStringExtra("goods_name");
        weight = intent.getStringExtra("weight");
        money = intent.getStringExtra("money");

        mShared = SharedPreferencesUtil.getSharedPreference();
        printType = mShared.getString("printType", "1");

        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void refreshDataList() {
        if ("1".equals(printType)) {
            //刷新“待收件”界面
            EventBus.getDefault().post(new TargetEvent(0));
        } else if ("2".equals(printType)) {
            //刷新“待打印”界面
            EventBus.getDefault().post(new TargetEvent(2));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (printThread != null) {
            Thread dummy = printThread;
            printThread = null;
            dummy.interrupt();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;

        if (printThread != null) {
            Thread dummy = printThread;
            printThread = null;
            dummy.interrupt();
        }
        try {
            HPRTPrinterHelper.PortClose();
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }

            if (mScaneceiver != null) {
                unregisterReceiver(mScaneceiver);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /***********************************************  面单内容 *********************************************/

    private void getPannelData() {
        Request<String> request = NoHttpRequest.waitMimeographRequest(user_id, mail_id, goods_name
                , weight, money, "");
        mRequestQueue.add(REQUEST_GET_DATA, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(PrintPannelActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.get());
                    Log.e("PrintPannelActivity",response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        savePannelMessage(jsonObject);
                        LoadDialogUtils.cannelLoadingDialog();
                        connectBluetooth();   //连接蓝牙
                        setResult(RESULT_OK);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                }
                // LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                ToastUtil.showShort("当前网络不佳");
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {
                // LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }

    private void savePannelMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        packageCode = StringUtil.handleNullResultForString(dataObj.getString("code"));
        barcode = StringUtil.handleNullResultForString(dataObj.getString("yundanhao"));
        date = StringUtil.handleNullResultForString(dataObj.getString("transit"));
        siteName = StringUtil.handleNullResultForString(dataObj.getString("place"));
        mail_id = StringUtil.handleNullResultForString(dataObj.getString("mail_id"));
        Receiver = StringUtil.handleNullResultForString(dataObj.getString("collect_name"));
        Receiver_Phone = StringUtil.handleNullResultForString(dataObj.getString("collect_phone"));
        Receiver_address = StringUtil.handleNullResultForString(dataObj.getString("collect_region"));
        Receiver_address1 = StringUtil.handleNullResultForString(dataObj.getString("collect_address"));
        Sender = StringUtil.handleNullResultForString(dataObj.getString("send_name"));
        Sender_Phone = StringUtil.handleNullResultForString(dataObj.getString("send_phone"));
        Sender_address = StringUtil.handleNullResultForString(dataObj.getString("send_region"));
        Sender_address1 = StringUtil.handleNullResultForString(dataObj.getString("send_address"));
        number = StringUtil.handleNullResultForString(dataObj.getString("number"));
        goods_name = StringUtil.handleNullResultForString(dataObj.getString("goods_name"));
        weight = StringUtil.handleNullResultForString(dataObj.getString("weight"));
        money = StringUtil.handleNullResultForString(dataObj.getString("mailing_momey"));
        express_id = StringUtil.handleNullResultForString(dataObj.getString("express_id"));
        printTime = DateUtil.getCurrentTimeFormat("yyyy-MM-dd");
    }


    private String packageCode="";      //标识码
    private String barcode="";     //运单号
    private String date="";    //标识码和日期
    private String siteName="";    //中转站
    private String mail_id="";    //订单号
    private String Receiver="";
    private String Receiver_Phone="";
    private String Receiver_address="";
    private String Receiver_address1="";
    private String Sender="";
    private String Sender_Phone="";
    private String Sender_address="";
    private String Sender_address1="";
    private String number="";     //"123123"
    private String goods_name="";     //商品名称
    private String weight="";     //计费重量
    private String express_id="";     //快递公司id
    private String printTime="";     //打印时间
    private String code="";     //标识码
    private String money="";    //价格
    private String fastLogoBig="";
    private String fastLogoMini="";
    private String printType;     //表示进入此界面的途径，用于打印后的界面刷新  “1” ： 待收件界面   “2” ：待打印界面  "3" : 已完界面


    private void printPanel() {
        try {
            HashMap<String, String> pum = new HashMap<String, String>();
            // pum.put("[packageCode]", packageCode);
            pum.put("[packageCode]", packageCode);
            pum.put("[barcode]", barcode);
            pum.put("[date]", date);
            pum.put("[siteName]", siteName);
            pum.put("[Receiver]", Receiver);
            pum.put("[Receiver_Phone]", Receiver_Phone);
            pum.put("[Receiver_address]", Receiver_address);
            pum.put("[Receiver_address_all]", Receiver_address + Receiver_address1);
            pum.put("[Receiver_address1]", Receiver_address1);
            pum.put("[Sender]", Sender);
            pum.put("[Sender_Phone]",  Sender_Phone);
            pum.put("[Sender_address]",  Sender_address);
            pum.put("[Sender_address_all]", Sender_address + Sender_address1);
            pum.put("[Sender_address1]", Sender_address1);
            pum.put("[wight]",weight);
            pum.put("[printTime]", printTime);
            pum.put("[stageCode]",number);
            pum.put("[goodName]", goods_name);
            if (Sender_address.contains("福州")) {
                pum.put("[money]", "运费:"+money);
            } else {
                pum.put("[money]", " ");
            }
            pum.put("[servicePhone]", "400-775-0008");
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("zhongtong_100mm.txt");//打印模版放在assets文件夹里
            String path = new String(InputStreamToByte(afis), "utf-8");//打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                path = path.replace(string, pum.get(string));
            }
            // HPRTPrinterHelper.printText(path);


            HPRTPrinterHelper.openEndStatic(true);//开启
            HPRTPrinterHelper.PrintData(path);//打印机打印

            InputStream inbmpBingBingLogo = this.getResources().getAssets().open("ic_logo_mini.png");// 顶部A栈logo
            Bitmap bitmap = BitmapFactory.decodeStream(inbmpBingBingLogo);
           // HPRTPrinterHelper.Expanded("20", "10", bitmap, (byte) 0);//第一联 顶部A栈logo

//            InputStream inbmp6 = this.getResources().getAssets().open("ic_send_logo.png");
//            Bitmap bitmap6 = BitmapFactory.decodeStream(inbmp6);
//            HPRTPrinterHelper.Expanded("525", "100", bitmap6, (byte) 0);// 第一联 派
//            InputStream inbmp7 = this.getResources().getAssets().open("ic_receive_logo.png");
//            Bitmap bitmap7 = BitmapFactory.decodeStream(inbmp7);
//            HPRTPrinterHelper.Expanded("525", "795", bitmap7, (byte) 0);// 第二联 收
//            InputStream inbmp8 = this.getResources().getAssets().open("ic_post_logo.png");
//            Bitmap bitmap8 = BitmapFactory.decodeStream(inbmp8);
//            HPRTPrinterHelper.Expanded("525", "1215", bitmap8, (byte) 0);// 第三联 寄


            /**
             * 2020-10-24
             * TEXT 8 0 110 1540 客服热线 [servicePhone]
             * TEXT 5 0 280 1460 驿站代码
             * TEXT 5 0 275 1495 [stageCode]
             * TEXT 55 0 448 1552 A栈公众号
             */
//            InputStream inbmp4 = this.getResources().getAssets().open("ic_code.png");
//            Bitmap bitmap4 = BitmapFactory.decodeStream(inbmp4);
//            HPRTPrinterHelper.Expanded("443", "1430", bitmap4, (byte) 0);//二维码
//            InputStream inbmp3 = this.getResources().getAssets().open("ic_logo_mini.png");
//            Bitmap bitmap3 = BitmapFactory.decodeStream(inbmp3);
//            HPRTPrinterHelper.Expanded("20", "1450", bitmap3, (byte) 0);//第二联 A栈logo

            HPRTPrinterHelper.AutLine("65", "395", 500, 5, true, false, Receiver_address + Receiver_address1);
            //HPRTPrinterHelper.AutLine("65", "890", 500, 5, true, false, Receiver_address + Receiver_address1);
            //HPRTPrinterHelper.AutLine("65", "1021", 500, 5, true, false, Sender_address + Sender_address1);
            HPRTPrinterHelper.AutLine("65", "532", 500, 5, true, false, Sender_address + Sender_address1);

            fastLogoBig = "";
            fastLogoMini = "";

            setLogoData();   //设置快递公司logo
            InputStream inbmp5 = this.getResources().getAssets().open(fastLogoMini);
            Bitmap bitmap5 = BitmapFactory.decodeStream(inbmp5);
            HPRTPrinterHelper.Expanded("410", "622", bitmap5, (byte) 0);//第一联 快递公司logo

//            InputStream inbmp1 = this.getResources().getAssets().open(fastLogoMini);
//            Bitmap bitmap1 = BitmapFactory.decodeStream(inbmp1);
//            HPRTPrinterHelper.Expanded("20", "725", bitmap1, (byte) 0);// 第二联 快递公司logo
//
//            InputStream inbmp2 = this.getResources().getAssets().open(fastLogoMini);
//            Bitmap bitmap2 = BitmapFactory.decodeStream(inbmp2);
//            HPRTPrinterHelper.Expanded("325", "1215", bitmap2, (byte) 0);//第三联 快递公司logo
            if ("1".equals(BluetoothSearchActivity.paper)) {
                HPRTPrinterHelper.Form();
            }
            HPRTPrinterHelper.Form();
            HPRTPrinterHelper.Print();

            int endStatus = HPRTPrinterHelper.getEndStatus(16);//获取打印状态
            HPRTPrinterHelper.openEndStatic(false);//关闭

            if (endStatus == 0) {
                //从已处理界面传递过来
//                if ("3".equals(printType)) {
//                    finish();
//                } else {
//                    //打印成功，跳转到打印详情列表
//                    actionToPrintDetail();
//                }
                finish();
            } else {
                ToastUtil.showShort("打印失败！");
            }


        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }

    private void setLogoData() {
        switch (express_id) {
            case "100101":    //中通
                fastLogoBig = "ic_zhongtong_big.png";
                fastLogoMini = "ic_zhongtong_mini.png";
                break;
          /*  case "100102":    //顺丰
                fastLogoBig = "ic_zhongtong_big.png";
                fastLogoMini = "ic_zhongtong_mini.png";
                break;*/
            case "100103":    //韵达
                fastLogoBig = "ic_zhongtong_big.png";
                fastLogoMini = "ic_zhongtong_mini.png";
                break;
            case "100104":    //申通
                fastLogoBig = "ic_shentong_big.png";
                fastLogoMini = "ic_shentong_mini.png";
                break;
            case "100105":    //德邦
                fastLogoBig = "ic_shentong_big.png";
                fastLogoMini = "ic_shentong_mini.png";
                break;
            case "100106":    //天天
                fastLogoBig = "ic_tiantian_big.png";
                fastLogoMini = "ic_tiantian_mini.png";
                break;
            case "100107":    //EMS
                fastLogoBig = "ic_tiantian_big.png";
                fastLogoMini = "ic_tiantian_mini.png";
                break;
            case "100108":    //优速
                fastLogoBig = "ic_yousu_big.png";
                fastLogoMini = "ic_yousu_mini.png";
                break;
            case "100102":    //圆通
                fastLogoBig = "ic_yuantong_big.jpg";
                fastLogoMini = "ic_yuantong_mini.jpg";
                break;
            case "100110":    //百世
                fastLogoBig = "ic_baishi_big.png";
                fastLogoMini = "ic_baishi_mini.png";
                break;
        }
    }


    private void actionToPrintDetail() {
        Intent intent = new Intent(this, PrintPreviewActivity.class);
        startActivity(intent);
    }


    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    @PermissionYes(110)
    private void getMultiYes(List<String> grantedPermissions) {
        scanBluetooth();
    }

    @PermissionNo(110)
    private void getMultiNo(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, 110).show();
        }
    }

    private void requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(PrintPannelActivity.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrintPannelActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        110);
            } else {
                scanBluetooth();
            }
        } else {
            scanBluetooth();
        }
    }

    /******************************************* 初始化蓝牙 ******************************************************/
    private void initBlueToothParam() {
        initBroadcastReceiverForBluetooth();    //监听蓝牙连接状态的广播
        initSetting();   //设置打印纸张类型
    }

    private void initSetting() {
        String paper = PFun.ReadSharedPreferencesData("papertype");
        if (!"".equals(paper)) {
            BluetoothSearchActivity.paper = paper;
        }
    }

    private void initBroadcastReceiverForBluetooth() {
        //获取蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        thisCon = this.getApplicationContext();
        PFun = new PublicFunction(thisCon);

        //监听蓝牙连接状态的广播
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, intent);

        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

        IntentFilter scanIntent = new IntentFilter();
        scanIntent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        scanIntent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        scanIntent.addAction(ACTION_PAIRING_REQUEST);
        scanIntent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        scanIntent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        scanIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mScaneceiver, scanIntent);
    }


    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                try {
                    HPRTPrinterHelper.PortClose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    //扫描结束
    public final BroadcastReceiver mScaneceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            //搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    if (device.getBluetoothClass().getMajorDeviceClass() == 1536) {
                        if (!addressTooth.contains(device.getAddress())) {
                            addressTooth.add(device.getAddress());
                            //搜索的蓝牙设备
                            HashMap<String, String> map = new HashMap<>();
                            map.put("name", device.getName());
                            map.put("address", device.getAddress());
                            mList.add(map);
                            map = null;
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "取消配对");
                    default:
                        break;
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                stopProgress();
            }
        }
    };

}
