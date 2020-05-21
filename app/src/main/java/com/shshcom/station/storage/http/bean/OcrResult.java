package com.shshcom.station.storage.http.bean;

/**
 * desc:
 * author: zhhli
 * 2020/5/21
 *
 * {
 *     "succeed": 1,
 *     "fail": 7,
 *     "succeed_lists": [{
 *         "pie_id": 145121,
 *         "number": "77111187755872",
 *         "code": "Y700940",
 *         "mobile": "18618203265",
 *         "msg": "",
 *         "express_id": 100101,
 *         "express_name": "中通快递",
 *         "picture": "https://qrcode.taowangzhan.com/express/20200521/1c10e35e31062840ea909e3288b60e41.jpg"
 *     }],
 *     "fail_lists": [{
 *         "pie_id": 145121,
 *         "number": "77111187755872",
 *         "code": "Y700940",
 *         "mobile": "18618203265",
 *         "msg": "",
 *         "express_id": 100101,
 *         "express_name": "中通快递",
 *         "picture": "https://qrcode.taowangzhan.com/express/20200521/1c10e35e31062840ea909e3288b60e41.jpg"
 *     }]
 * }
 */
public class OcrResult {




    /**
     * pie_id : 145242
     * number : YT4295748861510
     * code : 1000
     * mobile :
     * msg : 手机号识别失败
     * express_id : 100102
     * express_name : 圆通快递
     * picture : https://qrcode.taowangzhan.com/express/20200521/1ee4301d5381cac47144909ee96608e6.jpg
     */

    private int pie_id;
    private String number;
    private String code;
    private String mobile;
    private String msg;
    private int express_id;
    private String express_name;
    private String picture;

    public int getPie_id() {
        return pie_id;
    }

    public void setPie_id(int pie_id) {
        this.pie_id = pie_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getExpress_id() {
        return express_id;
    }

    public void setExpress_id(int express_id) {
        this.express_id = express_id;
    }

    public String getExpress_name() {
        return express_name;
    }

    public void setExpress_name(String express_name) {
        this.express_name = express_name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
