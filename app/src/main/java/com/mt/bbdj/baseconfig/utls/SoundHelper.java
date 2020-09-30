package com.mt.bbdj.baseconfig.utls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;


/**
 * Author : ZSK
 * Date : 2019/1/26
 * Description :  播放音频
 */
public class SoundHelper {

    private final int take_photo;
    private SoundPool soundPool;
    private int idReceive;
    private int idRepeat;
    private int idSuccess;
    private int idNotify;
    private int idChange;
    private int idEnter;
    private int out_promite;
    private int take_again;
    private int take_code_again;
    private int take_linear_again;
    private int scan_in_success;
    private int scan_in_fail;
    private int express_baishi;
    private int express_debang;
    private int express_ems;
    private int express_jd;
    private int express_jitu;
    private int express_shentong;
    private int express_shunfeng;
    private int express_tiantian;
    private int express_yousu;
    private int express_youzheng;
    private int express_yuantong ;
    private int express_yunda    ;
    private int express_zhongtong;
    private static SoundHelper helper;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public SoundHelper() {
        Context context = MyApplication.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder spb = new SoundPool.Builder();
            spb.setMaxStreams(1);
            // spb.setAudioAttributes(null); // 转换音频格式
            soundPool = spb.build(); // 创建SoundPool对象
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        }
        //idReceive = soundPool.load(context, R.raw.mp_promit, 1);
        idReceive = soundPool.load(context, R.raw.new_promet, 1);
        idRepeat = soundPool.load(context, R.raw.reapeat, 1);
        idSuccess = soundPool.load(context, R.raw.success, 1);
        idNotify = soundPool.load(context, R.raw.zxl_beep, 1);
        idChange = soundPool.load(context, R.raw.ic_change, 1);
        idEnter = soundPool.load(context, R.raw.ic_enter, 1);
        take_photo = soundPool.load(context, R.raw.take_picture, 1);
        out_promite = soundPool.load(context, R.raw.out_promite, 1);

        take_again = soundPool.load(context, R.raw.ic_take_again, 1);
        take_code_again = soundPool.load(context, R.raw.ic_code, 1);
        take_linear_again = soundPool.load(context, R.raw.ic_linnera, 1);

        scan_in_success = soundPool.load(context, R.raw.scan_in_success, 1);
        scan_in_fail = soundPool.load(context, R.raw.scan_in_fail, 1);


