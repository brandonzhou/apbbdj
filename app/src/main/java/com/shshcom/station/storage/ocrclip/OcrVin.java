package com.shshcom.station.storage.ocrclip;

/**
 * Vin 参数类，包括横屏和竖屏的参数，这里定义敏感区域，参数都是百分比的形式。
 */

public class OcrVin extends OcrTypeHelper {
    public OcrVin(int screenDirection) {
        //super();
        if (screenDirection == 1){
            //竖屏
            //左上方顶点设置
            this.leftPointX = (float) 0.025;
            this.leftPointY = (float) 0.4;
            //长宽设置
            this.width = (float) 0.95;
            this.height = (float) 0.08;
            this.namePositionX = (float) 0.35;
            this.namePositionY = (float) 0.35;
        }else {
            //横屏
            this.leftPointX = (float) 0.1;
            this.leftPointY = (float) 0.4;
            this.width = (float) 0.7;
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
        //设置 vin 码扫描识别的模板 id
        this.ocrId = "SV_ID_VIN_CARWINDOW";
        //vin 导入识别需要的模板
        this.importTempalgeID = "SV_ID_VIN_MOBILE";
        this.nameTextSize = 40;
        this.ocrTypeName = "VIN码";

    }

}
