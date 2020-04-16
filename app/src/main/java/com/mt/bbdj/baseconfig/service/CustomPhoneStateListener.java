package com.mt.bbdj.baseconfig.service;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.HashMap;

/**
 * 来去电监听
 */

public class CustomPhoneStateListener extends PhoneStateListener {

    private Context mContext;

    private String localPhone;

    private String user_id;

    private RequestQueue mRequestQueue;

    private final int REQUEST_COMMIT_PHONE = 1001;

    public CustomPhoneStateListener(Context context, String localPhone, String user_id, RequestQueue requestQueue) {
        mContext = context;
        this.localPhone = localPhone;
        this.user_id = user_id;
        this.mRequestQueue = requestQueue;
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);

    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                Log.d("A===挂断", incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                Log.d("A===响铃", incomingNumber);
                commitData(incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:    // 来电接通 或者 去电，去电接通  但是没法区分
                Log.d("A===接通", incomingNumber);
                break;
        }
    }

    private void commitData(String incomingNumber) {
        if (localPhone == null || "".equals(localPhone)){
            localPhone = "1";
        }
        HashMap<String,String> params = new HashMap<>();
        params.put("local_phone",localPhone);
        params.put("call_phone",incomingNumber);
        params.put("user_id",user_id);
        Request<String> request = NoHttpRequest.commitPhoneNumber(params);
        mRequestQueue.add(REQUEST_COMMIT_PHONE, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "CustomPhoneStateListener::" + response.get());
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }
}
