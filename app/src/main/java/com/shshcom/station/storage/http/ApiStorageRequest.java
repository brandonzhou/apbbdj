package com.shshcom.station.storage.http;

import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.utls.EncryptUtil;
import com.mt.bbdj.baseconfig.utls.MD5Util;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class ApiStorageRequest {


    /**
     * 1.1签名说明
     * 需每个接口都要传递签名值
     * Ⅰ、将所传参数按首字母大小顺序排序
     * 例：station_id=12&code=1234，结果为code1234station_id12
     * Ⅱ、将Ⅰ 生成的字符串后面拼接密钥secret
     * 例：code1234station_id12 + secret
     * Ⅲ、将Ⅱ 生成的字符串进行sha1 加密
     * Ⅳ、将 Ⅲ 生成的字符串进行md5 加密
     * Ⅴ、将 Ⅳ 生成的字符串转为大写
     * <p>
     * secret密钥值：take2019bbdj
     *
     * @param map
     * @return
     */
    public static void addSignature(Map<String, Object> map) {
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {

            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        // 构造签名键值对的格式
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> item : infoIds) {
            sb.append(item.getKey().trim());
            sb.append(item.getValue());
        }


        sb.append(Constant.key);
        String result = sb.toString();
        result = EncryptUtil.getSha1(result);
        //进行MD5加密
        result = MD5Util.toMD5(result);
        String sign = result.toUpperCase();

        map.put("signature", sign);
    }


    /**
     * station_id	String	驿站标识
     * number	String	快递单号
     * code	String	取件码
     * file	File	文件流，数据加密时不需要参与加密
     * <p>
     * http://qrcode.taowangzhan.com/bbapi/submit/stationUploadExpressImg3
     */
    public static Request<String> stationUploadExpressImg3(String eId, String pickCode, String station_id, String filePath, String expressCompanyId) {
        String url = "https://qrcode.taowangzhan.com/bbapi/submit/stationUploadExpressImg3";
        Map<String, Object> map = new HashMap<>();
        map.put("code", pickCode);
        map.put("number", eId);
        map.put("station_id", station_id);
        addSignature(map);
        map.put("express_id", expressCompanyId);

        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(map);

        File file = new File(filePath);

        if(file.exists()){
            BasicBinary fileBinary = new FileBinary(file);
            request.add("file", fileBinary);
        }


        return request;
    }


    /**
     * signature	String	签名值
     * station_id	String	驿站标识
     * http://qrcode.taowangzhan.com/bbapi/submit/stationOcrResult
     */

    public static Request<String> stationOcrResult(String station_id) {
        String url = "https://qrcode.taowangzhan.com/bbapi/submit/stationOcrResult";

        Map<String, Object> map = new HashMap<>();
        map.put("station_id", station_id);
        addSignature(map);
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(map);

        return request;
    }

    /**
     * 接口名称	编辑快递面单信息
     * 功能描述	修改识别失败的快递手机号、取件码、条形码、快递公司
     * https://qrcode.taowangzhan.com/bbapi/submit/stationUpdatePie
     * <p>
     * signature	String	签名值
     * <p>
     * station_id	String	驿站标识
     * pie_id	String	快递id
     * number	String	条形码
     * code	String	取件码
     * mobile	String	手机号
     * express_id	Number	快递公司id
     *
     * @param station_id
     * @return
     */
    public static Request<String> stationUpdatePie(String number, String code, String express_id,
                                                   String mobile, String pie_id, String station_id) {
        String url = "https://qrcode.taowangzhan.com/bbapi/submit/stationUpdatePie";

        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("express_id", express_id);
        map.put("number", number);
        map.put("mobile", mobile);
        map.put("pie_id", pie_id);
        map.put("station_id", station_id);
        addSignature(map);
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(map);

        return request;
    }


    /**
     * 快递公司列表
     *
     * @param station_id
     * @return
     */
    public static Request<String> getExpressCompany(String station_id) {
        String url = "https://meng.81dja.com/express/warehousing/getExpressCompany";

        Map<String, Object> map = new HashMap<>();
        map.put("user_id", station_id);
        addSignature(map);
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(map);
        return request;
    }


    /**
     * 删除快递信息
     * @param station_id
     * @param pie_id
     * @return
     */
    public static Request<String> stationSyncDelete(String station_id, String pie_id) {
        String url = "https://qrcode.taowangzhan.com/bbapi/submit/stationSyncDelete";

        Map<String, Object> map = new HashMap<>();
        map.put("station_id", station_id);
        map.put("pie_id", pie_id);
        addSignature(map);
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(map);
        return request;
    }


    /**
     * 手动输入快递信息
     *
     * @param station_id 驿站标识
     * @param barCode 快递单号
     * @param barCode 取件码
     * @param mobile 手机号
     * @param filePath 文件流
     * @return
     */
    public static Request<String> stationInputUploadExpress(String station_id, String barCode, String pickCode,String mobile, String filePath) {
        String url = "https://qrcode.taowangzhan.com/bbapi/submit2/stationInputUploadExpress";

        Map<String, Object> map = new HashMap<>();
        map.put("station_id", station_id);
        map.put("number", barCode);
        map.put("code", pickCode);
        map.put("mobile", mobile);
        addSignature(map);

        BasicBinary fileBinary = new FileBinary(new File(filePath));

        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(map);
        request.add("file", fileBinary);

        return request;
    }


    /**
     * 查询快递公司
     * @param station_id  驿站标识
     * @param barCode  快递单号
     * @return
     */
    public static Request<String> queryExpress(String station_id, String barCode) {
        String url = "https://qrcode.taowangzhan.com/bbapi/submit2/queryExpress";

        Map<String, Object> map = new HashMap<>();
        map.put("station_id", station_id);
        map.put("number", barCode);
        addSignature(map);

        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add(map);

        return request;
    }



}
