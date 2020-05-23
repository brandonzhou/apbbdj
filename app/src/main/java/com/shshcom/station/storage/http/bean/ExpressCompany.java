package com.shshcom.station.storage.http.bean;

import com.google.gson.Gson;

/**
 * 功能描述 : 快递公司信息
 * 创建人 : Administrator 创建时间: 20/5/23
 */
public class ExpressCompany {

    /**
     * express_id : 100101
     * express_name : 中通快递
     */

    private int express_id;
    private String express_name;

    public static ExpressCompany objectFromData(String str) {

        return new Gson().fromJson(str, ExpressCompany.class);
    }

    public int getExpress_id() {
        return express_id;
    }

    public void setExpress_id(int express_id) {
        this.express_id = express_id;
    }

    public String getExpress_name() {
        return express_name;
    }

    public void setExpress_name(String express_name) {
        this.express_name = express_name;
    }
}
