package com.mt.bbdj.baseconfig.db.core;

import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.PickupCodeDao;
import com.mt.bbdj.baseconfig.db.gen.ScanImageDao;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;

import java.util.List;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class GreenDaoUtil {

    private static DaoSession getDaoSession(){
        return GreenDaoManager.getInstance().getSession();
    }

    public static String getStationId(){
        return DbUserUtil.getUserBase().getUser_id();
    }



    public static void updatePickCode(PickupCode pickupCode){
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        pickupCode.setStationId(getStationId());
        long time = System.currentTimeMillis();
        pickupCode.setTime(time);

        PickupCode dbCode = getPickCodeLast();
        // 设置UID，确保目前只有一条数据，方便以后需求扩展
        pickupCode.setUId(dbCode.getUId());
        dao.insertOrReplace(pickupCode);
    }

    public static PickupCode getPickCodeLast(){
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

    public static void updateScanImageList(List<ScanImage> scanImageList){
        ScanImageDao dao = getDaoSession().getScanImageDao();
        dao.insertOrReplaceInTx(scanImageList);
    }

    public static List<ScanImage> listScanImage(ScanImage.State state, String batchNo) {
        ScanImageDao dao = getDaoSession().getScanImageDao();

        return dao.queryBuilder()
                .where(ScanImageDao.Properties.State.eq(state.name()),
                        ScanImageDao.Properties.BatchNo.eq(batchNo))
                .orderDesc(ScanImageDao.Properties.Time)
                .list();
    }

    public static List<ScanImage> listScanImage(String batchNo){
        ScanImageDao dao = getDaoSession().getScanImageDao();

        // 当天
        return dao.queryBuilder()
                .where(ScanImageDao.Properties.StationId.eq(getStationId()),
                        ScanImageDao.Properties.BatchNo.eq(batchNo))
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
                .where(ScanImageDao.Properties.StationId.eq(getStationId()))
                .orderDesc(ScanImageDao.Properties.Time)
                .limit(1).unique();
    }

    public static void deleteScanImage(String eId){
        ScanImageDao dao = getDaoSession().getScanImageDao();
        ScanImage image = findScanImage(eId);
        if(image!= null){
            dao.delete(image);
        }
    }


}
