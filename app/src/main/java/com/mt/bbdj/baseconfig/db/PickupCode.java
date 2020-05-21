package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

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
    private String stationId;

    private String type;//Type.type_3
    private String shelfNumber;
    private int startNumber;//2345
    // 当前号码
    private String currentNumber;//A12-19-2345


    @Generated(hash = 1826702311)
    public PickupCode(String stationId, String type, String shelfNumber,
            int startNumber, String currentNumber) {
        this.stationId = stationId;
        this.type = type;
        this.shelfNumber = shelfNumber;
        this.startNumber = startNumber;
        this.currentNumber = currentNumber;
    }


    @Generated(hash = 1138314330)
    public PickupCode() {
    }


    public PickupCode nextPickCode(){
        PickupCode newCode = new PickupCode();
        newCode.setStationId(stationId);
        newCode.setType(type);
        newCode.setShelfNumber(shelfNumber);




        if(type.equals(Type.type_4.getDesc()) || type.equals(Type.type_5.getDesc())){
            newCode.setStartNumber(startNumber);
            String currentStr = "单号尾号";
        }else {
            int nextNumber = startNumber+1;
            if(nextNumber>9999){
                nextNumber = 1000;
            }
            newCode.setStartNumber(nextNumber);
            newCode.setCurrentNumber(nextNumber+"");
        }

        return newCode;

    }


    


    public enum Type{
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

        public String getDesc(){
            return desc;
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


    public String getStationId() {
        return this.stationId;
    }


    public void setStationId(String stationId) {
        this.stationId = stationId;
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


    
}
