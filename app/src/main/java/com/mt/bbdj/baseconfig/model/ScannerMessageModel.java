package com.mt.bbdj.baseconfig.model;

/**
 * @Author : ZSK
 * @Date : 2019/11/2
 * @Description :
 */
public class ScannerMessageModel {

    private String code;    //取货码

    private String waybill;   //运单号

    private String phone;   //手机号码

    private String expressName;   //快递公司

    private String expressLogo;   //快递公司Logo

    private boolean isHavaPhone = false;    //没有手机号

    private boolean isHaveWayNumber = false;   //没有运单号

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ScannerMessageModel(String code) {
        this.code = code;
    }

    public String getWaybill() {
        return waybill;
    }


    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isHavaPhone() {
        return isHavaPhone;
    }

    public void setHavaPhone(boolean havaPhone) {
        isHavaPhone = havaPhone;
    }

    public boolean isHaveWayNumber() {
        return isHaveWayNumber;
    }

    public void setHaveWayNumber(boolean haveWayNumber) {
        isHaveWayNumber = haveWayNumber;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressLogo() {
        return expressLogo;
    }

    public void setExpressLogo(String expressLogo) {
        this.expressLogo = expressLogo;
    }
}
