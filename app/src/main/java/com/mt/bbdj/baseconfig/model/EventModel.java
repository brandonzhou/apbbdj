package com.mt.bbdj.baseconfig.model;


/**
 * Author : ZSK
 * Date : 2019/5/28
 * Description :
 */
public class EventModel {

    public static int TYPE_DESTORY  = 100;    //销毁界面

    public static int TYPE_SYNC_IMAGE = 101;   //上传图片

    public static int TYPE_ENTER_FAILURE_EXPRESS = 102;    //入库修改失败的快递公司

    public static int TYPE_FRESH = 103;   //刷新

    private int tagType;

    private Object data;

    public EventModel(int tagType, Object data) {
        this.tagType = tagType;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public EventModel(int tagType) {
        this.tagType = tagType;
    }

    public int getTagType(){
        return tagType;
    }
}
