package com.mt.bbdj.baseconfig.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mt.bbdj.baseconfig.service.MonitorPhoneService;

/**
 * @Author : ZSK
 * @Date : 2019/12/19
 * @Description :
 */
public class MonitorPhoneBroastcastReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.mt.bbdj.start")){
            Log.d("A===", "BroadcastReceiver-onCreate");
            Intent serviceIntent = new Intent(context,MonitorPhoneService.class);
            context.startService(serviceIntent);
        }
    }
}
