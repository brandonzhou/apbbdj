package com.mt.bbdj.baseconfig.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.community.activity.ClearOrderActivity;
import com.mt.bbdj.community.activity.CommunityActivity;
import com.mt.bbdj.community.activity.SendManagerActivity;
import com.mt.bbdj.community.activity.WaitHandleOrderActivity;
import com.mt.bbdj.community.activity.WaterOrderActivity;
import com.yanzhenjie.nohttp.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;
/**
 * Author : ZSK
 * Date : 2019/1/25
 * Description :  自定义接收器
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                LogUtil.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));


            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知");
               /* int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);*/
                boolean isPlay = SharedPreferencesUtil.getSharedPreference().getBoolean("isPlaySound",true);
                if (isPlay) {
                    SoundHelper.getInstance().playNotificationSound();
                }
                EventBus.getDefault().post(new TargetEvent(TargetEvent.COMMIT_FIRST_REFRESH));

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                LogUtil.d(TAG, "[MyReceiver] 用户点击打开了通知");

                //获取推送消息的方法
                String content = bundle.getString(JPushInterface.EXTRA_ALERT);

                if (null != content && content.startsWith("【干洗】")) {
                    Intent intentAction1 = new Intent(context, ClearOrderActivity.class);
                    intentAction1.putExtras(bundle);
                    intentAction1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intentAction1);
                } else if (null != content && content.startsWith("【桶装水】")){
                    Intent intentAction2 = new Intent(context, WaterOrderActivity.class);
                    intentAction2.putExtras(bundle);
                    intentAction2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intentAction2);
                }else if(null != content && content.startsWith("【零售】")){
                    Intent intentAction4 = new Intent(context, WaitHandleOrderActivity.class);
                    bundle.putString("type","1");
                    intentAction4.putExtras(bundle);
                    intentAction4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intentAction4);
                }else {
                    Intent intentAction3 = new Intent(context, SendManagerActivity.class);
                    intentAction3.putExtras(bundle);
                    intentAction3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intentAction3);
                }

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                LogUtil.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                LogUtil.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                LogUtil.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }


        } catch (Exception e) {

        }

    }
}
