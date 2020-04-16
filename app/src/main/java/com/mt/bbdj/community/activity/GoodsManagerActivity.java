package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Goods;
import com.mt.bbdj.baseconfig.model.GoodsManagerModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.GoodsManagerAdatpter;
import com.mt.bbdj.community.adapter.GoodsRackAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.params.ButtonParams;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class GoodsManagerActivity extends BaseActivity {

    @BindView(R.id.rl_goods_type)
    RecyclerView rl_goods_type;       //商品货架
    @BindView(R.id.rl_goods)
    RecyclerView rl_goods;    //商品类型
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.ll_add_goods_bottom)
    LinearLayout ll_add_goods_bottom;
    @BindView(R.id.ll_no_goods)
    LinearLayout ll_no_goods;
    @BindView(R.id.tv_no_goods)
    TextView tv_no_goods;
    @BindView(R.id.bt_add_goods_sherver)
    TextView bt_add_goods_sherver;
    @BindView(R.id.tv_manager_shelver)
    TextView tv_manager_shelver;      //重命名货架名称
    @BindView(R.id.tv_manager_delete)
    TextView tv_manager_delete;     //删除货架
    @BindView(R.id.ll_title)
    LinearLayout ll_title;

    private GoodsRackAdapter goodsRackAdapter;      //货架

    private GoodsManagerAdatpter goodsManagerAdatpter;    //商品
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;

    private String currentShelves_id = "";     //货架id

    private List<Goods> mList = new ArrayList<>();
    private List<GoodsManagerModel> mData = new ArrayList<>();
    private final int REQUEST_GOODS_TYPE = 100;         //获取货架
    private final int REQUEST_GOODS = 101;    //获取对应的商品
    private final int REQUEST_CHANGE_GOODS = 102;    //修改商品名称和价格
    private final int REQUEST_CHANGE_TOGGLE = 103;    //下架
    private final int REQUEST_DELETE_GOODS = 104;    //删除
    private final int REQUEST_ADD_SPECIAL_GOODS = 105;    //添加特价商品
    private final int REQUEST_DELETE_SHALVES = 106;    //删除货架
    private final int REQUEST_CHANGE_SHELVES_NAME = 107;    //重命名
    private final int REQUEST_SCAN_GOODS = 108;    //扫描二维码
    private final int REQUEST_ADD_SCANL_GOODS = 109;    //扫描商品信息
    private final int REQUEST_COMMIT_SCANL_GOODS = 110;    //提交商品
    private final int REQUEST_TAKE_PICTURE = 111;    //拍照
    private final int REQUEST_COMMIT_PICTURE = 112;    //上传图片

    private String picturePath = "/bbdj/picture";
    private String IMAGE_DIR = Environment.getExternalStorageDirectory() + "/bbdj/picture";
    private File f = new File(Environment.getExternalStorageDirectory(), picturePath);
    private File photoFile;

    private int currentPosition = 0;    //当前选择位置

    private PopupWindow popupWindow, changeNamePopu, changeShelvesNamePopu, changePricePopu, addgoodsWindow, specialPriceWindow, scanGoodsWindow;
    private EditText et_change_name;
    private EditText et_change_price;
    private GoodsManagerModel currentModel;
    private ImageView iv_image_;
    private TextView tv_goods_name_;
    private TextView tv_goods_price_;
    private TextView tv_goods_state;
    private TextView tv_special_start_time;
    private TextView tv_special_end_time;
    private EditText et_special_promotion_number;
    private TextView tv_special_goods_price;
    private EditText et_special_promotion_price;
    private AppCompatTextView tv_special_goods_name;
    private ImageView iv_special_goods_image;
    private LinearLayout ll_zero;
    private LinearLayout ll_zero_wrapper;
    private TextView et_change_shelves_name;
    private EditText et_scan_name;
    private TagFlowLayout rl_scan_goods_price;
    private EditText et_scan_price_self;
    private ImageView iv_scan_goods_picture;
    private LayoutInflater mInflater;
    private String goodsPrice = "";
    private ImageView iv_scan_delete;
    private String scanCode_id = "";
    private String mScanPrice = "";
    private String scanImageUrl = "";


    public static void actionTo(Context context) {
        Intent intent = new Intent(context, GoodsManagerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_manager);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(GoodsManagerActivity.this);
        ButterKnife.bind(this);
        initParams();
        initRecyclerView();
        initClickListener();

        initPopuStyle();   //初始化弹出框
        initChangeNamePopu();
        initChangeShelvesName();   //初始化货架弹框
        initChangePricePopu();
        initAddGoodsPopuStyle();     //添加商品的方式
        initSpecialPricePopu();        //特价商品
        initScanGoodsPopu();     //扫描添加界面

    }


    private void initChangeShelvesName() {
        if (changeShelvesNamePopu != null && changeShelvesNamePopu.isShowing()) {
            changeShelvesNamePopu.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.change_shelves_name, null);
            RelativeLayout iv_delete_shelves_name = selectView.findViewById(R.id.iv_delete_shelves_name);
            LinearLayout layout_close_shelves_name = selectView.findViewById(R.id.layout_close_shelves_name);
            RelativeLayout iv_comfirm_shelves_name = selectView.findViewById(R.id.iv_comfirm_shelves_name);
            et_change_shelves_name = selectView.findViewById(R.id.et_change_shelves_name);
            et_change_shelves_name.setText("");
            iv_delete_shelves_name.setOnClickListener(viewClicklistener);
            iv_comfirm_shelves_name.setOnClickListener(viewClicklistener);
            et_change_name.setOnClickListener(viewClicklistener);
            layout_close_shelves_name.setOnClickListener(viewClicklistener);

            changeShelvesNamePopu = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            changeShelvesNamePopu.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            changeShelvesNamePopu.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            changeShelvesNamePopu.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            changeShelvesNamePopu.setTouchable(true); // 设置popupwindow可点击
            changeShelvesNamePopu.setOutsideTouchable(true); // 设置popupwindow外部可点击
            changeShelvesNamePopu.setFocusable(true); // 获取焦点
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestGoodsType();     //请求商品货架

    }


    private void initClickListener() {
        //左侧导航栏
        goodsRackAdapter.setOnClickListener(new GoodsRackAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    ll_add_goods_bottom.setVisibility(View.GONE);
                    ll_title.setVisibility(View.GONE);
                } else {
                    ll_add_goods_bottom.setVisibility(View.VISIBLE);
                    ll_title.setVisibility(View.VISIBLE);
                }

                tv_no_goods.setText("请为【" + mList.get(position).getShelces_name() + "】货架添加商品");
                currentPosition = position;
                String shelves_id = mList.get(position).getShelves_id();
                currentShelves_id = shelves_id;
                goodsRackAdapter.setClickPosition(position);
                goodsRackAdapter.notifyDataSetChanged();
                requestGoods(shelves_id);    //请求对应的商品
            }
        });


        //右侧商品
        goodsManagerAdatpter.setOnItemClickListener(new GoodsManagerAdatpter.OnItemClickListener() {
            @Override
            public void onManagerListener(int position) {
                currentModel = mData.get(position);
                showSelectDialog();
            }
        });

        //添加商品货架
        bt_add_goods_sherver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddShelvesActivity.actionTo(GoodsManagerActivity.this, user_id);
            }
        });

        //重命名货架
        tv_manager_shelver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRenameShelves();
            }
        });

        //删除货架
        tv_manager_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });

    }

    private void showRenameShelves() {
        et_change_shelves_name.setText(mList.get(currentPosition).getShelces_name());
        if (changeShelvesNamePopu != null && !changeShelvesNamePopu.isShowing()) {
            changeShelvesNamePopu.showAtLocation(rl_goods, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void showDeleteDialog() {
        new CircleDialog.Builder()
                .setTitle("提示")
                .setText("\n确定删除【" + mList.get(currentPosition).getShelces_name() + "】货架吗?\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteShelves();
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
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
        ll_add_goods_bottom.setVisibility(View.GONE);
        ll_title.setVisibility(View.GONE);
    }

    private void requestGoodsType() {
        Request<String> request = null;
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        request = NoHttpRequest.requstStoreShelves(user_id, map);
        mRequestQueue.add(REQUEST_GOODS_TYPE, request, mResponseListener);
    }

    private void initRecyclerView() {
        goodsRackAdapter = new GoodsRackAdapter(mList);
        rl_goods_type.setAdapter(goodsRackAdapter);
        rl_goods_type.setLayoutManager(new LinearLayoutManager(this));
        goodsRackAdapter.notifyDataSetChanged();
        goodsManagerAdatpter = new GoodsManagerAdatpter(this, mData);
        rl_goods.setAdapter(goodsManagerAdatpter);
        rl_goods.setLayoutManager(new LinearLayoutManager(this));
        rl_goods.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#f4f4f4"), 1));
        goodsManagerAdatpter.notifyDataSetChanged();
    }


    @OnClick({R.id.ll_add_goods, R.id.rl_back, R.id.ll_add_scan})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.ll_add_goods:     //商品库选择
                selectGoodsByStore();
                break;
            case R.id.ll_add_scan:
                scanGoods();      //添加商品
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    private void selectGoodsByStore() {
        SelectGoodsByStoreActivity.actionTo(this, user_id, mList.get(currentPosition).getShelves_id(), mList.get(currentPosition).getShelces_name());
    }

    private void addGoodsForShelves() {
        if (mList.size() == 0) {
            return;
        }
        SelectGoodsPictureActivity.actionTo(this, user_id, mList.get(currentPosition));
    }


    public OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(GoodsManagerActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "GoodsManagerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();

                if ("5001".equals(code)) {
                    setData(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void setData(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_GOODS_TYPE:    //获取货架
                setGoodsType(jsonObject);
                break;
            case REQUEST_GOODS:   //获取对应的商品
                setGoods(jsonObject);
                break;
            case REQUEST_CHANGE_GOODS:   //修改商品
                refreshGoods(jsonObject);
                break;
            case REQUEST_CHANGE_TOGGLE:   //下架
                toggleGoods(jsonObject);
                break;
            case REQUEST_DELETE_GOODS:    //删除商品
                confirmGoods(jsonObject);
                break;
            case REQUEST_ADD_SPECIAL_GOODS:   //添加特殊商品
                addSpecialGoods(jsonObject);
                break;
            case REQUEST_DELETE_SHALVES:     //删除货架
                deleteShelvesData(jsonObject);
                break;
            case REQUEST_CHANGE_SHELVES_NAME:  //编辑货架名称
                requestGoodsType();    //刷新商品列表
                break;
            case REQUEST_ADD_SCANL_GOODS:     //获取扫描商品信息
                requestScanGoodsData(jsonObject);
                break;
            case REQUEST_COMMIT_SCANL_GOODS:   //提交扫描商品
                commitScanGoodsData(jsonObject);
                break;
            case REQUEST_COMMIT_PICTURE:    //上传图片
                commitPicture(jsonObject);
                break;
        }
    }

    private void commitPicture(JSONObject jsonObject) throws JSONException {
        //图片选择选择之后跳转
        JSONObject dataObject = jsonObject.getJSONObject("data");
        String pictureUrl = dataObject.getString("picurl");
        Glide.with(GoodsManagerActivity.this).load(pictureUrl).into(iv_scan_goods_picture);
        iv_scan_delete.setVisibility(View.VISIBLE);
        iv_scan_goods_picture.setVisibility(View.VISIBLE);
        scanImageUrl = pictureUrl;
    }

    private void commitScanGoodsData(JSONObject jsonObject) throws JSONException {
        ToastUtil.showShort(jsonObject.getString("msg"));
        // requestGoods(mList.get(currentPosition).getShelves_id());
        requestGoodsType();
    }

    private void requestScanGoodsData(JSONObject jsonObject) throws JSONException {
        int position = 0;
        List<HashMap<String, String>> listPrice = new ArrayList<>();
        JSONObject dataObj = jsonObject.getJSONObject("data");
        scanCode_id = dataObj.getString("code_id");
        String goodsName = StringUtil.handleNullResultForString(dataObj.getString("goods_name"));
        et_scan_name.setText(goodsName);
        scanImageUrl = StringUtil.handleNullResultForString(dataObj.getString("img"));
        if (!"".equals(scanImageUrl)) {
            iv_scan_goods_picture.setVisibility(View.VISIBLE);
            iv_scan_delete.setVisibility(View.VISIBLE);
            Glide.with(GoodsManagerActivity.this).load(scanImageUrl).into(iv_scan_goods_picture);
        } else {
            iv_scan_goods_picture.setVisibility(View.GONE);
            iv_scan_delete.setVisibility(View.GONE);
        }

        JSONArray priceArray = dataObj.getJSONArray("price");
        for (int i = 0; i < priceArray.length(); i++) {
            HashMap<String, String> map = new HashMap<>();
            JSONObject priceObj = priceArray.getJSONObject(i);

            String price = priceObj.getString("price");
            String type = priceObj.getString("type");
            map.put("price", price);
            map.put("type", type);
            if ("1".equals(type)) {
                position = i;
            }
            listPrice.add(map);
            map = null;
        }


        TagAdapter oneTagAdapter = new TagAdapter<HashMap<String, String>>(listPrice) {
            @Override
            public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_goods_price, rl_scan_goods_price, false);
                tv.setText("￥" + map.get("price"));
                return tv;
            }
        };

        if (listPrice.size() != 0) {
            oneTagAdapter.setSelectedList(position);
            mScanPrice = listPrice.get(position).get("price");
        }

        rl_scan_goods_price.setAdapter(oneTagAdapter);
        showGoodsDialog();
    }

    private void deleteShelvesData(JSONObject jsonObject) {
        requestGoodsType();
    }

    private void addSpecialGoods(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);
        requestGoodsType();    //刷新商品列表
    }


    private void confirmGoods(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);
        requestGoodsType();    //刷新商品列表
    }

    private void toggleGoods(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);
        requestGoods(mList.get(currentPosition).getShelves_id());
    }

    private void refreshGoods(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);
        requestGoods(mList.get(currentPosition).getShelves_id());
    }

    private void setGoods(JSONObject jsonObject) throws JSONException {
        JSONArray dataArray = jsonObject.getJSONArray("data");
        setGoodsData(dataArray);
    }

    private void setGoodsType(JSONObject jsonObject) throws JSONException {
        JSONArray dataArray = jsonObject.getJSONArray("data");
        mList.clear();
        goodsRackAdapter.notifyDataSetChanged();
        for (int i = 0; i < dataArray.length(); i++) {
            Goods goods = new Goods();
            JSONObject goodsType = dataArray.getJSONObject(i);
            goods.setShelves_id(goodsType.getString("shelves_id"));
            goods.setShelces_name(goodsType.getString("shelves_name"));
            goods.setWarehouse_shelves_id(goodsType.getString("warehouse_shelves_id"));
            goods.setSpecs(StringUtil.handleNullResultForNumber(goodsType.getString("num")));
            mList.add(goods);
            goods = null;

         /*   if (i == 0) {
                JSONArray goodsArray = goodsType.getJSONArray("goods");
                setGoodsData(goodsArray);
            }*/
        }
        //goodsRackAdapter.setClickPosition(0);
        goodsRackAdapter.setData(mList);
        goodsRackAdapter.notifyDataSetChanged();

        if (mList.size() > 0) {
            tv_no_goods.setText("请为" + mList.get(mList.size() > currentPosition ? currentPosition : 0).getShelces_name() + "货架添加商品");
            requestGoods(mList.get(mList.size() > currentPosition ? currentPosition : 0).getShelves_id());    //请求对应的商品
        }
    }

    private void setGoodsData(JSONArray goodsType) throws JSONException {
        mData.clear();
        goodsManagerAdatpter.notifyDataSetChanged();

        for (int i = 0; i < goodsType.length(); i++) {
            JSONObject goods = goodsType.getJSONObject(i);
            GoodsManagerModel goodsManagerModel = new GoodsManagerModel();
            goodsManagerModel.setGoodsId(goods.getString("goods_id"));
            goodsManagerModel.setGoodsName(goods.getString("goods_name"));
            goodsManagerModel.setGoodsPrice(goods.getString("price"));
            goodsManagerModel.setGoodsState(goods.getString("states"));
            goodsManagerModel.setImageUrl(goods.getString("img"));
            goodsManagerModel.setGoodsState(goods.getString("states"));
            goodsManagerModel.setIsSpecial(goods.getString("is_special"));
            goodsManagerModel.setStock(goods.getString("stock"));
            mData.add(goodsManagerModel);
            goodsManagerModel = null;
        }
        if (mData.size() == 0) {
            ll_no_goods.setVisibility(View.VISIBLE);
            rl_goods.setVisibility(View.GONE);
        } else {
            ll_no_goods.setVisibility(View.GONE);
            rl_goods.setVisibility(View.VISIBLE);
        }
        goodsManagerAdatpter.setData(mData);
        goodsManagerAdatpter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
        mList.clear();
        mList = null;
    }

    private void requestGoods(String currentShelves_id) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("shelves_id", currentShelves_id);
        Request<String> request = NoHttpRequest.requstStoreShelvesByType(user_id, map);
        mRequestQueue.add(REQUEST_GOODS, request, mResponseListener);
    }

    private void deleteShelves() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("shelves_id", currentShelves_id);
        Request<String> request = NoHttpRequest.deleteShalves(user_id, map);
        mRequestQueue.add(REQUEST_DELETE_SHALVES, request, mResponseListener);
    }


    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.goode_manager, null);
            RelativeLayout iv_delete = selectView.findViewById(R.id.iv_delete);
            LinearLayout layout_left_close = selectView.findViewById(R.id.layout_left_close);
            iv_image_ = selectView.findViewById(R.id.iv_image);
            tv_goods_name_ = selectView.findViewById(R.id.tv_goods_name);
            tv_goods_price_ = selectView.findViewById(R.id.tv_goods_price);
            tv_goods_state = selectView.findViewById(R.id.tv_goods_state);

            ll_zero = selectView.findViewById(R.id.ll_zero);
            LinearLayout ll_one = selectView.findViewById(R.id.ll_one);
            ll_zero_wrapper = selectView.findViewById(R.id.ll_zero_wrapper);
            LinearLayout ll_two = selectView.findViewById(R.id.ll_two);
            LinearLayout ll_three = selectView.findViewById(R.id.ll_three);
            LinearLayout ll_four = selectView.findViewById(R.id.ll_four);
            iv_delete.setOnClickListener(viewClicklistener);
            ll_zero.setOnClickListener(viewClicklistener);
            ll_one.setOnClickListener(viewClicklistener);
            ll_two.setOnClickListener(viewClicklistener);
            ll_three.setOnClickListener(viewClicklistener);
            ll_four.setOnClickListener(viewClicklistener);
            layout_left_close.setOnClickListener(viewClicklistener);

            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
        }
    }

    private void initChangeNamePopu() {
        if (changeNamePopu != null && changeNamePopu.isShowing()) {
            changeNamePopu.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.change_name, null);
            RelativeLayout iv_delete_name = selectView.findViewById(R.id.iv_delete_name);
            LinearLayout layout_close_name = selectView.findViewById(R.id.layout_close_name);
            RelativeLayout iv_comfirm = selectView.findViewById(R.id.iv_comfirm_name);
            et_change_name = selectView.findViewById(R.id.et_change_name);
            et_change_name.setText("");
            iv_delete_name.setOnClickListener(viewClicklistener);
            iv_comfirm.setOnClickListener(viewClicklistener);
            et_change_name.setOnClickListener(viewClicklistener);
            layout_close_name.setOnClickListener(viewClicklistener);

            changeNamePopu = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            changeNamePopu.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            changeNamePopu.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            changeNamePopu.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            changeNamePopu.setTouchable(true); // 设置popupwindow可点击
            changeNamePopu.setOutsideTouchable(true); // 设置popupwindow外部可点击
            changeNamePopu.setFocusable(true); // 获取焦点
        }
    }

    private void initChangePricePopu() {
        if (changePricePopu != null && changePricePopu.isShowing()) {
            changePricePopu.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.change_price, null);
            RelativeLayout iv_delete_price = selectView.findViewById(R.id.iv_delete_price);
            RelativeLayout iv_comfirm_price = selectView.findViewById(R.id.iv_comfirm_price);
            LinearLayout layout_left_price = selectView.findViewById(R.id.layout_left_price);
            et_change_price = selectView.findViewById(R.id.et_change_price);
            et_change_price.setText("");
            iv_delete_price.setOnClickListener(viewClicklistener);
            iv_comfirm_price.setOnClickListener(viewClicklistener);
            layout_left_price.setOnClickListener(viewClicklistener);

            changePricePopu = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            changePricePopu.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            changePricePopu.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            changePricePopu.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            changePricePopu.setTouchable(true); // 设置popupwindow可点击
            changePricePopu.setOutsideTouchable(true); // 设置popupwindow外部可点击
            changePricePopu.setFocusable(true); // 获取焦点
        }
    }

    private void showSelectDialog() {
        Glide.with(GoodsManagerActivity.this).load(currentModel.getImageUrl()).into(iv_image_);
        tv_goods_name_.setText(currentModel.getGoodsName());
        tv_goods_price_.setText(currentModel.getGoodsPrice());
        tv_goods_state.setText("1".equals(currentModel.getGoodsState()) ? "商品下架" : "商品上架");
        String isSpecial = currentModel.getIsSpecial();
        if ("1".equals(isSpecial)) {
            ll_zero_wrapper.setVisibility(View.VISIBLE);
        } else {
            ll_zero_wrapper.setVisibility(View.GONE);
        }
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(rl_goods, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private View.OnClickListener viewClicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_zero:
                    setSpecialPrice();     //设置为特价商品
                    break;
                case R.id.ll_one:
                    requestDelete();     //下架
                    break;
                case R.id.ll_two:
                    changeGoodsName();     //修改名称
                    break;
                case R.id.ll_three:
                    changePriceName();     //修改价格
                    break;
                case R.id.ll_four:              //删除商品
                    deleteGoods();
                    break;
                case R.id.iv_delete:
                    popupWindow.dismiss();
                    break;
                case R.id.iv_delete_name:
                    changeNamePopu.dismiss();
                    break;
                case R.id.iv_comfirm_name:    //确定修改名称
                    currentModel.setGoodsName(et_change_name.getText().toString());
                    commitGoodsName(et_change_name.getText().toString());
                    break;
                case R.id.iv_delete_price:
                    changePricePopu.dismiss();
                    break;
                case R.id.iv_comfirm_price:     //修改价格
                    currentModel.setGoodsPrice(et_change_price.getText().toString());
                    commitGoodsName(et_change_price.getText().toString());
                    break;
                case R.id.layout_left_close:
                    popupWindow.dismiss();
                    break;
                case R.id.layout_close_name:
                    changeNamePopu.dismiss();
                    break;
                case R.id.layout_left_price:
                    changePricePopu.dismiss();
                    break;
                case R.id.iv_delete_shelves_name:
                    changeShelvesNamePopu.dismiss();
                    break;
                case R.id.iv_comfirm_shelves_name:
                    commitShelvesName();
                    break;
                case R.id.layout_close_shelves_name:
                    changeShelvesNamePopu.dismiss();
                    break;

            }
        }
    };

    private void commitShelvesName() {
        changeShelvesNamePopu.dismiss();
        String content = et_change_shelves_name.getText().toString();
        if ("".equals(content)) {
            ToastUtil.showShort("货架名称不可为空");
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("user_id", user_id);
            map.put("shelves_id", currentShelves_id);
            map.put("shelves_name", content);
            Request<String> request = NoHttpRequest.changeShelvesName(map);
            mRequestQueue.add(REQUEST_CHANGE_SHELVES_NAME, request, mResponseListener);
        }
    }

    private void deleteGoods() {
        popupWindow.dismiss();

        new CircleDialog.Builder()
                .setTitle("温馨提示")
                .setText("\n确定删除此商品吗?\n")
                .setWidth(0.8f)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        comfirmDeleteGoods();    //确定删除
                    }
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    private void comfirmDeleteGoods() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("goods_id", currentModel.getGoodsId());
        Request<String> request = NoHttpRequest.deleteGoods(user_id, map);
        mRequestQueue.add(REQUEST_DELETE_GOODS, request, mResponseListener);
    }

    private void commitGoodsName(String message) {
        if ("".equals(message)) {
            ToastUtil.showShort("商品名称或价格不可为空");
        } else {
            changeNamePopu.dismiss();
            changePricePopu.dismiss();
            commitGoods();
        }
    }

    private void commitGoods() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("goods_id", currentModel.getGoodsId());
        map.put("name", currentModel.getGoodsName());
        map.put("price", currentModel.getGoodsPrice());
        Request<String> request = NoHttpRequest.changeGoodsNameAndPrice(user_id, map);
        mRequestQueue.add(REQUEST_CHANGE_GOODS, request, mResponseListener);
    }

    private void changeGoodsName() {
        popupWindow.dismiss();
        showChangeNameDialg();
    }

    private void changePriceName() {
        popupWindow.dismiss();
        showChangePriceDialg();
    }

    private void setSpecialPrice() {
        popupWindow.dismiss();
        showSpecialPriceDialg();
    }

    private void showSpecialPriceDialg() {
        Glide.with(this).load(currentModel.getImageUrl()).into(iv_special_goods_image);
        tv_special_goods_name.setText(currentModel.getGoodsName());
        tv_special_goods_price.setText("￥" + currentModel.getGoodsPrice());
        et_special_promotion_price.setText("");
        et_special_promotion_number.setText("");
        if (specialPriceWindow != null && !specialPriceWindow.isShowing()) {
            specialPriceWindow.showAtLocation(rl_goods, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void showChangePriceDialg() {
        et_change_price.setText(currentModel.getGoodsPrice());
        if (changePricePopu != null && !changePricePopu.isShowing()) {
            changePricePopu.showAtLocation(rl_goods, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void showChangeNameDialg() {
        et_change_name.setText(currentModel.getGoodsName());
        if (changeNamePopu != null && !changeNamePopu.isShowing()) {
            changeNamePopu.showAtLocation(rl_goods, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void requestDelete() {
        popupWindow.dismiss();
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("goods_id", currentModel.getGoodsId());
        Request<String> request = NoHttpRequest.toggleGoods(user_id, map);
        mRequestQueue.add(REQUEST_CHANGE_TOGGLE, request, mResponseListener);
    }

    private void scanGoods() {
        showSelectAddTypeDialog();    //选择添加商品方式
    }

    private void showSelectAddTypeDialog() {
        if (addgoodsWindow != null && !addgoodsWindow.isShowing()) {
            addgoodsWindow.showAtLocation(rl_goods, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void initSpecialPricePopu() {
        if (specialPriceWindow != null && specialPriceWindow.isShowing()) {
            specialPriceWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.view_special_price, null);
            iv_special_goods_image = selectView.findViewById(R.id.iv_special_goods_image);
            tv_special_goods_name = selectView.findViewById(R.id.tv_special_goods_name);
            tv_special_goods_price = selectView.findViewById(R.id.tv_special_goods_price);
            et_special_promotion_price = selectView.findViewById(R.id.et_special_promotion_price);
            tv_special_start_time = selectView.findViewById(R.id.tv_special_start_time);     //开始时间
            tv_special_end_time = selectView.findViewById(R.id.tv_special_end_time);             //结束时间
            et_special_promotion_number = selectView.findViewById(R.id.et_special_promotion_number);
            TextView tv_special_add_confirm = selectView.findViewById(R.id.tv_special_add_confirm);
            tv_special_add_confirm.setOnClickListener(mOnClickListener);
            tv_special_end_time.setOnClickListener(mOnClickListener);
            tv_special_start_time.setOnClickListener(mOnClickListener);

            specialPriceWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            specialPriceWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            specialPriceWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            specialPriceWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            specialPriceWindow.setTouchable(true); // 设置popupwindow可点击
            specialPriceWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            specialPriceWindow.setFocusable(true); // 获取焦点
            LinearLayout layout_pop_close = (LinearLayout) selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    specialPriceWindow.dismiss();
                }
            });
        }
    }

    private void initScanGoodsPopu() {
        if (scanGoodsWindow != null && scanGoodsWindow.isShowing()) {
            scanGoodsWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.view_select_scan_goods, null);
            et_scan_name = selectView.findViewById(R.id.et_scan_name);
            rl_scan_goods_price = selectView.findViewById(R.id.rl_scan_goods_price);
            et_scan_price_self = selectView.findViewById(R.id.et_scan_price_self);
            iv_scan_delete = selectView.findViewById(R.id.iv_scan_delete);
            RelativeLayout rl_scan_picture = selectView.findViewById(R.id.rl_scan_picture);
            LinearLayout layout_scan_left_close = selectView.findViewById(R.id.layout_scan_left_close);
            iv_scan_goods_picture = selectView.findViewById(R.id.iv_scan_goods_picture);
            TextView tv_scan_add_confirm = selectView.findViewById(R.id.tv_scan_add_confirm);

            tv_scan_add_confirm.setOnClickListener(mOnClickListener);
            layout_scan_left_close.setOnClickListener(mOnClickListener);
            rl_scan_picture.setOnClickListener(mOnClickListener);
            iv_scan_delete.setOnClickListener(mOnClickListener);

            scanGoodsWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            scanGoodsWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            scanGoodsWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            scanGoodsWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            scanGoodsWindow.setTouchable(true); // 设置popupwindow可点击
            scanGoodsWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            scanGoodsWindow.setFocusable(true); // 获取焦点

        }
    }

    private void initAddGoodsPopuStyle() {
        if (addgoodsWindow != null && addgoodsWindow.isShowing()) {
            addgoodsWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.view_select_goods, null);
            Button bt_take_by_normal = (Button) selectView.findViewById(R.id.bt_take_by_normal);
            Button bt_take_by_scan = (Button) selectView.findViewById(R.id.bt_take_by_scan);
            Button btnCancle = (Button) selectView.findViewById(R.id.bt_cancle);
            bt_take_by_normal.setOnClickListener(mOnClickListener);
            bt_take_by_scan.setOnClickListener(mOnClickListener);
            btnCancle.setOnClickListener(mOnClickListener);
            addgoodsWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            addgoodsWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            addgoodsWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            addgoodsWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            addgoodsWindow.setTouchable(true); // 设置popupwindow可点击
            addgoodsWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            addgoodsWindow.setFocusable(true); // 获取焦点
            LinearLayout layout_pop_close = (LinearLayout) selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addgoodsWindow.dismiss();
                }
            });
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_take_by_normal:
                    addGoodsForShelves();      //正常添加商品
                    addgoodsWindow.dismiss();
                    break;
                case R.id.bt_take_by_scan:     //扫描添加商品
                    // ScanGoodsActivity.actionTo(GoodsManagerActivity.this, user_id, mList.get(currentPosition));
                    ScanGoodsActivity.actionTo(GoodsManagerActivity.this, REQUEST_SCAN_GOODS);
                    addgoodsWindow.dismiss();
                    break;
                case R.id.bt_cancle:
                    addgoodsWindow.dismiss();
                    break;
                case R.id.tv_special_add_confirm:    //保存为特价商品
                    saveSpecialGoods();
                    break;
                case R.id.tv_special_start_time:     //开始时间
                    showTimeSelectDialog(1);
                    break;
                case R.id.tv_special_end_time:     //特价商品 结束时间
                    showTimeSelectDialog(2);
                    break;
                case R.id.layout_scan_left_close:
                    scanGoodsWindow.dismiss();
                    break;
                case R.id.iv_scan_delete:    //删除照片
                    iv_scan_goods_picture.setVisibility(View.GONE);
                    iv_scan_delete.setVisibility(View.GONE);
                    scanImageUrl = "";
                    break;
                case R.id.tv_scan_add_confirm:    //扫描添加
                    commitScanGoods();
                    break;
                case R.id.rl_scan_picture:     //点击添加商品
                    takePicture();
                    break;
            }
        }
    };

    private void commitScanGoods() {
        String name = et_scan_name.getText().toString();
        String price = et_scan_price_self.getText().toString();
        if (!"".equals(price)) {
            mScanPrice = price;
        }

        if ("".equals(scanImageUrl) || "".equals(mScanPrice) || "".equals(name)) {
            ToastUtil.showShort("请完善商品信息");
            return;
        }

        scanGoodsWindow.dismiss();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("shelves_id", mList.get(currentPosition).getShelves_id());
        params.put("name", name);
        params.put("shelves_name", mList.get(currentPosition).getShelces_name());
        params.put("img", scanImageUrl);
        params.put("code_id", scanCode_id);
        params.put("price", mScanPrice);
        params.put("class_name", mList.get(currentPosition).getShelces_name());
        params.put("class_id", mList.get(currentPosition).getShelves_id());
        params.put("lib_goods_id", "0");
        Request<String> request = NoHttpRequest.commitGoodsRequest(params);
        mRequestQueue.add(REQUEST_COMMIT_SCANL_GOODS, request, mResponseListener);
    }

    private void saveSpecialGoods() {
        String price = et_special_promotion_price.getText().toString();
        String number = et_special_promotion_number.getText().toString();
        String startTime = tv_special_start_time.getText().toString();
        String endTime = tv_special_end_time.getText().toString();

        if ("".equals(price)) {
            ToastUtil.showShort("请设置促销价格");
        } else if ("".equals(startTime)) {
            ToastUtil.showShort("请设置开始时间");
        } else if ("".equals(endTime)) {
            ToastUtil.showShort("请设置结束时间");
        } else if ("".equals(number)) {
            ToastUtil.showShort("请设置促销件数");
        } else {
            specialPriceWindow.dismiss();
            saveGoods(price, startTime, endTime, number);
        }
    }

    private void saveGoods(String price, String startTime, String endTime, String number) {
        specialPriceWindow.dismiss();
        Map<String, String> map = new HashMap<>();
        map.put("distributor_id", user_id);
        map.put("class_id", mList.get(0).getShelves_id());
        map.put("start_time", startTime);
        map.put("end_time", endTime);
        map.put("stock", number);
        map.put("price", price);
        map.put("product_id", currentModel.getGoodsId());
        Request<String> request = NoHttpRequest.saveSpecialGoods(map);
        mRequestQueue.add(REQUEST_ADD_SPECIAL_GOODS, request, mResponseListener);
    }

    private void requestScanGoods(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("goods_code", code);
        Request<String> request = NoHttpRequest.getScanGoodsMessage(map);
        mRequestQueue.add(REQUEST_ADD_SCANL_GOODS, request, mResponseListener);

    }

    private void showTimeSelectDialog(int type) {
        DateChooseWheelViewDialog endDateChooseDialog = new DateChooseWheelViewDialog(GoodsManagerActivity.this,
                new DateChooseWheelViewDialog.DateChooseInterface() {
                    @Override
                    public void getDateTime(String time, boolean longTimeChecked) {
                        if (type == 1) {
                            tv_special_start_time.setText(time);
                        } else {
                            tv_special_end_time.setText(time);
                        }
                    }
                });
        endDateChooseDialog.setTimePickerGone(true);
        endDateChooseDialog.setDateDialogTitle(type == 1 ? "开始时间" : "结束时间");
        endDateChooseDialog.showDateChooseDialog();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_SCAN_GOODS) {
            String code = data.getStringExtra("code");
            requestScanGoods(code);    //请求数据
        }
        if (requestCode == REQUEST_TAKE_PICTURE) {
            compressFile();   //压缩上传

        }
    }

    private void showGoodsDialog() {
        et_scan_price_self.setText("");
        if (scanGoodsWindow != null && !scanGoodsWindow.isShowing()) {
            scanGoodsWindow.showAtLocation(rl_goods, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void takePicture() {
        //判断SD卡是否可用
        if (SystemUtil.hasSdcard()) {
            if (!f.exists()) {
                f.mkdirs();
            }

            String uuid = UUID.randomUUID().toString();
            String path2 = uuid + ".jpg";
            photoFile = new File(f, path2);
            Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            startActivityForResult(intent, REQUEST_TAKE_PICTURE);
        }
    }

    private void compressFile() {
        Luban.with(this)
                .load(photoFile)
                .ignoreBy(100)
                .setTargetDir(IMAGE_DIR)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        uploadPicture(file.getAbsolutePath());    //上传图片
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    private void uploadPicture(String filePath) {
        if (!new File(filePath).exists()) {
            ToastUtil.showShort("文件不存在，请重拍！");
            return;
        }
        Request<String> request = NoHttpRequest.commitPictureRequest(filePath);
        mRequestQueue.add(REQUEST_COMMIT_PICTURE, request, mResponseListener);
    }
}
