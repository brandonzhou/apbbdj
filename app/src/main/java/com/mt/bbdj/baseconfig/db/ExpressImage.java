package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;



/**
 * @Author : ZSK
 * @Date : 2019/12/3
 * @Description :
 */
@Entity
public class ExpressImage {
    @Id
    private Long id;
    private String uuid;
    private String imagePath;  //路径
    private int isSync;   //是否同步
    private String user_id;  //对应的用户id
    private String shelfNumber;    //货架
    private String express_id;   //快递公司
    @Generated(hash = 477454502)
    public ExpressImage(Long id, String uuid, String imagePath, int isSync,
            String user_id, String shelfNumber, String express_id) {
        this.id = id;
        this.uuid = uuid;
        this.imagePath = imagePath;
        this.isSync = isSync;
        this.user_id = user_id;
        this.shelfNumber = shelfNumber;
        this.express_id = express_id;
    }
    @Generated(hash = 1499334995)
    public ExpressImage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getImagePath() {
        return this.imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public int getIsSync() {
        return this.isSync;
    }
    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getShelfNumber() {
        return this.shelfNumber;
    }
    public void setShelfNumber(String shelfNumber) {
        this.shelfNumber = shelfNumber;
    }
    public String getExpress_id() {
        return this.express_id;
    }
    public void setExpress_id(String express_id) {
        this.express_id = express_id;
    }
}
