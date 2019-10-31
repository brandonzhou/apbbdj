package com.mt.bbdj.baseconfig.view.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;


import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.RxImageTool;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 相机辅助类，和{@link CameraListener}共同使用，获取nv21数据等操作
 */
public class CameraHelper {
    private Rect framingRect;
    private Rect framingRectInPreview;
    private static final String TAG = "CameraHelper";
    private final boolean useOneShotPreviewCallback;
    private Camera mCamera;
    private int mCameraId;
    private Point previewViewSize;
    private TextureView previewDisplayView;
    private Camera.Size previewSize;
    private Camera.Size pictureSize;
    private DisplayMetrics displayMetrics;
    private Point specificPreviewSize;
    private int displayOrientation = 0;
    private int rotation;
    private int additionalRotation;
    private boolean isMirror = false;
    private PreviewCallback previewCallback;
    private final AutoFocusCallback autoFocusCallback;

    private Integer specificCameraId = null;
    private CameraListener cameraListener;
    private boolean previewing;
    private CameraConfigurationManager configManager;

    private Context mContext;

    public static int FRAME_WIDTH = -1;
    public static int FRAME_HEIGHT = -1;
    public static int FRAME_MARGINTOP = -1;

    static final int SDK_INT;

    static {
        int sdkInt;
        try {
            sdkInt = Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException nfe) {
            // Just to be safe
            sdkInt = 10000;
        }
        SDK_INT = sdkInt;
    }

    private boolean initialized;

    private CameraHelper(CameraHelper.Builder builder, Context context) {
        previewDisplayView = builder.previewDisplayView;
        specificCameraId = builder.specificCameraId;
        cameraListener = builder.cameraListener;
        rotation = builder.rotation;
        additionalRotation = builder.additionalRotation;
        previewViewSize = builder.previewViewSize;
        specificPreviewSize = builder.previewSize;
        isMirror = builder.isMirror;
        displayMetrics = builder.displayMetrics;
        previewDisplayView.setSurfaceTextureListener(textureListener);
        if (isMirror) {
            previewDisplayView.setScaleX(-1);
        }
        mContext = context;
        autoFocusCallback = new AutoFocusCallback();
        useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3;
        previewCallback = new PreviewCallback(this, useOneShotPreviewCallback);
        configManager = new CameraConfigurationManager(context);
    }

