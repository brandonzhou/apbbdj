package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;

/**
 * @Author : ZSK
 * @Date : 2019/11/14
 * @Description :   订单记录
 */
public class OrderRecordModel implements Serializable {

    private String order_number;    //订单号
    private String waybill_number;  //运单号
    private String settle_money;   //扣款
    private String flag;  //1：正常 2：取消
    private String states;   //不是2 就是无快递单号
    private String create_time;
    private String callback_states;    //1 ： 未扣款（等待快递公司返回重量） 2:已扣款
    private String callback_time;   //扣款时间

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getWaybill_number() {
        return waybill_number;
    }

    public void setWaybill_number(String waybill_number) {
        this.waybill_number = waybill_number;
    }

    public String getSettle_money() {
        return settle_money;
    }

    public void setSettle_money(String settle_money) {
        this.settle_money = settle_money;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCallback_states() {
        return callback_states;
    }

    public void setCallback_states(String callback_states) {
        this.callback_states = callback_states;
    }

    public String getCallback_time() {
        return callback_time;
    }

    public void setCallback_time(String callback_time) {
        this.callback_time = callback_time;
    }
}
