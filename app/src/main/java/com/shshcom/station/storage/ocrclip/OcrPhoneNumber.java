package com.shshcom.station.storage.ocrclip;


public class OcrPhoneNumber extends OcrTypeHelper {
    public OcrPhoneNumber(int screenDirection) {
        super();
        if (screenDirection == 1){
            // 竖屏
            this.leftPointX = (float) 0.025;
            this.leftPointY = (float) 0.4;
            this.width = (float) 0.95;
            this.height = (float) 0.08;
            this.namePositionX = (float) 0.5;
            this.namePositionY = (float) 0.35;
        }else {
            // 横屏
            this.leftPointX = (float) 0.15;
            this.leftPointY = (float) 0.4;
            this.width = (float) 0.55;
            this.height = (float) 0.14;
            this.namePositionX = (float) 0.38;
            this.namePositionY = (float) 0.35;
        }

        /**
         * 模板分为三种：
         * vin 码扫描识别模板id    : SV_ID_VIN_CARWINDOW
         * 手机号码扫描识别模板id  : SV_ID_YYZZ_MOBILEPHONE
         * vin 码导入识别模板id    :SV_ID_VIN_MOBILE(导入识别的id在PictureRecogActivity中设置)
         */
        //扫描识别需要绑定的模板 id 为手机号扫描识别
        this.ocrId = "SV_ID_YYZZ_MOBILEPHONE";
        //手机号是没有导入识别的
        this.importTempalgeID = "";
        this.nameTextSize = 40;
        this.ocrTypeName = "请将手机号码水平靠近横线";
    }


}
