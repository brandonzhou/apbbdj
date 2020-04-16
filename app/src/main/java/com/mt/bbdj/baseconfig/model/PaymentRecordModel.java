package com.mt.bbdj.baseconfig.model;

/**
 * @Author : ZSK
 * @Date : 2019/11/14
 * @Description :
 */
public class PaymentRecordModel {

    private String id;
    private String title;    //类目
    private String con_amount;   //交易钱
    private String con_balance;    //余额
    private String time;   //时间
    private String budget;    // 1: 为“-” 其他为“+”
    private String types;    //1: 和2时才有运单号
    private String waybill_number;    //单号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCon_amount() {
        return con_amount;
    }

    public void setCon_amount(String con_amount) {
        this.con_amount = con_amount;
    }

    public String getCon_balance() {
        return con_balance;
    }

    public void setCon_balance(String con_balance) {
        this.con_balance = con_balance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getWaybill_number() {
        return waybill_number;
    }

    public void setWaybill_number(String waybill_number) {
        this.waybill_number = waybill_number;
    }
}
