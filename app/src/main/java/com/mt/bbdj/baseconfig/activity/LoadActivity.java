package com.mt.bbdj.baseconfig.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.community.activity.CommunityActivity;
import com.mt.bbdj.community.activity.SelectGoodsByStoreActivity;

import org.greenrobot.eventbus.EventBus;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

//启动界面
public class LoadActivity extends AppCompatActivity {

    private int defauleSecond = 2;   //默认两秒
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            defauleSecond--;
            if (defauleSecond == 0) {
                //跳转应用
                startApplication();
            } else {
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    private SharedPreferences mShare;
    private SharedPreferences.Editor mEditor;

    private void startApplication() {
        Intent intent = new Intent();
        boolean firstStart = mShare.getBoolean("firstStart",true);

        if (firstStart) {   //若是第一次启动进入滑动引导页
            intent.setClass(this,SlideActivity.class);
            mEditor.putBoolean("firstStart",false);
            mEditor.commit();
        } else {
            //不是第一次则判断是否已经登录了
            String userName = mShare.getString("userName","");
            String password = mShare.getString("password","");

            //未登录则跳转到登录界面
            if ("".equals(userName) || "".equals(password) || null == userName
                    || null == password || "null".equals(userName) || "null".equals(password)) {
                intent.setClass(this,LoginByCodeActivity.class);
            } else {
                //若是已经登录过了，直接到程序主页
                intent.setClass(this,CommunityActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(LoadActivity.this);
        mShare = SharedPreferencesUtil.getSharedPreference();
        mEditor = SharedPreferencesUtil.getEditor();
        //倒计时2s
        handler.sendEmptyMessageDelayed(1,1000);
    }


}
