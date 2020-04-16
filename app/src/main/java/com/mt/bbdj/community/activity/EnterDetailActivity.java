package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.EnterFailureFragment;
import com.mt.bbdj.community.fragment.EnterSuccessFragment;

import java.util.ArrayList;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import me.yokeyword.fragmentation.SupportActivity;


public class EnterDetailActivity extends SupportActivity {

    private ViewPager viewPager;
    private SlidingTabLayout slt_title;
    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private SimpleFragmentPagerAdapter pagerAdapter;
    private int currentItem = 2;
    private RelativeLayout iv_back;
    private String courier_id;

    public static void actionTo(Context context){
        Intent intent = new Intent(context,EnterDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_detail);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(EnterDetailActivity.this);
        initView();
        initParams();
        initClickListener();
    }

    private void initParams() {
        UserBaseMessageDao userMessageDao = GreenDaoManager.getInstance().getSession().getUserBaseMessageDao();
        List<UserBaseMessage> userMessages = userMessageDao.queryBuilder().list();
        if (userMessages.size() != 0) {
            UserBaseMessage mUserMessage = userMessages.get(0);
            courier_id = mUserMessage.getUser_id();
        }
    }

    private void initClickListener() {
        iv_back.setOnClickListener(v -> finish());
    }

    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        iv_back = findViewById(R.id.iv_back);
        slt_title = findViewById(R.id.slt_title);
        list_fragment.clear();
        list_fragment.add(EnterSuccessFragment.getInstance(courier_id));
        //list_fragment.add(EnterHandleFragment.getInstance(courier_id));
        list_fragment.add(EnterFailureFragment.getInstance(courier_id));

        list_title.clear();
        list_title.add("入库成功");
        //list_title.add("处理中");
        list_title.add("待处理");
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, list_fragment, list_title);
        viewPager.setAdapter(pagerAdapter);
        slt_title.setViewPager(viewPager);
        slt_title.setCurrentTab(currentItem);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
