package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Goods;
import com.mt.bbdj.baseconfig.model.SearchGoodsModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.MiPictureHelper;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.GoodsImageAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;



public class SelectGoodsPictureActivity extends BaseActivity {

    private GridView goodsGridView;     //商品图片

    private GoodsImageAdapter mAdapter;    //示例商品

    private RelativeLayout rl_back;

    private List<Map<String, String>> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private String user_id;

    private final int REQUEST_IMAGE = 100;    //请求图片
    public static final int PHOTOHRAPH = 300;// 拍照
    private static final int REQUEST_CODE_SYSTEM = 200;    //系统相机
    private static final int PHOTORESOULT = 1;    //结果处理

    private String shelves_id = "";
    private Goods mGoods;

    private PopupWindow popupWindow;
    private View selectView;

    private SearchGoodsModel searchGoodsModel;

    private String picturePath = "/bbdj/picture";

    private String IMAGE_DIR = Environment.getExternalStorageDirectory() + "/bbdj/picture";
    private File f = new File(Environment.getExternalStorageDirectory(), picturePath);
    private File photoFile;
    private File compressPicture;
    public static final String IMAGE_UNSPECIFIED = "image/*";

    private int enterType = 0;

    public static void actionTo(Context context, String user_id, Goods goods) {
        Intent intent = new Intent(context, SelectGoodsPictureActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("goods", goods);
        intent.putExtra("enterType", 0);
        context.startActivity(intent);
    }

    public static void actionTo(Context context, String user_id, Goods goods, SearchGoodsModel searchGoodsModel) {
        Intent intent = new Intent(context, SelectGoodsPictureActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("goods", goods);
        intent.putExtra("enterType", 1);
        intent.putExtra("searchGoods", searchGoodsModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_goods_picture);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(SelectGoodsPictureActivity.this);
        EventBus.getDefault().register(this);
        initParams();
        initGridView();
        requestImage();    //请求实例图片
        initListener();
        initPopuStyle();
    }

    private void initListener() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.DESTORY) {
            finish();
        }
    }

    private void requestImage() {
        if (searchGoodsModel != null) {     //表示扫码添加
            initData();    //初始化数据源
        } else {    //手动添加
            Map<String, String> params = new HashMap<>();
            params.put("user_id", user_id);
            params.put("shelves_name", shelves_id);
            Request<String> request = NoHttpRequest.requestDemoImage(user_id, shelves_id, params);
            mRequestQueue.add(REQUEST_IMAGE, request, mResponseListener);
        }
    }


