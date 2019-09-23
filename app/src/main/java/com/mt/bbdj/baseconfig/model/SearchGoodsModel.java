package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : ZSK
 * @Date : 2019/8/14
 * @Description :
 */
public class SearchGoodsModel implements Serializable {

    private String code_id="0";      //扫描二维码的结果

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }

    private List<SearchGoods> data;

    public List<SearchGoods> getData() {
        return data;
    }

    public void setData(List<SearchGoods> data) {
        this.data = data;
    }

    public static class SearchGoods implements Serializable{

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        private String goods_id;   //商品id

        private String img;    //图片
    }
}
