package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * desc:取件码规则
 * author: zhhli
 * 2020/5/19
 */
@Entity(indexes = {
        @Index(value = "time DESC")
})
public class PickupCode implements Serializable {
    private static final long serialVersionUID = -5782895438563878774L;

    @Id(autoincrement = true)
    private long uId;

    private String stationId;

    private String type;//Type.type_3
    private String shelfNumber;
    private int startNumber;//2345
    // 当前号码
    private String currentNumber;//A12-19-2345

    private long time;


    @Generated(hash = 879675881)
    public PickupCode(long uId, String stationId, String type, String shelfNumber, int startNumber,
            String currentNumber, long time) {
        this.uId = uId;
        this.stationId = stationId;
        this.type = type;
        this.shelfNumber = shelfNumber;
        this.startNumber = startNumber;
        this.currentNumber = currentNumber;
        this.time = time;
    }


    @Keep
    public PickupCode() {
        time = System.currentTimeMillis();
    }


    public PickupCode nextPickCode(){
        PickupCode newCode = new PickupCode();
        newCode.setStationId(stationId);
        newCode.setType(type);
        newCode.setShelfNumber(shelfNumber);


        if(type.equals(Type.type_shelf_date_tail.getDesc()) || type.equals(Type.type_shelf_tail.getDesc())){
            newCode.setStartNumber(startNumber);
        }else {
            int nextNumber = startNumber+1;
            if(nextNumber>999999){
                nextNumber = 1000;
            }
            newCode.setStartNumber(nextNumber);
            newCode.setCurrentNumber(nextNumber+"");
        }

        newCode.createCurrentNumber();

        return newCode;

    }

    public String createCurrentNumber(){
        String currentStr = startNumber+"";
        if(Type.type_code.getDesc().equals(type)){
            currentStr = startNumber+"";
        }

        if(Type.type_shelf_code.getDesc().equals(type)){
            currentStr = shelfNumber+"-"+startNumber;
        }

        if(Type.type_shelf_date_code.getDesc().equals(type)){
            DateTime dateTime = DateTime.now();
            currentStr = shelfNumber+"-"+dateTime.getDayOfMonth()+"-"+startNumber;
        }

        if(Type.type_shelf_date_tail.getDesc().equals(type)){//单号尾号
            DateTime dateTime = DateTime.now();
            currentStr = shelfNumber+"-"+dateTime.getDayOfMonth()+"-单号尾号";
        }

        if(Type.type_shelf_tail.getDesc().equals(type)){
            currentStr = shelfNumber+"-"+"-单号尾号";
        }

        currentNumber = currentStr;



        return currentStr;
    }

    public String createRealPickCode(String barCode){
        String currentStr = startNumber+"";
        if(Type.type_code.getDesc().equals(type)){
            return startNumber+"";
        }

        if(Type.type_shelf_code.getDesc().equals(type)){
            return shelfNumber+"-"+startNumber;
        }

        if(Type.type_shelf_date_code.getDesc().equals(type)){
            DateTime dateTime = DateTime.now();
            return shelfNumber+"-"+dateTime.getDayOfMonth()+"-"+startNumber;
        }

        String tailNumber = barCode;

        if(tailNumber!= null && tailNumber.length()>4){
            int length = tailNumber.length();
            tailNumber = tailNumber.substring(length-4, length);
        }

        if(Type.type_shelf_date_tail.getDesc().equals(type)){//单号尾号
            DateTime dateTime = DateTime.now();
            currentStr = shelfNumber+"-"+dateTime.getDayOfMonth()+"-"+tailNumber;
        }

        if(Type.type_shelf_tail.getDesc().equals(type)){
            currentStr = shelfNumber+"-"+tailNumber;
        }

        return currentStr;
    }


    public boolean isTail(){
        return Type.type_shelf_tail.getDesc().equals(type) || Type.type_shelf_date_tail.getDesc().equals(type);
    }
    


    public enum Type{
        type_code("编号累加"),
        type_shelf_code("货架号+编号累加"),
        type_shelf_date_code("货架号+日期+编号累加"),
        type_shelf_date_tail("货架号+日期+单号尾号"),
        type_shelf_tail("货架号+单号尾号")
        ;

        private String desc;

        Type(String desc) {
            this.desc = desc;
        }

        public String getDesc(){
            return desc;
        }
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

    @Override
    public String toString() {
        return "PickupCode{" +
                "stationId='" + stationId + '\'' +
                ", type='" + type + '\'' +
                ", shelfNumber='" + shelfNumber + '\'' +
                ", startNumber=" + startNumber +
                ", currentNumber='" + currentNumber + '\'' +
                '}';
    }


    public long getUId() {
        return this.uId;
    }


    public void setUId(long uId) {
        this.uId = uId;
    }


    public long getTime() {
        return this.time;
    }


    public void setTime(long time) {
        this.time = time;
    }
}
