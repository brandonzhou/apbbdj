package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;

/**
 * @Author : ZSK
 * @Date : 2020/4/2
 * @Description :
 */
public class EnterDetailModel implements Serializable {

    private String pie_id;

    private String express_name;

    private String express_id;

    private String express_yundan;

    private String phone;

    private String current_state;

    private String message;

    private String code;

    private String isEnter;

    private Double createTime;

    public Double getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Double createTime) {
        this.createTime = createTime;
    }

    public String getIsEnter() {
        return isEnter;
    }

    public void setIsEnter(String isEnter) {
        this.isEnter = isEnter;
    }

    public String getExpress_id() {
        return express_id;
    }

    public void setExpress_id(String express_id) {
        this.express_id = express_id;
    }

    public String getPie_id() {
        return pie_id;
    }

    public void setPie_id(String pie_id) {
        this.pie_id = pie_id;
    }

    public String getExpress_name() {
        return express_name;
    }

    public void setExpress_name(String express_name) {
        this.express_name = express_name;
    }

    public String getExpress_yundan() {
        return express_yundan;
    }

    public void setExpress_yundan(String express_yundan) {
        this.express_yundan = express_yundan;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrent_state() {
        return current_state;
    }

    public void setCurrent_state(String current_state) {
        this.current_state = current_state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
