package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/9
 * @Description :
 */
public class Goods implements Serializable {

    private String goods_id = "0";    //商品id
    private String shelves_id;   //货架id
    private String shelces_name;   //货架名称
    private String warehouse_shelves_id = "";    //仓库货架id
    private String goods_name;    //商品名
    private String specs;    //商品规格
    private String price;   //商品价格
    private String imageUrl;    //商品图片

    private String code_id;      //扫描二维码的结果

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }

    public String getShelces_name() {
        return shelces_name;
    }

    public void setShelces_name(String shelces_name) {
        this.shelces_name = shelces_name;
    }


    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public String getShelves_id() {
        return shelves_id;
    }

    public void setShelves_id(String shelves_id) {
        this.shelves_id = shelves_id;
    }

    public String getWarehouse_shelves_id() {
        return warehouse_shelves_id;
    }

    public void setWarehouse_shelves_id(String warehouse_shelves_id) {
        this.warehouse_shelves_id = warehouse_shelves_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
