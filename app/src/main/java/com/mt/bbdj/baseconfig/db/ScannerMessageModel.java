package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.UUID;

/**
 * @Author : ZSK
 * @Date : 2019/11/2
 * @Description :
 */
@Entity()
public class ScannerMessageModel {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "code")
    private String code;    //取货码
    @Property(nameInDb = "waybill")
    private String waybill;   //运单号
    @Property(nameInDb = "phone")
    private String phone;   //手机号码
    @Property(nameInDb = "express_name")
    private String expressName;   //快递公司
    @Property(nameInDb = "express_logo")
    private String expressLogo;   //快递公司Logo
    @Property(nameInDb = "is_have_phone")
    private int isHavaPhone = 0;    //没有手机号
    @Property(nameInDb = "is_have_number")
    private int isHaveWayNumber = 0;   //没有运单号
    @Property(nameInDb = "max")
    private int MAXTAG = 0;   //最大标记位
    @Property(nameInDb = "timestamp")
    private String timestamp;    //暂时表示id
    @Property(nameInDb = "is_sync")
    private int isSync;   //0 ：未同步 1： 已同步
    @Property(nameInDb = "uuid")
    private String uuid = UUID.randomUUID().toString();   //唯一标识
    @Generated(hash = 2036949935)
    public ScannerMessageModel(Long id, String code, String waybill, String phone,
            String expressName, String expressLogo, int isHavaPhone,
            int isHaveWayNumber, int MAXTAG, String timestamp, int isSync,
            String uuid) {
        this.id = id;
        this.code = code;
        this.waybill = waybill;
        this.phone = phone;
        this.expressName = expressName;
        this.expressLogo = expressLogo;
        this.isHavaPhone = isHavaPhone;
        this.isHaveWayNumber = isHaveWayNumber;
        this.MAXTAG = MAXTAG;
        this.timestamp = timestamp;
        this.isSync = isSync;
        this.uuid = uuid;
    }
    @Generated(hash = 190405819)
    public ScannerMessageModel() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public String getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public int getIsSync() {
        return this.isSync;
    }
    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
