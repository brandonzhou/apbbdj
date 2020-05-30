package com.mt.bbdj.baseconfig.db.core;

import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.UserConfig;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.db.gen.UserConfigDao;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;

/**
 * desc: 查询用户信息相关
 * author: zhhli
 * 2020/5/29
 */
public class DbUserUtil {
    private static DaoSession getDaoSession(){
        return GreenDaoManager.getInstance().getSession();
    }

    public static UserBaseMessage getUserBase(){
        // LoginActivity  line280 登录成功，mUserMessageDao.deleteAll();
        // mUserMessageDao 中始终只有一条数据
        UserBaseMessageDao mUserMessageDao = getDaoSession().getUserBaseMessageDao();
        return mUserMessageDao.queryBuilder().unique();
    }

    public static String getStationId(){
        return getUserBase().getUser_id();
    }

    public static UserConfig getUserConfig(){
        UserConfigDao dao = getDaoSession().getUserConfigDao();
        String stationId = getStationId();
        UserConfig userConfig = dao.queryBuilder()
                .where(UserConfigDao.Properties.StationID.eq(stationId))
                .unique();

        if(userConfig == null){
            userConfig = new UserConfig();
            userConfig.setStationID(stationId);
            userConfig.setBatchNo(System.currentTimeMillis()+"");
            saveUserConfig(userConfig);
        }

        return userConfig;
    }

    public static void saveUserConfig(UserConfig userConfig){
        UserConfigDao dao = getDaoSession().getUserConfigDao();
        dao.insertOrReplace(userConfig);
    }



}