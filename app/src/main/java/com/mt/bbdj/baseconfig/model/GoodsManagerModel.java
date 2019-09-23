package com.mt.bbdj.baseconfig.model;

/**
 * @Author : ZSK
 * @Date : 2019/8/10
 * @Description :   管理门店商品的商品
 */
public class GoodsManagerModel {

    private String imageUrl;    //图片的Url;

    private String goodsName;     //商品名称

    private String goodsPrice;    //商品价格

    private String goodsId;       //商品的id

    private String goodsState;    //商品的状态

    private String isSpecial;   //是不是特价商品   1 : 不是特价商品

    private String stock;    //库存

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(String isSpecial) {
        this.isSpecial = isSpecial;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(String goodsState) {
        this.goodsState = goodsState;
    }
}
