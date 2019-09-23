package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;

/**
 * @Author : ZSK
 * @Date : 2019/8/22
 * @Description :  仓库商品
 */
public class StoreGoodsModel implements Serializable {

    private String goods_id;    //商品id
    private String goods_name;    //商品名称
    private String goods_price;    //商品价格
    private String goods_img;    //商品图片
    private String flag;     //商品状态

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoods_img() {
        return goods_img;
    }

    public void setGoods_img(String goods_img) {
        this.goods_img = goods_img;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