    public void start() {
        synchronized (this) {
            if (mCamera != null) {
                return;
            }
            //相机数量为2则打开1,1则打开0,相机ID 1为前置，0为后置
            mCameraId = Camera.getNumberOfCameras() - 1;
            //若指定了相机ID且该相机存在，则打开指定的相机
            if (specificCameraId != null && specificCameraId <= mCameraId) {
                mCameraId = specificCameraId;
            }

            //没有相机
            if (mCameraId == -1) {
                if (cameraListener != null) {
                    cameraListener.onCameraError(new Exception("camera not found"));
                }
                return;
            }
            if (mCamera == null) {
                mCamera = Camera.open(mCameraId);
                if (!initialized) {
                    initialized = true;
                    configManager.initFromCameraParameters(mCamera);
                }
                configManager.setDesiredCameraParameters(mCamera);
            }
            displayOrientation = getCameraOri(rotation);
            mCamera.setDisplayOrientation(displayOrientation);
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);

                //预览大小设置
                previewSize = parameters.getPreviewSize();
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                if (supportedPreviewSizes != null && supportedPreviewSizes.size() > 0) {
                    previewSize = getBestSupportedSize(supportedPreviewSizes, previewViewSize);
                }
                Camera.Size pictureSie = getOptimalPictureSize(getSupportedPictureSizes());
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                parameters.setPictureSize(pictureSie.width, pictureSie.height);

                //对焦模式设置
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
                     if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    }else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }
                }

                mCamera.setParameters(parameters);
                mCamera.setPreviewTexture(previewDisplayView.getSurfaceTexture());
                mCamera.setPreviewCallbackWithBuffer(previewCallback);
                mCamera.addCallbackBuffer(new byte[previewSize.width * previewSize.height * 3 / 2]);

                mCamera.startPreview();
                mCamera.cancelAutoFocus();
                if (cameraListener != null) {
                    cameraListener.onCameraOpened(mCamera, mCameraId, displayOrientation, isMirror);
                }
            } catch (Exception e) {
                if (cameraListener != null) {
                    cameraListener.onCameraError(e);
                }
            }
            previewing = true;
        }
    }



    private Camera.Size getOptimalPictureSize(List<Camera.Size> pictureSizes) {
        Camera.Size pictureSize = null;
        for (int i = 0; i < pictureSizes.size(); i++) {
            pictureSize = pictureSizes.get(i);
            if (pictureSize.width == displayMetrics.widthPixels && pictureSize.height == displayMetrics.heightPixels) {
                return pictureSize;
            }
        }

        for (int i = 0; i < pictureSizes.size(); i++) {
            pictureSize = pictureSizes.get(i);
            if (pictureSize.width > displayMetrics.widthPixels && pictureSize.height > displayMetrics.heightPixels) {
                return pictureSize;
            }
        }
        return null;
    }


    public Camera.Size getPreviewViewSize() {
        return previewSize;
    }

    private int getCameraOri(int rotation) {
        int degrees = rotation * 90;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        additionalRotation /= 90;
        additionalRotation *= 90;
        degrees += additionalRotation;
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public void stop() {
        synchronized (this) {
            if (mCamera == null) {
                return;
            }
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            previewing = false;
            if (cameraListener != null) {
                cameraListener.onCameraClosed();
            }
        }
    }

    public void requestPreviewFrame(Handler handler, int message) {
        if (mCamera != null && previewing) {
            previewCallback.setHandler(handler, message);
            if (useOneShotPreviewCallback) {
                mCamera.setOneShotPreviewCallback(previewCallback);
            } else {
                mCamera.setPreviewCallback(previewCallback);
            }
        }
    }

    public Camera getmCamera() {
        return mCamera;
    }

    public void requestAutoFocus(Handler handler, int tag) {
        if (mCamera != null && previewing) {
            mCamera.cancelAutoFocus();
            autoFocusCallback.setHandler(handler, tag);
           /* Message message = handler.obtainMessage(tag, tag);
            handler.sendMessageDelayed(message, 1500L);*/
            //mCamera.autoFocus(autoFocusCallback);
            delayHandler.sendEmptyMessageDelayed(1,2000L);
            Log.d(TAG, "Requesting auto-focus callback");
        }
    }

    private Handler delayHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCamera != null) {
                mCamera.autoFocus(autoFocusCallback);
            }
        }
    };

    public boolean isStopped() {
        synchronized (this) {
            return mCamera == null;
        }
    }

    public void release() {
        synchronized (this) {
            stop();
            previewDisplayView = null;
            specificCameraId = null;
            cameraListener = null;
            previewViewSize = null;
            specificPreviewSize = null;
            previewSize = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, Point previewViewSize) {
        if (sizes == null || sizes.size() == 0) {
            return mCamera.getParameters().getPreviewSize();
        }
        Camera.Size[] tempSizes = sizes.toArray(new Camera.Size[0]);
        Arrays.sort(tempSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                if (o1.width > o2.width) {
                    return -1;
                } else if (o1.width == o2.width) {
                    return o1.height > o2.height ? -1 : 1;
                } else {
                    return 1;
                }
            }
        });
        sizes = Arrays.asList(tempSizes);

        Camera.Size bestSize = sizes.get(0);
        float previewViewRatio;
        if (previewViewSize != null) {
            previewViewRatio = (float) previewViewSize.x / (float) previewViewSize.y;
        } else {
            previewViewRatio = (float) bestSize.width / (float) bestSize.height;
        }

        if (previewViewRatio > 1) {
            previewViewRatio = 1 / previewViewRatio;
        }
        boolean isNormalRotate = (additionalRotation % 180 == 0);

        for (Camera.Size s : sizes) {
            if (specificPreviewSize != null && specificPreviewSize.x == s.width && specificPreviewSize.y == s.height) {
                return s;
            }
            if (isNormalRotate) {
                if (Math.abs((s.height / (float) s.width) - previewViewRatio) < Math.abs(bestSize.height / (float) bestSize.width - previewViewRatio)) {
                    bestSize = s;
                }
            } else {
                if (Math.abs((s.width / (float) s.height) - previewViewRatio) < Math.abs(bestSize.width / (float) bestSize.height - previewViewRatio)) {
                    bestSize = s;
                }
            }
        }
        return bestSize;
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public List<Camera.Size> getSupportedPictureSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPictureSizes();
    }


    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
