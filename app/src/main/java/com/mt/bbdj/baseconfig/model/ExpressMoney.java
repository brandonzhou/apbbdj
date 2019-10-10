package com.mt.bbdj.baseconfig.model;

/**
 * @Author : ZSK
 * @Date : 2019/10/9
 * @Description :
 */
public class ExpressMoney {

    private String express_id;

    private String money_id;

    private String logo;

    private String name;

    private String price;

    public String getMoney_id() {
        return money_id;
    }

    public void setMoney_id(String money_id) {
        this.money_id = money_id;
    }

    public String getExpress_id() {
        return express_id;
    }

    public void setExpress_id(String express_id) {
        this.express_id = express_id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
