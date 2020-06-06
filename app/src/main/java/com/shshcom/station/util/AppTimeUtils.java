package com.shshcom.station.util;


import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * des:
 * http://tangpj.com/2017/05/02/dateformat/
 * @author zhhli
 * @date 2018/3/24
 */

public class AppTimeUtils {

    // YYYYMMDDHHMMSS
    public static final String defaultFormat = "yyyy-MM-dd HH:mm:ss";

    public static SimpleDateFormat createFormat(String format){
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    public static Date str2Date(String time, SimpleDateFormat dateFormat){
        try {
            return dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String date2String(Date date, SimpleDateFormat dateFormat){
        return dateFormat.format(date);
    }


    public static boolean isSameDay(DateTime dateTime1, DateTime dateTime2) {
        int days = Math.abs(Days.daysBetween(dateTime1.withMillisOfDay(0), dateTime2.withMillisOfDay(0)).getDays());
        if (days < 1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 距离一个小时
     *
     * @param dateTime1
     * @param dateTime2
     * @return
     */
    public static boolean isBetweenHour(DateTime dateTime1, DateTime dateTime2) {
        int hour = Math.abs(Hours.hoursBetween(dateTime1, dateTime2).getHours());
        if (hour < 1) {
            return true;
        } else {
            return false;
        }
    }



    public static String getYmdhsTime(Date date) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date);

        return time;
    }

    public static String getYmdTime(Date date) {
        String time = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);

        return time;
    }

    public static String getFormatTime(Date date, String format){
        String time = new SimpleDateFormat(format, Locale.getDefault()).format(date);
        return time;
    }


}
