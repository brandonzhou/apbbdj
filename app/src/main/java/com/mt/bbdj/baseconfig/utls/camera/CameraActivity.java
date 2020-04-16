package com.mt.bbdj.baseconfig.utls.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginByCodeActivity;
import com.mt.bbdj.baseconfig.db.ExpressImage;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressImageDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.service.UploadService;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.PrintPannelActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;


/**
 * @author ZSK
 * @date 2019/2/28 0028 16:23
 * 注释:Android自定义相机
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_IMAGE_PATH = "imagePath";
    private String picturePath = "/bbdj/express";
    public static final int REQUEST_COMMIT_PICTURE = 100;
    private File cameraFolder = new File(Environment.getExternalStorageDirectory(), picturePath);
    /**
     * 相机预览
     */
    private FrameLayout mPreviewLayout;
    /**
     * 拍摄按钮视图
     */
    private RelativeLayout mPhotoLayout;

    /**
     * 闪光灯
     */
    private ImageView mFlashButton;
    /**
     * 拍照按钮
     */
    private ImageView mPhotoButton;


    /**
     * 聚焦视图
     */
    private OverCameraView mOverCameraView;
    /**
     * 相机类
     */
    private Camera mCamera;
    /**
     * Handle
     */
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    /**
     * 取消按钮
     */
    private Button mCancleButton;
    /**
     * 是否开启闪光灯
     */
    private boolean isFlashing;
    /**
     * 图片流暂存
     */
    private byte[] imageData;
    /**
     * 拍照标记
     */
    private boolean isTakePhoto;
    /**
     * 是否正在聚焦
     */
    private boolean isFoucing;
    /**
     * 蒙版类型
     */
    private MongolianLayerType mMongolianLayerType;

    /**
     * 护照出入境蒙版
     */
    private ImageView mPassportEntryAndExitImage;
    /**
     * 提示文案容器
     */
    private RelativeLayout rlCameraTip;
    private File imageFile;
    private String currentName;
    private RequestQueue mRequestQueue;
    private String mCode;
    private String mShelfNumber;
    private int mNumber;
    private DaoSession mDaoSession;
    private String user_id;

    private int enterAccount = 0;
    private ExpressImageDao expressImageDao;
    private Intent mImageService;
    private String express_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camre_layout);
        fitComprehensiveScreen();
        initParams();
        initView();
        requestPermission();
        setOnclickListener();
    }

    private void requestPermission() {
        if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(110)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .callback(this)
                    .start();
        }
    }

    @PermissionYes(110)
    private void getMultiYes(List<String> grantedPermissions) {
    }

    @PermissionNo(110)
    private void getMultiNo(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, 110).show();
        }
    }

    private void initParams() {
        mImageService = new Intent(this, UploadService.class);
        mImageService.putExtra("isBegin",true);
        mRequestQueue = NoHttp.newRequestQueue();
        mCode = getIntent().getStringExtra("shelves");
        express_id = getIntent().getStringExtra("express_id");
        mShelfNumber = getIntent().getStringExtra("num");
        mNumber = StringUtil.changeStringToInt(getIntent().getStringExtra("number"));
        mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        expressImageDao = mDaoSession.getExpressImageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }


    /**
     * 作者：郭翰林
     * 时间：2018/7/9 0009 8:54
     * 注释：适配全面屏
     */
    private void fitComprehensiveScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    /**
     * 启动拍照界面
     *
     * @param activity
     * @param requestCode
     * @param type
     */
    public static void actionTo(Activity activity, int requestCode, MongolianLayerType type, String code,String  mShelfNumber, String number,String express_id) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra("MongolianLayerType", type);
        intent.putExtra("shelves", code);
        intent.putExtra("number", number);
        intent.putExtra("num", mShelfNumber);
        intent.putExtra("express_id", express_id);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 注释：设置监听事件
     * 时间：2019/3/1 0001 11:13
     * 作者：郭翰林
     */
    private void setOnclickListener() {
        mCancleButton.setOnClickListener(this);
        mFlashButton.setOnClickListener(this);
        mPhotoButton.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !isTakePhoto) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "自动聚焦超时,请调整合适的位置拍摄！", Toast.LENGTH_SHORT);
                        isFoucing = false;
                        mOverCameraView.setFoucuing(false);
                        mOverCameraView.disDrawTouchFocusRect();
                    }
                };
                //设置聚焦超时
                mHandler.postDelayed(mRunnable, 3000);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 注释：自动对焦回调
     * 时间：2019/3/1 0001 10:02
     * 作者：郭翰林
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFoucing = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();
            //停止聚焦超时回调
            mHandler.removeCallbacks(mRunnable);
        }
    };

    /**
     * 注释：拍照并保存图片到相册
     * 时间：2019/3/1 0001 15:37
     * 作者：郭翰林
     */
    private void takePhoto() {
        if (enterAccount >= mNumber) {
            showPromitDialog();
            return;
        }
        isTakePhoto = true;
        //调用相机拍照
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera1) {
                //视图动画
                // mPhotoLayout.setVisibility(View.GONE);
                // AnimSpring.getInstance(mConfirmLayout).startRotateAnim(120, 360);
                imageData = data;
                // mPhotoLayout.setVisibility(View.VISIBLE);
                savePhoto();
                LubanToZip();    //压缩上传
                //停止预览
                mCamera.stopPreview();
            }
        });
    }

    private void showPromitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("货架入库已达上限,请重新选择货架")
                .setPositiveButton("确定", (dialogInterface, i) -> finish());
        builder.create().show();
    }

    /**
     * 注释：切换闪光灯
     * 时间：2019/3/1 0001 15:40
     * 作者：郭翰林
     */
    private void switchFlash() {
        isFlashing = !isFlashing;
        mFlashButton.setImageResource(isFlashing ? R.drawable.flash_open : R.drawable.flash_close);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(isFlashing ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Toast.makeText(this, "该设备不支持闪光灯", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 注释：取消保存
     * 时间：2019/3/1 0001 16:31
     * 作者：郭翰林
     */
    private void cancleSavePhoto() {
        mPhotoLayout.setVisibility(View.VISIBLE);
        // AnimSpring.getInstance(mPhotoLayout).startRotateAnim(120, 360);
        //开始预览
        mCamera.startPreview();
        imageData = null;
        isTakePhoto = false;
    }

    /**
     * 解析拍出照片的路径
     *
     * @param data
     * @return
     */
    public static String parseResult(Intent data) {
        return data.getStringExtra(KEY_IMAGE_PATH);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancle_button) {
            finish();
        } else if (id == R.id.take_photo_button) {
            if (!isTakePhoto) {
                takePhoto();
            }
        } else if (id == R.id.flash_button) {
            switchFlash();
        }
    }

    private void LubanToZip() {
        Luban.with(this)
                .load(imageFile)
                .ignoreBy(100)
                .putGear(50)//压缩等级
                .setTargetDir(cameraFolder.getAbsolutePath())
                .setRenameListener(filePath -> currentName)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        LogUtil.d("压缩前", System.currentTimeMillis()+"");
                    }

                    @Override
                    public void onSuccess(File file) {
                        enterAccount++;
                        if (enterAccount >= mNumber) {
                            showPromitDialog();
                        }
                        LogUtil.d("压缩后",System.currentTimeMillis()+"");
                        ExpressImage expressImage = new ExpressImage();
                        expressImage.setShelfNumber(mCode+mShelfNumber);
                        expressImage.setUuid(UUID.randomUUID().toString());
                        expressImage.setUser_id(user_id);
                        expressImage.setIsSync(0);
                        expressImage.setExpress_id(express_id);
                        expressImage.setImagePath(file.getAbsolutePath());
                        expressImageDao.save(expressImage);
                        CameraActivity.this.startService(mImageService);   //通知上传
                        //uploadPicture(file.getAbsolutePath());    //上传图片
                        isTakePhoto = false;
                        mCamera.startPreview();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShort("图片压缩失败，请重试");
                        isTakePhoto = false;
                        mCamera.startPreview();
                    }
                }).launch();
    }

    private void uploadPicture(String filePath) {
        if (!new File(filePath).exists()) {
            ToastUtil.showShort("文件不存在，请重拍！");
            return;
        }
        Request<String> request = NoHttpRequest.commitPannelPictureRequest(filePath, user_id,UUID.randomUUID().toString(),express_id);
        mRequestQueue.add(REQUEST_COMMIT_PICTURE, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                LoadDialogUtils.getInstance().showLoadingDialog(CameraActivity.this);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "CameraActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("statusCode").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        enterAccount++;
                        if (enterAccount >= mNumber) {
                            showPromitDialog();
                        }
                        ToastUtil.showShort(msg);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    isTakePhoto = false;
                    mCamera.startPreview();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showShort("请拍摄清晰图片");
                    LoadDialogUtils.cannelLoadingDialog();
                    isTakePhoto = false;
                    mCamera.startPreview();
                }
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                ToastUtil.showShort("请拍摄清晰图片");
                isTakePhoto = false;
                mCamera.startPreview();
                LoadDialogUtils.cannelLoadingDialog();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    /**
     * 注释：蒙版类型
     * 时间：2019/2/28 0028 16:26
     * 作者：郭翰林
     */
    public enum MongolianLayerType {
        /**
         * 护照个人信息
         */
        PASSPORT_PERSON_INFO,
        /**
         * 护照出入境
         */
        PASSPORT_ENTRY_AND_EXIT,
        /**
         * 身份证正面
         */
        IDCARD_POSITIVE,
        /**
         * 身份证反面
         */
        IDCARD_NEGATIVE,
        /**
         * 港澳通行证正面
         */
        HK_MACAO_TAIWAN_PASSES_POSITIVE,
        /**
         * 港澳通行证反面
         */
        HK_MACAO_TAIWAN_PASSES_NEGATIVE,
        /**
         * 银行卡
         */
        BANK_CARD
    }

    /**
     * 注释：初始化视图
     * 时间：2019/3/1 0001 11:12
     * 作者：郭翰林
     */
    private void initView() {

        mCancleButton = findViewById(R.id.cancle_button);
        mPreviewLayout = findViewById(R.id.camera_preview_layout);
        mPhotoLayout = findViewById(R.id.ll_photo_layout);
        mPhotoButton = findViewById(R.id.take_photo_button);
        mFlashButton = findViewById(R.id.flash_button);
        rlCameraTip = findViewById(R.id.camera_tip);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager()
                .getDefaultDisplay().getMetrics(dm);

        mCamera = Camera.open();
        CameraPreview preview = new CameraPreview(this, mCamera,dm);
        mOverCameraView = new OverCameraView(this);
        mPreviewLayout.addView(preview);
        mPreviewLayout.addView(mOverCameraView);
        if (mMongolianLayerType == null) {
            rlCameraTip.setVisibility(View.GONE);
            return;
        }
    }


    /**
     * 注释：保持图片
     * 时间：2019/3/1 0001 16:32
     * 作者：郭翰林
     */
    private void savePhoto() {
        FileOutputStream fos = null;

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
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    Intent intent = new Intent();
                    intent.putExtra(KEY_IMAGE_PATH, imagePath);
                    setResult(RESULT_OK, intent);
                } catch (IOException e) {
                    setResult(RESULT_FIRST_USER);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRequestQueue == null) {
            mRequestQueue.cancelAll();
            mRequestQueue.stop();
        }
    }
}
