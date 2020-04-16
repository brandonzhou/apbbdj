package com.mt.bbdj.baseconfig.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.RxTool;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.zto.recognition.phonenumber.OCRManager;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.xiaomi.MiPushRegistar;


/**
 * Author : ZSK
 * Date : 2018/12/25
 * Description :
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    private String TAG = "MyApplication===";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        //初始化数据存储
        initDatabase();

        initNoHttp();


        initOcr();

        RxTool.init(this);
        ToastUtil.init(this);

        CrashReport.initCrashReport(getApplicationContext(), "28ee43e70a", false);
        //bug收集
        // CrashHandler.getInstance().init(this);

        SoundHelper.init();

        initSettingPush();   //初始化推送
    }

    private void initSettingPush() {
        UMConfigure.init(this,"5dba79dd4ca3571590000a81","Umeng",UMConfigure.DEVICE_TYPE_PHONE,"eda94e78a7288bb51eff5c79ff8b0809");
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG,"注册成功：deviceToken：-------->  " + deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG,"注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });

        HuaWeiRegister.register(this);
        MiPushRegistar.register(this,"2882303761518221507","5931822122507");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.install(this);

    }


    private void initOcr() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, getApplicationContext());
    }

    private void initDatabase() {
        SharedPreferencesUtil.init(this);
        GreenDaoManager.getInstance().init(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    private void initNoHttp() {
        Logger.setDebug(true);
        Logger.setTag("photoFile");
        // 如果你需要自定义配置：
        InitializationConfig config = InitializationConfig.newBuilder(this)
                // 全局连接服务器超时时间，单位毫秒，默认10s。
                .connectionTimeout(10 * 1000)
                // 全局等待服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(10 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .cacheStore(
                        // 如果不使用缓存，setEnable(false)禁用。
                        new DBCacheStore(this).setEnable(true)
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现CookieStore接口。
                .cookieStore(
                        // 如果不维护cookie，setEnable(false)禁用。
                        new DBCookieStore(this).setEnable(true)
                )
                .networkExecutor(new URLConnectionNetworkExecutor())
                .build();
        NoHttp.initialize(config);
    }


}
