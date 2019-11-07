package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Author : ZSK
 * @Date : 2019/11/2
 * @Description :
 */
@Entity()
public class ScannerMessageModel {

    private String code;    //取货码

    private String waybill;   //运单号

    private String phone;   //手机号码

    private String expressName;   //快递公司

    private String expressLogo;   //快递公司Logo

    private int isHavaPhone = 0;    //没有手机号

    private int isHaveWayNumber = 0;   //没有运单号

    private int MAXTAG = 0;   //最大标记位

    @Generated(hash = 469646478)
    public ScannerMessageModel(String code, String waybill, String phone,
            String expressName, String expressLogo, int isHavaPhone,
            int isHaveWayNumber, int MAXTAG) {
        this.code = code;
        this.waybill = waybill;
        this.phone = phone;
        this.expressName = expressName;
        this.expressLogo = expressLogo;
        this.isHavaPhone = isHavaPhone;
        this.isHaveWayNumber = isHaveWayNumber;
        this.MAXTAG = MAXTAG;
    }

    @Generated(hash = 190405819)
    public ScannerMessageModel() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWaybill() {
        return this.waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExpressName() {
        return this.expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressLogo() {
        return this.expressLogo;
    }

    public void setExpressLogo(String expressLogo) {
        this.expressLogo = expressLogo;
    }

    public int getIsHavaPhone() {
        return this.isHavaPhone;
    }

    public void setIsHavaPhone(int isHavaPhone) {
        this.isHavaPhone = isHavaPhone;
    }

    public int getIsHaveWayNumber() {
        return this.isHaveWayNumber;
    }

    public void setIsHaveWayNumber(int isHaveWayNumber) {
        this.isHaveWayNumber = isHaveWayNumber;
    }

    public int getMAXTAG() {
        return this.MAXTAG;
    }

    public void setMAXTAG(int MAXTAG) {
        this.MAXTAG = MAXTAG;
    }

}
