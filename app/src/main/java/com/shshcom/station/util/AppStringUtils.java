package com.shshcom.station.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.text.DecimalFormat;

/**
 * des:
 * Created by zhh_li
 * on 2017/7/24.
 */

public class AppStringUtils {

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 更改TextView某一段字体的颜色值
     *
     * @param text
     * @param subTextArray
     * @return
     */
    public static SpannableStringBuilder getTextSpan(int subTextBgColor, CharSequence text, String... subTextArray) {
        if (text == null || subTextArray == null) {
            return null;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int begin = 0;
        int end = 0;
        for (int i = 0; i < subTextArray.length; i++) {
            String subText = subTextArray[i];
            begin = text.toString().indexOf(subText, end);
            if (begin > -1) {
                // -1 未找到
                end = begin + subText.length();
                style.setSpan(new ForegroundColorSpan(subTextBgColor), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        return style;
    }


    public static SpannableStringBuilder getTextSpan(int subTextBgColor, SpannableStringBuilder style, String subText, String subNew) {
        if (style == null || subText == null) {
            return null;
        }

        int end = 0;
        int begin = style.toString().indexOf(subText, end);
        if (begin > -1) {
            // -1 未找到
            String stringNew = style.toString().replace(subText, subNew);
            style = new SpannableStringBuilder(stringNew);
            end = begin + subNew.length();
            style.setSpan(new ForegroundColorSpan(subTextBgColor), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        return null;
    }


    /**
     * https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html
     *
     * @param object number
     * @return ¥1, 234, 567, 890.35
     */
    public static String getDecimalFormat(Object object) {
        DecimalFormat decimalFormat = new DecimalFormat("\u00A5###,##0.00");
        String output = decimalFormat.format(object);
        return output;
    }

    public static String getDecimalFormatNotY(Object object) {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        String output = decimalFormat.format(object);
        return output;
    }

    public static String getDecimalFormatNotYZero(Object object) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        String output = decimalFormat.format(object);
        return output;
    }


}
