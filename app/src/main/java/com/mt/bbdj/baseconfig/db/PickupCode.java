package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:取件码规则
 * author: zhhli
 * 2020/5/19
 */
@Entity
public class PickupCode {
    @Id
    private String user_id;

    private String type;//Type.type_3
    private String shelfNumber;
    private int startNumber;//2345
    // 当前号码
    private String currentNumber;//A12-19-2345

    @Generated(hash = 1952477772)
    public PickupCode(String user_id, String type, String shelfNumber,
            int startNumber, String currentNumber) {
        this.user_id = user_id;
        this.type = type;
        this.shelfNumber = shelfNumber;
        this.startNumber = startNumber;
        this.currentNumber = currentNumber;
    }
    @Generated(hash = 1138314330)
    public PickupCode() {
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getShelfNumber() {
        return this.shelfNumber;
    }
    public void setShelfNumber(String shelfNumber) {
        this.shelfNumber = shelfNumber;
    }
    public int getStartNumber() {
        return this.startNumber;
    }
    public void setStartNumber(int startNumber) {
        this.startNumber = startNumber;
    }
    public String getCurrentNumber() {
        return this.currentNumber;
    }
    public void setCurrentNumber(String currentNumber) {
        this.currentNumber = currentNumber;
    }


    enum Type{
        type_1("编号累加"),
        type_2("货架号+编号累加"),
        type_3("货架号+日期+编号累加"),
        type_4("货架号+日期+单号尾号"),
        type_5("货架号+单号尾号")
        ;

        private String desc;

        Type(String desc) {
            this.desc = desc;
        }
    }


    public static List<Type> typeList(){
        List<Type> types = new ArrayList<>();
        types.add(Type.type_1);
        types.add(Type.type_2);
        types.add(Type.type_3);
        types.add(Type.type_4);
        types.add(Type.type_5);
        return types;
    }
}
