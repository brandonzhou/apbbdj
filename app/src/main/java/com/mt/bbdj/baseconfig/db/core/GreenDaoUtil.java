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

    private static String user_id;

    private static DaoSession getDaoSession(){
        return GreenDaoManager.getInstance().getSession();
    }

    public static String getUserId(){
        if(user_id!= null){
            return user_id;
        }
        UserBaseMessageDao mUserMessageDao = getDaoSession().getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        return user_id;
    }

    public static void updatePickCode(PickupCode pickupCode){
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        pickupCode.setUser_id(getUserId());
        dao.insertOrReplace(pickupCode);
    }

    public static PickupCode getPickCode(){
        PickupCodeDao dao = getDaoSession().getPickupCodeDao();
        PickupCode pickupCode = dao.queryBuilder()
                .where(PickupCodeDao.Properties.User_id.eq(getUserId()))
                .unique();
        return pickupCode;
    }

    public static void updateScanImage(ScanImage scanImage){
        ScanImageDao dao = getDaoSession().getScanImageDao();
        dao.insertOrReplace(scanImage);
    }

    public static List<ScanImage> listScanImage(ScanImage.State state){
        ScanImageDao dao = getDaoSession().getScanImageDao();

        return dao.queryBuilder()
                .where(ScanImageDao.Properties.User_id.eq(getUserId()),
                        ScanImageDao.Properties.State.eq(state.name()))
                .list();

    }


}
