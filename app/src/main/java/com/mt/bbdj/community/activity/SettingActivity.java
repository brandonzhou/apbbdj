package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginByCodeActivity;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.PackageUtils;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.shshcom.station.base.ICaseBack;
import com.shshcom.station.setting.domain.UrgeSettingUseCase;
import com.shshcom.station.setting.http.bean.AutoUrgeData;
import com.shshcom.station.setting.ui.activity.AutoUrgeSettingActivity;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back;
    private LinearLayout ll_address_manager, ll_change_password, ll_about_app,ll_setting_print,ll_setting_print_code;
    private Button bt_cannel;
    private TextView tv_app_version;
    private TextView tv_setting_urge_type;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SettingActivity.this);
        initView();
        initData();
    }

    private void initData() {
        editor = SharedPreferencesUtil.getEditor();
        UrgeSettingUseCase.INSTANCE.getPackageUrgeSetting(new ICaseBack<AutoUrgeData>() {
            @Override
            public void onError(@NotNull String error) {
                ToastUtil.showLong(error);

            }

            @Override
            public void onSuccess(AutoUrgeData result) {
                if (result != null && result.getCurrentType() != null) {
                    String type = result.getCurrentType().getMsg();
                    tv_setting_urge_type.setText(type);
                }
            }
        });

    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back);
        ll_address_manager = findViewById(R.id.ll_address_manager);
        ll_change_password = findViewById(R.id.ll_change_password);
        ll_about_app = findViewById(R.id.ll_about_app);
        tv_app_version = findViewById(R.id.tv_app_version);
        tv_setting_urge_type = findViewById(R.id.tv_setting_urge_type);
        ll_setting_print_code = findViewById(R.id.ll_setting_print_code);
        ll_setting_print = findViewById(R.id.ll_setting_print);
        bt_cannel = findViewById(R.id.bt_cannel);
        rl_back.setOnClickListener(this);
        ll_address_manager.setOnClickListener(this);
        ll_change_password.setOnClickListener(this);
        ll_setting_print_code.setOnClickListener(this);
        ll_setting_print.setOnClickListener(this);
        ll_about_app.setOnClickListener(this);
        bt_cannel.setOnClickListener(this);

        String version = PackageUtils.getVersionName(this);
        tv_app_version.setText(version);
        findViewById(R.id.ll_setting_auto_notify).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_change_password:    //修改密码
                showChangePasswordPannel();
                break;
            case R.id.ll_setting_auto_notify:    //自动催取
                AutoUrgeSettingActivity.Companion.openActivity(this, 1);
                break;
            case R.id.ll_address_manager:   //地址管理
                showAddressManagerPannel();
                break;
            case R.id.ll_about_app:   //关于app
                showAboutAppPannel();
                break;
            case R.id.ll_setting_print:  //打印标签
                PrintLabel();
                break;
            case R.id.ll_setting_print_code:  //取件码
                PrintCode();
                break;
            case R.id.bt_cannel:
                takeoutLogin();
                break;
        }
    }

    private void PrintCode() {
        PrintCodeSettingActivity.actionTo(this);
    }

    private void PrintLabel() {
        PrintLabelSettingActivity.actionTo(this);
    }

    private void showChangePasswordPannel() {
        Intent intent = new Intent(this,ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void showAddressManagerPannel() {
        Intent intent = new Intent(this, MyAddressActivity.class);
        startActivity(intent);
    }

    private void showAboutAppPannel() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }

    private void takeoutLogin() {
        //editor.putBoolean("firstStart",true);
        editor.putString("userName", "");
        editor.putString("password", "");
        editor.putBoolean("update", false);
        editor.commit();
        EventBus.getDefault().post(new TargetEvent(111));
        Intent intent = new Intent(this, LoginByCodeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String type = data.getStringExtra("type");
            tv_setting_urge_type.setText(type);
        }
    }
}
