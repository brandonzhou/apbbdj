package com.shshcom.camera;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.vision.barcode.Barcode;
import com.king.mlkit.vision.barcode.analyze.BarcodeScanningAnalyzer;
import com.king.mlkit.vision.camera.AnalyzeResult;
import com.king.mlkit.vision.camera.BaseCameraScanActivity;
import com.king.mlkit.vision.camera.analyze.Analyzer;

import java.util.List;
import java.util.regex.Pattern;

/**
 常见的条码类型

 QR码:
 在微信上常见的就是QR二维码，呈正方形，在3个角落印有像“回”字的图案，它们起到帮助解码软件定位的作用。
 QR码无论以任何角度扫描，数据都可正确被读取，且支持中文字符。

 CODE128/CODE39:
 CODE128码是广泛应用在企业内部管理、物流系统方面的条码码制，
 CODE128码可表示从 ASCII 0到ASCII 127 共128个字符，故称128码, 其中包含了数字、字母和符号字符。
 而CODE39是CODE128的精简版本，能显示的字符比CODE128少。

 EAN13/EAN8:
 EAN是欧洲物品编码的缩写，其中13位数字的EAN-13主要应用于超市和零售业。
 我国的分配到的代码有690-699。EAN8为EAN13的缩短版本，只能表示8位数字。

 UPC-A/UPC-E
 UPC是商品统一代码，目前主要在美国和加拿大地区使用，
 UPC码仅可用来表示数字，字码集为数字0~9。
 UPC码共有A、B、C、D、E等五种版本，常用的是UPC-A(12位数字)，UPC-E(6位数字)。

 Codabar库德巴码
 库德巴码是主要用于医疗卫生、图书情报、物资等领域。
 库德巴码可表示数字和字母信息，其字符集为数字0—9，A,B,C,D 4个大写英文字母以及6个特殊字符（-、：、/、. 、+、$），
 共20个字符。其中A,B,C,D只用作起始符和终止符。

 */
abstract public class SHCameraScanActivity extends BaseCameraScanActivity<List<Barcode>> {

    private View ivFlashlight;

    /**
     * 创建分析器，默认分析条码格式 128
     * @return
     */
    @Nullable
    @Override
    public Analyzer<List<Barcode>> createAnalyzer(){
        return new BarcodeScanningAnalyzer(Barcode.FORMAT_CODE_128);
    }


    /**
     * 处理识别 条码结果，返回code值
     * @param result ML
     * @return 条码 code
     */
    public String processScanResult(@NonNull AnalyzeResult<List<Barcode>> result){
        for(Barcode barcode : result.getResult()){
            @Nullable  String code = barcode.getRawValue();
            if(isMatchExpressCode(code)){
                return code;
            }
        }

        return "";
    }

    public Barcode processScanResultBarcode(@NonNull AnalyzeResult<List<Barcode>> result){
        for(Barcode barcode : result.getResult()){
            @Nullable  String code = barcode.getRawValue();
            if(isMatchExpressCode(code)){
                return barcode;
            }
        }

        return null;
    }


    final String regex = "^[A-Za-z0-9]+$";
    public boolean isMatchExpressCode(final CharSequence input) {
        return input != null && input.length() > 9 && Pattern.matches(regex, input);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int ivFlashlightId = getFlashlightId();
        if(ivFlashlightId != View.NO_ID && ivFlashlightId != 0){
            ivFlashlight = findViewById(ivFlashlightId);
            if(ivFlashlight != null){
                ivFlashlight.setOnClickListener(v -> onClickFlashlight());
            }
        }
    }

    /**
     * 获取 {@link #ivFlashlight} 的ID
     * @return  如果不需要手电筒按钮可以返回{@link View#NO_ID}
     */
    public int getFlashlightId(){
        return View.NO_ID;
    }

    /**
     * 点击手电筒
     */
    protected void onClickFlashlight(){
        toggleTorchState();
    }

    /**
     * 切换闪光灯状态（开启/关闭）
     */
    protected void toggleTorchState(){
        if(getCameraScan() != null){
            boolean isTorch = getCameraScan().isTorchEnabled();
            getCameraScan().enableTorch(!isTorch);
            if(ivFlashlight != null){
                ivFlashlight.setSelected(!isTorch);
            }
        }
    }


}
