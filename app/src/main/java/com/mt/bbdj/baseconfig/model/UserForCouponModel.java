package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;

/**
 * @Author : ZSK
 * @Date : 2019/8/21
 * @Description :  用户的优惠券状况
 */
public class UserForCouponModel implements Serializable {

    private String member_id;     //用户ID
    private String type;    //类型 1 ： 已使用 2 ：未使用
    private String user_name;    //用户名称
    private String headImge;     //用户头型
    private String last_buy_time;   //最后一次购买

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getHeadImge() {
        return headImge;
    }

    public void setHeadImge(String headImge) {
        this.headImge = headImge;
    }

    public String getLast_buy_time() {
        return last_buy_time;
    }

    public void setLast_buy_time(String last_buy_time) {
        this.last_buy_time = last_buy_time;
    }
}
