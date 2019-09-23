package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
 import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.widget.DateChooseWheelViewDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class SetWayMoneyActivity extends BaseActivity {

    private TagFlowLayout tagFlowLayoutOne, tagFlowLayoutTwo, tagFlowLayoutThree;
    private PopupWindow popupWindow;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private final int REQUEST_REQUEST_WAY_MONEY = 100;
    private final int REQUEST_REQUEST_COMMIT_SETTING = 101;
    private LayoutInflater mInflater;
    private ImageView tv_back;
    private Button bt_save;
    private TextView tv_start_time,tv_end_time;

    private String term_id = "";
    private String freight_id = "";
    private String starting_id = "";

    public static void actionTo(Context context) {
        Intent intent = new Intent(context, SetWayMoneyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_way_number);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SetWayMoneyActivity.this);
        initView();
        initParams();
        requestData();   //请求数据
        initListener();
    }



    private void initListener() {
        //返回
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //保存
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitSetting();
            }
        });

        //起送价格
        tagFlowLayoutThree.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                starting_id = listThree.get(position).get("starting_id");
                return true;
            }
        });

        //运费
        tagFlowLayoutTwo.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                freight_id = listTwo.get(position).get("freight_id");
                return true;
            }
        });

        //免运费限度
        tagFlowLayoutOne.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                term_id = listOne.get(position).get("term_id");
                return true;
            }
        });

        //开始时间
        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeSelectDialog(1);
            }
        });

        //结束时间
        tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeSelectDialog(2);
            }
        });
    }

    private void showTimeSelectDialog(int type) {
        DateChooseWheelViewDialog endDateChooseDialog = new DateChooseWheelViewDialog(SetWayMoneyActivity.this,
                new DateChooseWheelViewDialog.DateChooseInterface() {
                    @Override
                    public void getDateTime(String time, boolean longTimeChecked) {
                        if (type == 1) {
                            tv_start_time.setText(time);
                        } else {
                            tv_end_time.setText(time);
                        }
                    }
                });
        endDateChooseDialog.setTimePickerGone(true);
        endDateChooseDialog.setDateDialogTitle(type==1?"开始时间":"结束时间");
        endDateChooseDialog.showDateChooseDialog();
    }


    private void commitSetting() {
        String end_time = tv_end_time.getText().toString();
        String start_time = tv_start_time.getText().toString();
        if ("".equals(freight_id) || "".equals(term_id) || "".equals(starting_id)) {
            ToastUtil.showShort("请完善设置");
        } else if ("".equals(end_time) || "".equals(start_time)) {
            ToastUtil.showShort("请设置营业时间");
        }else {
            commitData();
        }
    }

    private void commitData() {
        Request<String> request = null;
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("term_id", term_id);
        map.put("freight_id", freight_id);
        map.put("starting_id", starting_id);
        map.put("start_time", tv_start_time.getText().toString());
        map.put("end_time", tv_end_time.getText().toString());
        request = NoHttpRequest.commitWayMoneySetting(user_id, map);
        mRequestQueue.add(REQUEST_REQUEST_COMMIT_SETTING, request, mResponseListener);
    }

    private void initParams() {

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        mInflater = getLayoutInflater();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }


    }

    private void requestData() {
        Request<String> request = null;
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        request = NoHttpRequest.requestWayMoney(user_id, map);
        mRequestQueue.add(REQUEST_REQUEST_WAY_MONEY, request, mResponseListener);
    }

    private void initView() {
        tagFlowLayoutOne = findViewById(R.id.id_free_one);
        tagFlowLayoutTwo = findViewById(R.id.id_free_two);
        tagFlowLayoutThree = findViewById(R.id.id_free_three);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_back = findViewById(R.id.tv_back);
        bt_save = findViewById(R.id.bt_save);
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(SetWayMoneyActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "LoginActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleEvent(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
                ToastUtil.showShort("请求失败！");
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            LoadDialogUtils.cannelLoadingDialog();
            ToastUtil.showShort("请求失败！");
        }

        @Override
        public void onFinish(int what) {
            //  LoadDialogUtils.cannelLoadingDialog();
        }
    };

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        if (what == REQUEST_REQUEST_WAY_MONEY) {
            //设置运费数据
            setWayNumberData(jsonObject);
        } else if (what == REQUEST_REQUEST_COMMIT_SETTING) {
            String msg = jsonObject.get("msg").toString();
            ToastUtil.showShort(msg);
            finish();
        }
    }

    private void setWayNumberData(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray term_list = data.getJSONArray("term_list");
        JSONArray freight_list = data.getJSONArray("freight_list");
        JSONArray starting_list = data.getJSONArray("starting_list");
        String start_time = data.getString("start_time");
        String end_time = data.getString("end_time");
        tv_start_time.setText(StringUtil.handleNullResultForString(start_time));
        tv_end_time.setText(StringUtil.handleNullResultForString(end_time));
        setListOne(term_list);
        setListTwo(freight_list);
        setListThree(starting_list);
    }

    List<HashMap<String, String>> listThree = new ArrayList<>();

    private void setListThree(JSONArray starting_list) throws JSONException {
        int position = 0;
        for (int i = 0; i < starting_list.length(); i++) {
            JSONObject entity = starting_list.getJSONObject(i);
            String term_id = entity.getString("starting_id");
            String title = entity.getString("title");
            String type = entity.getString("type");
            HashMap<String, String> map = new HashMap<>();
            if ("2".equals(type)) {
                position = i;
            }
            map.put("starting_id", term_id);
            map.put("title", title);
            map.put("type", type);
            listThree.add(map);
            map = null;
        }

        if (listThree.size() != 0) {
            starting_id = listThree.get(position).get("starting_id");
        }

        TagAdapter threeTagAdapter = new TagAdapter<HashMap<String, String>>(listThree) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_flow, tagFlowLayoutOne, false);
                tv.setText(map.get("title"));
                return tv;
            }
        };

        threeTagAdapter.setSelectedList(position);
        tagFlowLayoutThree.setAdapter(threeTagAdapter);
    }

    List<HashMap<String, String>> listTwo = new ArrayList<>();

    private void setListTwo(JSONArray freight_list) throws JSONException {
        int position = 0;
        for (int i = 0; i < freight_list.length(); i++) {
            JSONObject entity = freight_list.getJSONObject(i);
            String term_id = entity.getString("freight_id");
            String title = entity.getString("title");
            String type = entity.getString("type");
            HashMap<String, String> map = new HashMap<>();
            if ("2".equals(type)) {
                position = i;
            }
            map.put("freight_id", term_id);
            map.put("title", title);
            map.put("type", type);
            listTwo.add(map);
            map = null;
        }

        if (listTwo.size() != 0) {
            freight_id = listTwo.get(position).get("freight_id");
        }

        TagAdapter twoTagAdapter = new TagAdapter<HashMap<String, String>>(listTwo) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_flow, tagFlowLayoutOne, false);
                tv.setText(map.get("title"));
                return tv;
            }
        };
        twoTagAdapter.setSelectedList(position);
        tagFlowLayoutTwo.setAdapter(twoTagAdapter);

    }

    List<HashMap<String, String>> listOne = new ArrayList<>();

    private void setListOne(JSONArray term_list) throws JSONException {
        int position = 0;
        for (int i = 0; i < term_list.length(); i++) {
            JSONObject entity = term_list.getJSONObject(i);
            String term_id = entity.getString("term_id");
            String title = entity.getString("title");
            String type = entity.getString("type");
            if ("2".equals(type)) {
                position = i;
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("term_id", term_id);
            map.put("title", title);
            map.put("type", type);
            listOne.add(map);
            map = null;
        }

        if (listOne.size() != 0) {
            term_id = listOne.get(position).get("term_id");
        }

        TagAdapter oneTagAdapter = new TagAdapter<HashMap<String, String>>(listOne) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_flow, tagFlowLayoutOne, false);
                tv.setText(map.get("title"));
                return tv;
            }
        };

        oneTagAdapter.setSelectedList(position);
        tagFlowLayoutOne.setAdapter(oneTagAdapter);
    }
}
