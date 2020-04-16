package com.mt.bbdj.baseconfig.utls;

import android.annotation.SuppressLint;
import android.app.Application;
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
    private int take_zhongtong;
    private int take_yunda;
    private int take_yuantong;
    private int take_baishi;
    private int take_shentong;
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
        idNotify = soundPool.load(context, R.raw.beep, 1);
        idChange = soundPool.load(context, R.raw.ic_change, 1);
        idEnter = soundPool.load(context, R.raw.ic_enter, 1);
        take_photo = soundPool.load(context, R.raw.take_picture, 1);
        out_promite = soundPool.load(context, R.raw.out_promite, 1);

        take_again = soundPool.load(context, R.raw.ic_take_again, 1);
        take_code_again = soundPool.load(context, R.raw.ic_code, 1);
        take_linear_again = soundPool.load(context, R.raw.ic_linnera, 1);

        take_zhongtong = soundPool.load(context, R.raw.play_zhongtong, 1);
        take_yunda = soundPool.load(context, R.raw.play_yunda,1);
        take_yuantong = soundPool.load(context, R.raw.play_yuantong, 1);
        take_baishi = soundPool.load(context, R.raw.play_baishi, 1);
        take_shentong = soundPool.load(context, R.raw.ic_shentong, 1);
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

    public void playZhongtong(){
        soundPool.play(take_zhongtong,1,1,10,0,1);
    }

    public void playYunda(){
        soundPool.play(take_yunda,1,1,10,0,1);
    }

    public void playYuantong(){
        soundPool.play(take_yuantong,1,1,10,0,1);
    }
    public void playShentong(){
        soundPool.play(take_shentong,1,1,10,0,1);
    }

    public void playBaishi(){
        soundPool.play(take_baishi,1,1,10,0,1);
    }


    public void release() {
        soundPool.unload(idReceive);
        soundPool.release();
    }

}