        express_baishi = soundPool.load(context, R.raw.express_baishi, 1);
        express_debang = soundPool.load(context, R.raw.express_debang, 1);
        express_ems = soundPool.load(context, R.raw.express_ems, 1);
        express_jd = soundPool.load(context, R.raw.express_jd, 1);
        express_jitu = soundPool.load(context, R.raw.express_jitu, 1);
        express_shentong = soundPool.load(context, R.raw.express_shentong, 1);
        express_shunfeng  = soundPool.load(context, R.raw.express_shunfeng  , 1);
        express_tiantian  = soundPool.load(context, R.raw.express_tiantian  , 1);
        express_yousu     = soundPool.load(context, R.raw.express_yousu     , 1);
        express_youzheng  = soundPool.load(context, R.raw.express_youzheng  , 1);
        express_yuantong  = soundPool.load(context, R.raw.express_yuantong  , 1);
        express_yunda     = soundPool.load(context, R.raw.express_yunda     , 1);
        express_zhongtong = soundPool.load(context, R.raw.express_zhongtong , 1);
    }

    public static void  init() {
        if (helper == null) {
            helper = new SoundHelper();
        }
    }

    public static SoundHelper getInstance() {
        return helper;
    }

    //SoundPool.play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
    //soundID：Load()返回的声音ID号
    //leftVolume：左声道音量设置
    //rightVolume：右声道音量设置
    //priority：指定播放声音的优先级，数值越高，优先级越大。
    //loop：指定是否循环：-1表示无限循环，0表示不循环，其他值表示要重复播放的次数
    //rate：指定播放速率：1.0的播放率可以使声音按照其原始频率，而2.0的播放速率，可以使声音按照其
    //原始频率的两倍播放。如果为0.5的播放率，则播放速率是原始频率的一半。播放速率的取值范围是0.5至2.0。

    public void playNotificationSound() {

        soundPool.play(idReceive,1,1,10,0,1);
    }

    //入库重复提示
    public void playNotifiRepeatSound() {
        soundPool.play(idRepeat,1,1,10,0,1);
    }

    //入库成功提示音
    public void playNotifiSuccessSound() {
        soundPool.play(idSuccess,1,1,10,0,1);
    }


    //入库成功提示音
    public void playNotifiSound() {
        soundPool.play(idNotify,1,1,10,0,1);
    }

    //无订单
    public void playChangeSound() {
        soundPool.play(idChange,1,1,10,0,1);
    }

    //已入库
    public void playEnterSound() {
        soundPool.play(idEnter,1,1,10,0,1);
    }

    //拍照成功
    public void takePhoto(){
        soundPool.play(take_photo,1,1,10,0,1);
    }

    public void outPromite(){soundPool.play(out_promite,1,1,10,0,1);}

    //识别识别取件码拍照
    public void playTakeCodeAgain(){
        soundPool.play(take_code_again,1,1,10,0,1);
    }

    //条形码
    public void playTakeLinearAgain(){
        soundPool.play(take_linear_again,1,1,10,0,1);
    }




    public void release() {
        soundPool.unload(idReceive);
        soundPool.release();
    }


    /*
    "express_id": 100101,"express_name": "中通快递"
    "express_id": 100102,"express_name": "圆通快递"
    "express_id": 100103,"express_name": "申通快递"
    "express_id": 100104,"express_name": "韵达快递"
    "express_id": 100105,"express_name": "顺丰快递"
    "express_id": 100106,"express_name": "德邦快递"
    "express_id": 100107,"express_name": "百世快递"
    "express_id": 100108,"express_name": "EMS"
    "express_id": 100109,"express_name": "宅急送"
    "express_id": 100110,"express_name": "优速快递"
    "express_id": 100111,"express_name": "快捷快递"
    "express_id": 100112,"express_name": "安能快递"
    "express_id": 100113,"express_name": "天天快递"
    "express_id": 100114,"express_name": "京东快递"
    "express_id": 100115,"express_name": "天猫超市"
    "express_id": 100116,"express_name": "其他快递"
    "express_id": 100117,"express_name": "中国邮政"
    "express_id": 100118,"express_name": "苏宁物流"
     */
    public void playExpress(int express_id){
        switch (express_id){
            case 100101	: // "中通快递"
                soundPool.play(express_zhongtong,1,1,10,0,1);
                break;
            case 100102	: // "圆通快递"
                soundPool.play(express_yuantong,1,1,10,0,1);
                break;
            case 100103	: // "申通快递"
                soundPool.play(express_shentong,1,1,10,0,1);
                break;
            case 100104	: // "韵达快递"
                soundPool.play(express_yunda,1,1,10,0,1);
                break;
            case 100105	: // "顺丰快递"
                soundPool.play(express_shunfeng,1,1,10,0,1);
                break;
            case 100106	: // "德邦快递"
                soundPool.play(express_debang,1,1,10,0,1);
                break;
            case 100107	: // "百世快递"
                soundPool.play(express_baishi,1,1,10,0,1);
                break;
            case 100108	: // "EMS"
                soundPool.play(express_ems,1,1,10,0,1);
                break;
            case 100110	: // "优速快递"
                soundPool.play(express_yousu,1,1,10,0,1);
                break;
            case 100113	: // "天天快递"
                soundPool.play(express_tiantian,1,1,10,0,1);
                break;
            case 100114	: // "京东快递"
                soundPool.play(express_jd,1,1,10,0,1);
                break;
            case 100117	: // "中国邮政"
                soundPool.play(express_youzheng,1,1,10,0,1);
                break;
            case 100109	: // "宅急送"
            case 100111	: // "快捷快递"
            case 100112	: // "安能快递"
            case 100115: // "天猫超市"
            case 100116: // "其他快递"
            case 100118: // "苏宁物流"
            default:
                playNotifiSuccessSound();
                break;

        }
    }

    public void scanInSuccess(Context context) {

        soundPool.play(scan_in_success, 1, 1, 10, 0, 1);
    }

    public void scanInFail(Context context) {
        soundPool.play(scan_in_fail, 1, 1, 10, 0, 1);
    }

}
