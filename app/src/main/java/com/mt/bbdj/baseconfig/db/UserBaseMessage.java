package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author : ZSK
 * Date : 2019/1/2
 * Description :   用户基础信息表
 */

@Entity(nameInDb = "user_base")
public class UserBaseMessage {
    static final long serialVersionUID = -15515457L;
    @Id(autoincrement = true)
    private Long mainId;   //主键
    @Property(nameInDb = "user_id")
    private String user_id;        //用户id
    @Property(nameInDb = "user_type")
    private String user_type;        //用户类型 企业版  社区版
    @Property(nameInDb = "headimg")
    private String headimg;       //头像
    @Property(nameInDb = "mingcheng")
    private String mingcheng;       //驿站名称
    @Property(nameInDb = "contacts")
    private String contacts;       //联系人
    @Property(nameInDb = "contact_number")
    private String contact_number;       //联系人电话
    @Property(nameInDb = "contact_email")
    private String contact_email;       //联系人邮箱
    @Property(nameInDb = "birthday")
    private String birthday;     //入驻天数
    @Property(nameInDb = "balance")
    private String balance;     //剩余金额
    @Property(nameInDb = "contact_account")
    private String contact_account;    //注册人电话
    @Property(nameInDb = "address")
    private String address;    //地址
    @Property(nameInDb = "latitude")
    private String latitude;    //纬度
    @Property(nameInDb = "longitude")
    private String longitude;    //经度
    @Property(nameInDb = "zto_company_id")
    private String zto_company_id;
    @Property(nameInDb = "zto_company_key")
    private String zto_company_key;
    @Generated(hash = 165793381)
    public UserBaseMessage(Long mainId, String user_id, String user_type,
            String headimg, String mingcheng, String contacts,
            String contact_number, String contact_email, String birthday,
            String balance, String contact_account, String address, String latitude,
            String longitude, String zto_company_id, String zto_company_key) {
        this.mainId = mainId;
        this.user_id = user_id;
        this.user_type = user_type;
        this.headimg = headimg;
        this.mingcheng = mingcheng;
        this.contacts = contacts;
        this.contact_number = contact_number;
        this.contact_email = contact_email;
        this.birthday = birthday;
        this.balance = balance;
        this.contact_account = contact_account;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zto_company_id = zto_company_id;
        this.zto_company_key = zto_company_key;
    }
    @Generated(hash = 1135630984)
    public UserBaseMessage() {
    }
    public Long getMainId() {
        return this.mainId;
    }
    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser_type() {
        return this.user_type;
    }
    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
    public String getHeadimg() {
        return this.headimg;
    }
    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }
    public String getMingcheng() {
        return this.mingcheng;
    }
    public void setMingcheng(String mingcheng) {
        this.mingcheng = mingcheng;
    }
    public String getContacts() {
        return this.contacts;
    }
    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    public String getContact_number() {
        return this.contact_number;
    }
    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }
    public String getContact_email() {
        return this.contact_email;
    }
    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }
    public String getBirthday() {
        return this.birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getBalance() {
        return this.balance;
    }
    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getContact_account() {
        return this.contact_account;
    }
    public void setContact_account(String contact_account) {
        this.contact_account = contact_account;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getLatitude() {
        return this.latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return this.longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getZto_company_id() {
        return this.zto_company_id;
    }
    public void setZto_company_id(String zto_company_id) {
        this.zto_company_id = zto_company_id;
    }
    public String getZto_company_key() {
        return this.zto_company_key;
    }
    public void setZto_company_key(String zto_company_key) {
        this.zto_company_key = zto_company_key;
    }
}
