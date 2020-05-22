package com.mt.bbdj.baseconfig.db.gen;

import com.mt.bbdj.baseconfig.db.BluetoothMessage;
import com.mt.bbdj.baseconfig.db.City;
import com.mt.bbdj.baseconfig.db.County;
import com.mt.bbdj.baseconfig.db.ExpressImage;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.MingleArea;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.Province;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.db.ScannerMessageModel;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.WaillMessage;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bluetoothMessageDaoConfig;
    private final DaoConfig cityDaoConfig;
    private final DaoConfig countyDaoConfig;
    private final DaoConfig expressImageDaoConfig;
    private final DaoConfig expressLogoDaoConfig;
    private final DaoConfig mingleAreaDaoConfig;
    private final DaoConfig pickupCodeDaoConfig;
    private final DaoConfig provinceDaoConfig;
    private final DaoConfig scanImageDaoConfig;
    private final DaoConfig scannerMessageModelDaoConfig;
    private final DaoConfig userBaseMessageDaoConfig;
    private final DaoConfig waillMessageDaoConfig;

    private final BluetoothMessageDao bluetoothMessageDao;
    private final CityDao cityDao;
    private final CountyDao countyDao;
    private final ExpressImageDao expressImageDao;
    private final ExpressLogoDao expressLogoDao;
    private final MingleAreaDao mingleAreaDao;
    private final PickupCodeDao pickupCodeDao;
    private final ProvinceDao provinceDao;
    private final ScanImageDao scanImageDao;
    private final ScannerMessageModelDao scannerMessageModelDao;
    private final UserBaseMessageDao userBaseMessageDao;
    private final WaillMessageDao waillMessageDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bluetoothMessageDaoConfig = daoConfigMap.get(BluetoothMessageDao.class).clone();
        bluetoothMessageDaoConfig.initIdentityScope(type);

        cityDaoConfig = daoConfigMap.get(CityDao.class).clone();
        cityDaoConfig.initIdentityScope(type);

        countyDaoConfig = daoConfigMap.get(CountyDao.class).clone();
        countyDaoConfig.initIdentityScope(type);

        expressImageDaoConfig = daoConfigMap.get(ExpressImageDao.class).clone();
        expressImageDaoConfig.initIdentityScope(type);

        expressLogoDaoConfig = daoConfigMap.get(ExpressLogoDao.class).clone();
        expressLogoDaoConfig.initIdentityScope(type);

        mingleAreaDaoConfig = daoConfigMap.get(MingleAreaDao.class).clone();
        mingleAreaDaoConfig.initIdentityScope(type);

        pickupCodeDaoConfig = daoConfigMap.get(PickupCodeDao.class).clone();
        pickupCodeDaoConfig.initIdentityScope(type);

        provinceDaoConfig = daoConfigMap.get(ProvinceDao.class).clone();
        provinceDaoConfig.initIdentityScope(type);

        scanImageDaoConfig = daoConfigMap.get(ScanImageDao.class).clone();
        scanImageDaoConfig.initIdentityScope(type);

        scannerMessageModelDaoConfig = daoConfigMap.get(ScannerMessageModelDao.class).clone();
        scannerMessageModelDaoConfig.initIdentityScope(type);

        userBaseMessageDaoConfig = daoConfigMap.get(UserBaseMessageDao.class).clone();
        userBaseMessageDaoConfig.initIdentityScope(type);

        waillMessageDaoConfig = daoConfigMap.get(WaillMessageDao.class).clone();
        waillMessageDaoConfig.initIdentityScope(type);

        bluetoothMessageDao = new BluetoothMessageDao(bluetoothMessageDaoConfig, this);
        cityDao = new CityDao(cityDaoConfig, this);
        countyDao = new CountyDao(countyDaoConfig, this);
        expressImageDao = new ExpressImageDao(expressImageDaoConfig, this);
        expressLogoDao = new ExpressLogoDao(expressLogoDaoConfig, this);
        mingleAreaDao = new MingleAreaDao(mingleAreaDaoConfig, this);
        pickupCodeDao = new PickupCodeDao(pickupCodeDaoConfig, this);
        provinceDao = new ProvinceDao(provinceDaoConfig, this);
        scanImageDao = new ScanImageDao(scanImageDaoConfig, this);
        scannerMessageModelDao = new ScannerMessageModelDao(scannerMessageModelDaoConfig, this);
        userBaseMessageDao = new UserBaseMessageDao(userBaseMessageDaoConfig, this);
        waillMessageDao = new WaillMessageDao(waillMessageDaoConfig, this);

        registerDao(BluetoothMessage.class, bluetoothMessageDao);
        registerDao(City.class, cityDao);
        registerDao(County.class, countyDao);
        registerDao(ExpressImage.class, expressImageDao);
        registerDao(ExpressLogo.class, expressLogoDao);
        registerDao(MingleArea.class, mingleAreaDao);
        registerDao(PickupCode.class, pickupCodeDao);
        registerDao(Province.class, provinceDao);
        registerDao(ScanImage.class, scanImageDao);
        registerDao(ScannerMessageModel.class, scannerMessageModelDao);
        registerDao(UserBaseMessage.class, userBaseMessageDao);
        registerDao(WaillMessage.class, waillMessageDao);
    }
    
    public void clear() {
        bluetoothMessageDaoConfig.clearIdentityScope();
        cityDaoConfig.clearIdentityScope();
        countyDaoConfig.clearIdentityScope();
        expressImageDaoConfig.clearIdentityScope();
        expressLogoDaoConfig.clearIdentityScope();
        mingleAreaDaoConfig.clearIdentityScope();
        pickupCodeDaoConfig.clearIdentityScope();
        provinceDaoConfig.clearIdentityScope();
        scanImageDaoConfig.clearIdentityScope();
        scannerMessageModelDaoConfig.clearIdentityScope();
        userBaseMessageDaoConfig.clearIdentityScope();
        waillMessageDaoConfig.clearIdentityScope();
    }

    public BluetoothMessageDao getBluetoothMessageDao() {
        return bluetoothMessageDao;
    }

    public CityDao getCityDao() {
        return cityDao;
    }

    public CountyDao getCountyDao() {
        return countyDao;
    }

    public ExpressImageDao getExpressImageDao() {
        return expressImageDao;
    }

    public ExpressLogoDao getExpressLogoDao() {
        return expressLogoDao;
    }

    public MingleAreaDao getMingleAreaDao() {
        return mingleAreaDao;
    }

    public PickupCodeDao getPickupCodeDao() {
        return pickupCodeDao;
    }

    public ProvinceDao getProvinceDao() {
        return provinceDao;
    }

    public ScanImageDao getScanImageDao() {
        return scanImageDao;
    }

    public ScannerMessageModelDao getScannerMessageModelDao() {
        return scannerMessageModelDao;
    }

    public UserBaseMessageDao getUserBaseMessageDao() {
        return userBaseMessageDao;
    }

    public WaillMessageDao getWaillMessageDao() {
        return waillMessageDao;
    }

}
