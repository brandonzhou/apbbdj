package com.mt.bbdj.community.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.BluetoothScanAdapter;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

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

public class PrintTakeNumber1Activity extends BaseActivity implements View.OnClickListener {

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
    private PrintThread printDataThread;

    private int connectPosition = 0; //蓝牙连接位置
    private List<HashMap<String, String>> dataList;


    @SuppressLint("HandlerLeak")
    Handler mPrintHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LoadDialogUtils.cannelLoadingDialog();
            if (msg.what != 0) {
                ToastUtil.showShort("连接失败，请重试！");
            } else {
                printDataThread.start();
            }
        }
    };


    public static void actionTo(Context context,PrintTagModel printTagModel) {
        Intent intent = new Intent(context, PrintTakeNumber1Activity.class);
        intent.putExtra("printData",printTagModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pannel);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(PrintTakeNumber1Activity.this);
        initView();
        initPannelData();
        initBlueToothParam();    //初始化蓝牙设备
        initPrintListener();     //初始化打印监听
    }

    private void initPannelData() {
        Intent intent = getIntent();
        PrintTagModel printData = (PrintTagModel) intent.getSerializableExtra("printData");
        dataList = printData.getData();
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
                    connectBluetooth();    //连接
                } catch (Exception e) {
                    LoadDialogUtils.cannelLoadingDialog();
                    e.printStackTrace();
                }
            }
        });

    }

    private void connectBluetooth() {
        if (mList.size() == 0) {
            return;
        }
        LoadDialogUtils.getInstance().showLoadingDialog(this);
        //获取蓝牙mvc地址
        String toothAddress = mList.get(connectPosition).get("address");

        if (!toothAddress.contains(":")) {
            return;
        }
        printThread = new Thread(() -> {
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
        });
        printThread.start();
    }

    public class PrintThread extends Thread{
        @Override
        public void run() {
            for (HashMap<String,String> data : dataList) {
                String code = data.get("code");
                String qrcode = data.get("qrcode");
                code = StringUtil.handleNullResultForString(code);
                qrcode = StringUtil.handleNullResultForString(qrcode);
                printPanel(code,qrcode);    //打印面单
            }
        }
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
        printDataThread = new PrintThread();
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

        if (printThread != null) {
            Thread dummy = printThread;
            printThread = null;
            dummy.interrupt();
        }

        if (printDataThread != null) {
            Thread printThread = printDataThread;
            printDataThread = null;
            printThread.interrupt();
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
    private void printPanel(String code,String qrcode) {
        try {
            HashMap<String, String> pum = new HashMap<String, String>();
            pum.put("[code]", code);
            pum.put("[title]", "");
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("number1.txt");//打印模版放在assets文件夹里
            String path = new String(InputStreamToByte(afis), "utf-8");//打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                path = path.replace(string, pum.get(string));
            }
            // HPRTPrinterHelper.printText(path);

            HPRTPrinterHelper.openEndStatic(true);//开启
            HPRTPrinterHelper.PrintData(path);//打印机打印

            if ("1".equals(BluetoothNumberActivity.paper)) {
                HPRTPrinterHelper.Form();
            }

            HPRTPrinterHelper.PrintQR(HPRTPrinterHelper.BARCODE, "380", "70", "2", "6", qrcode);

            HPRTPrinterHelper.Form();
            HPRTPrinterHelper.Print();

            int endStatus = HPRTPrinterHelper.getEndStatus(16);//获取打印状态
            HPRTPrinterHelper.openEndStatic(false);//关闭

            if (endStatus == 0) {
                //finish();
            } else {
                ToastUtil.showShort("打印失败！");
            }
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }


   /* private void printPanel(String code,String qrcode) {
        try {
            HashMap<String, String> pum = new HashMap<String, String>();
            pum.put("[code]", code);
            pum.put("[title]", "");
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("number1.txt");//打印模版放在assets文件夹里
            String path = new String(InputStreamToByte(afis), "utf-8");//打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                path = path.replace(string, pum.get(string));
            }
            // HPRTPrinterHelper.printText(path);

            HPRTPrinterHelper.openEndStatic(true);//开启
            HPRTPrinterHelper.PrintData(path);//打印机打印

            if ("1".equals(BluetoothNumberActivity.paper)) {
                HPRTPrinterHelper.Form();
            }

            HPRTPrinterHelper.PrintQR(HPRTPrinterHelper.BARCODE, "380", "70", "2", "6", qrcode);

            HPRTPrinterHelper.Form();
            HPRTPrinterHelper.Print();

            int endStatus = HPRTPrinterHelper.getEndStatus(16);//获取打印状态
            HPRTPrinterHelper.openEndStatic(false);//关闭

            if (endStatus == 0) {
                //finish();
            } else {
                ToastUtil.showShort("打印失败！");
            }
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }*/


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
            if (ContextCompat.checkSelfPermission(PrintTakeNumber1Activity.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrintTakeNumber1Activity.this,
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
