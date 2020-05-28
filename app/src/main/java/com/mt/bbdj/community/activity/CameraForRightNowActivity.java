package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.ocr.sdk.utils.LogUtil;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressImageDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.service.UploadService;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Preview;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CameraForRightNowActivity extends AppCompatActivity {

    private CameraView camera;
    private ImageView iv_back;
    private TextView tv_express, tv_take_picture, tv_last_one, tv_failure, tv_loading,tv_handling, tv_success,tv_express_name,tv_express_number;

    public static final String KEY_IMAGE_PATH = "imagePath";
    private String picturePath = "/bbdj/picture";
    public static final int REQUEST_COMMIT_PICTURE = 100;
    public static final int REQUEST_DELETE = 101;
    public static final int REQUEST_ENTER = 102;
    public static final int REQUEST_HISTORY = 103;
    private File cameraFolder = new File(Environment.getExternalStorageDirectory(), picturePath);
    private String currentName;
    private File imageFile;
    private String user_id = "";
    private String express_id = "";
    private String express_name = "";
    private ExpressImageDao expressImageDao;
    private String courier_id = "";
    private Intent mImageService;
    private RequestQueue mRequestQueue;
    private String oldPicture;
    private String mCode;
    private TextView tv_message;
    private RelativeLayout layout_success;
    private LinearLayout layout_failure;

    private TextView tv_failure_messgae;
    private TextView tv_failure_title;
    private EditText et_failure_code;
    private EditText et_failure_express;
    private EditText et_failure_phone;
    private EditText et_failure_danhao;
    private TextView tv_failure_enter;
    private TextView tv_failure_delete;

    private TextView tv_success_code;
    private TextView tv_success_phone;
    private TextView tv_success_express;
    private TextView tv_success_yundan;
    private LinearLayout tv_express_detail;

    private String pie_id = "";
    private String mobile = "";
    private String mYundan = "";

    private boolean isFirst = true;
    private UserBaseMessageDao userMessageDao;

    public static void actionTo(Context context, String user_id) {
        Intent intent = new Intent(context, CameraForRightNowActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_right_now);
        initParams();
        initView();
        initClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }

    private void initClickListener() {
        iv_back.setOnClickListener(view -> finish());
        tv_failure_enter.setOnClickListener(view -> {
            comfirmEnter();
        });
        tv_failure_delete.setOnClickListener(view -> {
            delete();
        });
    }

    private void comfirmEnter() {
        express_name = et_failure_express.getText().toString();
        mYundan = et_failure_danhao.getText().toString();
        mobile = et_failure_phone.getText().toString();
        mCode = et_failure_code.getText().toString();

        if (express_name.length() == 0 || mCode.length() == 0 || mobile.length() == 0 || mYundan.length() == 0) {
            ToastUtil.showShort("请完善信息");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("courier_id", courier_id);
        map.put("uuid", UUID.randomUUID().toString());
        map.put("pie_id", pie_id);
        map.put("number", mYundan);
        map.put("code", mCode);
        map.put("mobile", mobile);
        map.put("express_name", express_name);
        String signature = StringUtil.getsignature2(map);
        Request<String> request = NoHttpRequest.comfirmEnterRequest(signature, map);
        mRequestQueue.add(REQUEST_ENTER, request, onResponseListener);
    }

    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        express_name = getIntent().getStringExtra("express_name");
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        expressImageDao = mDaoSession.getExpressImageDao();
         userMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userMessages = userMessageDao.queryBuilder().list();
        if (userMessages.size() != 0) {
            UserBaseMessage mUserMessage = userMessages.get(0);
            courier_id = mUserMessage.getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
        mImageService = new Intent(this, UploadService.class);
        mImageService.putExtra("isBegin", true);
    }

    private void initView() {
        camera = findViewById(R.id.camera);
        tv_express_detail = findViewById(R.id.tv_express_detail);
        tv_success_code = findViewById(R.id.tv_success_code);
        tv_success_phone = findViewById(R.id.tv_success_phone);
        tv_success_express = findViewById(R.id.tv_success_express);
        tv_success_yundan = findViewById(R.id.tv_success_yundan);

        tv_last_one = findViewById(R.id.tv_last_one);
        tv_failure = findViewById(R.id.tv_failure);
        tv_loading = findViewById(R.id.tv_loading);
        tv_handling = findViewById(R.id.tv_handing);
        tv_success = findViewById(R.id.tv_success);
        tv_express_name = findViewById(R.id.tv_express_name);
        tv_express_number = findViewById(R.id.tv_express_number);

        tv_message = findViewById(R.id.tv_message);
        layout_success = findViewById(R.id.layout_success);
        layout_failure = findViewById(R.id.layout_failure);
        tv_express = findViewById(R.id.tv_express);
        iv_back = findViewById(R.id.iv_back);
        tv_take_picture = findViewById(R.id.tv_take_picture);
        //tv_express.setText(express_name);

        tv_failure_messgae = findViewById(R.id.tv_failure_messgae);
        tv_failure_title = findViewById(R.id.tv_failure_title);
        et_failure_code = findViewById(R.id.et_failure_code);
        et_failure_express = findViewById(R.id.et_failure_express);
        et_failure_phone = findViewById(R.id.et_failure_phone);
        et_failure_danhao = findViewById(R.id.et_failure_danhao);
        tv_failure_enter = findViewById(R.id.tv_failure_enter);
        tv_failure_delete = findViewById(R.id.tv_failure_delete);

        camera.setLifecycleOwner(this);
        camera.addCameraListener(new Listener());

        tv_take_picture.setOnClickListener(v -> {
            if (camera.isTakingPicture()) return;
            if (camera.getPreview() != Preview.GL_SURFACE) {
                return;
            }
            camera.takePictureSnapshot();
            SoundHelper.getInstance().takePhoto();
        });

        tv_express_detail.setOnClickListener(view -> EnterDetailActivity.actionTo(CameraForRightNowActivity.this));
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {

        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            ToastUtil.showShort(exception.getReason() + "");
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            if (camera.isTakingVideo()) {
                return;
            }

            result.toBitmap(900, 1600, bitmap -> {
                savePhoto(bitmap);
               // ToastUtil.showShort("拍照成功");
                oldPicture = imageFile.getAbsolutePath();
                mCode = "";
                // zipPhoto();
            });
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);

        }

        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();

        }

        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();

        }

        @Override
        public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);

        }

        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);

        }
    }


    private void commitPicture() {
        Map<String, String> map = new HashMap<>();
        map.put("station_id", user_id);
        map.put("uuid", UUID.randomUUID().toString());
        Request<String> request = NoHttpRequest.commitExpressPictureRequest(map, imageFile.getAbsolutePath());
        mRequestQueue.add(REQUEST_COMMIT_PICTURE, request, onResponseListener);
    }

    private void delete() {
       /* Map<String, String> map = new HashMap<>();
        map.put("courier_id", courier_id);
        map.put("pie_id", pie_id);
        map.put("uuid", UUID.randomUUID().toString());
        String signature = StringUtil.getsignature2(map);
        Request<String> request = NoHttpRequest.deleteExpressRequest(signature, map);
        mRequestQueue.add(REQUEST_DELETE, request, onResponseListener);*/
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
           // tv_take_picture.setEnabled(false);
            layout_failure.setVisibility(View.GONE);
            LoadDialogUtils.getInstance().showLoadingDialog(CameraForRightNowActivity.this);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "CameraForRightNowActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.getString("code");
                hideAllpannel();
                if (what == REQUEST_DELETE) {
                    handleDelete(jsonObject, code);
                } else if (what == REQUEST_COMMIT_PICTURE) {
                    handleCommitPicture(jsonObject, code);
                } else if (what == REQUEST_HISTORY) {
                    handleHistoryData(jsonObject);    //处理未完成数据
                } else {
                    tv_take_picture.setEnabled(true);
                    handleEnter(jsonObject, code);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                LoadDialogUtils.cannelLoadingDialog();
                ToastUtil.showShort("网络异常请重试！");
                if (what == REQUEST_COMMIT_PICTURE) {
                    tv_take_picture.setEnabled(true);
                }
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            if (what == REQUEST_COMMIT_PICTURE) {
                tv_take_picture.setEnabled(true);
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        @Override
        public void onFinish(int what) {
        //    LoadDialogUtils.cannelLoadingDialog();
        }
    };

    private void handleHistoryData(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.getString("code");
        String msg = jsonObject.getString("msg");
        if ("5001".equals(code)) {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            String last_code = StringUtil.handleNullResultForNumber(dataObj.getString("last_code"));
            String success = StringUtil.handleNullResultForNumber(dataObj.getString("success"));
            String fail = StringUtil.handleNullResultForNumber(dataObj.getString("fail"));
            String loading = StringUtil.handleNullResultForNumber(dataObj.getString("loading"));
            String processeding = StringUtil.handleNullResultForNumber(dataObj.getString("processeding"));
            String number = StringUtil.handleNullResultForString(dataObj.getString("number"));
            String express_name = StringUtil.handleNullResultForString(dataObj.getString("express_name"));

            tv_express_name.setText(express_name);
            tv_express_number.setText(number);
            tv_last_one.setText(last_code);
            tv_success.setText(success);
            tv_loading.setText(loading);
            tv_handling.setText(processeding);
            tv_failure.setText(fail);
        } else {
            ToastUtil.showShort(msg);
        }
    }

    private void handleEnter(JSONObject jsonObject, String code) throws JSONException {
        if ("5001".equals(code)) {
            layout_success.setVisibility(View.VISIBLE);
            tv_take_picture.setEnabled(true);
            JSONObject dataObj = jsonObject.getJSONObject("data");
            tv_success_code.setText(StringUtil.handleNullResultForString(dataObj.getString("code")));
            tv_success_phone.setText(StringUtil.handleNullResultForString(dataObj.getString("mobile")));
            tv_success_express.setText(StringUtil.handleNullResultForString(dataObj.getString("express_name")));
            tv_success_yundan.setText(StringUtil.handleNullResultForString(dataObj.getString("number")));
        } else if ("5002".equals(code)) {
            setFailure(jsonObject, false);
            tv_take_picture.setEnabled(false);
        } else if ("5003".equals(code)) {
            tv_take_picture.setEnabled(false);
            setFailure(jsonObject, true);
        }
    }

    private void handleDelete(JSONObject jsonObject, String code) {
        if ("5001".equals(code)) {
            ToastUtil.showShort("删除成功");
            tv_take_picture.setEnabled(true);
        } else {
            layout_failure.setVisibility(View.VISIBLE);
            ToastUtil.showShort("删除失败");
        }
    }

    private void handleCommitPicture(JSONObject jsonObject, String code) throws JSONException {

        if ("5001".equals(code)){
            requestData();
            playSound(jsonObject);
        } else if ("5002".equals(code)){
            //requestData();
            ToastUtil.showShort(jsonObject.getString("msg"));
            SoundHelper.getInstance().playTakeCodeAgain();
        } else if ("5003".equals(code)){
            ToastUtil.showShort(jsonObject.getString("msg"));
            SoundHelper.getInstance().playTakeLinearAgain();
        } else {
            ToastUtil.showShort(jsonObject.getString("msg"));
        }
      /*  if ("5001".equals(code)) {
            setSuccess(jsonObject);
        }  else if ("5002".equals(code)) {
            setFailure(jsonObject,true);
        } else if ("5003".equals(code)) {
            setFailure(jsonObject,true);
        } else if ("4005".equals(code)) {
            showPromitDialog(jsonObject);
        }*/
    }

    private void playSound(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String express_id = dataObj.getString("express_id");
        SoundHelper.getInstance().playExpress(Integer.parseInt(express_id));
    }

    private void showPromitDialog(JSONObject jsonObject) throws JSONException {
        String promit = jsonObject.getString("msg");
        tv_message.setVisibility(View.VISIBLE);
        tv_message.setText(promit);
        tv_take_picture.setEnabled(true);
    }

    private void setSuccess(JSONObject jsonObject) throws JSONException {
        layout_success.setVisibility(View.VISIBLE);
        tv_take_picture.setEnabled(true);
        JSONObject dataObj = jsonObject.getJSONObject("data");
        tv_success_code.setText(StringUtil.handleNullResultForString(dataObj.getString("code")));
        tv_success_phone.setText(StringUtil.handleNullResultForString(dataObj.getString("mobile")));
        tv_success_express.setText(StringUtil.handleNullResultForString(dataObj.getString("express_name")));
        tv_success_yundan.setText(StringUtil.handleNullResultForString(dataObj.getString("number")));
    }

    private void setFailure(JSONObject jsonObject, boolean isDelete) throws JSONException {
        layout_failure.setVisibility(View.VISIBLE);
        tv_failure_delete.setVisibility(isDelete ? View.VISIBLE : View.INVISIBLE);
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String msg = StringUtil.handleNullResultForString(jsonObject.getString("msg"));
        String code = StringUtil.handleNullResultForString(dataObj.getString("code"));
        String number = StringUtil.handleNullResultForString(dataObj.getString("number"));
        mobile = StringUtil.handleNullResultForString(dataObj.getString("mobile"));
        express_name = StringUtil.handleNullResultForString(dataObj.getString("express_name"));
        pie_id = StringUtil.handleNullResultForString(dataObj.getString("pie_id"));
        mYundan = StringUtil.handleNullResultForString(dataObj.getString("number"));
        mCode = StringUtil.handleNullResultForString(dataObj.getString("code"));

        tv_failure_title.setText(msg);
        et_failure_code.setText(code);
        et_failure_express.setText(express_name);
        et_failure_phone.setText(mobile);
        et_failure_danhao.setText(number);
    }

    private void hideAllpannel() {
        tv_message.setVisibility(View.GONE);
        layout_success.setVisibility(View.GONE);
        layout_failure.setVisibility(View.GONE);
    }


    private void savePhoto(Bitmap bitmap) {
        //判断SD卡是否可用
        if (!SystemUtil.hasSdcard()) {
            ToastUtil.showShort("无SD卡");
            return;
        }

        //相册文件夹
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }
        //保存的图片文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentName = "IMG_" + simpleDateFormat.format(new Date()) + ".jpg";
        String imagePath = cameraFolder.getAbsolutePath() + File.separator + currentName;
        imageFile = new File(imagePath);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        commitPicture();   //上传图片
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isOpened()) {
            camera.open();
            // SoundHelper.getInstance().playChangeSound();
        }
    }

    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("station_id", courier_id);
        params.put("uuid", UUID.randomUUID().toString());
        Request<String> request = NoHttpRequest.getCurrentData(params);
        mRequestQueue.add(REQUEST_HISTORY, request, onResponseListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.destroy();
        }
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
