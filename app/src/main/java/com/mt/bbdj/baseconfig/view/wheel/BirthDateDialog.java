package com.mt.bbdj.baseconfig.view.wheel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


import com.mt.bbdj.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Administrator
 * 
 */
public class BirthDateDialog extends Dialog implements
		android.view.View.OnClickListener {
	/**
	 */
	public interface PriorityListener {
		/**
		 * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
		 */
		public void refreshPriorityUI(String year, String month, String day,
                                      String hours, String mins);
	}

	private PriorityListener lis;
	private boolean scrolling = false;
	private Context context = null;
	public Button softInfo = null;
	public Button softInfoButton = null;
	private NumericWheelAdapter year_adapter = null;
	private NumericWheelAdapter month_adapter = null;
	private NumericWheelAdapter day_adapter = null;
	private NumericWheelAdapter hours_adapter = null;
	private NumericWheelAdapter mins_adapter = null;
	private NumericWheelAdapter seconds_adapter = null;
	private Button btn_sure = null;
	private Button btn_cancel = null;
	private int curYear = 0;
	private int curMonth = 0;
	private int curDay = 0;
	private int hours = 0;
	private int mins = 0;
	private int seconds = 0;
	private WheelView monthview = null;
	private WheelView yearview = null;
	private WheelView dayview = null;
	private WheelView hoursview = null;
	private WheelView minsview = null;
	private WheelView secondsview = null;
	private static int theme = R.style.myDialog;// 主题
	private LinearLayout date_layout;
	private int width, height;// 对话框宽高
	private TextView title_tv;
	private String title;

	public BirthDateDialog(final Context context,
                           final PriorityListener listener, int currentyear, int currentmonth,
                           int currentday, int hours, int mins, int seconds, int width,
                           int height, String title) {
		super(context, theme);
		this.context = context;
		lis = listener;
		this.curYear = currentyear;
		this.curMonth = currentmonth;
		this.curDay = currentday;
		this.width = width;
		this.title = title;
		this.hours = hours;
		this.mins = mins;
		this.height = height;
		this.seconds = seconds;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_select_wheel);
		btn_sure = (Button) findViewById(R.id.confirm_btn);
		btn_sure.setOnClickListener(this);
		btn_cancel = (Button) findViewById(R.id.cancel_btn);
		btn_cancel.setOnClickListener(this);
		date_layout = (LinearLayout) findViewById(R.id.date_selelct_layout);
		LayoutParams lparams_hours = new LayoutParams(width, height / 3 + 10);
		date_layout.setLayoutParams(lparams_hours);
		title_tv = (TextView) findViewById(R.id.diaolog_title_tv);
		title_tv.setText(title);
		yearview = (WheelView) findViewById(R.id.year);
		monthview = (WheelView) findViewById(R.id.month);
		dayview = (WheelView) findViewById(R.id.day);
		hoursview = (WheelView) findViewById(R.id.hours);
		minsview = (WheelView) findViewById(R.id.mins);
		secondsview = (WheelView) findViewById(R.id.seconds);
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateDays(yearview, monthview, dayview);
				}
			}
		};
		monthview.addChangingListener(listener);
		yearview.addChangingListener(listener);
		Calendar calendar = Calendar.getInstance();
		if (this.curYear == 0 || this.curMonth == 0) {
			curYear = calendar.get(Calendar.YEAR);
			curMonth = calendar.get(Calendar.MONTH) + 1;
			curDay = calendar.get(Calendar.DAY_OF_MONTH);
		}
		// 初始化数�?�?
		year_adapter = new NumericWheelAdapter(1900, 2100);
		yearview.setAdapter(year_adapter);
		int cc = curYear - 1900;// 按下标来�?
		yearview.setCurrentItem(cc);// 传�?过去的是下标
		yearview.setVisibleItems(5);
		month_adapter = new NumericWheelAdapter(1, 12, "%02d");
		monthview.setAdapter(month_adapter);
		monthview.setCurrentItem(curMonth - 1);
		monthview.setCyclic(false);
		monthview.setVisibleItems(5);
		updateDays(yearview, monthview, dayview);
		dayview.setCyclic(false);
		dayview.setVisibleItems(5);
		hours_adapter = new NumericWheelAdapter(0, 23, "%02d");
		hoursview.setAdapter(hours_adapter);
		hoursview.setCurrentItem(hours);
		hoursview.setCyclic(false);
		hoursview.setVisibleItems(5);
		mins_adapter = new NumericWheelAdapter(0, 59, "%02d");
		minsview.setAdapter(mins_adapter);
		minsview.setCurrentItem(mins);
		minsview.setCyclic(false);
		minsview.setVisibleItems(5);
		seconds_adapter = new NumericWheelAdapter(0, 59, "%02d");
		secondsview.setAdapter(seconds_adapter);
		secondsview.setCurrentItem(seconds);
		secondsview.setCyclic(false);
		secondsview.setVisibleItems(5);
	}

	/**
	 * 根据年份和月份来更新日期
	 */
	private void updateDays(WheelView year, WheelView month, WheelView day) {
		// 添加大小月月份并将其转换为list,方便之后的判�?
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);
		int year_num = year.getCurrentItem() + 1900;
		// 判断大小月及是否闰年,用来确定"�?的数�?
		if (list_big.contains(String.valueOf(month.getCurrentItem() + 1))) {
			day_adapter = new NumericWheelAdapter(1, 31, "%02d");
		} else if (list_little
				.contains(String.valueOf(month.getCurrentItem() + 1))) {
			day_adapter = new NumericWheelAdapter(1, 30, "%02d");
		} else {
			if ((year_num % 4 == 0 && year_num % 100 != 0)
					|| year_num % 400 == 0)
				day_adapter = new NumericWheelAdapter(1, 29, "%02d");
			else
				day_adapter = new NumericWheelAdapter(1, 28, "%02d");
		}
		dayview.setAdapter(day_adapter);
		dayview.setCurrentItem(curDay - 1);
	}

	public BirthDateDialog(Context context, PriorityListener listener) {
		super(context, theme);
		this.context = context;
	}

	public BirthDateDialog(Context context, String birthDate) {
		super(context, theme);
		this.context = context;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm_btn:
			lis.refreshPriorityUI(year_adapter.getValues(),
					month_adapter.getValues(), day_adapter.getValues(),
					hours_adapter.getValues(), mins_adapter.getValues());

			this.dismiss();
			break;
		case R.id.cancel_btn:
			this.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
