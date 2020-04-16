package com.mt.bbdj.baseconfig.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import androidx.annotation.NonNull;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.MainActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/12/19
 * @Description :
 */
public class MonitorPhoneService extends Service {

    public static final String TAG = "A===";

    public static final String ACTION_REGISTER_LISTENER = "action_register_listener";
    private String user_id;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        registerPhoneStateListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_logo_))
                .setContentTitle("兵兵到家")
                .setSmallIcon(R.drawable.ic_logo_)
                .setContentText("正在运行");
        Notification notification = builder.build(); // 获取构建好的Notificationnotification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(123456, notification);
        return START_STICKY;
    }

    private void registerPhoneStateListener() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String localPhone = telephonyManager.getLine1Number();
        mRequestQueue = NoHttp.newRequestQueue();
        CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener(this,localPhone,user_id,mRequestQueue);
        if (telephonyManager != null) {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind action: " + intent.getAction());
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind action: " + intent.getAction());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind action: " + intent.getAction());
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Intent intent = new Intent("com.mt.bbdj.start");
        sendBroadcast(intent);
        super.onDestroy();
    }
}
