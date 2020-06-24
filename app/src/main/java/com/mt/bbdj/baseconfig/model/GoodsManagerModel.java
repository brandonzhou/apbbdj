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

    private String stock;    //库存  称重 克

    private Long stockUi;    //库存  UI显示 千克

    private String type;    // type类型 1-计件 2-称重

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

    public String getType() {
        return type;
    }

    public GoodsManagerModel setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isWeight() {
        // type类型 1-计件 2-称重
        return "2".equals(type);
    }


    public long getStockUi() {
        if (stockUi == null) {
            long num = Long.parseLong(stock);

            if (isWeight()) {
                num = num / 1000;
            }

            stockUi = num;
        }
        return stockUi;
    }

    public void modifyStockUI(long num, boolean isAdd) {
        int k = isAdd ? 1 : -1;
        stockUi = getStockUi() + num * k;


    }


}
