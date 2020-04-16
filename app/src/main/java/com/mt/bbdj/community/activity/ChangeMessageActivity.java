package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.tech.NfcA;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginActivity;
import com.mt.bbdj.baseconfig.db.City;
import com.mt.bbdj.baseconfig.db.County;
import com.mt.bbdj.baseconfig.db.Province;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.JsonBean;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.FastmailMessageAdapter;
import com.mt.bbdj.community.adapter.HistoryAddressAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//修改地址
public class ChangeMessageActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    RelativeLayout ivBack;        //返回
    @BindView(R.id.tv_address_manager)
    TextView tvAddressManager;      //标题
    @BindView(R.id.et_input_name)
    EditText etInputName;           //输入姓名
    @BindView(R.id.et_input_phone)
    EditText etInputPhone;          //输入电话
    @BindView(R.id.tv_select_genel_address)
    TextView tvSelectGenelAddress;    //选择大致地址
    @BindView(R.id.tv_select_arrow)
    ImageView tvSelectArrow;        //选择箭头
    @BindView(R.id.tv_select_detail_address)
    EditText tvSelectDetailAddress;     //详细地址
    @BindView(R.id.bt_save_address)
    Button btSaveAddress;      //保存按钮
    @BindView(R.id.rl_address_list)
    XRecyclerView rlAddressList;     //列表

    @BindView(R.id.tv_clear)
    TextView tv_clear;    //清除
    @BindView(R.id.bt_decode)
    Button bt_decode;    //解析
    @BindView(R.id.et_decode_message)
    EditText et_decode_message;
    @BindView(R.id.ll_record_address)
    LinearLayout ll_record_address;


    private Intent mIntent;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private ProvinceDao mProvinceDao;     //省
    private CityDao mCityDao;     //市
    private CountyDao mCountyDao;   //县

    private String book_id;    //地址id

    private ArrayList<JsonBean> options1Items = new ArrayList<>();    //省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();   //市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();     //县/区

    private List<Province> provincesList = new ArrayList<Province>();
    private List<City> citysList = new ArrayList<City>();
    private List<County> areasList = new ArrayList<County>();

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private String mProvince="";
    private String mCity="";
    private String mCountry="";


    private final int TYPE_CHANGE_ADDRESS = 1;    //修改地址
    private final int TYPE_ADD_ADDRESS = 2;    //新添地址
    private final int TYPE_DECODE_ADDRESS = 3;    //新添地址
    private final int TYPE_GET_ADDRESS = 4;   //获取地址列表

    private boolean isChange = false;    // false : 添加  true : 修改
    private int mType;
    private HistoryAddressAdapter messageAdapter;
    private List<HashMap<String,String>> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_message);
        ButterKnife.bind(this);
        initParams();
        initData();
        initAreaData();
        initHistoryAddress();   //获取历史地址
    }

    private void initHistoryAddress() {
        mList = new ArrayList<>();
        messageAdapter = new HistoryAddressAdapter(mList);
        rlAddressList.setFocusable(false);
        rlAddressList.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rlAddressList.setLayoutManager(mLayoutManager);
        rlAddressList.setAdapter(messageAdapter);
        requestData();
        initListener();
    }

    private void initListener() {
        messageAdapter.setItemClickListener(new HistoryAddressAdapter.OnItemSelectClickListener() {
            @Override
            public void onItemSelectClick(int position) {
                Intent intent = new Intent();
                HashMap<String,String> item = mList.get(position);
                intent.putExtra("book_id",item.get("book_id"));
                intent.putExtra("book_name",item.get("book_name"));
                intent.putExtra("book_telephone",item.get("book_telephone"));
                intent.putExtra("book_region",item.get("book_region"));
                intent.putExtra("book_address",item.get("book_address"));
                intent.putExtra("book_province",item.get("book_province"));
                intent.putExtra("book_city",item.get("book_city"));
                intent.putExtra("book_area",item.get("book_area"));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void initParams() {
        mIntent = getIntent();
        book_id = mIntent.getStringExtra("address_id");
        mType = mIntent.getIntExtra("type", 1);
        if (null == book_id || "".equals(book_id)) {
            isChange = false;
           // ll_record_address.setVisibility(View.GONE);
        } else {
            isChange = true;
           // ll_record_address.setVisibility(View.VISIBLE);
        }
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mProvinceDao = mDaoSession.getProvinceDao();
        mCityDao = mDaoSession.getCityDao();
        mCountyDao = mDaoSession.getCountyDao();

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(ChangeMessageActivity.this, "提交中...");
    }

    private void initData() {
        etInputName.setText(getIntent().getStringExtra("book_name"));
        etInputPhone.setText(getIntent().getStringExtra("book_telephone"));
        tvSelectGenelAddress.setText(getIntent().getStringExtra("book_region"));
        tvSelectDetailAddress.setText(getIntent().getStringExtra("book_address"));
        mProvince = getIntent().getStringExtra("book_province");
        mCity = getIntent().getStringExtra("book_city");
        mCountry = getIntent().getStringExtra("book_area");
        //book_id
    }

    @OnClick({R.id.iv_back, R.id.tv_select_genel_address, R.id.tv_select_arrow, R.id.bt_save_address,
            R.id.tv_clear, R.id.bt_decode,R.id.et_decode_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_select_genel_address:     //选择按钮
            case R.id.tv_select_arrow:
                selectGenelAddress();
                break;
            case R.id.bt_save_address:       //保存
                handleMessageEvent();
                break;
            case R.id.tv_clear:
                et_decode_message.setText("");
                break;
            case R.id.bt_decode:
                decodeMessage();
                break;
        }
    }

    private void decodeMessage() {
        String message = et_decode_message.getText().toString();
        if ("".equals(message)) {
            ToastUtil.showShort("内容不可为空！");
            return ;
        }
        Request<String> request = NoHttpRequest.decodeMessage(message);
        mRequestQueue.add(TYPE_DECODE_ADDRESS, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONArray itemsArray = result.getJSONArray("items");
                    if (itemsArray.length() == 0) {
                        ToastUtil.showShort("解析出错！");
                    }else {
                        JSONObject item = itemsArray.getJSONObject(0);
                        mProvince = item.getString("province");
                        mCity = item.getString("city");
                        String phone = item.getString("phone");
                        mCountry = item.getString("district");
                        String name = item.getString("name");
                        String address = item.getString("address");
                        tvSelectGenelAddress.setText(mProvince + mCity + mCountry);
                        tvSelectDetailAddress.setText(address);
                        etInputName.setText(name);
                        etInputPhone.setText(phone);
                        et_decode_message.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                ToastUtil.showShort(response.get());
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    private void requestData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getStageAddressRequest(user_id,mType+"");
        mRequestQueue.add(TYPE_GET_ADDRESS, request, mResponseListener);
    }


    private void handleMessageEvent() {
        //表示修改地址
        if (isChange) {
            changAddress();
        } else {
            //表示添加地址
            saveAddress();
        }
    }

    private void saveAddress() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        String realName = etInputName.getText().toString();
        String telephone = etInputPhone.getText().toString();
        String region = tvSelectGenelAddress.getText().toString();
        String address = tvSelectDetailAddress.getText().toString();

        //判断条件是否正确
        if (!isRightAboutMessage(realName, telephone, region, address)) {
            return;
        }
        Request<String> request = NoHttpRequest.addAddressBook(user_id, realName, telephone, mProvince, mCity, mCountry, address, mType + "");
        mRequestQueue.add(TYPE_ADD_ADDRESS, request, mResponseListener);
    }

    private void changAddress() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        String realName = etInputName.getText().toString();
        String telephone = etInputPhone.getText().toString();
        String region = tvSelectGenelAddress.getText().toString();
        String address = tvSelectDetailAddress.getText().toString();

        //判断条件是否正确
        if (!isRightAboutMessage(realName, telephone, region, address)) {
            return;
        }
        Request<String> request = NoHttpRequest.changeAddressBook(user_id, realName, telephone, mProvince, mCity, mCountry, address, book_id);
        mRequestQueue.add(TYPE_CHANGE_ADDRESS, request, mResponseListener);
    }


    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            if (what == TYPE_CHANGE_ADDRESS) {
                dialogLoading.show();
            }

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ChangeMessageActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    hanldeResult(what,jsonObject);
                }
                if (what == TYPE_CHANGE_ADDRESS || what == TYPE_ADD_ADDRESS) {
                    ToastUtil.showShort(msg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                dialogLoading.cancel();
                ToastUtil.showShort("提交失败，请重试！");
            }
            dialogLoading.cancel();
        }


        @Override
        public void onFailed(int what, Response<String> response) {
            dialogLoading.cancel();
        }

        @Override
        public void onFinish(int what) {
            dialogLoading.cancel();
        }
    };

    private void hanldeResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case TYPE_CHANGE_ADDRESS:    //修改地址
                handleChangeAddress(jsonObject);
                break;
            case TYPE_GET_ADDRESS:     //获取历史地址列表
                handleHistoryAddress(jsonObject);
                break;
            case TYPE_ADD_ADDRESS:    //添加地址
                handleSaveAddress(jsonObject);
                break;
        }
    }

    private void handleSaveAddress(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String book_id = data.getString("book_id");
        Intent intent = new Intent();
        intent.putExtra("book_id", book_id);
        intent.putExtra("book_name", etInputName.getText().toString());
        intent.putExtra("book_telephone", etInputPhone.getText().toString());
        intent.putExtra("book_region", tvSelectGenelAddress.getText().toString());
        intent.putExtra("book_address", tvSelectDetailAddress.getText().toString());
        intent.putExtra("book_province", mProvince);
        intent.putExtra("book_city", mCity);
        intent.putExtra("book_area", mCountry);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void handleHistoryAddress(JSONObject jsonObject) throws JSONException {
        mList.clear();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0;i < jsonArray.length();i++) {
            HashMap<String,String> map = new HashMap<>();
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String book_id = jsonObject1.getString("book_id");
            String book_name = jsonObject1.getString("book_name");
            String book_telephone = jsonObject1.getString("book_telephone");
            String book_region = jsonObject1.getString("book_region");
            String book_address = jsonObject1.getString("book_address");

            String book_province = jsonObject1.getString("book_province");
            String book_city = jsonObject1.getString("book_city");
            String book_area = jsonObject1.getString("book_area");


            if ("".equals(book_region) || "null".equals(book_region)
                    || "".equals(book_address) || "null".equals(book_address)) {
                book_region = "无";
                book_address = "";
            }
            map.put("book_id",book_id);
            map.put("book_name",book_name);
            map.put("book_telephone",book_telephone);
            map.put("book_region",book_region);
            map.put("book_address",book_address);

            map.put("book_province",book_province);
            map.put("book_city",book_city);
            map.put("book_area",book_area);
            mList.add(map);
            map = null;
        }
        messageAdapter.notifyDataSetChanged();
    }

    private void handleChangeAddress(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String book_id = data.getString("book_id");
        Intent intent = new Intent();
        intent.putExtra("book_id", book_id);
        intent.putExtra("book_name", etInputName.getText().toString());
        intent.putExtra("book_telephone", etInputPhone.getText().toString());
        intent.putExtra("book_region", tvSelectGenelAddress.getText().toString());
        intent.putExtra("book_address", tvSelectDetailAddress.getText().toString());
        intent.putExtra("book_province", mProvince);
        intent.putExtra("book_city", mCity);
        intent.putExtra("book_area", mCountry);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isRightAboutMessage(String realName, String telephone, String region, String address) {
        if ("".equals(realName)) {
            ToastUtil.showShort("姓名不可为空！");
            return false;
        }
        if ("".equals(telephone)) {
            ToastUtil.showShort("手机号码不可为空！");
            return false;
        }
       /* if (!StringUtil.isMobile(telephone)) {
            ToastUtil.showShort("手机号码不合法！");
            return false;
        }*/
        if ("".equals(region) || "".equals(address)) {
            ToastUtil.showShort("地址信息不完善！");
            return false;
        }
        return true;
    }

    private void selectGenelAddress() {
        if (options1Items.size() > 0 && options2Items.size() > 0 && options3Items.size() > 0) {
            showPickerView();
        }
    }

    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mProvince = options1Items.get(options1).getPickerViewText();
                mCity = options2Items.get(options1).get(options2);
                mCountry = options3Items.get(options1).get(options2).get(options3);
                tvSelectGenelAddress.setText(mProvince + mCity + mCountry);
            }
        }).setSelectOptions(0, 0, 0)  //设置默认选中项
                .setTitleText("地区选择")
                .setTitleSize(16)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(16)
                .setLineSpacingMultiplier(2.5f)
                .setCancelColor(Color.parseColor("#0da95f"))
                .setSubmitColor(Color.parseColor("#0da95f"))
                .build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initAreaData() {
        ArrayList<JsonBean> jsonBean = new ArrayList<>();
        provincesList = mProvinceDao.queryBuilder().list();
        for (int i = 0; i < provincesList.size(); i++) {
            JsonBean item = new JsonBean();
            item.setName(provincesList.get(i).getRegion_name());
            item.setProvince(provincesList.get(i).getId());
            List<JsonBean.CityBean> cityBeansList = new ArrayList<>();
            citysList = mCityDao.queryBuilder().where(CityDao.Properties.Parent_id.eq(provincesList.get(i).getId())).list();
            for (int il = 0; il < citysList.size(); il++) {
                JsonBean.CityBean item1 = new JsonBean.CityBean();
                item1.setName(citysList.get(il).getRegion_name());
                item1.setCity(citysList.get(il).getId());
                List<String> area = new ArrayList<>();
                List<String> areaId = new ArrayList<>();
                areasList = mCountyDao.queryBuilder().where(CountyDao.Properties.Parent_id.eq(citysList.get(il).getId())).list();
                for (int i2 = 0; i2 < areasList.size(); i2++) {
                    area.add(areasList.get(i2).getRegion_name());
                    areaId.add(areasList.get(i2).getId());
                }
                if (area.size() < 0) {
                    area.add(citysList.get(il).getRegion_name());
                    areaId.add(citysList.get(il).getId());
                }
                item1.setArea(area);
                item1.setAreaId(areaId);
                cityBeansList.add(item1);
            }
            if (citysList.size() <= 0) {
                JsonBean.CityBean item1 = new JsonBean.CityBean();
                item1.setName(provincesList.get(i).getRegion_name());
                item1.setCity(provincesList.get(i).getId());
                List<String> area = new ArrayList<>();
                List<String> areaId = new ArrayList<>();

                area.add(provincesList.get(i).getRegion_name());
                areaId.add(provincesList.get(i).getId());


                item1.setArea(area);
                item1.setAreaId(areaId);
                cityBeansList.add(item1);
            }
            item.setCityList(cityBeansList);
            jsonBean.add(item);
        }
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {   //遍历省份
            ArrayList<String> cityList = new ArrayList<>();     //该省的城市列表
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();    //该省的所有地区列表

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {   //遍历该省的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);   //添加城市
                ArrayList<String> city_ArrayList = new ArrayList<>();    //该城市的所有地区列表

                //若是无地区数据，天剑空字符串，放置数据为null 导致三个选型的长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null ||
                        jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_ArrayList.add("");
                } else {
                    city_ArrayList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                province_AreaList.add(city_ArrayList);   //添加该省所有地区数据
            }

            //添加城市数据
            options2Items.add(cityList);

            //添加地区数据
            options3Items.add(province_AreaList);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);

            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
