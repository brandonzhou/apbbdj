package com.shshcom.station.storage.domain;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class ScanStorageCase {

    private Context context;


    private PickupCode pickupCode;

    private ScanStorageCase(){

    }

    private static class Hold{
        static ScanStorageCase instance = new ScanStorageCase();
    }


    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    public ScanStorageCase getInstance(){
        return Hold.instance;
    }


    public PickupCode getCurrentPickCode(){
        PickupCode code = GreenDaoUtil.getPickCode();
        if(code == null){
            code = new PickupCode();
            code.setCurrentNumber("1000");
            code.setType(PickupCode.Type.type_1.getDesc());
        }
        return pickupCode;
    }

    public void updatePickCode(PickupCode pickupCode){
        this.pickupCode = pickupCode;

        GreenDaoUtil.updatePickCode(pickupCode);
    }



}
