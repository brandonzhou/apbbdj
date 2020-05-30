package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * desc: 驿站用户 本地信息配置
 * author: zhhli
 * 2020/5/29
 */
@Entity
public class UserConfig {
    @Id
    private String stationID;// 驿站ID

    private String batchNo;// 拍照入库-批次号

    @Generated(hash = 688359017)
    public UserConfig(String stationID, String batchNo) {
        this.stationID = stationID;
        this.batchNo = batchNo;
    }

    @Generated(hash = 523434660)
    public UserConfig() {
    }

    public String getStationID() {
        return this.stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public String getBatchNo() {
        return this.batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    
}