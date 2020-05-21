package com.shshcom.station.storage.http.bean;

import java.util.List;

/**
 * desc:
 * author: zhhli
 * 2020/5/21
 */
public class StationOrcResult {
    int succeed;
    int fail;

    List<OcrResult> succeed_lists;
    List<OcrResult> fail_lists;


    public StationOrcResult() {
    }


    public int getSucceed() {
        return succeed;
    }

    public StationOrcResult setSucceed(int succeed) {
        this.succeed = succeed;
        return this;
    }

    public int getFail() {
        return fail;
    }

    public StationOrcResult setFail(int fail) {
        this.fail = fail;
        return this;
    }

    public List<OcrResult> getSucceed_lists() {
        return succeed_lists;
    }

    public StationOrcResult setSucceed_lists(List<OcrResult> succeed_lists) {
        this.succeed_lists = succeed_lists;
        return this;
    }

    public List<OcrResult> getFail_lists() {
        return fail_lists;
    }

    public StationOrcResult setFail_lists(List<OcrResult> fail_lists) {
        this.fail_lists = fail_lists;
        return this;
    }
}
