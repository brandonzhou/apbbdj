 package com.shshcom.station.storage.ml;

 import android.content.Context;
 import android.net.Uri;
 import android.os.Vibrator;
 import android.view.MotionEvent;
 import android.view.ScaleGestureDetector;
 import android.view.View;

 import androidx.annotation.FloatRange;
 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.camera.core.Camera;
 import androidx.camera.core.CameraSelector;
 import androidx.camera.core.FocusMeteringAction;
 import androidx.camera.core.ImageAnalysis;
 import androidx.camera.core.ImageCapture;
 import androidx.camera.core.ImageCaptureException;
 import androidx.camera.core.ImageProxy;
 import androidx.camera.core.MeteringPoint;
 import androidx.camera.core.Preview;
 import androidx.camera.core.TorchState;
 import androidx.camera.core.ZoomState;
 import androidx.camera.lifecycle.ProcessCameraProvider;
 import androidx.camera.view.PreviewView;
 import androidx.core.content.ContextCompat;
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentActivity;
 import androidx.lifecycle.LifecycleOwner;
 import androidx.lifecycle.MutableLiveData;

 import com.google.common.util.concurrent.ListenableFuture;
 import com.king.mlkit.vision.camera.AnalyzeResult;
 import com.king.mlkit.vision.camera.CameraScan;
 import com.king.mlkit.vision.camera.analyze.Analyzer;
 import com.king.mlkit.vision.camera.config.CameraConfig;
 import com.king.mlkit.vision.camera.manager.AmbientLightManager;
 import com.king.mlkit.vision.camera.manager.BeepManager;
 import com.king.mlkit.vision.camera.util.LogUtils;

 import java.io.File;
 import java.nio.ByteBuffer;
 import java.util.concurrent.Executors;

 public class MyBaseCameraScan <T> extends CameraScan<T> {


     /**
      * Defines the maximum duration in milliseconds between a touch pad
      * touch and release for a given touch to be considered a tap (click) as
      * opposed to a hover movement gesture.
      */
     private static final int HOVER_TAP_TIMEOUT = 150;

     /**
      * Defines the maximum distance in pixels that a touch pad touch can move
      * before being released for it to be considered a tap (click) as opposed
      * to a hover movement gesture.
      */
     private static final int HOVER_TAP_SLOP = 20;

     private FragmentActivity mFragmentActivity;
     private Context mContext;
     private LifecycleOwner mLifecycleOwner;
     private PreviewView mPreviewView;

     private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
     private Camera mCamera;

     private MyResolutionCameraConfig mCameraConfig;
     private Analyzer<T> mAnalyzer;
     ImageCapture imageCapture;


     /**
      * 是否分析
      */
     private volatile boolean isAnalyze = true;

     /**
      * 是否已经分析出结果
      */
     private volatile boolean isAnalyzeResult;

     private View flashlightView;

     private MutableLiveData<AnalyzeResult<T>> mResultLiveData;

     private OnScanResultCallback mOnScanResultCallback;
     private Analyzer.OnAnalyzeListener<AnalyzeResult<T>> mOnAnalyzeListener;

     private BeepManager mBeepManager;
     private AmbientLightManager mAmbientLightManager;

     private int mOrientation;
     private int mScreenWidth;
     private int mScreenHeight;
     private long mLastAutoZoomTime;
     private long mLastHoveTapTime;
     private boolean isClickTap;
     private float mDownX;
     private float mDownY;



     private Vibrator mVibrator;
     private int oritation;
     private int screenRotation;



     public MyBaseCameraScan(@NonNull FragmentActivity activity, @NonNull  PreviewView previewView){
         this.mFragmentActivity = activity;
         this.mLifecycleOwner = activity;
         this.mContext = activity;
         this.mPreviewView = previewView;
         initData();
     }

     public MyBaseCameraScan(@NonNull Fragment fragment, @NonNull PreviewView previewView){
         this.mFragmentActivity = fragment.getActivity();
         this.mLifecycleOwner = fragment;
         this.mContext = fragment.getContext();
         this.mPreviewView = previewView;
         initData();
     }

     private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener(){
         @Override
         public boolean onScale(ScaleGestureDetector detector) {
             float scale = detector.getScaleFactor();
             if(mCamera != null){
                 float ratio = mCamera.getCameraInfo().getZoomState().getValue().getZoomRatio();
                 zoomTo(ratio * scale);
             }
             return true;
         }

     };

     private void initData(){
         mResultLiveData = new MutableLiveData<>();
         mResultLiveData.observe(mLifecycleOwner, result -> {
             isAnalyzeResult = false;
             if(result != null){
                 handleAnalyzeResult(result);
             }else if(mOnScanResultCallback != null){
                 mOnScanResultCallback.onScanResultFailure();
             }
         });

         mOnAnalyzeListener = new Analyzer.OnAnalyzeListener<AnalyzeResult<T>>() {

             @Override
             public void onSuccess(@NonNull AnalyzeResult<T> result) {
                 mResultLiveData.postValue(result);
             }

             @Override
             public void onFailure() {
                 mResultLiveData.postValue(null);
             }
         };

         mOrientation = mContext.getResources().getConfiguration().orientation;

         ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(mContext, mOnScaleGestureListener);
         mPreviewView.setOnTouchListener((v, event) -> {
             handlePreviewViewClickTap(event);
             if(isNeedTouchZoom()){
                 return scaleGestureDetector.onTouchEvent(event);
             }
             return false;
         });

         mBeepManager = new BeepManager(mContext);
         mAmbientLightManager = new AmbientLightManager(mContext);
         if(mAmbientLightManager != null){
             mAmbientLightManager.register();
             mAmbientLightManager.setOnLightSensorEventListener((dark, lightLux) -> {
                 if(flashlightView != null){
                     if(dark){
                         if(flashlightView.getVisibility() != View.VISIBLE){
                             flashlightView.setVisibility(View.VISIBLE);
                             flashlightView.setSelected(isTorchEnabled());
                         }
                     }else if(flashlightView.getVisibility() == View.VISIBLE && !isTorchEnabled()){
                         flashlightView.setVisibility(View.INVISIBLE);
                         flashlightView.setSelected(false);
                     }

                 }
             });
         }

     }

     private void handlePreviewViewClickTap(MotionEvent event){
         if(event.getPointerCount() == 1){
             switch (event.getAction()){
                 case MotionEvent.ACTION_DOWN:
                     isClickTap = true;
                     mDownX = event.getX();
                     mDownY = event.getY();
                     mLastHoveTapTime = System.currentTimeMillis();
                     break;
                 case MotionEvent.ACTION_MOVE:
                     isClickTap = distance(mDownX,mDownY,event.getX(),event.getY()) < HOVER_TAP_SLOP;
                     break;
                 case MotionEvent.ACTION_UP:
                     if(isClickTap && mLastHoveTapTime + HOVER_TAP_TIMEOUT > System.currentTimeMillis()){
                         startFocusAndMetering(event.getX(),event.getY());
                     }
                     break;
             }
         }
     }

     private float distance(float aX, float aY, float bX, float bY) {
         float xDiff = aX - bX;
         float yDiff = aY - bY;
         return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
     }

     private void startFocusAndMetering(float x, float y){
         if(mCamera != null){
             LogUtils.d("startFocusAndMetering:" + x + "," + y);
             MeteringPoint point = mPreviewView.getMeteringPointFactory().createPoint(x,y);
             mCamera.getCameraControl().startFocusAndMetering(new FocusMeteringAction.Builder(point).build());
         }
     }



     private void initConfig(){
         if(mCameraConfig == null){
             mCameraConfig = new MyResolutionCameraConfig(mContext);
         }
     }


     @Override
     public CameraScan setCameraConfig(CameraConfig cameraConfig) {
         if(cameraConfig != null){
             //this.mCameraConfig = cameraConfig;
         }
         return this;
     }

     @Override
     public void startCamera(){
         initConfig();
         mCameraProviderFuture = ProcessCameraProvider.getInstance(mContext);
         mCameraProviderFuture.addListener(() -> {

                 // 在 initConfig()中，已配置 MyResolutionCameraConfig(mContext)，设置对应的mTargetSize
                 Preview preview = mCameraConfig.options(new Preview.Builder());

                 //相机选择器
                 CameraSelector cameraSelector = mCameraConfig.options(new CameraSelector.Builder());


                 //图像分析
                 ImageAnalysis imageAnalysis = mCameraConfig.options(new ImageAnalysis.Builder()
                         .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST));
                 imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), image -> {
 //                    Log.e("zzzzzz", "isAnalyze: "+ isAnalyze + " isAnalyzeResult:"+ isAnalyzeResult);
                     if(isAnalyze && !isAnalyzeResult && mAnalyzer != null){
                         isAnalyzeResult = true;
                         mAnalyzer.analyze(image,mOnAnalyzeListener);
                     }
                     image.close();
                 });

                 imageCapture = mCameraConfig.options(new ImageCapture.Builder()
                         //优化捕获速度，可能降低图片质量
                         .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) );

             try{
                 if(mCamera != null){
                     mCameraProviderFuture.get().unbindAll();
                 }

                 //绑定到生命周期
                 mCamera = mCameraProviderFuture.get().bindToLifecycle(mLifecycleOwner, cameraSelector, preview, imageAnalysis, imageCapture);
                 //设置SurfaceProvider
                 preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
             }catch (Exception e){
                 LogUtils.e(e);
             }

         }, ContextCompat.getMainExecutor(mContext));
     }

     public byte[] imageProxyToNV21(ImageProxy imageProxy){
         ImageProxy.PlaneProxy[] plane = imageProxy.getPlanes();
         ByteBuffer yBuffer = plane[0].getBuffer();  // Y
         ByteBuffer uBuffer = plane[1].getBuffer();  // U
         ByteBuffer vBuffer = plane[2].getBuffer();  // V

         int ySize = yBuffer.remaining();
         int uSize = uBuffer.remaining();
         int vSize = vBuffer.remaining();

         byte[] nv21 = new byte[ySize + uSize + vSize];
 //        System.out.println("ySize : " + ySize + "; uSize :" + uSize + ";vSize :" + vSize);
         //U and V are swapped
         yBuffer.get(nv21, 0, ySize);
         vBuffer.get(nv21, ySize, vSize);
         uBuffer.get(nv21, ySize + vSize, uSize);
         return nv21;
     }

     /**
      * YUV_420_888转NV21
      *
      * @param image CameraX ImageProxy
      * @return byte array
      */
     public static byte[] yuv420ToNv21(ImageProxy image) {
         ImageProxy.PlaneProxy[] planes = image.getPlanes();
         ByteBuffer yBuffer = planes[0].getBuffer();
         ByteBuffer uBuffer = planes[1].getBuffer();
         ByteBuffer vBuffer = planes[2].getBuffer();
         int ySize = yBuffer.remaining();
         int uSize = uBuffer.remaining();
         int vSize = vBuffer.remaining();
         int size = image.getWidth() * image.getHeight();
         byte[] nv21 = new byte[size * 3 / 2];
         yBuffer.get(nv21, 0, ySize);
         vBuffer.get(nv21, ySize, vSize);
         byte[] u = new byte[uSize];
         uBuffer.get(u);
         //每隔开一位替换V，达到VU交替
         int pos = ySize + 1;
         for (int i = 0; i < uSize; i++) {
             if (i % 2 == 0) {
                 nv21[pos] = u[i];
                 pos += 2;
             }
         }
         return nv21;
     }

     /**
      * 处理分析结果
      * @param result
      */
     private synchronized void handleAnalyzeResult(AnalyzeResult<T> result){

         if(isAnalyzeResult || !isAnalyze){
             return;
         }

         if(mBeepManager != null){
             mBeepManager.playBeepSoundAndVibrate();
         }

         if(mOnScanResultCallback != null){
             mOnScanResultCallback.onScanResultCallback(result);
         }
     }


     @Override
     public void stopCamera(){
         if(mCameraProviderFuture != null){
             try {
                 mCameraProviderFuture.get().unbindAll();
             }catch (Exception e){
                 LogUtils.e(e);
             }
         }
     }

     @Override
     public CameraScan setAnalyzeImage(boolean analyze) {
         isAnalyze = analyze;
         return this;
     }

     @Override
     public CameraScan setAnalyzer(Analyzer<T> analyzer) {
         mAnalyzer = analyzer;
         return this;
     }

     @Override
     public void zoomIn(){
         if(mCamera != null){
             float ratio = mCamera.getCameraInfo().getZoomState().getValue().getZoomRatio() + 0.1f;
             float maxRatio = mCamera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio();
             if(ratio <= maxRatio){
                 mCamera.getCameraControl().setZoomRatio(ratio);
             }
         }
     }

     @Override
     public void zoomOut(){
         if(mCamera != null){
             float ratio = mCamera.getCameraInfo().getZoomState().getValue().getZoomRatio() - 0.1f;
             float minRatio = mCamera.getCameraInfo().getZoomState().getValue().getMinZoomRatio();
             if(ratio >= minRatio){
                 mCamera.getCameraControl().setZoomRatio(ratio);
             }
         }
     }


     @Override
     public void zoomTo(float ratio) {
         if(mCamera != null){
             ZoomState zoomState = mCamera.getCameraInfo().getZoomState().getValue();
             float maxRatio = zoomState.getMaxZoomRatio();
             float minRatio = zoomState.getMinZoomRatio();
             float zoom = Math.max(Math.min(ratio,maxRatio),minRatio);
             mCamera.getCameraControl().setZoomRatio(zoom);
         }
     }

     @Override
     public void lineZoomIn() {
         if(mCamera != null){
             float zoom = mCamera.getCameraInfo().getZoomState().getValue().getLinearZoom() + 0.1f;
             if(zoom <= 1f){
                 mCamera.getCameraControl().setLinearZoom(zoom);
             }
         }
     }

     @Override
     public void lineZoomOut() {
         if(mCamera != null){
             float zoom = mCamera.getCameraInfo().getZoomState().getValue().getLinearZoom() - 0.1f;
             if(zoom >= 0f){
                 mCamera.getCameraControl().setLinearZoom(zoom);
             }
         }
     }

     @Override
     public void lineZoomTo(@FloatRange(from = 0.0,to = 1.0) float linearZoom) {
         if(mCamera != null){
             mCamera.getCameraControl().setLinearZoom(linearZoom);
         }
     }

     @Override
     public void enableTorch(boolean torch) {
         if(mCamera != null && hasFlashUnit()){
             mCamera.getCameraControl().enableTorch(torch);
         }
     }

     @Override
     public boolean isTorchEnabled() {
         if(mCamera != null){
             return mCamera.getCameraInfo().getTorchState().getValue() == TorchState.ON;
         }
         return false;
     }

     /**
      * 是否支持闪光灯
      * @return
      */
     @Override
     public boolean hasFlashUnit(){
         if(mCamera != null){
             return mCamera.getCameraInfo().hasFlashUnit();
         }
         return false;
     }

     @Override
     public CameraScan setVibrate(boolean vibrate) {
         if(mBeepManager != null){
             mBeepManager.setVibrate(vibrate);
         }
         return this;
     }

     @Override
     public CameraScan setPlayBeep(boolean playBeep) {
         if(mBeepManager != null){
             mBeepManager.setPlayBeep(playBeep);
         }
         return this;
     }

     @Override
     public CameraScan setOnScanResultCallback(OnScanResultCallback callback) {
         this.mOnScanResultCallback = callback;
         return this;
     }

     @Nullable
     @Override
     public Camera getCamera(){
         return mCamera;
     }


     @Override
     public void release() {
         isAnalyze = false;
         flashlightView = null;
         if(mAmbientLightManager != null){
             mAmbientLightManager.unregister();
         }
         if(mBeepManager != null){
             mBeepManager.close();
         }
         stopCamera();
     }

     @Override
     public CameraScan bindFlashlightView(@Nullable View v) {
         flashlightView = v;
         if(mAmbientLightManager != null){
             mAmbientLightManager.setLightSensorEnabled(v != null);
         }
         return this;
     }

     public CameraScan setDarkLightLux(float lightLux){
         if(mAmbientLightManager != null){
             mAmbientLightManager.setDarkLightLux(lightLux);
         }
         return this;
     }

     public CameraScan setBrightLightLux(float lightLux){
         if(mAmbientLightManager != null){
             mAmbientLightManager.setBrightLightLux(lightLux);
         }
         return this;
     }

     public ImageCapture getImageCapture(){
         return  imageCapture;
     }

     public void takePicture(File file){
         ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file)
                 .build();

         imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor() , new ImageCapture.OnImageSavedCallback() {
             @Override
             public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                 Uri savedUri = outputFileResults.getSavedUri();

                 if(savedUri == null){
                     savedUri = Uri.fromFile(file);
                 }
 //                outputFilePath = file.getAbsolutePath();
 //                onFileSaved(savedUri);
             }

             @Override
             public void onError(@NonNull ImageCaptureException exception) {
                 LogUtils.e( "Photo capture failed: "+exception.getMessage(), exception);
             }
         });

     }


 }
