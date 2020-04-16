package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.BarHorizontalChart;
import com.mt.bbdj.baseconfig.view.NumberFormatter;
import com.openxu.cview.chart.bean.BarBean;
import com.openxu.utils.DensityUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2019/1/29
 * Description :   日报
 */
public class MonthFromsFragment extends BaseFragment {

    Unbinder unbinder;
    private String user_id;
    private RequestQueue mRequestQueue;
    private TextView tvSendNumber;    //寄件
    private TextView tvPaiNumber;   //派件
    private TextView tvServiceNumber;   //服务数

    private BarHorizontalChart chart;
    private TextView tvCurrentTime;        //

    private String startTime, endTime;     //开始时间和结束时间的时间戳


    private View view;

    private TextView dateTitle;   //统计时间标题

    private final int REQUEST_DATE_REPORT = 100;     //请求日报数据


    private List<HashMap<String, String>> mList = new ArrayList<>();

    private HorizontalBarChart barChart;
    private List<HashMap<String, String>> PackageOrder = new ArrayList<HashMap<String, String>>();
    private TimePickerView timePickerDate;
    private List<List<BarBean>> chartData = new ArrayList<>();
    private List<String> strXList = new ArrayList<>();
    private boolean isGetData = false;

    public static MonthFromsFragment getInstance() {
        MonthFromsFragment bf = new MonthFromsFragment();
        return bf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_froms_month, container, false);
        unbinder = ButterKnife.bind(this, view);
        initParams();
        initView(view);
        initDialog();
        initListener();
      //  requestData();
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter&& !isGetData) {
            isGetData = true;
            requestData();
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onPause() {
        super.onPause();
        isGetData = false;
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getReportByMonthRequest(user_id, startTime,endTime);
        mRequestQueue.add(REQUEST_DATE_REPORT, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MonthFromsFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    JSONObject data = jsonObject.getJSONObject("data");
                    if ("5001".equals(code)) {
                        handleResult(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void handleResult(JSONObject data) throws JSONException {
        String mailsum = data.getString("mailsum");
        String piesum = data.getString("piesum");
        String servicesum = data.getString("servicesum");
        strXList.clear();
        chartData.clear();
        tvSendNumber.setText(mailsum);
        tvPaiNumber.setText(piesum);
        tvServiceNumber.setText(servicesum);
        JSONArray compdata = data.getJSONArray("compdata");
        for (int i = 0;i < compdata.length();i++) {
            JSONArray jsonArray = compdata.getJSONArray(i);
            String send = jsonArray.getString(0);     //寄件
            String pai = jsonArray.getString(1);      //派件
            String service = jsonArray.getString(2);     //服务
            String time = jsonArray.getString(3);     //时间
            //String standTime = DateUtil.changeStampToStandrdTime("M-dd",time);

            float sendNumber = Float.parseFloat(send);
            float paiNumber = Float.parseFloat(pai);
            float serviceNumber = Float.parseFloat(service);

            List<BarBean> list = new ArrayList<>();
            list.add(new BarBean(sendNumber, "lable1"));
            list.add(new BarBean(paiNumber, "lable2"));
            list.add(new BarBean(serviceNumber, "lable3"));
            chartData.add(list);
            strXList.add(i+1+"月");
        }
        setChartDate(strXList, chartData);
    }

    private void initListener() {
        tvCurrentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDate.show();
            }
        });
    }


    private void initView(View view) {
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvSendNumber = view.findViewById(R.id.tv_send_number);
        tvPaiNumber = view.findViewById(R.id.tv_pai_number);
        tvServiceNumber = view.findViewById(R.id.tv_service_number);
        dateTitle = view.findViewById(R.id.tv_date_title);

        String year = DateUtil.getYear()+"";

        dateTitle.setText(year+"年1月-"+year+"年12月");

        tvCurrentTime.setText(DateUtil.getCurrentTimeFormat("yyyy年MM月"));

        endTime = DateUtil.getSomeDayStamp(DateUtil.getCurrentMonthLastDate());
        startTime = DateUtil.getSomeDayStamp(DateUtil.getCurrentMonthFirstDate());
        barChart = view.findViewById(R.id.chart_barchart);
        chart = view.findViewById(R.id.chart);
        chart.setBarSpace(DensityUtil.dip2px(getActivity(), 1));  //双柱间距
        chart.setBarItemSpace(DensityUtil.dip2px(getActivity(), 10));  //柱间距
        chart.setDebug(false);
        chart.setBarNum(3);
        chart.setBarColor(new int[]{Color.parseColor("#F6B23B"), Color.parseColor("#57A7E8"), Color.parseColor("#84E76A")});
    }

    private void setChartDate(List<String> strXList, List<List<BarBean>> dataList) {
        chart.setLoading(false);
        chart.setData(dataList, strXList);
    }



    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();

    }


    private void initDialog() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        //startDate.set(2013,1,1);
        Calendar endDate = Calendar.getInstance();
        //endDate.set(2020,1,1);

        String yearStr = DateUtil.yearDate();
        int year = Integer.parseInt(yearStr);

        int month = DateUtil.monthDate();

        int date = DateUtil.currentDate();

        //正确设置方式 原因：注意事项有说明
        startDate.set(2010, 0, 1);
        endDate.set(year, month, date);

        timePickerDate = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String yearStr = DateUtil.getStrDate(date, "yyyy");
                String monthStr = DateUtil.getStrDate(date, "MM");
                int yearInt = Integer.parseInt(yearStr);
                int monthInt = Integer.parseInt(monthStr);
                String first = DateUtil.getFisrtDayOfMonth(yearInt,monthInt);
                String last = DateUtil.getLastDayOfMonth(yearInt,monthInt);
                startTime = DateUtil.getSomeDayStamp(first+" 00:00:00");
                endTime = DateUtil.getSomeDayStamp(last+" 23:59:59");
                tvCurrentTime.setText(DateUtil.getStrDate(date, "yyyy年MM月"));
                requestData();
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerDate.returnData();
                                timePickerDate.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerDate.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(18)
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.5f)
                //  .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
