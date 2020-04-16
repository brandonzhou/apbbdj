package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.baidu.ocr.sdk.utils.LogUtil;
import com.bumptech.glide.Glide;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.ExpressImage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressImageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.service.UploadService;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.utls.camera.CameraActivity;
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
import java.util.UUID;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class TakePictureActivity extends AppCompatActivity {

    private CameraView camera;
    private ImageView capturePictureSnapshot,iv_old_picture,iv_back;
    private TextView tv_express;
    private AppCompatTextView tv_code;
    private FrameLayout fram_layout_picture;

    public static final String KEY_IMAGE_PATH = "imagePath";
    private String picturePath = "/bbdj/picture";
    public static final int REQUEST_COMMIT_PICTURE = 100;
    private File cameraFolder = new File(Environment.getExternalStorageDirectory(), picturePath);
    private String currentName;
    private File imageFile;
    private String user_id="";
    private String express_id="";
    private String express_name="";
    private ExpressImageDao expressImageDao;
    private String courier_id="";
    private Intent mImageService;
    private RequestQueue mRequestQueue;
    private String oldPicture;
    private String mCode;

    public static void actionTo(Context context,String user_id,String express_id,String express){
        Intent intent = new Intent(context,TakePictureActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("express_id",express_id);
        intent.putExtra("express_name",express);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_picture);
        initParams();
        initView();
        initClickListener();
        requestData();
    }

    private void initClickListener() {
        fram_layout_picture.setOnClickListener(view -> CheckPictureDetailActivity.actionTo(TakePictureActivity.this,mCode,oldPicture));
        iv_back.setOnClickListener(view -> finish());
    }


    private void initParams() {
        user_id = getIntent().getStringExtra("user_id");
        express_id = getIntent().getStringExtra("express_id");
        express_name = getIntent().getStringExtra("express_name");
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        expressImageDao = mDaoSession.getExpressImageDao();
        mRequestQueue = NoHttp.newRequestQueue();
        mImageService = new Intent(this, UploadService.class);
        mImageService.putExtra("isBegin",true);
    }

    private void initView() {
        capturePictureSnapshot = findViewById(R.id.capturePictureSnapshot);
        camera = findViewById(R.id.camera);
        tv_express = findViewById(R.id.tv_express);
        iv_old_picture = findViewById(R.id.iv_old_picture);
        iv_back = findViewById(R.id.iv_back);
        tv_code = findViewById(R.id.tv_code);
        fram_layout_picture = findViewById(R.id.fram_layout_picture);
        tv_express.setText(express_name);
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new Listener());

        capturePictureSnapshot.setOnClickListener(v -> {
            if (camera.isTakingPicture()) return;
            if (camera.getPreview() != Preview.GL_SURFACE) {
                return;
            }
            camera.takePictureSnapshot();
            SoundHelper.getInstance().takePhoto();
        });
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {

        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            ToastUtil.showShort(exception.getReason()+"");
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            if (camera.isTakingVideo()) {
                return;
            }

            result.toBitmap(900, 1600, bitmap -> {
                savePhoto(bitmap);
                ToastUtil.showShort("拍照成功");
                oldPicture = imageFile.getAbsolutePath();
                mCode = "";
                setHistoreData();
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

    private void zipPhoto() {
        Luban.with(this)
                .load(imageFile)
                .ignoreBy(250)
                .setTargetDir(cameraFolder.getAbsolutePath())
                .setRenameListener(filePath -> currentName)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        LogUtil.d("压缩前", System.currentTimeMillis()+"");
                    }

                    @Override
                    public void onSuccess(File file) {

                        LogUtil.d("压缩后",System.currentTimeMillis()+"");
                        ExpressImage expressImage = new ExpressImage();
                        expressImage.setShelfNumber("0");
                        expressImage.setUuid(UUID.randomUUID().toString());
                        expressImage.setUser_id(user_id);
                        expressImage.setIsSync(0);
                        expressImage.setExpress_id(express_id);
                        expressImage.setImagePath(file.getAbsolutePath());
                        expressImageDao.save(expressImage);
                        TakePictureActivity.this.startService(mImageService);   //通知上传
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShort("图片压缩失败，请重试");
                    }
                }).launch();

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

        ExpressImage expressImage = new ExpressImage();
        expressImage.setShelfNumber("0");
        expressImage.setUuid(UUID.randomUUID().toString());
        expressImage.setUser_id(user_id);
        expressImage.setIsSync(0);
        expressImage.setExpress_id(express_id);
        expressImage.setImagePath(imageFile.getAbsolutePath());
        expressImageDao.save(expressImage);
        TakePictureActivity.this.startService(mImageService);   //通知上传
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
        params.put("station_id", user_id);
        Request<String> request = NoHttpRequest.getHistoryLastData(params);
        mRequestQueue.add(1001, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "CameraActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        handleResult(jsonObject);
                    } else {
                        ToastUtil.showShort(msg);
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

    private void handleResult(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        oldPicture = data.getString("picture");
        mCode = StringUtil.handleNullResultForString(data.getString("code"));
        setHistoreData();
    }

    private void setHistoreData() {
        Glide.with(TakePictureActivity.this).load(oldPicture).error(R.drawable.ic_error_outline_white_48dp).into(iv_old_picture);
        tv_code.setText(StringUtil.handleNullResultForString(mCode));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null){
            camera.destroy();
        }
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mRequestQueue = null;
    }
}
