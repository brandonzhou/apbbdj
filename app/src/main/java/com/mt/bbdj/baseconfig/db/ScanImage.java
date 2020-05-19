package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * desc:扫描图片数据
 * author: zhhli
 * 2020/5/19
 */
@Entity
public class ScanImage {
    public enum State{
        none,
        uploading,
        upload_fail,
    }

    @Id
    String eId;// 快递单号

    String pickCode;// 取件码
    String localPath;

    String state;

    private String user_id;//对应的用户id

    @Generated(hash = 1884495144)
    public ScanImage(String eId, String pickCode, String localPath, String state,
            String user_id) {
        this.eId = eId;
        this.pickCode = pickCode;
        this.localPath = localPath;
        this.state = state;
        this.user_id = user_id;
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

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}