//            start();
            if (mCamera != null) {
                try {
                    mCamera.setPreviewTexture(surfaceTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            Log.i(TAG, "onSurfaceTextureSizeChanged: " + width + "  " + height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            stop();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview();
        int previewFormat = configManager.getPreviewFormat();
        String previewFormatString = configManager.getPreviewFormatString();
        switch (previewFormat) {
            // This is the standard Android format which all devices are REQUIRED to support.
            // In theory, it's the only one we should ever care about.
            case PixelFormat.YCbCr_420_SP:
                // This format has never been seen in the wild, but is compatible as we only care
                // about the Y channel, so allow it.
            case PixelFormat.YCbCr_422_SP:
                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
            default:
                // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can read it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                            rect.width(), rect.height());
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);
    }


    public Rect getFramingRectInPreview() {
        Rect localRect = new Rect();

        int framgHeight = RxImageTool.dp2px(250);    //扫码框的高
        previewDisplayView.getGlobalVisibleRect(localRect);
        Log.d("A--localRect_old::::", localRect.toString());
        framgHeight = framgHeight + localRect.top;

        Camera.Size presizeSize = getPreviewViewSize();
        Log.d("A--presizeSize::::", presizeSize.width + " " + presizeSize.height + "");
        Point screenResolution = configManager.getScreenResolution();

        int scale = screenResolution.y / framgHeight;

        Log.d("A--screenResolution::::", screenResolution.toString());
        Point preViewPoint = new Point(presizeSize.width, presizeSize.height);
        Log.d("A--preViewPoint::::", preViewPoint.toString());
        localRect.left = localRect.left * preViewPoint.y / screenResolution.x;
        localRect.right = localRect.right * preViewPoint.y / screenResolution.x;
        localRect.top = localRect.top * preViewPoint.x / screenResolution.y;
        localRect.bottom = localRect.bottom * preViewPoint.x / (screenResolution.y * scale);
        framingRectInPreview = localRect;
        Log.d("A--localRect::::", localRect.toString());

        return framingRectInPreview;
    }


    public Rect getFramingRect() {
        try {
            Point screenResolution = configManager.getScreenResolution();
            // if (framingRect == null) {
            if (mCamera == null) {
                return null;
            }

            int leftOffset = (screenResolution.x - FRAME_WIDTH) / 2;

            int topOffset;
            if (FRAME_MARGINTOP != -1) {
                topOffset = FRAME_MARGINTOP;
            } else {
                topOffset = (screenResolution.y - FRAME_HEIGHT) / 2;
            }
            framingRect = new Rect(leftOffset, topOffset, leftOffset + FRAME_WIDTH, topOffset + FRAME_HEIGHT);
            // }
            return framingRect;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void takePicture(Camera.PictureCallback pictureCallback) {
        if (mCamera != null) {
            mCamera.takePicture(null, null, null, pictureCallback);
        }
    }


    public void changeDisplayOrientation(int rotation) {
        if (mCamera != null) {
            this.rotation = rotation;
            displayOrientation = getCameraOri(rotation);
            mCamera.setDisplayOrientation(displayOrientation);
            if (cameraListener != null) {
                cameraListener.onCameraConfigurationChanged(mCameraId, displayOrientation);
            }
        }
    }

    public static final class Builder {

        /**
         * 预览显示的textureView
         */
        private TextureView previewDisplayView;

        /**
         * 是否镜像显示，只支持textureView
         */
        private boolean isMirror;
        /**
         * 指定的相机ID
         */
        private Integer specificCameraId;
        /**
         * 事件回调
         */
        private CameraListener cameraListener;
        /**
         * 屏幕的长宽，在选择最佳相机比例时用到
         */
        private Point previewViewSize;
        /**
         * 传入getWindowManager().getDefaultDisplay().getRotation()的值即可
         */
        private int rotation;
        /**
         * 指定的预览宽高，若系统支持则会以这个预览宽高进行预览
         */
        private Point previewSize;

        /**
         * 额外的旋转角度（用于适配一些定制设备）
         */
        private int additionalRotation;

        private Context mContext;

        private DisplayMetrics displayMetrics;

        /**
         * 图片尺寸
         */
        private Camera.Size pictureSize;

        public Builder() {
        }


        public Builder previewOn(TextureView val) {
            previewDisplayView = val;
            return this;
        }


        public Builder isMirror(boolean val) {
            isMirror = val;
            return this;
        }

        public Builder previewSize(Point val) {
            previewSize = val;
            return this;
        }

        public Builder setContext(Context context) {
            mContext = context;
            return this;
        }

        public Builder previewViewSize(Point val) {
            previewViewSize = val;
            return this;
        }

        public Builder pictureSize(DisplayMetrics dm) {

            displayMetrics = dm;
            return this;
        }

        public Builder rotation(int val) {
            rotation = val;
            return this;
        }

        public Builder additionalRotation(int val) {
            additionalRotation = val;
            return this;
        }

        public Builder specificCameraId(Integer val) {
            specificCameraId = val;
            return this;
        }

        public Builder cameraListener(CameraListener val) {
            cameraListener = val;
            return this;
        }

        public CameraHelper build() {
            if (previewViewSize == null) {
                Log.e(TAG, "previewViewSize is null, now use default previewSize");
            }
            if (cameraListener == null) {
                Log.e(TAG, "cameraListener is null, callback will not be called");
            }
            if (previewDisplayView == null) {
                throw new RuntimeException("you must preview on a textureView or a surfaceView");
            }
            return new CameraHelper(this, mContext);
        }
    }

}
