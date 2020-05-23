package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.community.activity.MatterShopActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author : ZSK
 * Date : 2018/12/28
 * Description :  处理字符串
 */
public class StringUtil {

    //获取固定位数的随机数
    public static String getRandomNumberString(int amount) {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < amount; i++) {
            result += random.nextInt(10);
        }
        return result;
    }

    //获取固定位数的随机数
    public static String getRandomCode(int n) {
        String a = "0123456789qwertyuiopasdfghjklzxcvbnmABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] rands = new char[n];
        for (int i = 0; i < rands.length; i++) {
            int rand = (int) (Math.random() * a.length());
            rands[i] = a.charAt(rand);
        }
        return String.valueOf(rands);
    }

    public static String getEffectCode(int code) {
        if (code < 10) {
            return "00" + code;
        } else if (code < 100) {
            return "0" + code;
        } else {
            return code + "";
        }
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        // return strNum.matches("[0-9]{1,}");
        String reg = "\\d+(\\.\\d+)?";
        return strNum.matches(reg);
    }


    public static String getWxChartPayforSign(Map<String, String> map) {
        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }

            }
//			sb.append(PropertyManager.getProperty("SIGNKEY"));
            result = sb.toString();
            //进行MD5加密
            result = MD5Util.toMD5(result);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    //截取字符串str的最后account位
    public static String splitStringFromLast(String str, int account) {
        if (str == null || str.length() == 0) {
            return "";
        }
        if (str.length() < account) {
            return str;
        }
        return str.substring(str.length() - account);
    }

    /**
     * 获取签名字符串
     *
     * @param timeStamp  当前时间戳
     * @param randomStr  随机值
     * @param encryption 加密值
     * @return
     */
    public static String getSignatureString(String timeStamp, String randomStr, String encryption) {
        String str = timeStamp + randomStr + encryption;
        String signature = EncryptUtil.toMD5(EncryptUtil.getSha1(str));
        return signature.toUpperCase();
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
        /*
            移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
            联通：130、131、132、152、155、156、185、186
            电信：133、153、180、189、166,199（1349卫通）
            总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String num = "[1][3456789]\\d{9}";
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 中国电信号段 133、149、153、173、177、180、181、189、199
     * 中国联通号段 130、131、132、145、155、156、166、175、176、185、186
     * 中国移动号段 134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198
     * 其他号段
     * 14号段以前为上网卡专属号段，如中国联通的是145，中国移动的是147等等。
     * 虚拟运营商
     * 电信：1700、1701、1702
     * 移动：1703、1705、1706
     * 联通：1704、1707、1708、1709、171
     * 卫星通信：1349
     **/

    public static String isPhone(String number) {
        String regex = "((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}";
        if (number.length() == 0) {
            return "";
        } else {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(number);

            //查找字符串中是否有符合的子字符串
            while (matcher.find()) {
                return matcher.group();
            }
            return "";
        }

    }


    //判断身份证是否合法
    public static boolean isID(String idNumber) {
        if (idNumber == null || "".equals(idNumber)) {
            return false;
        }
        String regular = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        return idNumber.matches(regular);
    }

    //判断身份证是否合法
    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        boolean matches = IDNumber.matches(regularExpression);

        //判断第18位校验值
        if (matches) {
            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        System.out.println("身份证最后一位:" + String.valueOf(idCardLast).toUpperCase() +
                                "错误,正确的应该是:" + idCardY[idCardMod].toUpperCase());
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("异常:" + IDNumber);
                    return false;
                }
            }

        }
        return matches;
    }


    /**
     * 重新拼接字符串
     *
     * @param dataStr   目标字符串
     * @param subNumber 分割字符串位数
     * @param spaceData 中间填充的字符串
     * @return
     */
    public static String changeStringFormat(String dataStr, int subNumber, String spaceData) {
        StringBuffer dataBuffer = new StringBuffer();
        if (dataStr == null || dataStr.length() == 0) {
            return "";
        }
        int dataLength = dataStr.length();
        if (dataLength < subNumber) {
            return dataStr;
        }

        int wholeAmount = dataLength / subNumber;    //取整
        int lessAmount = dataLength % subNumber;   //取余

        int position = 0;
        for (int i = 0; i < wholeAmount; i++) {
            String subStr = dataStr.substring(position, position + subNumber);
            dataBuffer.append(subStr + spaceData);
            position += subNumber;
        }

        if (lessAmount != 0) {
            String lessStr = dataStr.substring(subNumber * wholeAmount);
            dataBuffer.append(lessStr);
        }

        return dataBuffer.toString();
    }


    //处理数字但是为null的数据
    public static String handleNullResultForNumber(String data) {
        if (data == null || "null".equals(data)) {
            return "0";
        } else {
            return data;
        }
    }

    //处理字符串类型但是为null的数据
    public static String handleNullResultForString(String data) {
        if (data == null || "null".equals(data) || "nullnull".equals(data)) {
            return "";
        } else {
            return data;
        }
    }

    //交换两个字符串
    public static String[] changeStr1ToStr2(String str1, String str2) {

        return new String[]{str2, str1};
    }

    public static double handeNullResultForDouble(Double data){
        if (data == null){
            return 0;
        }
        return data;
    }

    //生成混合字符串
    public static String getMixString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        String numberStr = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(63);
            sb.append(str.charAt(number));
        }
        sb.append("_");
        for (int j = 0; j < length / 2; j++) {
            int number1 = random.nextInt(10);
            sb.append(numberStr.charAt(number1));
        }
        return sb.toString();
    }

    //获取assets下的json字符串
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获取assets资源管理器
        AssetManager assetManager = context.getAssets();
        //IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    //加密电话号码
    public static String encryptPhone(String phone) {
        String encryptStr = phone;
        if ("".equals(encryptStr)) {
            return "";
        }
        int length = encryptStr.length();
        StringBuilder builder = new StringBuilder();
        builder.append(encryptStr.substring(0, 3));
        builder.append("****");
        builder.append(encryptStr.substring(length - 3));
        return builder.toString();
    }

    //加密银行卡号
    public static String encryptBankNumber(String bankNumber) {
        String encryptStr = bankNumber;
        if ("".equals(encryptStr)) {
            return "";
        }
        int length = encryptStr.length();
        StringBuilder builder = new StringBuilder();
        builder.append(encryptStr.substring(0, 3));
        builder.append("************");
        builder.append(encryptStr.substring(length - 3));
        return builder.toString();
    }

    public static double changeStringToDouble(String data) {
        if (null == data || "".equals(data)) {
            return 0;
        }
        return Double.parseDouble(data);
    }

    public static int changeStringToInt(String data) {
        if (null == data || "".equals(data)) {
            return 0;
        }
        return Integer.parseInt(data);
    }

    public static String formatDouble(double data) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(data) + "";
    }


    public static float formatStringToFloat(String str) {
        if (str == null || "".equals(str)) {
            return 0;
        }
        return Float.parseFloat(str);
    }

    //加密方式 sha1、md5
    public static String getsignature2(Map<String, String> map) {
        String result = "";
        String sign = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {

                String key = item.getKey();
                String val = item.getValue();
                sb.append(key + val);
                /*if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }*/
            }
            sb.append(Constant.key);
            result = sb.toString();
            result = EncryptUtil.getSha1(result);
            //进行MD5加密
            result = MD5Util.toMD5(result);
            sign = result.toUpperCase();
        } catch (Exception e) {
            return null;
        }
        return sign;
    }

    //加密方式 sha1、md5
    public static String getsignature(Map<String, String> map) {
        String result = "";
        String sign = "";

        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {

                String key = item.getKey();
                String val = item.getValue();
                sb.append(key + val);
                /*if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }*/
            }
            sb.append(Constant.key);
            result = sb.toString();

            result = EncryptUtil.getSha1(result);
            //进行MD5加密
            result = MD5Util.toMD5(result);
            sign = result.toUpperCase();
        } catch (Exception e) {
            return null;
        }
        return sign;
    }

    public static String[] handleCode(String code) {
        if (code == null || "".equals(code)) {
            String firstData = DateUtil.getCurrentDay();
            return new String[]{firstData, ""};
        } else if (code.length() < 4) {
            return new String[]{code, ""};
        } else {
            String result1 = code.substring(0, 4);
            String result2 = code.substring(4, code.length());
            return new String[]{result1, result2};
        }
    }

    public static String[] spliteStrFormat(String tagStr, String format) {
        String[] tagCode = new String[]{"-", "-"};
        if (tagStr == null || "".equals(tagStr)) {
            return tagCode;
        } else {
            String[] tag = tagStr.split(format);
            if (tag.length == 0) {
                tagCode[0] = tag[0];
            } else if (tag.length > 1) {
                tagCode[0] = tag[0];
                tagCode[1] = tag[1];
            }
            return tagCode;
        }
    }

    public static String[] spliteStrFormat(String tagStr) {
        String[] tagCode = new String[]{"-", "-"};
        if (tagStr == null || "".equals(tagStr)) {
            return tagCode;
        } else if (tagStr.length() < 2) {
            tagCode[0] = tagStr;
        } else {
            tagCode[0] = tagStr.substring(0,2);
            tagCode[1] = tagStr.substring(2,tagStr.length());
            return tagCode;
        }
        return tagCode;
    }

    public static String formatCode(int tagNumber){
        if (tagNumber < 10) {
            return "000"+tagNumber;
        }else if (tagNumber < 100){
            return "00"+tagNumber;
        } else if (tagNumber < 1000){
            return "0"+tagNumber;
        } else {
            return tagNumber+"";
        }
    }

    //手机号码格式
    public static boolean isMachinePhone(String data){
        String regex = "^[1]([3-9])[0-9]{9}$";
        Pattern p=Pattern.compile(regex);
        Matcher m=p.matcher(data);
        return m.matches();
    }

    /**
     * 是否快递单号
     * 字母或数字
     * @param input
     * @return
     */
    public static boolean isMatchExpressCode( final CharSequence input) {
        String regex = "^[A-Za-z0-9]+$";
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }
}
