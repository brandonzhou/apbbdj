package com.mt.bbdj.baseconfig.utls.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * @author 郭翰林
 * @date 2019/2/28 0028 17:06
 * 注释:相机预览视图
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean isPreview;
    private Context context;
    private DisplayMetrics dm;
    /**
     * 预览尺寸集合
     */
    private final SizeMap mPreviewSizes = new SizeMap();
    /**
     * 图片尺寸集合
     */
    private final SizeMap mPictureSizes = new SizeMap();
    /**
     * 屏幕旋转显示角度
     */
    private int mDisplayOrientation;
    /**
     * 设备屏宽比
     */
    private AspectRatio mAspectRatio;

    /**
     * 注释：构造函数
     * 时间：2019/2/28 0028 17:10
     * 作者：郭翰林
     *
     * @param context
     * @param mCamera
     */
    public CameraPreview(Context context, Camera mCamera,DisplayMetrics dm) {
        super(context);
        this.context = context;
        this.mCamera = mCamera;
        this.dm = dm;
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mDisplayOrientation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        mAspectRatio = AspectRatio.of(16, 9);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    private Size getPictureSize(SortedSet<Size> sizes) {
        if (sizes != null && sizes.size() != 0){
            return sizes.last();
        } else {
            return getOptimalPictureSize(mCamera.getParameters().getSupportedPictureSizes());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            //设置设备高宽比
            mAspectRatio = getDeviceAspectRatio((Activity) context);
            //设置预览方向
            mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            //获取所有支持的预览尺寸
            mPreviewSizes.clear();
            List<Size> previewSizeList = new ArrayList<>();
            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                mPreviewSizes.add(new Size(size.width, size.height));
                previewSizeList.add(new Size(size.width,size.height));
            }
            //获取所有支持的图片尺寸
            mPictureSizes.clear();
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                mPictureSizes.add(new Size(size.width, size.height));
            }
            // Size previewSize = getOptimalPreviewSize(mPreviewSizes.sizes(mAspectRatio));
            Size previewSize = getOptimalPreviewSize(previewSizeList,width,height);
            //Size pictureSize = mPictureSizes.sizes(mAspectRatio).last();
            Size pictureSize = getPictureSize(mPictureSizes.sizes(mAspectRatio));
            //设置相机参数
            parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
            parameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setRotation(90);
            mCamera.setParameters(parameters);
            //把这个预览效果展示在SurfaceView上面
            mCamera.setPreviewDisplay(holder);
            //开启预览效果
            mCamera.startPreview();
            isPreview = true;
        } catch (IOException e) {
            Log.e("CameraPreview", "相机预览错误: " + e.getMessage());
        }
//        if (holder.getSurface() == null) {
//            return;
//        }
//        //停止预览效果
//        mCamera.stopPreview();
//        //重新设置预览效果
//        try {
//            mCamera.setPreviewDisplay(mHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            if (isPreview) {
                //正在预览
                mCamera.stopPreview();
                mCamera.release();
            }
        }
    }


    /**
     * 注释：获取设备屏宽比
     * 时间：2019/3/4 0004 12:55
     * 作者：郭翰林
     */
    private AspectRatio getDeviceAspectRatio(Activity activity) {
        int width = activity.getWindow().getDecorView().getWidth();
        int height = activity.getWindow().getDecorView().getHeight();
        return AspectRatio.of(height, width);
    }

    /**
     * 注释：选择合适的预览尺寸
     * 时间：2019/3/4 0004 11:25
     * 作者：郭翰林
     *
     * @param sizes
     * @return
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private Size chooseOptimalSize(SortedSet<Size> sizes) {
        int desiredWidth;
        int desiredHeight;
        final int surfaceWidth = getWidth();
        final int surfaceHeight = getHeight();
        if (isLandscape(mDisplayOrientation)) {
            desiredWidth = surfaceHeight;
            desiredHeight = surfaceWidth;
        } else {
            desiredWidth = surfaceWidth;
            desiredHeight = surfaceHeight;
        }
        Size result = null;
        for (Size size : sizes) {
            if (desiredWidth <= size.getWidth() && desiredHeight <= size.getHeight()) {
                return size;
            }
            result = size;
        }
        return result;
    }

    private Size getOptimalPreviewSize(List<Size> sizes,int width, int height) {
        Camera.Size result = null;
        final Camera.Parameters p = mCamera.getParameters();
        //特别注意此处需要规定rate的比是大的比小的，不然有可能出现rate = height/width，但是后面遍历的时候，current_rate = width/height,所以我们限定都为大的比小的。
        float rate = (float) Math.max(width, height)/ (float)Math.min(width, height);
        float tmp_diff;
        float min_diff = -1f;
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            float current_rate = (float) Math.max(size.width, size.height)/ (float)Math.min(size.width, size.height);
            tmp_diff = Math.abs(current_rate-rate);
            if( min_diff < 0){
                min_diff = tmp_diff ;
                result = size;
            }
            if( tmp_diff < min_diff ){
                min_diff = tmp_diff ;
                result = size;
            }
        }
        return new Size(result.width,result.height);
    }

    private Size getOptimalPictureSize(List<Camera.Size> pictureSizes) {
        Camera.Size pictureSize = null;
        for (int i = 0; i < pictureSizes.size(); i++) {
            pictureSize = pictureSizes.get(i);
            if (pictureSize.width == dm.widthPixels && pictureSize.height == dm.heightPixels) {
                return new Size(pictureSize.width,pictureSize.height);
            }
        }

        for (int i = 0; i < pictureSizes.size(); i++) {
            pictureSize = pictureSizes.get(i);
            if (pictureSize.width > dm.widthPixels && pictureSize.height > dm.heightPixels) {
                return  new Size(pictureSize.width,pictureSize.height);
            }
        }
        return null;
    }

    /**
     * Test if the supplied orientation is in landscape.
     *
     * @param orientationDegrees Orientation in degrees (0,90,180,270)
     * @return True if in landscape, false if portrait
     */
    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == 90 ||
                orientationDegrees == 270);
    }
}
