package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.CouponUserFragment;

import java.util.ArrayList;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class CouponUseDetailActivity extends BaseActivity {
    private RelativeLayout rl_back;

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private String user_id;
    private String coupon_id;

    public static void actionTo(Context context, String user_id, String coupon_id) {
        Intent intent = new Intent(context, CouponUseDetailActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("coupon_id", coupon_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_use_detail);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(CouponUseDetailActivity.this);
        initParams();
        initFragment();
        initView();
    }

    private void initView() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        RelativeLayout rl_back = findViewById(R.id.rl_back);
        SlidingTabLayout sltTitle = findViewById(R.id.tl_4);
        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                this, list_fragment, list_title);
        viewPager.setAdapter(pagerAdapter);
        sltTitle.setViewPager(viewPager);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initFragment() {
        LoadDialogUtils.cannelLoadingDialog();
        list_fragment.clear();
        list_fragment.add(new CouponUserFragment(user_id, "2", coupon_id));    //未领取
        list_fragment.add(new CouponUserFragment(user_id, "1", coupon_id));    //已领取
        list_fragment.add(new CouponUserFragment(user_id, "3", coupon_id));    //已使用
        list_title.clear();
        list_title.add("未领取");
        list_title.add("已领取");
        list_title.add("已使用");
    }

    private void initParams() {
        coupon_id = getIntent().getStringExtra("coupon_id");
        user_id = getIntent().getStringExtra("user_id");

    }

}
