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

    @Id
    private Long uId;

    private String stationId;

    private String type;//Type.type_3
    private int shelfId;
    private String shelfNumber;
    private int startNumber;//2345
    // 当前号码
    private String lastCode = "";//A12-19-2345

    private long time;


    @Keep
    public PickupCode() {
        time = System.currentTimeMillis();
    }


    @Generated(hash = 1510827388)
    public PickupCode(Long uId, String stationId, String type, int shelfId, String shelfNumber, int startNumber,
                      String lastCode, long time) {
        this.uId = uId;
        this.stationId = stationId;
        this.type = type;
        this.shelfId = shelfId;
        this.shelfNumber = shelfNumber;
        this.startNumber = startNumber;
        this.lastCode = lastCode;
        this.time = time;
    }


    public PickupCode nextPickCode() {
        PickupCode newCode = new PickupCode();
        newCode.setUId(uId);
        newCode.setStationId(stationId);
        newCode.setType(type);
        newCode.setShelfNumber(shelfNumber);
        newCode.setLastCode(lastCode);
        newCode.setShelfId(shelfId);


        if (type.equals(Type.type_shelf_date_tail.getDesc()) || type.equals(Type.type_shelf_tail.getDesc())) {
            newCode.setStartNumber(startNumber);
        } else {
            int nextNumber = startNumber + 1;
            if (nextNumber > 999999) {
                nextNumber = 1000;
            }
            newCode.setStartNumber(nextNumber);
        }

        newCode.createCurrentNumber();

        return newCode;

    }

    public String createCurrentNumber() {
        String currentStr = startNumber + "";
        if (Type.type_code.getDesc().equals(type)) {
            currentStr = startNumber + "";
        }

        if (Type.type_shelf_code.getDesc().equals(type)) {
            currentStr = shelfNumber + "-" + startNumber;
        }

        if (Type.type_shelf_date_code.getDesc().equals(type)) {
            DateTime dateTime = DateTime.now();
            currentStr = shelfNumber + "-" + dateTime.getDayOfMonth() + "-" + startNumber;
        }

        if (Type.type_shelf_date_tail.getDesc().equals(type)) {//单号尾号
            DateTime dateTime = DateTime.now();
            currentStr = shelfNumber + "-" + dateTime.getDayOfMonth() + "-单号尾号";
        }

        if (Type.type_shelf_tail.getDesc().equals(type)) {
            currentStr = shelfNumber + "-单号尾号";
        }


        return currentStr;
    }

    public String createRealPickCode(String barCode) {

        if (Type.type_code.getDesc().equals(type)) {
            lastCode = startNumber + "";
            return lastCode;
        }

        if (Type.type_shelf_code.getDesc().equals(type)) {
            lastCode = shelfNumber + "-" + startNumber;
            return lastCode;
        }

        if (Type.type_shelf_date_code.getDesc().equals(type)) {
            DateTime dateTime = DateTime.now();
            lastCode = shelfNumber + "-" + dateTime.getDayOfMonth() + "-" + startNumber;
            return lastCode;
        }

        String currentStr = startNumber + "";
        String tailNumber = barCode;

        if (tailNumber != null && tailNumber.length() > 4) {
            int length = tailNumber.length();
            tailNumber = tailNumber.substring(length - 4, length);
        }

        if (Type.type_shelf_date_tail.getDesc().equals(type)) {//单号尾号
            DateTime dateTime = DateTime.now();
            currentStr = shelfNumber + "-" + dateTime.getDayOfMonth() + "-" + tailNumber;
        }

        if (Type.type_shelf_tail.getDesc().equals(type)) {
            currentStr = shelfNumber + "-" + tailNumber;
        }

        lastCode = currentStr;

        return currentStr;
    }


    public boolean isTail() {
        return Type.type_shelf_tail.getDesc().equals(type) || Type.type_shelf_date_tail.getDesc().equals(type);
    }


    public enum Type {
        type_code(0, "编号累加"),
        type_shelf_code(1, "货架号+编号累加"),
        type_shelf_date_code(2, "货架号+日期+编号累加"),
        type_shelf_date_tail(3, "货架号+日期+单号尾号"),
        type_shelf_tail(4, "货架号+单号尾号");

        private String desc;
        private int rule;

        Type(int rule, String desc) {
            this.desc = desc;
            this.rule = rule;
        }

        public String getDesc() {
            return desc;
        }

        public int getRule() {
            return rule;
        }

        public static Type from(int rule) {
            Type[] types = Type.values();
            for (Type type : types) {
                if (type.rule == rule) {
                    return type;
                }
            }
            return Type.type_shelf_code;
        }

        public static Type from(String desc) {
            Type[] types = Type.values();
            for (Type type : types) {
                if (type.desc.equals(desc)) {
                    return type;
                }
            }
            return Type.type_shelf_code;
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
        return createCurrentNumber();
    }


//    @Override
//    public String toString() {
//        return "PickupCode{" +
//                "stationId='" + stationId + '\'' +
//                ", type='" + type + '\'' +
//                ", shelfNumber='" + shelfNumber + '\'' +
//                ", startNumber=" + startNumber +
//                ", currentNumber='" + currentNumber + '\'' +
//                '}';
//    }


    public Long getUId() {
        return this.uId;
    }


    public void setUId(Long uId) {
        this.uId = uId;
    }


    public long getTime() {
        return this.time;
    }


    public void setTime(long time) {
        this.time = time;
    }


    public int getShelfId() {
        return this.shelfId;
    }


    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }


    public String getLastCode() {
        return this.lastCode;
    }


    public void setLastCode(String lastCode) {
        this.lastCode = lastCode;
    }

}
