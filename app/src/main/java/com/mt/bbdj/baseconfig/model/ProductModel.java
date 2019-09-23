package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/4/27
 * Description : 订单中产品 ： 快递、桶装水、干洗
 */
public class ProductModel implements Serializable {

    //公用属性：
    private int type;    //0:快递、1:桶装水、2:干洗 3:空白，为了屏幕适配
    private boolean isShowType;   //是否显示大标题
    private boolean isShowBottom;   //是否显示底部分隔栏
    private String productName;     //姓名、桶装水品牌、干洗校区
    private String address;    //地址
    private String repairPerson;   //维修师傅
    private String repairPhone;     //维修电话

    public String getRepairPerson() {
        return repairPerson;
    }

    public void setRepairPerson(String repairPerson) {
        this.repairPerson = repairPerson;
    }

    public String getRepairPhone() {
        return repairPhone;
    }

    public void setRepairPhone(String repairPhone) {
        this.repairPhone = repairPhone;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    private int orderState;   //订单状态

    public String getMail_id() {
        return mail_id;
    }

    public void setMail_id(String mail_id) {
        this.mail_id = mail_id;
    }

    private String mail_id;     //订单id


    public boolean isShowBottom() {
        return isShowBottom;
    }

    public void setShowBottom(boolean showBottom) {
        isShowBottom = showBottom;
    }

    public boolean isShowType() {

        return isShowType;
    }

    public void setShowType(boolean showType) {
        isShowType = showType;
    }

    private String phone;  //联系电话


    //桶装水
    private String waterNumber;    //桶装水数量
    private String context;    //备注
    private String orders_id;    //订单id
    private String accountPrice;    //总价
    private String createTime;     //订单创建时间
    private String handleTime;    //处理时间

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    private String orderNumber;    //订单号
    private String states;     //1 ：未接单  2:已结单

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAccountPrice() {
        return accountPrice;
    }

    public void setAccountPrice(String accountPrice) {
        this.accountPrice = accountPrice;
    }

    public String getOrders_id() {
        return orders_id;
    }

    public void setOrders_id(String orders_id) {
        this.orders_id = orders_id;
    }

    public List<HashMap<String, String>> getWaterMessageList() {
        return waterMessageList;
    }

    public void setWaterMessageList(List<HashMap<String, String>> waterMessageList) {
        this.waterMessageList = waterMessageList;
    }

    private List<HashMap<String, String>> waterMessageList;   //桶装水信息

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWaterNumber() {
        return waterNumber;
    }

    public void setWaterNumber(String waterNumber) {
        this.waterNumber = waterNumber;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    //干洗
    private String surplusTime;   //剩余时间
    private int clearState;   //干洗状态 ： 1：待接单  2：已接单  3：拒单  4：已送达  5：等待干洗店取件  6：等待干洗店送达  7：等待客户取件  8：确认报价
    private List<HashMap<String, String>> clearMessageList;
    private String clearStateName;    //状态名称
    private String payfor;    //支付状态
    private String juli_time;    //距离时间

    public String getJuli_time() {
        return juli_time;
    }

    public void setJuli_time(String juli_time) {
        this.juli_time = juli_time;
    }

    public String getPayfor() {
        return payfor;
    }

    public void setPayfor(String payfor) {
        this.payfor = payfor;
    }

    public String getClearStateName() {
        return clearStateName;

    }

    public void setClearStateName(String clearStateName) {
        this.clearStateName = clearStateName;
    }

    public List<HashMap<String, String>> getClearMessageList() {
        return clearMessageList;

    }

    public void setClearMessageList(List<HashMap<String, String>> clearMessageList) {
        this.clearMessageList = clearMessageList;
    }

    public int getClearState() {
        return clearState;
    }

    public void setClearState(int clearState) {
        this.clearState = clearState;
    }

    public String getSurplusTime() {

        return surplusTime;
    }

    public void setSurplusTime(String surplusTime) {
        this.surplusTime = surplusTime;
    }
}
