package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;

/**
 * @Author : ZSK
 * @Date : 2019/8/20
 * @Description :  优惠券
 */
public class CouponModel implements Serializable {
    private String coupon_id;      //id
    private String term_money;        //满足金额
    private String reduction_money;     //折扣、满减金额
    private String types;   //1、折扣 2、满减
    private String starttime;     //开始时间
    private String endtime;    //结束时间
    private String effection;    //1：有效 0：过期

    public String getEffection() {
        return effection;
    }

    public void setEffection(String effection) {
        this.effection = effection;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getTerm_money() {
        return term_money;
    }

    public void setTerm_money(String term_money) {
        this.term_money = term_money;
    }

    public String getReduction_money() {
        return reduction_money;
    }

    public void setReduction_money(String reduction_money) {
        this.reduction_money = reduction_money;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
