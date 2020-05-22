package com.mt.bbdj.baseconfig.db.core;

import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.PickupCodeDao;
import com.mt.bbdj.baseconfig.db.gen.ScanImageDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;

import java.util.List;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class GreenDaoUtil {

    private static String stationId;

    private static DaoSession getDaoSession(){
        return GreenDaoManager.getInstance().getSession();
    }

    public static String getStationId(){
        stationId = "12";
        if(stationId!= null){
            return stationId;
        }
        UserBaseMessageDao mUserMessageDao = getDaoSession().getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            stationId = list.get(0).getUser_id();
        }

        return stationId;
    }

    public static void updatePickCode(PickupCode pickupCode){
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        pickupCode.setStationId(getStationId());
        dao.insertOrReplace(pickupCode);
    }

    public static PickupCode getPickCode(){
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        PickupCode pickupCode = dao.queryBuilder()
                .where(PickupCodeDao.Properties.StationId.eq(getStationId()))
                .unique();

        if(pickupCode == null){
            pickupCode = new PickupCode();
            pickupCode.setStartNumber(1000);
            pickupCode.setCurrentNumber("1000");
            pickupCode.setType(PickupCode.Type.type_code.getDesc());
        }

        return pickupCode;
    }

    public static void updateScanImage(ScanImage scanImage){
        ScanImageDao dao = getDaoSession().getScanImageDao();
        dao.insertOrReplace(scanImage);
    }

    public static List<ScanImage> listScanImage(ScanImage.State state){
        ScanImageDao dao = getDaoSession().getScanImageDao();

        return dao.queryBuilder()
                .where(ScanImageDao.Properties.State.eq(state.name()))
                .orderDesc(ScanImageDao.Properties.Time)
                .list();
    }

    public static ScanImage findScanImage(String eId){
        ScanImageDao dao = getDaoSession().getScanImageDao();

        return dao.queryBuilder()
                .where(ScanImageDao.Properties.EId.eq(eId))
                .unique();
    }

    public static ScanImage getLastScanImage(){
        ScanImageDao dao = getDaoSession().getScanImageDao();

        return dao.queryBuilder()
                .orderDesc(ScanImageDao.Properties.Time)
                .limit(1).unique();
    }

    public static void deleteScanImage(String eId){
        ScanImageDao dao = getDaoSession().getScanImageDao();
        dao.deleteByKey(eId);
    }


}
