package com.mt.bbdj.baseconfig.db.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mt.bbdj.baseconfig.db.gen.DaoMaster;
import com.mt.bbdj.baseconfig.db.gen.PickupCodeDao;
import com.mt.bbdj.baseconfig.db.gen.ScanImageDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;

import org.greenrobot.greendao.database.Database;

/**
 * Author : ZSK
 * Date : 2019/4/22
 * Description :
 */
public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // 升级时，创建新表
        DaoMaster.createAllTables(db, true);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);

        //操作数据库的更新 有几个表升级都可以传入到下面
        if (oldVersion < 37) {
            MigrationHelper.migrate(db, UserBaseMessageDao.class);
            MigrationHelper.migrate(db, PickupCodeDao.class);
        }

        if (oldVersion < 38) {
            MigrationHelper.migrate(db, ScanImageDao.class);
        }

    }
}