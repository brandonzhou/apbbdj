package com.mt.bbdj.baseconfig.application;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.multidex.MultiDex;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author : ZSK
 * @Date : 2019/8/7
 * @Description :
 */
public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    @Keep
    @SophixEntry(MyApplication.class)
    static class RealApplicationStub {}
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
        MultiDex.install(this);
        initSophix();
    }
    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData("27763077-1", "782dde0d4b8923dab68fcb31b3f2db49", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCEHZykgi6HUNeixv+j7uNXcgkrnk4uxb9v3YCBhl1wwXT6islWEHpbGZJVoKIp4lSwtGEwtShAz0mCtP4PYiSHSPe1r5AsNokxNF7IK2xmSStYp7Y7kFbvBxFR4Jp7QsEdMUe9I3ioSfRv/bo/SPic2uluWmHs55TH4v7RrqhKhqdpksqJMY9/22FFoJ1lGVnu1cnUw+2pAhhQWwOiLyRvBdKB76AzMWFy3lolse2OJ/CAw4qQUX4i/hJSdJ4vFltOFW1Qh8FjjIEEf6ykWQdJrXrVBcNs6uEto0DF7/+X3CmGBfGXfCcR9yznc+3vlgSa2ft9znjqofukcIjct+ORAgMBAAECggEAQlU/1dVLGUO80QzLQ/roiHpIIm9cX92Hrdnv8JiPB9MKMdVHG4AwftcLAxUg9pid3w7iEXuTl168UPUg7oZfQtGfFcjtQVWsclkSEbzDs2OfcObb93IUQ/UvfAWiaqrsnQSmOU+6//wJz8T8I1Q3B0Jem7qjmjf4GphdStg/xRK2/l6EVcOEPamrIC2+WTdSfKTPT6BJY+tZnMXdPyyAan+eK6jy83nt7EdquVaWmnZbGOIu4SkJsjrk5a9/6hw5zHlchmkjCj3JPmSkhipN+WEayWTJ/McDBg72MmU5fbE6Q26S8rZ96C230SLFwR0hTAgFwQk3XlKT3wlwXigs8QKBgQDMVGuXxs3w3td3718OCD91zvzZXD8KhVmoLJE9hMKhFAEUvhe9vPRzxH0/RNqIkY+8CrBu/V44TIYuS1tGP8CPd6r01auNU0gKADKvtVEnR8ln/3lmg+78NrwBHtSUrOVW+BO5tdj7ubLAgKwzcp5D3M7CCfbjsmUuEMobCNgnPQKBgQClhk8Qrk1qYpLssPi6QwQzbweYn6vJeLfqgpFgcCxyvsvhSUTWoA5orFKgXnRTI5iK+v5IVlpL231V8gMehZC48ztO9cFX6SSwDuPhmKylX0f1l9k/2vLq+FW+4DlxApwTxNPNrLzojmk5BhARSExbXjkku+aEbSJLj5QqkKyS5QKBgDZWLNaL07A+UmZejZSZtOaLSMo0sb8GfzUtxOHrMCbNvwvZEU8vtIIkunncwZCXxPVokkqxriCimPwupfMkePyuAhRhzeEjSIEAVHpNaWtct76kPZB5fVAs7goOPNHcuJBMIFK+fRlT5Kk0jpaP4G6DhagsJi+e/TEm3dgangCxAoGAHwsAWw2FgVU5XuCV1UDqUccZ8prHegKE45tXLnH7NA3qaLb4DRR5f1IKMP9l6426dx45gFGJn75rVCoOQbQk/zrGvblDaHxWIBq6zD14647iDQJMgThL5JtsYV7mBvbJVL/ORT2HNEo5G7OyYbtgbqZOK0B+LAnXq7oivJTjeK0CgYEAh1Z0BFmiTvn80q2I1t/inpdk1X4T78g2X+/OT/dPgR8DTKqr8iEzwr0CklYZPthFvm1GU7bDjaBNAzQF+sdqvVcMqArApFIItf6ltmi9zLaKvJ9YNWva+tkfhWx1mA2Efd8fvB3UaDXzwTsPDflwf5VZKjrJYM848x3nhk0Yp5Q=")
                .setEnableDebug(true)
                .setAesKey("1234567891234567")
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {

                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            EventBus.getDefault().post(new TargetEvent(TargetEvent.KILL_PROCESS));
                        } /*else if (code == PatchStatus.CODE_LOAD_FAIL){
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            SophixManager.getInstance().cleanPatches();
                        }*/
                    }
                }).initialize();
    }
}
