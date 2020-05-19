package com.mt.bbdj.baseconfig.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.mt.bbdj.baseconfig.db.ExpressImage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EXPRESS_IMAGE".
*/
public class ExpressImageDao extends AbstractDao<ExpressImage, Long> {

    public static final String TABLENAME = "EXPRESS_IMAGE";

    /**
     * Properties of entity ExpressImage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public final static Property ImagePath = new Property(2, String.class, "imagePath", false, "IMAGE_PATH");
        public final static Property IsSync = new Property(3, int.class, "isSync", false, "IS_SYNC");
        public final static Property User_id = new Property(4, String.class, "user_id", false, "USER_ID");
        public final static Property ShelfNumber = new Property(5, String.class, "shelfNumber", false, "SHELF_NUMBER");
        public final static Property Express_id = new Property(6, String.class, "express_id", false, "EXPRESS_ID");
    }


    public ExpressImageDao(DaoConfig config) {
        super(config);
    }
    
    public ExpressImageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EXPRESS_IMAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"UUID\" TEXT," + // 1: uuid
                "\"IMAGE_PATH\" TEXT," + // 2: imagePath
                "\"IS_SYNC\" INTEGER NOT NULL ," + // 3: isSync
                "\"USER_ID\" TEXT," + // 4: user_id
                "\"SHELF_NUMBER\" TEXT," + // 5: shelfNumber
                "\"EXPRESS_ID\" TEXT);"); // 6: express_id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EXPRESS_IMAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ExpressImage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String imagePath = entity.getImagePath();
        if (imagePath != null) {
            stmt.bindString(3, imagePath);
        }
        stmt.bindLong(4, entity.getIsSync());
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(5, user_id);
        }
 
        String shelfNumber = entity.getShelfNumber();
        if (shelfNumber != null) {
            stmt.bindString(6, shelfNumber);
        }
 
        String express_id = entity.getExpress_id();
        if (express_id != null) {
            stmt.bindString(7, express_id);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ExpressImage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String imagePath = entity.getImagePath();
        if (imagePath != null) {
            stmt.bindString(3, imagePath);
        }
        stmt.bindLong(4, entity.getIsSync());
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(5, user_id);
        }
 
        String shelfNumber = entity.getShelfNumber();
        if (shelfNumber != null) {
            stmt.bindString(6, shelfNumber);
        }
 
        String express_id = entity.getExpress_id();
        if (express_id != null) {
            stmt.bindString(7, express_id);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ExpressImage readEntity(Cursor cursor, int offset) {
        ExpressImage entity = new ExpressImage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // imagePath
            cursor.getInt(offset + 3), // isSync
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // user_id
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // shelfNumber
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // express_id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ExpressImage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setImagePath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIsSync(cursor.getInt(offset + 3));
        entity.setUser_id(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setShelfNumber(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setExpress_id(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ExpressImage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ExpressImage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ExpressImage entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}