package com.shshcom.station.storage.http.bean;

/**
 * desc:
 * author: zhhli
 * 2020/5/21
 */
public class BaseResult<T> {
    public static final int successCode = 5001;
    String msg;
    int code;
    T data;

    public BaseResult() {
    }

    public String getMsg() {
        return msg;
    }

    public BaseResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public BaseResult setCode(int code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public BaseResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess(){
        return successCode == code;
    }
}
