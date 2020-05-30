package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

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

    private String stationId;//对应的用户id


    private String pickCode;// 取件码

    private String batchNo;// 批次号

    private String state;

    private String phone;

    // 快递公司ID
    private String expressCompanyId;



    private long time;

    private String localPath;

@Generated(hash = 820179988)
public ScanImage(String eId, String stationId, String pickCode, String batchNo,
        String state, String phone, String expressCompanyId, long time,
        String localPath) {
    this.eId = eId;
    this.stationId = stationId;
    this.pickCode = pickCode;
    this.batchNo = batchNo;
    this.state = state;
    this.phone = phone;
    this.expressCompanyId = expressCompanyId;
    this.time = time;
    this.localPath = localPath;
}

@Generated(hash = 233529434)
public ScanImage() {
}

public String getEId() {
    return this.eId;
}

public void setEId(String eId) {
    this.eId = eId;
}

public String getStationId() {
    return this.stationId;
}

public void setStationId(String stationId) {
    this.stationId = stationId;
}

public String getPickCode() {
    return this.pickCode;
}

public void setPickCode(String pickCode) {
    this.pickCode = pickCode;
}

public String getBatchNo() {
    return this.batchNo;
}

public void setBatchNo(String batchNo) {
    this.batchNo = batchNo;
}

public String getState() {
    return this.state;
}

public void setState(String state) {
    this.state = state;
}

public String getPhone() {
    return this.phone;
}

public void setPhone(String phone) {
    this.phone = phone;
}

public String getExpressCompanyId() {
    return this.expressCompanyId;
}

public void setExpressCompanyId(String expressCompanyId) {
    this.expressCompanyId = expressCompanyId;
}

public long getTime() {
    return this.time;
}

public void setTime(long time) {
    this.time = time;
}

public String getLocalPath() {
    return this.localPath;
}

public void setLocalPath(String localPath) {
    this.localPath = localPath;
}




}