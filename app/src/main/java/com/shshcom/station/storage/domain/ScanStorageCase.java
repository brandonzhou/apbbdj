package com.shshcom.station.storage.domain;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.mt.bbdj.baseconfig.utls.LogUtil;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class ScanStorageCase {

    private Context context;



    private SHCameraHelp help;

    public ScanStorageCase(Context context) {
        this.context = context;
        help = new SHCameraHelp();


    }



}
