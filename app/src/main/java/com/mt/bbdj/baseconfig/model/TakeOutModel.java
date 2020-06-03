package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/6/24
 * @Description : 外卖具体类
 */
public class TakeOutModel implements Serializable {

    private String orders_id;    //订单id
    private String order_number;    //订单号
    private String state;      //接单、未接单
    private String estimatedTime;     //预计时间
    private String currentTimeState;     //超时或者剩余时间
    private String address;    //地址
    private String name;    //姓名
    private String phoneNumber;    //电话号码
    private String orderState;    //订单状态
    private String payStates;     //支付状态
    private String total;    //总价
    private String latitude;   //纬度
    private String longitude;   //经度
    private String mode;   //配送方式

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }



    public String getPayStates() {
        return payStates;
    }

    public void setPayStates(String payStates) {
        this.payStates = payStates;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrders_id() {
        return orders_id;
    }

    public void setOrders_id(String orders_id) {
        this.orders_id = orders_id;
    }

    private List<HashMap<String,String>> takeOutList;     //产品列表

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getCurrentTimeState() {
        return currentTimeState;
    }

    public void setCurrentTimeState(String currentTimeState) {
        this.currentTimeState = currentTimeState;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public List<HashMap<String, String>> getTakeOutList() {
        return takeOutList;
    }

    public void setTakeOutList(List<HashMap<String, String>> takeOutList) {
        this.takeOutList = takeOutList;
    }

    public String getOrder_number() {
        return order_number;
    }

    public TakeOutModel setOrder_number(String order_number) {
        this.order_number = order_number;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public TakeOutModel setMode(String mode) {
        this.mode = mode;
        return this;
    }
}
