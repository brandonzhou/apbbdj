package com.shshcom.station.storage.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.king.mlkit.vision.camera.AnalyzeResult;
import com.king.mlkit.vision.camera.analyze.Analyzer;
import com.king.mlkit.vision.camera.util.BitmapUtils;
import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.application.ZCSJApplication;
import com.shshcom.station.storage.ocrclip.ERectFindView;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TzScanAnalyzer implements Analyzer<List<Barcode>> {


    public interface OcrRecognizeCallback{
        void ocrCallback(String mobilePhone);
    }

    private TextRecognizer textRecognizer;
    private BarcodeScanner barcodeScanner;

    private ERectFindView rectFindView;


    private OcrRecognizeCallback ocrRecognizeCallback;



    private boolean openRecog = false;

    //private static final Pattern PATTERN =Pattern.compile("(1)\\d{10}");
//    private static final Pattern PATTERN = Pattern.compile("((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}");
    /*模型添加匹配95013 、 匹配手机号码换行和中间空格*/
    private static final Pattern PATTERN = Pattern.compile("(((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8})|((95013)\\d{5,12})|(((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{7}\\s*[0-9])");

    private  @NonNull OnAnalyzeListener<AnalyzeResult<List<Barcode>>> listener;

    public void setOpenRecog(boolean openRecog) {
        this.openRecog = openRecog;
    }





    /**
     * 需要分析识别区域
     */
    private Rect analyzeAreaRect = new Rect();


    public TzScanAnalyzer() {
        TextRecognizerOptionsInterface options = new  ChineseTextRecognizerOptions.Builder().build();
        textRecognizer = TextRecognition.getClient(options);
        barcodeScanner = BarcodeScanning.getClient(new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_CODE_128).build());


        int screenRotation = ((WindowManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int oritation = 1;
        if (screenRotation == 0 || screenRotation == 2) // 竖屏状态下

        Log.e("xxxxxx", "TzScanAnalyzer: screenRotation: "+ screenRotation+ " oritation: "+ oritation);


    }

    public void setOcrRecognizeCallback(OcrRecognizeCallback ocrRecognizeCallback){
        this.ocrRecognizeCallback = ocrRecognizeCallback;
    }


    public void setRectFindView(ERectFindView rectFindView) {
        this.rectFindView = rectFindView;
    }


    @Override
    public void analyze(@NonNull ImageProxy imageProxy, @NonNull OnAnalyzeListener<AnalyzeResult<List<Barcode>>> listener) {
        this.listener = listener;
//        int rotation = imageProxy.getImageInfo().getRotationDegrees();

        try {

            //tzOcrSDK.saveImage(bitmap, "source.png");

            if(openRecog){
                // OCR 识别
                recognizeOCR(imageProxy);

            }else {
                // 识别 条码
                final Bitmap bitmap = BitmapUtils.getBitmap(imageProxy);
                InputImage inputImage = InputImage.fromBitmap(bitmap,0);
                barcodeScanner.process(inputImage)
                        .addOnSuccessListener(result -> {
                            if(result == null || result.isEmpty()){
                                listener.onFailure();
                            }else{
                                listener.onSuccess(new AnalyzeResult(bitmap,result));
                            }
                        }).addOnFailureListener(e -> {
                    listener.onFailure();
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Bitmap getClipBitmap(Bitmap bitmap, Rect rect) {
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height(), null, false);
    }


    /**
     * ocr 识别手机号
     * @param imageProxy 视频流
     */
    private void recognizeOCR(ImageProxy imageProxy){


        boolean useWenTongOCR = false;

        if(useWenTongOCR){
            //Bitmap source = BitmapUtils.getBitmap(imageProxy);
            // 注意!!! 提取 bitmap后，进行 useWenTongOCR(imageProxy)，会造成文通无法识别
            //useWenTongOCR(imageProxy);
        }else {
            Bitmap source = BitmapUtils.getBitmap(imageProxy);
            int imageHeight = source.getHeight();
            int imageWidth = source.getWidth();

            if(rectFindView == null){
                // 重新扫描
                listener.onFailure();
                return;
            }
            // bug -> rectFindView  null
            analyzeAreaRect = rectFindView.getScanBoxAreaRect(imageWidth, imageHeight);
            //tzOcrSDK.saveImage(bitmap, "analyzeAreaRect.png");

            Bitmap bitmap = getClipBitmap(source, analyzeAreaRect);

            //useTFLite(bitmap);
            useML(bitmap);
        }



    }



    /**
     * 使用Google MLKit 进行 OCR识别
     * @param bitmap 预览框中的截图
     */
    private void useML(final Bitmap bitmap){

        InputImage inputImage = InputImage.fromBitmap(bitmap,0);


        textRecognizer.process(inputImage).addOnSuccessListener(result -> {
            if(result != null){

                String phone = getPhone(result.getText());
                if(!phone.isEmpty() && openRecog){
                    ocrRecognizeCallback.ocrCallback(phone);
                }

                Log.d("zzzzzz OCR result", "ocr result: " + result.getText());
                Log.d("zzzzzz OCR result", "\nocr phone: " + phone);

                // 触发重新扫描分析
                listener.onFailure();

            }
        }).addOnFailureListener(e -> {
            // 触发重新扫描分析
            listener.onFailure();
            e.printStackTrace();
        });
    }

    // 还需继续优化，手机号提取
    private String getPhone(String text){
        Matcher m = PATTERN.matcher(text);

        if(m.find()){
            return replaceAllSpace(m.group());
        }
        return "";

    }


    /**
     * 移除号码中间的空格、制表符、回车、换行
     * @param text
     * @return
     */
    private String replaceAllSpace(String text) {
        String result = "";
        if (text != null || !text.isEmpty()) {
            Pattern pattern = Pattern.compile("\\s*|\\t|\\n|\\r");
            Matcher matcher = pattern.matcher(text);
            result = matcher.replaceAll("");
            return result;

        }
        return result;
    }







    /**
     * YUV_420_888转NV21
     *
     * @param image CameraX ImageProxy
     * @return byte array
     */
    private byte[] yuv420ToNv21(ImageProxy image) {
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




}


