package com.mt.bbdj.baseconfig.internet;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.mt.bbdj.baseconfig.model.ExpressMoney;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.MD5Util;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : ZSK
 * Date : 2018/12/27
 * Description :  网络请求的封装
 */
public class NoHttpRequest {

    /**
     * 获取验证码的请求
     *
     * @param phoneNumber : 手机号码
     * @param type        : 1 注册  2 忘记密码  3 提现
     */
    public static Request<String> getIdentifyCodeRequest(String phoneNumber, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_IDENTIFY_CODE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_IDENTIFY_CODE);
        request.add("signature", signature);
        request.add("phone", phoneNumber);
        request.add("type", type);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        return request;
    }

    /**
     * 上传图片请求
     *
     * @param filePath 图片的路径
     */
    public static Request<String> commitPictureRequest(@NonNull String filePath) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        BasicBinary fileBinary = new FileBinary(new File(filePath));
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_PICTURE, RequestMethod.POST);
        request.add("method", InterApi.ACTION_COMMIT_PICTURE);
        request.add("file", fileBinary);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        return request;
    }

    /**
     * 上传图片请求
     *
     */
    public static Request<String> commitPannelPictureRequest(@NonNull String filePath,String station_id,String uuid,String express_id) {
        HashMap<String,String> params = new HashMap<>();
        params.put("station_id",station_id);
        params.put("uuid",uuid);
        params.put("express_id",express_id);
        String signature = StringUtil.getsignature(params);
        BasicBinary fileBinary = new FileBinary(new File(filePath));
        Request<String> request = NoHttp.createStringRequest("https://qrcode.taowangzhan.com/bbapi/Submit/stationUploadExpressImg", RequestMethod.POST);
        request.add("station_id", station_id);
        request.add("file", fileBinary);
        request.add("uuid", uuid);
        request.add("express_id", express_id);
        request.add("signature", signature);
        return request;
    }

    /**
     * 上传注册信息的请求
     *
     * @return
     */
    public static Request<String> commitRegisterRequest(String phone, String password, String realname, String idcard,
                                                        String just_card, String back_card, String license, String number,
                                                        String contacts, String contact_number, String province, String city,
                                                        String area, String address, String door_photo, String internal_photo,
                                                        String latitude, String longitude, HashMap<String, String> params) {

        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW
                + InterApi.ACTION_COMMIT_REGISTER_MESSAGE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("type_id", "");
        request.add("phone", phone);        //注册电话
        request.add("number", number);        //工号
        request.add("password", password);    //密码
        request.add("realname", realname);    //真实姓名
        request.add("idcard", idcard);       //身份证号
        request.add("just_card", just_card);    //正面照片
        request.add("back_card", back_card);   //背面照片
        request.add("license", license);      //营业执照
        request.add("contacts", contacts);      //联系人
        request.add("contact_number", contact_number);      //联系人电话
        request.add("province", province);      //省
        request.add("city", city);      //市
        request.add("area", area);      //县
        request.add("address", address);      //详细地址
        request.add("door_photo", door_photo);      //门头照
        request.add("internal_photo", internal_photo); //内部照
        request.add("latitude", latitude);
        request.add("longitude", longitude);
        return request;
    }


    /**
     * 找回密码的请求
     *
     * @return
     */
    public static Request<String> changePasswordRequst(HashMap<String, String> params) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW
                + InterApi.ACTION_CHANGE_PASSWORD, RequestMethod.POST);
        String signature = StringUtil.getsignature(params);
        request.add("signature", signature);
        request.add("phone", params.get("phone"));    //加密值
        request.add("password", params.get("password"));    //加密值
        return request;
    }

    /**
     * 找回密码的请求
     *
     * @return
     */
    public static Request<String> changeNewPasswordRequst(String user_id, String oldPassword, String newPassword) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_NEW_PASSWORD, RequestMethod.GET);
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        request.add("method", InterApi.ACTION_CHANGE_NEW_PASSWORD);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("old_password", oldPassword);
        request.add("new_password", newPassword);
        return request;
    }

    /**
     * 登录请求
     *
     * @param username   用户名
     * @param password   密码
     * @param receive_id 极光别名
     * @param device     //设备类型  1： android  2:ios
     * @return
     */
    public static Request<String> loginRequest(String username, String password, String receive_id, String device, HashMap<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW
                + InterApi.ACTION_LOGIN, RequestMethod.POST);
        request.add("signature", signature);
        request.add("account", username);
        request.add("password", password);
        request.add("receive_id", receive_id);
        request.add("device", device);
        return request;
    }

    /**
     * 登录请求 验证码
     *
     * @param username   用户名
     * @param password   密码
     * @param receive_id 极光别名
     * @param device     //设备类型  1： android  2:ios
     * @return
     */
    public static Request<String> loginByCodeRequest(String username, String password, String receive_id, String device, HashMap<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW
                + InterApi.ACTION_LOGIN_BY_CODE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("account", username);
        request.add("code", password);
        request.add("receive_id", receive_id);
        request.add("device", device);
        return request;
    }

    /**
     * 充值记录
     *
     * @param type    1： 短信  2：面单
     * @param page    请求页数
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getRechargeRecodeRequst(String type, String page, String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_RECHARGE_RECODE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_RECHARGE_RECODE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("page", page);
        return request;
    }

    /**
     * 获取面单商品
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getRechargePannelRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_RECHARGE_PANNEL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_RECHARGE_PANNEL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 短信充值
     *
     * @param user_id    用户id
     * @param message_id 商品id
     * @return
     */
    public static Request<String> getRechargeMoneyRequest(String user_id, String message_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_RECHARGE_MONEY, RequestMethod.GET);
        request.add("method", InterApi.ACTION_RECHARGE_MONEY);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("message_id", message_id);
        return request;
    }

    /**
     * 面单单价
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getPannelUnitePriceRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PANNEL_UNITE_PRICE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PANNEL_UNITE_PRICE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 面单充值
     *
     * @param user_id     用户id
     * @param face_id     面单id
     * @param face_number 面单数量
     * @return
     */
    public static Request<String> getPannelRechargeRequest(String user_id, String face_id, String face_number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PANNEL_RECHARGEL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PANNEL_RECHARGEL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("face_id", face_id);
        request.add("face_number", face_number);
        return request;
    }

    /**
     * 驿站地址
     *
     * @param user_id 用户id
     * @param type    用户类型  1 ： 寄件人  2： 收件人
     * @return
     */
    public static Request<String> getStageAddressRequest(String user_id, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_STAGE_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_STAGE_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 获取我的地址
     *
     * @param user_id
     * @return
     */
    public static Request<String> getMyAddressRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取地区地址
     *
     * @param user_id
     * @return
     */
    public static Request<String> getAreaRequest(String user_id, String express_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_AREA, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_AREA);
        request.add("signature", signature);
        request.add("express_id", express_id);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 更新快递公司状态
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param type       1：寄件  2：派件
     * @return
     */
    public static Request<String> updateExpressState(String user_id, String express_id, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_UPDATE_EXPRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_UPDATE_EXPRESS);
        request.add("signature", signature);
        request.add("express_id", express_id);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 修改地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @param book_id   地址id
     * @return
     */
    public static Request<String> changeAddressBook(String user_id, String realName, String telephone,
                                                    String province, String city, String area, String address, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHNAGE_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHNAGE_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("province", province);
        request.add("city", city);
        request.add("area", area);
        request.add("address", address);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 修改收获地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @param book_id   地址id
     * @return
     */
    public static Request<String> changeMyAddressBook(String user_id, String realName, String telephone,
                                                      String province, String city, String area, String address, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHNAGE_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHNAGE_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("province", province);
        request.add("city", city);
        request.add("area", area);
        request.add("address", address);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 添加地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @return
     */
    public static Request<String> addAddressBook(String user_id, String realName, String telephone,
                                                 String province, String city, String county, String address, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ADD_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ADD_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("area", county);
        request.add("province", province);
        request.add("city", city);
        request.add("type", type);
        request.add("address", address);
        return request;
    }

    /**
     * 添加收获地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @return
     */
    public static Request<String> addMyAddressBook(String user_id, String realName, String telephone,
                                                   String province, String city, String county, String address, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ADD_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ADD_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("area", county);
        request.add("province", province);
        request.add("city", city);
        request.add("type", type);
        request.add("address", address);
        return request;
    }


    /**
     * 删除我的地址
     *
     * @param user_id 用户id
     * @param book_id 地址id
     * @return
     */
    public static Request<String> deleteMyAddressRequest(String user_id, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 删除地址簙
     *
     * @param user_id 用户id
     * @param book_id 地址id
     * @return
     */
    public static Request<String> deleteAddressRequest(String user_id, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 获取快递公司的请求
     *
     * @param user_id 用户id
     * @param type    类型 1： 快递公司  2：物流公司
     * @return
     */
    public static Request<String> getExpressageRequest(String user_id, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_EXPRESSAGE_LIST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_EXPRESSAGE_LIST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 获取物品的类型
     *
     * @param user_id 用户ID
     * @return
     */
    public static Request<String> getGoodsTypeRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GOODS_TYPE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GOODS_TYPE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 实名认证
     *
     * @param user_id   用户id
     * @param book_id   寄件人id
     * @param realname  姓名
     * @param idcard    身份证号
     * @param just_card 正面照
     * @param back_card 反面照
     * @return
     */
    public static Request<String> commitIdentification(String user_id, String book_id,
                                                       String realname, String idcard, String just_card, String back_card) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_AUTHENTICATION, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_AUTHENTICATION);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        request.add("realname", realname);
        request.add("idcard", idcard);
        request.add("just_card", just_card);
        request.add("back_card", back_card);
        return request;
    }

    /**
     * 实名认证
     *
     * @param user_id   用户id
     * @param realname  姓名
     * @param idcard    身份证号
     * @param just_card 正面照
     * @param back_card 反面照
     * @return
     */
    public static Request<String> commitIdentificationForManager(String user_id, String mail_id,
                                                                 String realname, String idcard, String just_card, String back_card) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_IDENTIFICATION_FOR_MANAGER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_IDENTIFICATION_FOR_MANAGER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("idcard", idcard);
        request.add("mail_id", mail_id);
        request.add("realname", realname);
        request.add("just_card", just_card);
        request.add("back_card", back_card);
        return request;
    }


    /**
     * 验证是否是实名
     *
     * @param user_id
     * @param book_id
     * @return
     */
    public static Request<String> isIdentifyRequest(String user_id, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_IS_IDENTIFY_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_IS_IDENTIFY_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 下单
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param send_id    寄件人id
     * @param collect_id 收件人id
     * @param type_id    物品id
     * @param weight     重量
     * @param content    备注
     * @return
     */
    public static Request<String> commitOrderRequest(String user_id, String express_id, String send_id
            , String collect_id, String type_id, String weight, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_ORDER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("send_id", send_id);
        request.add("collect_id", collect_id);
        request.add("type_id", type_id);
        request.add("weight", weight);
        request.add("content", content);
        return request;
    }

    /**
     * @param user_id
     * @param express_id
     * @param start_province
     * @param start_city
     * @param end_province
     * @param end_city
     * @param weight
     * @return
     */
    public static Request<String> getEstimateRequest(String user_id, String express_id, String start_province, String start_city,
                                                     String end_province, String end_city, String weight) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ESTIMMATE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ESTIMMATE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("start_province", start_province);
        request.add("start_city", start_city);
        request.add("end_province", end_province);
        request.add("weight", weight);
        request.add("end_city", end_city);
        return request;
    }

    /**
     * 待收件请求
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param keywords   关键字
     * @param page       页码
     * @return
     */
    public static Request<String> getWaitCollecRequest(String user_id, String express_id, String keywords, String page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_WAIT_COLLECT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_WAIT_COLLECT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("keywords", keywords);
        request.add("page", page);
        return request;
    }

    /**
     * 已处理请求
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param keywords   关键字
     * @param page       页码
     * @param starttime  开始时间
     * @return
     */
    public static Request<String> getFinishEventRequest(String user_id, String express_id, String keywords
            , String page, String starttime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_HAVE_FINISH, RequestMethod.GET);
        request.add("method", InterApi.ACTION_HAVE_FINISH);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("keywords", keywords);
        request.add("page", page);
        request.add("starttime", starttime);
        return request;
    }

    /**
     * 催单
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getHandleEventRequest(String user_id, String mail_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_HANDLE
                + InterApi.ACTION_HANDLE_FINISH, RequestMethod.POST);
        request.add("method", InterApi.ACTION_HANDLE_FINISH);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("mail_id", mail_id);
        return request;
    }

    /**
     * 待打印请求
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param keywords   关键字
     * @param page       页码
     * @param starttime  开始时间
     * @return
     */
    public static Request<String> getWaitPrintRequest(String user_id, String express_id, String keywords
            , String page, String starttime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_WAIT_PRINT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_WAIT_PRINT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("keywords", keywords);
        request.add("page", page);
        request.add("starttime", starttime);
        return request;
    }

    /**
     * 获取订单详情
     *
     * @param user_id
     * @param mail_id
     * @return
     */
    public static Request<String> getOrderDetailRequest(String user_id, String mail_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ORDER_DETAIL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ORDER_DETAIL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        return request;
    }

    /**
     * 获取订单
     *
     * @param user_id
     * @return
     */
    public static Request<String> getCauseForCannelOrderRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CANNEL_ORDER_CAUSE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CANNEL_ORDER_CAUSE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 取消订单
     *
     * @param user_id   用户id
     * @param mail_id   订单id
     * @param reason_id 取消原因id
     * @param content   备注
     * @return
     */
    public static Request<String> commitCannelOrderCauseRequest(String user_id, String mail_id, String reason_id, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_CANNEL_ORDER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_CANNEL_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("reason_id", reason_id);
        request.add("content", content);
        return request;
    }

    /**
     * 取消订单
     *
     * @param user_id   用户id
     * @param reason_id 取消原因id
     * @return
     */
    public static Request<String> confirmServiceCannelOrder(String user_id, String orders_id, String reason_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_COMMIT_SERVICE_ORDER, RequestMethod.POST);
        request.add("method", InterApi.ACTION_COMMIT_SERVICE_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("orders_id", orders_id);
        request.add("reason_id", reason_id);
        return request;
    }

    /**
     * 打印时验证身份是否实名
     *
     * @param user_id 用户id
     * @param mail_id 订单id
     * @return
     */
    public static Request<String> identifySealRequest(String user_id, String mail_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_IDETIFY_AT_SEAL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_IDETIFY_AT_SEAL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        return request;
    }

    /**
     * 先存后打
     *
     * @param user_id    用户id
     * @param mail_id    订单id
     * @param goods_name 物品名称
     * @param weight     重量
     * @param money      运费
     * @param content    备注
     * @return
     */
    public static Request<String> commitRecordMailRequest(String user_id, String mail_id,
                                                          String goods_name, String weight, String money, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_SAVE_MAIL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_SAVE_MAIL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("goods_name", goods_name);
        request.add("weight", weight);
        request.add("money", money);
        request.add("content", content);
        return request;
    }

    /**
     * 待打印 中修改物品信息
     *
     * @param user_id    用户id
     * @param mail_id    订单id
     * @param goods_name 物品名称
     * @param weight     重量
     * @param money      运费
     * @param content    备注
     * @return
     */
    public static Request<String> waitMimeographRequest(String user_id, String mail_id,
                                                        String goods_name, String weight, String money, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PRINT_ONCE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PRINT_ONCE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("goods_name", goods_name);
        request.add("weight", weight);
        request.add("money", money);
        request.add("content", content);
        return request;
    }


    /**
     * 再打一单
     *
     * @param user_id    用户id
     * @param mail_id    订单id
     * @param goods_name 物品名称
     * @param weight     重量
     * @param money      运费
     * @param content    备注
     * @return
     */
    public static Request<String> commitRecordMailDetailRequest(String user_id, String mail_id,
                                                                String goods_name, String weight, String money, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_SAVE_MAIL_DETAIL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_SAVE_MAIL_DETAIL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("goods_name", goods_name);
        request.add("weight", weight);
        request.add("money", money);
        request.add("content", content);
        return request;
    }

    /*  *//**
     * 获取社区版首页的信息
     *
     * @param user_id 用户id
     * @return
     *//*
    public static Request<String> getPannelmessageRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_PANNEL_MESSAGE_rEQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_PANNEL_MESSAGE_rEQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }*/

    /**
     * 获取社区版首页的信息
     *
     * @return
     */
    public static Request<String> getPannelmessageRequest(HashMap<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_1
                + InterApi.ACTION_GET_PANNEL_MESSAGE_rEQUEST, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        return request;
    }

    /**
     * 获取预估价格
     *
     * @param user_id        用户id
     * @param express_id     快递公司id
     * @param start_province 寄送省份
     * @param start_city     寄送城市
     * @param end_province   收件省份
     * @param end_city       收件城市
     * @param weight         重量
     * @return
     */
    public static Request<String> getPredictMoneyRequest(String user_id, String express_id, String start_province
            , String start_city, String end_province, String end_city, String weight) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_PREDICT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_PREDICT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("start_province", start_province);
        request.add("start_city", start_city);
        request.add("end_province", end_province);
        request.add("end_city", end_city);
        request.add("weight", weight);
        return request;
    }

    /**
     * 获取快递公司图标信息
     *
     * @param user_id
     * @return
     */
    public static Request<String> getExpressLogoRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取快递公司图标信息
     *
     * @return
     */
    public static Request<String> getExpressRequest(HashMap<String,String> parms) {
        String signature = StringUtil.getsignature(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5
                + InterApi.ACTION_GET_EXPRESS_REQUEST, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", parms.get("user_id"));
        return request;
    }

    /**
     * 获取快递公司图标信息
     *
     * @return
     */
    public static Request<String> getExpressList(HashMap<String,String> parms) {
        String signature = StringUtil.getsignature(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5
                + InterApi.ACTION_GET_EXPRESS_REQUEST1, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", parms.get("user_id"));
        return request;
    }


    /**
     * 下载快递公司logo
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @return
     */
    public static Request<Bitmap> uploadLogoRequest(String user_id, String express_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<Bitmap> request = NoHttp.createImageRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        return request;
    }

    /**
     * 短信管理
     *
     * @param user_id 用户id
     * @param type    类型 0：失败 1：成功
     * @param page    页数
     * @return
     */
    public static Request<String> getMessageManagerRequest(String user_id, int type, int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MESSAGE_MANAGER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MESSAGE_MANAGER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("page", page);
        return request;
    }

    /**
     * 重新发送短信
     *
     * @param user_id    用户id
     * @param type       1：单个  2：全部
     * @param message_id 短信id
     * @return
     */
    public static Request<String> sendMessageRequest(String user_id, int type, String message_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_SEND_MESSAGE_AGAIN, RequestMethod.GET);
        request.add("method", InterApi.ACTION_SEND_MESSAGE_AGAIN);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("message_id", message_id);
        return request;
    }

    /**
     * 获取投诉管理
     *
     * @param user_id 用户id
     * @param type    类型
     * @return
     */
    public static Request<String> getComplainManagerRequest(String user_id, int type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMPLAIN_MANAGER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMPLAIN_MANAGER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 搜索物流信息
     *
     * @param user_id  用户id
     * @param numberse 运单号
     * @param express  快递公司id
     * @return
     */
    public static Request<String> getSearchPackRequest(String user_id, String numberse, String express) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_SEARCH_PACKAGE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_SEARCH_PACKAGE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("numbers", numberse);
        request.add("express", express);
        return request;
    }

    /**
     * 获取用户的基本信息
     *
     * @param user_id
     * @return
     */
    public static Request<String> getUserBaseMessageRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_USER_BASEMESSAGE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_USER_BASEMESSAGE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取通知公告消息
     *
     * @param user_id
     * @param page
     * @return
     */
    public static Request<String> getNotificationRequest(String user_id, int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_NOTIFICATION_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_NOTIFICATION_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        return request;
    }

    /**
     * 获取系统消息
     *
     * @param user_id
     * @param page
     * @return
     */
    public static Request<String> getSystmeMessageRequest(String user_id, int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MESSAGE_CENTER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MESSAGE_CENTER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        return request;
    }

    /**
     * 检测是否绑定账户
     *
     * @param user_id
     * @return
     */
    public static Request<String> checkisBindAccountRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHECK_BIND_ACCOUNT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHECK_BIND_ACCOUNT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 绑定账号
     *
     * @param user_id  用户id
     * @param type     1 银行卡 2 支付宝
     * @param realname 姓名
     * @param number   卡号
     * @param bank     开户行
     * @param account  支付宝账户
     * @return
     */
    public static Request<String> getBindAccountRequest(String user_id, String type
            , String realname, String account, String bank, String number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_BIND_ALI_ACCOUNT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_BIND_ALI_ACCOUNT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("realname", realname);
        request.add("number", number);
        request.add("bank", bank);
        request.add("account", account);
        return request;
    }

    /**
     * 获取申请提现
     *
     * @param user_id
     * @param type
     * @param money
     * @return
     */
    public static Request<String> getApplyGetMoneyRequest(String user_id, String type, String money) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_APPLY_MONEY, RequestMethod.GET);
        request.add("method", InterApi.ACTION_APPLY_MONEY);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("money", money);
        return request;
    }

    /**
     * 获取消费记录
     *
     * @param user_id 用户id
     * @param page    页码
     * @param start   开始时间
     * @param endtime 结束时间
     * @return
     */
    public static Request<String> getMoneyRecordRequest(String user_id, int page, String start, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MONRY_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MONRY_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        request.add("starttime", start);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 获取消费记录
     *
     * @param user_id 用户id
     * @param page    页码
     * @return
     */
    public static Request<String> getConsumeRecordRequest(String user_id, int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CONSUME_RECORD_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CONSUME_RECORD_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        return request;
    }

    /**
     * 获取消费记录
     *
     * @param user_id 用户id
     * @param con_id  记录id
     * @return
     */
    public static Request<String> getConsumeDetailRequest(String user_id, String con_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CONSUME_DETAIL_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CONSUME_DETAIL_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("con_id", con_id);
        return request;
    }

    /**
     * 获取充值记录
     *
     * @param user_id 用户id
     * @param page    页码
     * @param start   开始时间
     * @param endtime 结束时间
     * @return
     */
    public static Request<String> getRechargeRecordRequest(String user_id, int page, String start, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_RECHARGE_RECORD_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_RECHARGE_RECORD_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        request.add("starttime", start);
        request.add("endtime", endtime);
        return request;
    }


    /**
     * 获取客户列表请求
     *
     * @param user_id
     * @return
     */
    public static Request<String> getClientListRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CLIENT_LIST_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CLIENT_LIST_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取客户管理列表
     *
     * @param user_id
     * @return
     */
    public static Request<String> getClientManagerListRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CLIENT_MANAGER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CLIENT_MANAGER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 添加客户信息
     *
     * @param user_id      用户id
     * @param realname     姓名
     * @param telephone    联系电话
     * @param region       地区
     * @param address      详细地址
     * @param company_name 公司名称
     * @param content      备注
     * @return
     */
    public static Request<String> addClientRequest(String user_id, String realname,
                                                   String telephone, String region,
                                                   String address, String company_name, String content) {

        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ADD_CLIENT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ADD_CLIENT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("realname", realname);
        request.add("telephone", telephone);
        request.add("region", region);
        request.add("address", address);
        request.add("company_name", company_name);
        request.add("content", content);
        return request;
    }

    /**
     * 编辑客户信息
     *
     * @param user_id      用户id
     * @param realname     姓名
     * @param telephone    电话号码
     * @param region       地区
     * @param address      地址
     * @param company_name 公司名称
     * @param content      备注
     * @param customer_id  地址的id
     * @return
     */
    public static Request<String> editClientMessage(String user_id, String realname,
                                                    String telephone, String region,
                                                    String address, String company_name, String content, String customer_id) {

        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_EDIT_CLIENT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_EDIT_CLIENT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("realname", realname);
        request.add("telephone", telephone);
        request.add("region", region);
        request.add("address", address);
        request.add("company_name", company_name);
        request.add("content", content);
        request.add("customer_id", customer_id);
        return request;
    }

    /**
     * 删除客户信息
     *
     * @param user_id     用户id
     * @param customer_id 地址id
     * @return
     */
    public static Request<String> deleteClientRequest(String user_id, String customer_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_CLIENT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_CLIENT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("customer_id", customer_id);
        return request;
    }

    /**
     * 获取客户订单
     *
     * @param user_id
     * @param customer_id 客户id
     * @param page        页码
     * @return
     */
    public static Request<String> getClientOrderDetailRequest(String user_id, String customer_id, int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_CLIENT_ORDER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_CLIENT_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("customer_id", customer_id);
        request.add("page", page);
        return request;
    }

    /**
     * 个人中心订单
     *
     * @param user_id
     * @param type
     * @return
     */
    public static Request<String> getMyOrderList(String user_id, int type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_MY_ORDER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_MY_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 获取订单详情
     *
     * @param user_id  用户id
     * @param order_id 订单详情
     * @return
     */
    public static Request<String> getMyOrderDetailRequest(String user_id, String order_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_MY_ORDER_DETAIL_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_MY_ORDER_DETAIL_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("order_id", order_id);
        return request;
    }

    /**
     * 获取物料商城
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getGoodsListRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GOODS_LIST_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GOODS_LIST_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取商品详情接口
     *
     * @param user_id
     * @param product_id
     * @return
     */
    public static Request<String> getGoodsListDetailRequest(String user_id, String product_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GOODS_DETAIL_LIST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GOODS_DETAIL_LIST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("product_id", product_id);
        return request;
    }

    /**
     * 加入购物车
     *
     * @param user_id    用户id
     * @param product_id 商品id
     * @param genre_id   型号id
     * @return
     */
    public static Request<String> joinGoodsRequest(String user_id, String product_id, String genre_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_JOIN_GOODS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_JOIN_GOODS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("product_id", product_id);
        request.add("genre_id", genre_id);
        return request;
    }

    /**
     * 立即购买
     *
     * @param user_id    用户id
     * @param product_id 产品id
     * @param genre_id   型号id
     * @param address_id 地址id
     * @param number     数量
     * @return
     */
    public static Request<String> payForMoneyRightNowRequest(String user_id, String product_id,
                                                             String genre_id, String address_id, int number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PAYFOR_ATONCE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PAYFOR_ATONCE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("product_id", product_id);
        request.add("genre_id", genre_id);
        request.add("address_id", address_id);
        request.add("number", number);
        return request;
    }

    /**
     * 批量购买
     *
     * @param user_id    用户id
     * @param cart_id    购物车记录id
     * @param address_id 地址id
     * @param content    备注
     * @return
     */
    public static Request<String> payForMoreGoodsRequest(String user_id, String cart_id,
                                                         String address_id, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PAYFOR_MORE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PAYFOR_MORE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("cart_id", cart_id);
        request.add("address_id", address_id);
        request.add("content", content);
        return request;
    }

    /**
     * 获取购物车列表
     *
     * @param user_id
     * @return
     */
    public static Request<String> getShopCarGoodsRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_SHOP_CAR_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_SHOP_CAR_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 删除购物车数据
     *
     * @param user_id 用户id
     * @param cart_id 购物车记录id
     * @return
     */
    public static Request<String> deleteGoodsRequest(String user_id, String cart_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_GOODS_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_GOODS_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("cart_id", cart_id);
        return request;
    }

    /**
     * 修改商品的数量
     *
     * @param user_id 用户id
     * @param cart_id 购物车id
     * @param number  数量
     * @return
     */
    public static Request<String> changeGoodsNumberRequest(String user_id, String cart_id, int number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_GOODS_NUMBER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHANGE_GOODS_NUMBER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("cart_id", cart_id);
        request.add("number", number);
        return request;
    }

    /**
     * 获取交接管理
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param type       1: 表示待交接  2：已交接
     * @param starttime  开始时间
     * @param endtime    结束时间
     * @return
     */
    public static Request<String> getChangeManagerRequest(String user_id, String express_id, int type, String starttime, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_MANAGER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHANGE_MANAGER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("type", type);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }


    /**
     * 获取待入库数据
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @return
     */
    public static Request<String> getEnterStoreRequest(String user_id, String express_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_EXPRESS_WAIT_STORE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_EXPRESS_WAIT_STORE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("express", express_id);
        return request;
    }

    /**
     * 确认入库
     *
     * @param user_id    用户id
     * @param package_id 入库的数据
     * @return
     */
    public static Request<String> confirmEnterStoreRequest(String user_id, String package_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_CONFIRM_ENTER_STORE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CONFIRM_ENTER_STORE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("package_id", package_id);
        return request;
    }


    /**
     * 去除业务员入错的包裹
     *
     * @param user_id    用户id
     * @param package_id 入库的数据
     * @return
     */
    public static Request<String> deleteEnterRecorde(String user_id, String package_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_DELETE_ENTER_STORE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_ENTER_STORE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("package_id", package_id);
        return request;
    }


    /**
     * 确认交接
     *
     * @param user_id    用户id
     * @param handto     签名文件
     * @param mailing_id 寄件id
     * @return
     */
    public static Request<String> sendChangeRequest(String user_id, String handto, String mailing_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_SNED_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHANGE_SNED_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mailing_id", mailing_id);
        request.add("handto", handto);
        return request;
    }

    /**
     * 数据中心
     *
     * @param user_id   用户id
     * @param starttime 开始时间时间戳
     * @param endtime   结束时间时间戳
     * @return
     */
    public static Request<String> getDataCenterRequest(String user_id, String starttime, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DATA_CENTER_rEQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DATA_CENTER_rEQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 财务管理
     *
     * @param user_id   用户id
     * @param starttime 开始时间
     * @param endtime   结束时间
     * @return
     */
    public static Request<String> getMoneyManagerRequest(String user_id, String starttime, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_MONEY_MANAGER_REQEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_MONEY_MANAGER_REQEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 取消订单
     *
     * @param user_id
     * @param mail_id
     * @return
     */
    public static Request<String> cannelOrderRequest(String user_id, String mail_id, String reason_id, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CANNEL_ORDER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CANNEL_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("reason_id", reason_id);
        request.add("content", content);
        return request;
    }

    //微信支付请求接口
    public static Request<String> getWeiChartPayforRequest(String user_id, String money) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest("http://www.81dja.com/Payment/WeChatPay", RequestMethod.GET);
        request.add("method", InterApi.ACTION_CANNEL_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("money", money);
        return request;
    }

    //支付宝支付请求接口
    public static Request<String> getAliPayforRequest(String user_id, String money) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest("http://www.81dja.com/Payment/AliPay", RequestMethod.GET);
        request.add("method", InterApi.ACTION_CANNEL_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("money", money);
        return request;
    }

    //微信支付请求接口
    public static Request<String> getAliaPayforRequest(String user_id, String money) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest("http://www.81dja.com/Payment/WeChatPay", RequestMethod.GET);
        request.add("method", InterApi.ACTION_CANNEL_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("money", money);
        return request;
    }

    /**
     * 首页全局搜索
     *
     * @param user_id  用户id
     * @param keywords 关键字
     * @return
     */
    public static Request<String> getGlobalSendRequest(String user_id, String keywords) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GLOBALE_SEND_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GLOBALE_SEND_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("keywords", keywords);
        return request;
    }

    /**
     * 确认完成订单
     *
     * @param signature
     * @param params
     * @return
     */
    public static Request<String> completeTakeOrders(String signature, Map<String, String> params) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_COMPLETE_ORDERS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("orders_id", params.get("orders_id"));
        return request;
    }


    /**
     * 首页全局搜索
     * @return
     */
    public static Request<String> getGloableReceiveRequest(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_SEARCH_GLOBAL_PI, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("value", params.get("key"));
        request.add("type", params.get("type"));
        request.add("page", params.get("page"));
        return request;
    }


    /**
     * 财务首页
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getMoneyManagerRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_MONEY_MANAGER_REQUESTR, RequestMethod.GET);
        request.add("method", InterApi.ACTION_MONEY_MANAGER_REQUESTR);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取昨天支出
     *
     * @param user_id   用户id
     * @param starttime 开始时间戳
     * @param endtime   结束时间戳
     * @return
     */
    public static Request<String> getYesterDayPayforRequest(String user_id, String starttime, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_YESTERDAY_SEND_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_YESTERDAY_SEND_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 获取昨天寄件的数据
     *
     * @param user_id   用户id
     * @param starttime 开始时间戳
     * @param endtime   结束时间戳
     * @return
     */
    public static Request<String> getYesterDaySendforRequest(String user_id, String starttime, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_YESTERDAY_SEND__REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_YESTERDAY_SEND__REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }


    /**
     * 获取昨天派件的数据
     *
     * @param user_id   用户id
     * @param starttime 开始时间戳
     * @param endtime   结束时间戳
     * @return
     */
    public static Request<String> getYesterDayPaiforRequest(String user_id, String starttime, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_YESTERDAY_PAI__REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_YESTERDAY_PAI__REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 数据排行榜
     *
     * @param user_id   用户id
     * @param starttime 开始时间
     * @param endtime   结束时间
     * @param types     类型  1：全部  2：寄件  3：派件  4：服务
     * @return
     */
    public static Request<String> getSortRequest(String user_id, String starttime, String endtime, String types) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_SORT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_SORT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        request.add("types", types);
        return request;
    }

    /**
     * 获取日报数据
     *
     * @param user_id   用户id
     * @param starttime 选择的时间
     * @return
     */
    public static Request<String> getReportBydateRequest(String user_id, String starttime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_REPORT_DATE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_REPORT_DATE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        return request;
    }

    /**
     * 获取月报数据
     *
     * @param user_id   用户id
     * @param starttime 选择的时间
     * @return
     */
    public static Request<String> getReportByMonthRequest(String user_id, String starttime, String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACITON_REPORT_MONTH_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACITON_REPORT_MONTH_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 添加备注
     *
     * @param user_id    用户id
     * @param mailing_id 订单id
     * @param content    备注
     * @return
     */
    public static Request<String> addMarkRequest(String user_id, String mailing_id, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ADD_MARK_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ADD_MARK_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mailing_id", mailing_id);
        request.add("content", content);
        return request;
    }

    /**
     * 检测快递的运单号
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param number     运单号
     * @return
     */
    public static Request<String> checkWaybillRequest(String user_id, String express_id, String number, String picturl) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_CHECK_WAY_BILL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHECK_WAY_BILL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("picurl", picturl);
        request.add("number", number);
        return request;
    }

    /**
     * 全部入库
     *
     * @param user_id    用户id
     * @param express_id 快递公司
     * @param str_data   数据
     * @return
     */
    public static Request<String> enterRecordeRequest(String user_id, String express_id, String str_data) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_ENTER_RECORDE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ENTER_RECORDE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("str_data", str_data);
        return request;
    }

    /**
     * 提货码
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getNewPackagerCodeFromHourse(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_GET_NEW_PACKAGE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_NEW_PACKAGE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }


    /**
     * 全部出库
     *
     * @param user_id 用户id
     * @param out_id  出库id
     * @return
     */
    public static Request<String> outOfRepertoryRequest(String user_id, String out_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_OUT_OF_REPERTORY_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_OUT_OF_REPERTORY_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("out_id", out_id);
        return request;
    }

    /**
     * 获取入库的列表
     *
     * @param user_id    用户id
     * @param starttime  开始时间
     * @param endtime    结束时间
     * @param express_id 快递公司id
     * @param page       页数
     * @return
     */
    public static Request<String> getEnterRepertoryRequest(String user_id, String starttime, String endtime, String express_id, String page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_ENTER_REPERTORY_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ENTER_REPERTORY_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        request.add("page", page);
        return request;
    }

    /**
     * 获取出库的列表
     *
     * @param user_id    用户id
     * @param starttime  开始时间
     * @param endtime    结束时间
     * @param express_id 快递公司id
     * @param page       页数
     * @return
     */
    public static Request<String> getOutRepertoryRequest(String user_id, String starttime, String endtime, String express_id, String page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_OUT_REPERTORY_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_OUT_REPERTORY_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        request.add("page", page);
        return request;
    }

    /**
     * 获取寄件详情
     *
     * @param user_id 用户id
     * @param pie_id
     * @return
     */
    public static Request<String> getExpressDetailRequest(String user_id, String pie_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_EXPRESS_DETAIL_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_EXPRESS_DETAIL_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("pie_id", pie_id);
        return request;
    }

    /**
     * 干洗确认送达
     *
     * @param user_id   用户id
     * @param orders_id
     * @return
     */
    public static Request<String> confirmClearSend(String user_id, String orders_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_CONFIRM_CLEAR_ORDER_REQUEST, RequestMethod.POST);
        request.add("method", InterApi.ACTION_CONFIRM_CLEAR_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("orders_id", orders_id);
        return request;
    }


    /**
     * 上传扫描图片
     *
     * @param filePath 图片的路径
     */
    public static Request<String> commitScanPictureRequest(@NonNull String filePath) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        BasicBinary fileBinary = new FileBinary(new File(filePath));
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER
                + InterApi.ACTION_COMMIT_SCAN_PICTURE, RequestMethod.POST);
        request.add("method", InterApi.ACTION_COMMIT_SCAN_PICTURE);
        request.add("file", fileBinary);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        return request;
    }

    /**
     * 智能解析
     */
    public static Request<String> decodeMessage(@NonNull String address) {
        Request<String> request = NoHttp.createStringRequest("https://hdgateway.zto.com/Word_AnalysisAddress", RequestMethod.POST);
        request.add("address", address);
        return request;
    }

    /**
     * 获取订单
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getExpressDetailRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_GET_ORDER, RequestMethod.POST);
        request.add("method", InterApi.ACTION_GET_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        return request;
    }


    /**
     * 桶装水送达
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> confirmWaterSend(String user_id, String order_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_WATER_SEND, RequestMethod.POST);
        request.add("method", InterApi.ACTION_WATER_SEND);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("orders_id", order_id);
        return request;
    }

    /**
     * 桶装水接单
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> confirmWaterReive(String user_id, String order_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_WATER_RECEIVE, RequestMethod.POST);
        request.add("method", InterApi.ACTION_WATER_RECEIVE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("orders_id", order_id);
        return request;
    }

    /**
     * 获取取消订单原因
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getCannelResult(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_GET_SERTVICE_ORDER, RequestMethod.POST);
        request.add("method", InterApi.ACTION_GET_SERTVICE_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        return request;
    }

    /**
     * 干洗接单
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> receiveClearOrder(String user_id, String order_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_CANNEL_CLEAR_ORDER, RequestMethod.POST);
        request.add("method", InterApi.ACTION_CANNEL_CLEAR_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("orders_id", order_id);
        return request;
    }


    /**
     * 干洗类目
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> requestClearType(String user_id, String order_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_REQUEST_CLEAR_TYPE, RequestMethod.POST);
        request.add("method", InterApi.ACTION_REQUEST_CLEAR_TYPE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("orders_id", order_id);
        return request;
    }

    /**
     * 干洗报价
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> commitPrice(String user_id, String order_id, String commodity_id, String number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ORDER
                + InterApi.ACTION_REQUEST_CLEAR_PRICE, RequestMethod.POST);
        request.add("method", InterApi.ACTION_REQUEST_CLEAR_PRICE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("distributor_id", user_id);
        request.add("orders_id", order_id);
        request.add("commodity_id", commodity_id);
        request.add("number", number);
        return request;
    }

    /**
     * 请求首页的banner
     *
     * @param signature
     * @param distributor_id
     * @return
     */
    public static Request<String> getBanner(String signature, String distributor_id) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_BANNER, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", distributor_id);
        return request;
    }


    /**
     * 选择配送方式
     *
     * @param signature
     * @param params
     * @return
     */
    public static Request<String> selectDistributionMode(String signature, Map<String, String> params) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_SELECT_DIS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("orders_id", params.get("orders_id"));
        request.add("types", params.get("types"));
        request.add("money", params.get("money"));
        return request;
    }

    /**
     * 更改配送方式
     *
     * @param signature
     * @param params
     * @return
     */
    public static Request<String> changeDistributionMode(String signature, Map<String, String> params) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_CHANGEDIS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("orders_id", params.get("orders_id"));
        request.add("types", params.get("types"));
        request.add("money", "0");
        return request;
    }

    /**
     * 取消订单
     *
     * @param signature
     * @param params
     * @return
     */
    public static Request<String> cannelTakeOrders(String signature, Map<String, String> params) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_CANNEL_ORDERS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("orders_id", params.get("orders_id"));
        request.add("reason_id", params.get("reason_id"));
        return request;
    }

    /**
     * 确认接单
     *
     * @param signature
     * @param params
     * @return
     */
    public static Request<String> receiveTakeOrders(String signature, Map<String, String> params) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_RECEIVE_ORDERS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("orders_id", params.get("orders_id"));
        return request;
    }

    /**
     * @param signature
     * @param distributor_id
     * @return
     */
    public static Request<String> getTakeOrders(String signature, String distributor_id, String mType) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_TAKE_ORDER, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", distributor_id);
        request.add("type", mType);
        return request;
    }

    /**
     * 请求门店的货架
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> requstStoreShelves(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_STORE_SHELVES, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 请求商户运费
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> requestWayMoney(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_WAY_MONEY, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", user_id);
        return request;
    }

    /**
     * 请求商户运费
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> commitWayMoneySetting(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_COMMIT_SETTING, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", user_id);
        request.add("term_id", params.get("term_id"));
        request.add("freight_id", params.get("freight_id"));
        request.add("starting_id", params.get("starting_id"));
        request.add("start_time", params.get("start_time"));
        request.add("end_time", params.get("end_time"));
        return request;
    }

    /**
     * 请求门店的商品
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> requstStoreShelvesByType(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("shelves_id", params.get("shelves_id"));
        return request;
    }

    /**
     * 请求门店的商品
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> deleteShalves(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_DELETE_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("shelves_id", params.get("shelves_id"));
        return request;
    }


    /**
     * 修改商品名称和价格
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> changeGoodsNameAndPrice(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_CHAGNE_GOODS_PRICE_NAME, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("goods_id", params.get("goods_id"));
        request.add("name", params.get("name"));
        request.add("price", params.get("price"));
        return request;
    }

    /**
     * 修改货架名称
     *
     * @return
     */
    public static Request<String> changeShelvesName(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_CHAGNE_SHELVES_NAME, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("shelves_id", params.get("shelves_id"));
        request.add("shelves_name", params.get("shelves_name"));
        return request;
    }


    /**
     * 删除商品
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> deleteGoods(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_DELETE_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("goods_id", params.get("goods_id"));
        return request;
    }

    /**
     * 下架
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> toggleGoods(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_TOGGLE_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("goods_id", params.get("goods_id"));
        return request;
    }

    /**
     * 请求实例图片
     *
     * @param user_id              用户id
     * @param warehouse_shelves_id 仓库货架id
     * @return
     */
    public static Request<String> requestDemoImage(String user_id, String warehouse_shelves_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_IMAGE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("shelves_name", warehouse_shelves_id);
        return request;
    }

    /**
     * 上传商品条形码
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> commitGoodsCode(String user_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_COMMIT_GOODS_CODE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("goods_code", params.get("goods_code"));
        return request;
    }


    /**
     * 请求实例名称
     *
     * @param user_id  用户id
     * @param goods_id 仓库货架id
     * @return
     */
    public static Request<String> requestGoodsName(String user_id, String goods_id, Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_NAME, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", user_id);
        request.add("goods_id", goods_id);
        return request;
    }

    /**
     * 添加商品
     *
     * @return
     */
    public static Request<String> commitGoodsRequest(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_ADD_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("shelves_id", params.get("shelves_id"));
        request.add("name", params.get("name"));
        request.add("img", params.get("img"));
        request.add("shelves_name", params.get("shelves_name"));
        request.add("price", params.get("price"));
        request.add("code_id", params.get("code_id"));
        request.add("class_name", params.get("class_name"));
        request.add("class_id", params.get("class_id"));
        request.add("lib_goods_id", params.get("lib_goods_id"));
        return request;
    }

    /**
     * 生成优惠券
     *
     * @return
     */
    public static Request<String> createCouponRequest(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_CREATE_COUPON, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("term_money", params.get("term_money"));
        request.add("reduction_money", params.get("reduction_money"));
        request.add("types", params.get("types"));
        request.add("starttime", params.get("starttime"));
        request.add("endtime", params.get("endtime"));
        return request;
    }


    public static Request<String> getStationUser(String paramString, int paramInt) {
        String str1 = DateUtil.getCurrentTimeStamp();
        String str2 = StringUtil.getRandomNumberString(7);
        String str3 = StringUtil.splitStringFromLast(str1, 4);
        String str4 = StringUtil.getSignatureString(str1, str2, str3);
        Request localRequest = NoHttp.createStringRequest("http://www.81dja.com/ServiceOrders/getStationUser", RequestMethod.POST);
        localRequest.add("method", "getStationUser");
        localRequest.add("signature", str4);
        localRequest.add("timeStamp", str1);
        localRequest.add("randomStr", str2);
        localRequest.add("Encryption", str3);
        localRequest.add("distributor_id", paramString);
        localRequest.add("page", paramInt);
        return localRequest;
    }

    public static Request<String> getMyServerOrders(String paramString, int paramInt) {
        String str1 = DateUtil.getCurrentTimeStamp();
        String str2 = StringUtil.getRandomNumberString(7);
        String str3 = StringUtil.splitStringFromLast(str1, 4);
        String str4 = StringUtil.getSignatureString(str1, str2, str3);
        Request localRequest = NoHttp.createStringRequest("http://www.81dja.com/ServiceOrders/getMyServerOrders", RequestMethod.POST);
        localRequest.add("method", "getMyServerOrders");
        localRequest.add("signature", str4);
        localRequest.add("timeStamp", str1);
        localRequest.add("randomStr", str2);
        localRequest.add("Encryption", str3);
        localRequest.add("distributor_id", paramString);
        localRequest.add("page", paramInt);
        return localRequest;
    }

    /**
     * 查询优惠券
     *
     * @param params
     * @return
     */
    public static Request<String> checkCouponData(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_SEARCH_COUPON, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        return request;
    }

    /**
     * 搜索货架
     *
     * @param params
     * @return
     */
    public static Request<String> searchShelves(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_SEARCH_SHELVES, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("shelves_name", params.get("shelves_name"));
        request.add("page", params.get("page"));
        return request;
    }

    /**
     * 搜索货架和商品
     *
     * @param params
     * @return
     */
    public static Request<String> searchGetPreset(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP_1
                + InterApi.ACTION_REQUEST_SEARCH_SHELVES_AND_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("page", params.get("page"));
        request.add("name", params.get("name"));
        return request;
    }

    /**
     * 添加货架
     *
     * @param params
     * @return
     */
    public static Request<String> addShelves(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_ADD_SHELVES, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("shelves_id", params.get("shelves_id"));
        return request;
    }


    /**
     * 添加货架和商品
     *
     * @param params
     * @return
     */
    public static Request<String> addShelvesAndGoods(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP_1
                + InterApi.ACTION_REQUEST_ADD_SHELVES_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("preset_id", params.get("preset_id"));
        return request;
    }


    /**
     * 获取关注人数
     *
     * @param params
     * @return
     */
    public static Request<String> getFollowStation(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_FLOW_STATION, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("coupon_id", params.get("coupon_id"));
        request.add("page", params.get("page"));
        return request;
    }

    /**
     * 获取我的客户
     *
     * @param params
     * @return
     */
    public static Request<String> getMyClient(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_MY_CLIENT, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("page", params.get("page"));
        return request;
    }

    /**
     * 获取商品库数据
     *
     * @param params
     * @return
     */
    public static Request<String> getGoodsStore(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_GOODS_STORE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("type_id", params.get("type_id"));
        request.add("distributor_id", params.get("distributor_id"));
        request.add("page", params.get("page"));
        return request;
    }


    /**
     * 添加商品
     *
     * @param params
     * @return
     */
    public static Request<String> addGoodsRequest(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_ADD_GOODS_BY_STORE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("class_id", params.get("class_id"));
        request.add("title", params.get("title"));
        request.add("thumb", params.get("thumb"));
        request.add("price", params.get("price"));
        request.add("house_id", params.get("house_id"));
        request.add("distributor_id", params.get("distributor_id"));
        return request;
    }

    /**
     * 搜索商品
     *
     * @param params
     * @return
     */
    public static Request<String> searchGoodsRequest(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_SEARCH_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("search", params.get("search"));
        request.add("page", params.get("page"));
        return request;
    }


    /**
     * 添加商品
     *
     * @param params
     * @return
     */
    public static Request<String> getGoodsPriceRequest(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP
                + InterApi.ACTION_REQUEST_GET_GOODS_PRICE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("goods_id", params.get("goods_id"));
        return request;
    }

    /**
     * 发放优惠券
     *
     * @param params
     * @return
     */
    public static Request<String> dispathCoupon(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_DISPATH_COUPON, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("coupon_id", params.get("coupon_id"));
        request.add("member_data", params.get("member_data"));
        return request;
    }

    /**
     * 发放优惠券
     *
     * @param params
     * @return
     */
    public static Request<String> requestServiceDetail(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_ORDER_DETAIL, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("orders_id", params.get("orders_id"));
        return request;
    }


    /**
     * 发放优惠券
     *
     * @param params
     * @return
     */
    public static Request<String> outException(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_9
                + InterApi.ACTION_REQUEST_OUT_EXCEPTION, RequestMethod.POST);
        request.add("signature", signature);
        request.add("express_id", params.get("express_id"));
        return request;
    }

    /**
     * 发放优惠券
     *
     * @param params
     * @return
     */
    public static Request<String> outHourse(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_9
                + InterApi.ACTION_REQUEST_OUT_HOURSE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("unusual_type", params.get("unusual_type"));
        request.add("station_id", params.get("station_id"));
        request.add("number", params.get("number"));
        return request;
    }



    /**
     * 获取快递员配送的金额
     *
     * @return
     */
    public static Request<String> getSendByExpressMoney(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_GETSEND_BY_WXPRESS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("orders_id", params.get("orders_id"));
        return request;
    }

    /**
     * 发放优惠券
     *
     * @param params
     * @return
     */
    public static Request<String> getCouponUseRequest(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_COUPON_RECORD, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("type", params.get("type"));
        request.add("coupon_id", params.get("coupon_id"));
        return request;
    }

    /**
     * 设置特价商品
     *
     * @param params
     * @return
     */
    public static Request<String> saveSpecialGoods(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_URL_3
                + InterApi.ACTION_REQUEST_ADD_SPECIAL_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("distributor_id", params.get("distributor_id"));
        request.add("class_id", params.get("class_id"));
        request.add("start_time", params.get("start_time"));
        request.add("end_time", params.get("end_time"));
        request.add("stock", params.get("stock"));
        request.add("price", params.get("price"));
        request.add("product_id", params.get("product_id"));
        return request;
    }


    /**
     * 扫描添加
     *
     * @param params
     * @return
     */
    public static Request<String> getScanGoodsMessage(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_SHOP_1
                + InterApi.ACTION_REQUEST_SCANL_GOODS, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("goods_code", params.get("goods_code"));
        return request;
    }

    /**
     * 我的信息
     *
     * @param params
     * @return
     */
    public static Request<String> getMyMessage(Map<String, String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_3
                + InterApi.ACTION_REQUEST_MY_MESSAGE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        return request;
    }

    /**
     * 派件 对快递公司 收费管理
     *
     * @param user_id
     * @return
     */
    public static Request<String> getExpressMoney(String user_id) {
        String str1 = DateUtil.getCurrentTimeStamp();
        String str2 = StringUtil.getRandomNumberString(7);
        String str3 = StringUtil.splitStringFromLast(str1, 4);
        String str4 = StringUtil.getSignatureString(str1, str2, str3);
        Request localRequest = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER + InterApi.ACTION_REQUEST_EXPRESS_MONEY, RequestMethod.GET);
        localRequest.add("method", "getExpressMoney");
        localRequest.add("signature", str4);
        localRequest.add("timeStamp", str1);
        localRequest.add("randomStr", str2);
        localRequest.add("Encryption", str3);
        localRequest.add("distributor_id", user_id);
        return localRequest;
    }

    /**
     * 更改 驿站对快递员的收费
     *
     * @param user_id
     * @return
     */
    public static Request<String> changeExpressMoney(ExpressMoney expressMoney, String user_id) {
        String str1 = DateUtil.getCurrentTimeStamp();
        String str2 = StringUtil.getRandomNumberString(7);
        String str3 = StringUtil.splitStringFromLast(str1, 4);
        String str4 = StringUtil.getSignatureString(str1, str2, str3);
        Request localRequest = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS_ENTER + InterApi.ACTION_REQUEST_SET_EXPRESS_MONEY, RequestMethod.GET);
        localRequest.add("method", "saveExpressMoney");
        localRequest.add("signature", str4);
        localRequest.add("timeStamp", str1);
        localRequest.add("randomStr", str2);
        localRequest.add("Encryption", str3);
        localRequest.add("distributor_id", user_id);
        localRequest.add("money_id", expressMoney.getMoney_id());
        localRequest.add("express_id", expressMoney.getExpress_id());
        localRequest.add("money", expressMoney.getPrice());
        return localRequest;
    }

    /**
     *入库
     * @return
     */
    public static Request<String> managerRestory(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_REQUEST_ENTER, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("check_data", params.get("check_data"));
        return request;
    }

    /**
     *入库
     * @return
     */
    public static Request<String> managerRestory2(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_REQUEST_ENTER2, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("check_data", params.get("check_data"));
        return request;
    }

    /**
     *出库
     * @return
     */
    public static Request<String> outRestory(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_REQUEST_OUT_RESPORT, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("pie_data", params.get("pie_data"));
        return request;
    }

    /**
     *出库
     * @return
     */
    public static Request<String> outRestoryByQCode(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_REQUEST_OUT_RESPORT_BY_QCODE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", params.get("station_id"));
        request.add("code", params.get("code"));
        return request;
    }

    /**
     *出库
     * @return
     */
    public static Request<String> isObservice(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_7+InterApi.ACTION_REQUEST_IS_OBSERVICE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", params.get("station_id"));
        request.add("pie_id", params.get("pie_id"));
        return request;
    }

    /**
     *添加修改临时数据
     * @return
     */
    public static Request<String> commitSubgleRestory(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_REQUEST_ENTER_SINGLE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("mobile", params.get("mobile"));
        request.add("user_id", params.get("user_id"));
        request.add("code", params.get("code"));
        request.add("number", params.get("number"));
        request.add("express_id", params.get("express_id"));
        request.add("check_id", params.get("check_id"));
        request.add("uuid", params.get("uuid"));
        return request;
    }


    /**
     *添加修改临时数据
     * @return
     */
    public static Request<String> commitSubgleRestory2(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_4+InterApi.ACTION_REQUEST_ENTER_SINGLE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("mobile", params.get("mobile"));
        request.add("user_id", params.get("user_id"));
        request.add("code", params.get("code"));
        request.add("number", params.get("number"));
        request.add("express_id", params.get("express_id"));
        request.add("check_id", params.get("check_id"));
        request.add("uuid", params.get("uuid"));
        return request;
    }
    /**
     * 修改取件码
     * @return
     */
    public static Request<String> changeTakeCode(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_REQUEST_CODE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("check_id", params.get("check_id"));
        request.add("uuid", params.get("uuid"));
        request.add("shelves_code", params.get("shelves_code"));
        return request;
    }

    /**
     * 删除
     * @return
     */
    public static Request<String> deleteRestory(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_REQUEST_DELETE_RESPORT, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("check_id", params.get("check_id"));
        request.add("uuid", params.get("uuid"));
        return request;
    }


    /**
     * 删除
     * @return
     */
    public static Request<String> deleteRestory2(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_4+InterApi.ACTION_REQUEST_DELETE_RESPORT, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("check_id", params.get("check_id"));
        return request;
    }

    /**
     * 取件码
     * @return
     */
    public static Request<String> getExpressCode(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_4+InterApi.ACTION_REQUEST_GET_EXPRESS_CODE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        return request;
    }


    /**
     * 取件码
     * @return
     */
    public static Request<String> checkEnterNumber(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_CHECK_ENTER_CODE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("number", params.get("number"));
        request.add("user_id", params.get("user_id"));
        request.add("check_id", params.get("check_id"));
        return request;
    }

    /**
     * 取件码
     * @return
     */
    public static Request<String> checkEnterNumber2(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_4+InterApi.ACTION_CHECK_ENTER_CODE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("number", params.get("number"));
        request.add("user_id", params.get("user_id"));
        return request;
    }

    /**
     * 历史数据
     * @return
     */
    public static Request<String> getHistoryData(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_4+InterApi.ACTION_GET_HISTORY_DATA, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        return request;
    }

    /**
     * 历史数据
     * @return
     */
    public static Request<String> getHistoryLastData(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_6+InterApi.ACTION_GET_HISTORY_LAST_DATA, RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", params.get("station_id"));
        return request;
    }

    /**
     * 检测出库订单的状态
     *
     * @return
     */
    public static Request<String> checkOutWailnumberStateRequest(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_5+InterApi.ACTION_CHECK_PHONE, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("number", params.get("number"));
        return request;
    }

    /**
     * 检测出库订单的状态
     *
     * @return
     */
    public static Request<String> getOrderRecorde(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_3+InterApi.ACTION_GET_ORDER_RECORDER, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("page", params.get("page"));
        return request;
    }

    /**
     * 上传手机号码
     *
     * @return
     */
    public static Request<String> commitPhoneNumber(HashMap<String,String> params) {
        String signature = StringUtil.getsignature(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVICE_NEW_1+InterApi.ACTION_COMMIT_RECORDER, RequestMethod.POST);
        request.add("signature", signature);
        request.add("user_id", params.get("user_id"));
        request.add("local_phone", params.get("local_phone"));
        request.add("call_phone", params.get("call_phone"));
        return request;
    }

    /**
     * 修改信息
     *
     * @return
     */
    public static Request<String> commitDataRequest(HashMap<String,String> parms) {
        String signature = StringUtil.getsignature(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER+"station/Stationexpress/savePieinfo", RequestMethod.POST);
        request.add("signature", signature);
        request.add("pie_id", parms.get("pie_id"));
        request.add("mobile", parms.get("mobile"));
        request.add("waybill_number", parms.get("waybill_number"));
        request.add("pickup_code", parms.get("pickup_code"));
        request.add("station_id", parms.get("station_id"));
        return request;
    }

    /**
     * 删除数据
     *
     * @return
     */
    public static Request<String> delFailureEnterData(HashMap<String,String> parms) {
        String signature = StringUtil.getsignature(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER+"station/Stationexpress/delPieinfo", RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", parms.get("station_id"));
        request.add("pie_id", parms.get("pie_id"));
        return request;
    }

    /**
     * 获取失败入库记录
     *
     * @return
     */
    public static Request<String> getFailureEnterData(HashMap<String,String> parms) {
        String signature = StringUtil.getsignature(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER+"station/Stationexpress/pieFailRecord", RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", parms.get("station_id"));
        request.add("page", parms.get("page"));
        return request;
    }

    /**
     * 获取入库情况
     *
     * @return
     */
    public static Request<String> getEnterStateData(HashMap<String,String> parms) {
        String signature = StringUtil.getsignature(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER+"station/Stationexpress/indexData", RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", parms.get("station_id"));
        return request;
    }

    /**
     * 确认入库
     */
    public static Request<String> comfirmEnterRequest(String signature, Map<String, String> params) {
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER + "/bbapi/Submit/stationUploadExpressImg2", RequestMethod.POST);
        request.add("signature", signature);
        request.add("uuid", params.get("uuid"));
        request.add("number", params.get("number"));
        request.add("pie_id", params.get("pie_id"));
        request.add("courier_id", params.get("courier_id"));
        request.add("express_name", params.get("express_name"));
        request.add("mobile", params.get("mobile"));
        request.add("code", params.get("code"));
        return request;
    }


    /**
     * 上传修改图片请求
     */
    public static Request<String> commitExpressPictureRequest(Map<String,String> params,String file) {
        String signature = StringUtil.getsignature2(params);
        BasicBinary fileBinary = new FileBinary(new File(file));
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER + "bbapi/Submit/stationUploadExpressImg2", RequestMethod.POST);
        request.add("signature", signature);
        request.add("file", fileBinary);
        request.add("uuid", params.get("uuid"));
        request.add("station_id", params.get("station_id"));
        return request;
    }

    /**
     * 获取历史记录
     *
     * @return
     */
    public static Request<String> getCurrentData(HashMap<String, String> parms) {
        String signature = StringUtil.getsignature2(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER + "bbapi/Submit/getStationLastPie", RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", parms.get("station_id"));
        request.add("uuid", parms.get("uuid"));
        return request;
    }

    /**
     * 入库记录
     *
     * @return
     */
    public static Request<String> getEnterDateDetail(HashMap<String, String> parms) {
        String signature = StringUtil.getsignature2(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER  + "bbapi/Submit/getStationDataList", RequestMethod.POST);
        request.add("signature", signature);
        request.add("station_id", parms.get("station_id"));
        request.add("type", parms.get("type"));
        request.add("uuid", parms.get("uuid"));
        return request;
    }

    /**
     * 修改失败
     */
    public static Request<String> commitChange(Map<String, String> params) {
        String signature = StringUtil.getsignature2(params);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER + "/bbapi/Submit/stationUpdatePie", RequestMethod.POST);
        request.add("signature", signature);
        request.add("uuid", params.get("uuid"));
        request.add("station_id", params.get("station_id"));
        request.add("pie_id", params.get("pie_id"));
        request.add("express_id", params.get("express_id"));
        request.add("code", params.get("code"));
        request.add("mobile", params.get("mobile"));
        request.add("number", params.get("number"));
        return request;
    }

    /**
     * 快递详情
     *
     * @return
     */
    public static Request<String> expressDetail(HashMap<String, String> parms) {
        String signature = StringUtil.getsignature2(parms);
        Request<String> request = NoHttp.createStringRequest(InterApi.BASE_URL_ENTER  + "bbapi/Submit/showPie", RequestMethod.POST);
        request.add("signature", signature);
        request.add("pie_id", parms.get("pie_id"));
        request.add("uuid", parms.get("uuid"));
        return request;
    }


}
