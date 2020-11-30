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

    private static DaoSession getDaoSession() {
        return GreenDaoManager.getInstance().getSession();
    }

    public static String getStationId() {
        return DbUserUtil.getUserBase().getUser_id();
    }


    public static void insertPickCode(PickupCode pickupCode) {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        dao.save(pickupCode);
    }

    // 重置覆盖
    public static void restorePickCodeList(List<PickupCode> list) {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        dao.deleteAll();
        dao.insertInTx(list);
    }

    public static void delPickCode(Long uId) {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        dao.deleteByKey(uId);
    }


    public static void updatePickCode(PickupCode pickupCode) {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        pickupCode.setStationId(getStationId());

        PickupCode dbCode = getPickCode(pickupCode.getShelfId());
//        PickupCode dbCode = getPickCodeLast();
        //----- 设置UID，确保每个货架只有一条数据
        if (dbCode != null) {
            pickupCode.setUId(dbCode.getUId());
        }
        dao.insertOrReplace(pickupCode);


    }

    public static PickupCode getPickCode(int shelfId) {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        PickupCode pickupCode = dao.queryBuilder()
                .where(PickupCodeDao.Properties.StationId.eq(getStationId()),
                        PickupCodeDao.Properties.ShelfId.eq(shelfId))
                .unique();

        return pickupCode;
    }

    public static PickupCode getPickCodeLast() {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        PickupCode pickupCode = dao.queryBuilder()
                .where(PickupCodeDao.Properties.StationId.eq(getStationId()))
                .orderDesc(PickupCodeDao.Properties.Time)
                .limit(1).unique();

//        if(pickupCode == null){
//            pickupCode = new PickupCode();
//            pickupCode.setStartNumber(1000);
//            pickupCode.setType(PickupCode.Type.type_code.getDesc());
//        }

        return pickupCode;
    }

    public static List<PickupCode> listPickupCodeAll() {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        return dao.queryBuilder()
                .list();
    }

    /**
     * @return 列出所有货架取件码
     */
    public static List<PickupCode> listPickupCodeHasShelf() {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        return dao.queryBuilder()
                .where(PickupCodeDao.Properties.StationId.eq(getStationId()),
                        PickupCodeDao.Properties.Type.notEq(PickupCode.Type.type_code.getDesc()))
                .list();
    }

    public static PickupCode listPickupCodeOnlyNumber() {
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        return dao.queryBuilder()
                .where(PickupCodeDao.Properties.StationId.eq(getStationId()),
                        PickupCodeDao.Properties.Type.eq(PickupCode.Type.type_code.getDesc()))
                .limit(1).unique();
    }


    public static void updateScanImage(ScanImage scanImage) {
        ScanImageDao dao = getDaoSession().getScanImageDao();
        dao.insertOrReplace(scanImage);
    }

    public static void updateScanImageList(List<ScanImage> scanImageList) {
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
