package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;

/**
 * desc:扫描图片数据
 * author: zhhli
 * 2020/5/19
 */
@Entity(indexes = {
        @Index(value = "time DESC", unique = false)
})
public class ScanImage {
    public enum State {
        none,
        uploading,
        upload_fail,
        upload_success,
    }

    @Id
    private String eId;// 快递单号

    private String pickCode;// 取件码
    private String localPath;

    private String state;

    // 快递公司ID
    private String expressCompanyId;

    private int batchNo;// 批次号

    private String stationId;//对应的用户id

    private long time;

    @Generated(hash = 103779709)
    public ScanImage(String eId, String pickCode, String localPath, String state,
                     String expressCompanyId, int batchNo, String stationId, long time) {
        this.eId = eId;
        this.pickCode = pickCode;
        this.localPath = localPath;
        this.state = state;
        this.expressCompanyId = expressCompanyId;
        this.batchNo = batchNo;
        this.stationId = stationId;
        this.time = time;
    }

    @Keep
    public ScanImage() {
        time = System.currentTimeMillis();
    }

    public String getEId() {
        return this.eId;
    }

    public void setEId(String eId) {
        this.eId = eId;
    }

    public String getPickCode() {
        return this.pickCode;
    }

    public void setPickCode(String pickCode) {
        this.pickCode = pickCode;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStationId() {
        return this.stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getExpressCompanyId() {
        return this.expressCompanyId;
    }

    public void setExpressCompanyId(String expressCompanyId) {
        this.expressCompanyId = expressCompanyId;
    }

    public int getBatchNo() {
        return this.batchNo;
    }

    public void setBatchNo(int batchNo) {
        this.batchNo = batchNo;
    }


}