    private void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("goods_id", "0");
        map.put("img", "");
        map.put("type", "0");
        map.put("code_id", "0");
        mList.add(map);
        List<SearchGoodsModel.SearchGoods> dataList = searchGoodsModel.getData();
        if (dataList.size() != 0) {
            for (int i = 0; i < dataList.size(); i++) {
                SearchGoodsModel.SearchGoods entity = dataList.get(i);
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("goods_id", entity.getGoods_id());
                map1.put("img", entity.getImg());
                map1.put("type","1");
                map1.put("code_id", searchGoodsModel.getCode_id());
                mList.add(map1);
                map1 = null;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initGridView() {
        goodsGridView = findViewById(R.id.gridview);
        rl_back = findViewById(R.id.rl_back);
        mAdapter = new GoodsImageAdapter(this, mList);
        goodsGridView.setAdapter(mAdapter);
        goodsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    showSelectDialog();     //选择相册
                } else {
                    mGoods.setGoods_id(mList.get(position).get("goods_id"));
                    mGoods.setImageUrl(mList.get(position).get("img"));
                    mGoods.setCode_id(mList.get(position).get("code_id"));
                    AddGoodsNameActivity.actionTo(SelectGoodsPictureActivity.this, mGoods);
                }
            }
        });
    }


    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        mGoods = (Goods) getIntent().getSerializableExtra("goods");
        enterType =  getIntent().getIntExtra("enterType",0);
        searchGoodsModel = (SearchGoodsModel) getIntent().getSerializableExtra("searchGoods");
        shelves_id = mGoods.getShelces_name();
        user_id = getIntent().getStringExtra("user_id");
    }

    public OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            LoadDialogUtils.getInstance().showLoadingDialog(SelectGoodsPictureActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "SelectGoodsPictureActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
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

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_IMAGE:    //获取实例图片
                setDemoImage(jsonObject);
                break;
        }
    }

    private void setDemoImage(JSONObject jsonObject) throws JSONException {
        JSONArray dataArray = jsonObject.getJSONArray("data");
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("goods_id", "");
        map1.put("img", "");
        map1.put("code_id", "0");
        map1.put("type", "0");
        mList.add(map1);
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject1 = dataArray.getJSONObject(i);
            String goods_id = jsonObject1.getString("goods_id");
            String img = jsonObject1.getString("img");
            HashMap<String, String> map = new HashMap<>();
            map.put("goods_id", goods_id);
            map.put("img", img);
            map.put("code_id", "0");
            map.put("type", "1");

            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
        mList.clear();
        mList = null;
    }

    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.view_picture_select, null);
            Button takeCamera = (Button) selectView.findViewById(R.id.bt_take_camera);
            Button takeFromAlbum = (Button) selectView.findViewById(R.id.bt_take_from_album);
            Button btnCancle = (Button) selectView.findViewById(R.id.bt_cancle);
            takeCamera.setOnClickListener(mOnClickListener);
            takeFromAlbum.setOnClickListener(mOnClickListener);
            btnCancle.setOnClickListener(mOnClickListener);
            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            LinearLayout layout_pop_close = (LinearLayout) selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
    }


    //图片来源点击事件
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_take_camera:
                    takePicture();     //拍照
                    break;
                case R.id.bt_take_from_album:
                    takePictureFromAlbum();   //相册选择
                    break;
                case R.id.bt_cancle:
                    popupWindow.dismiss();
                    break;
            }
        }
    };

    private void takePicture() {
        //判断SD卡是否可用
        if (SystemUtil.hasSdcard()) {
            if (!f.exists()) {
                f.mkdirs();
            }

            String uuid = UUID.randomUUID().toString();
            String path2 = uuid + ".jpg";
            photoFile = new File(f, path2);
            compressPicture = new File(f, uuid);
            Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            startActivityForResult(intent, PHOTOHRAPH);
            popupWindow.dismiss();
        }
    }

    private void takePictureFromAlbum() {
        //判断SD卡是否可用
        if (SystemUtil.hasSdcard()) {
            if (!f.exists()) {
                f.mkdirs();
            }
            String uuid = UUID.randomUUID().toString();
            String path2 = uuid + ".jpg";
            photoFile = new File(f, path2);
            compressPicture = new File(f, uuid);
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            startActivityForResult(intent, REQUEST_CODE_SYSTEM);
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PHOTOHRAPH:
                takePictureByCamera();     //拍照
                break;
            case REQUEST_CODE_SYSTEM:   //系统相机
                takePictureBySystem(data);
                break;
        }
    }

    private void takePictureByCamera() {
        compressFile();
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
        mRequestQueue.add(2, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(SelectGoodsPictureActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RegisterAccount::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    if ("5001".equals(code)) {
                        //图片选择选择之后跳转
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        String pictureUrl = dataObject.getString("picurl");
                        mGoods.setImageUrl(pictureUrl);
                        mGoods.setCode_id(enterType == 0?"0":searchGoodsModel.getCode_id());
                        AddGoodsNameActivity.actionTo(SelectGoodsPictureActivity.this, mGoods);
                    } else {
                        ToastUtil.showShort("上传失败，请重试！");
                        // restorePictureState();    //还原图片位
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDialogUtils.cannelLoadingDialog();
                    ToastUtil.showShort("上传失败，请重试！");
                    //  restorePictureState();
                }
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {
                LoadDialogUtils.cannelLoadingDialog();
            }
        });
    }


    private void takePictureBySystem(Intent data) {
        String pickPath = MiPictureHelper.getPath(SelectGoodsPictureActivity.this, data.getData());
        compressFile(pickPath);
    }

    private void compressFile(String filePath) {
        Luban.with(this)
                .load(filePath)
                .ignoreBy(100)
                .setTargetDir(IMAGE_DIR)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        //     Glide.with(RegisterCompleteActivity.this).load(file.getPath()).into(imageViews[clickType]);
                        uploadPicture(file.getAbsolutePath());    //上传图片
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    private void showSelectDialog() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(rl_back, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

}
