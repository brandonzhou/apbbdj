package com.shshcom.station.storage.ocrclip;

/**
 *  这里封装了模板参数，根据不同的类型返回了不同的模板，具体参数在具体的参数类中；OcrVin: vin 类， OcrPhoneNumber: 手机号类。
 */
public  class OcrTypeHelper {

    /*VIN*/
    public static final int OCR_TYPE_VIN = 1;
    /*MOBILE*/
    public static final int OCR_TYPE_MOBILE = 2;

    /*横屏*/
    public static final int SCREENH_ORIZONTAL = 2;
    /*竖屏*/
    public static final int SCREENT_VERTICAL = 1;

   public float leftPointX;
    public float leftPointY;
    public float width;
    public float height;
    public float namePositionX;
    public float namePositionY;
    public int nameTextSize;
    public String ocrTypeName;
    public String ocrId;
    public String importTempalgeID;

    OcrTypeHelper ocrTypeHelper = null;

    public  OcrTypeHelper(int currentType,int screenDirection) {
        if (currentType == OCR_TYPE_VIN){
            // vin
            ocrTypeHelper = new OcrVin(screenDirection);
        }else if (currentType == OCR_TYPE_MOBILE){
            // 手机号码
            ocrTypeHelper = new OcrPhoneNumber(screenDirection);
        }
        }
    public OcrTypeHelper() {
    }

    public OcrTypeHelper getOcr(){
        return ocrTypeHelper;
    }
    @Override
    public String toString() {
        String result = "";
        result = "OcrTypeHeler: leftpointX :" + leftPointX + ",leftPointY:" + leftPointY + ",width:" + width + ",height:" + height
        + ",namePointX:" + namePositionX + ",namePointY:" + namePositionY;
        return  result;
    }
}
