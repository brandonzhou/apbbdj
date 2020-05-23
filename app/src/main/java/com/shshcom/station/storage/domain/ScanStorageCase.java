package com.shshcom.station.storage.domain;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.shshcom.station.storage.http.ApiStorageRequest;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.ExpressCompany;
import com.shshcom.station.storage.http.bean.OcrResult;
import com.shshcom.station.storage.http.bean.StationOrcResult;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * desc:
 * author: zhhli
 * 2020/5/19
 */
public class ScanStorageCase {

    private Context context;

    // Ocr 识别结果
    private StationOrcResult orcResult;

    private ScanStorageCase(){

    }

    private static class Hold{
        static ScanStorageCase instance = new ScanStorageCase();
    }


    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ScanStorageCase getInstance(){
        return Hold.instance;
    }


    public PickupCode getCurrentPickCode(){
        return GreenDaoUtil.getPickCode();

    }

    public void updatePickCode(PickupCode pickupCode){
        GreenDaoUtil.updatePickCode(pickupCode);
    }


    public ScanImage getLastScanImage(){
        return GreenDaoUtil.getLastScanImage();
    }

    public int getScanImageSize(){
        return GreenDaoUtil.listScanImage().size();
    }

    public ScanImage searchScanImageFromDb(String eId){
        return GreenDaoUtil.findScanImage(eId);
    }

    public void saveScanImage(String eId, PickupCode pickCode, byte[] imageData){
        ScanImage image = new ScanImage();
        image.setEId(eId);

        String strPickCode = pickCode.createRealPickCode(eId);
        image.setPickCode(strPickCode);

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                SHCameraHelp shCameraHelp = new SHCameraHelp();
                String file = shCameraHelp.saveImage(context, image.getEId(),imageData);

                image.setLocalPath(file);
                image.setState(ScanImage.State.uploading.name());
                GreenDaoUtil.updateScanImage(image);

                boolean success = uploadImage(image);
                if(success){
                    image.setState(ScanImage.State.upload_success.name());
                }else {
                    image.setState(ScanImage.State.upload_fail.name());
                }
                GreenDaoUtil.updateScanImage(image);

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private boolean uploadImage(ScanImage image){
        String stationId = GreenDaoUtil.getStationId();

        Request<String> request = ApiStorageRequest.stationUploadExpressImg3(image.getEId(),image.getPickCode(),stationId, image.getLocalPath());
        //Request<String> request = ApiStorageRequest.stationOcrResult(stationId);
        Response<String> response = NoHttp.startRequestSync(request);

        if(response.isSucceed()){
            String data = response.get();
            LogUtil.d("nohttp_", data);
            BaseResult result = JSONObject.parseObject(data, BaseResult.class);
            return result.isSuccess();
        }


        return false;

    }





    public StationOrcResult getOrcResult() {
        return orcResult;
    }

    public ScanStorageCase setOrcResult(StationOrcResult orcResult) {
        this.orcResult = orcResult;
        return this;
    }

    public Observable<BaseResult<StationOrcResult>> httpOcrResult(){
        return Observable.create(new ObservableOnSubscribe<BaseResult<StationOrcResult>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<StationOrcResult>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.stationOcrResult(stationId);

                Response<String> response = NoHttp.startRequestSync(request);
                if(response.isSucceed()){
                    String data = response.get();
                    LogUtil.d("nohttp_", data);
                    //BaseResult<StationOrcResult> result = JSON.parseObject(data, new TypeReference<BaseResult<StationOrcResult>>(){});
                    Gson gson = new Gson();
                    BaseResult result = gson.fromJson(data , new TypeToken<BaseResult<StationOrcResult>>(){}.getType());
                    if(result.isSuccess()){
                        //orcResult = result.getData();
                        emitter.onNext(result);
                    }else {
                        emitter.onError(new Throwable(result.getMsg()));
                    }
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    /**
     * 获取快递公司列表
     * @return
     */
    public Observable<BaseResult<ArrayList<ExpressCompany>>> httpGetExpressCompany(){
        return Observable.create(new ObservableOnSubscribe<BaseResult<ArrayList<ExpressCompany>>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<ArrayList<ExpressCompany>>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.getExpressCompany(stationId);
                Response<String> response = NoHttp.startRequestSync(request);

                if(response.isSucceed()){
                    String data = response.get();
                    LogUtil.d("nohttp_", data);
                    BaseResult<ArrayList<ExpressCompany>> result = JSON.parseObject(data, new TypeReference<BaseResult<ArrayList<ExpressCompany>>>(){});
                    if(result.isSuccess()){
                        emitter.onNext(result);
                    }else {
                        emitter.onError(new Throwable(result.getMsg()));
                    }
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 编辑快递面单信息
     * @param curOcrResult
     * @return
     */
    public Observable<BaseResult<String>> httpStationUpdatePie(OcrResult curOcrResult){
        return Observable.create(new ObservableOnSubscribe<BaseResult<String>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<String>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.stationUpdatePie(curOcrResult.getNumber(),curOcrResult.getCode(),""+curOcrResult.getExpress_id(),
                        curOcrResult.getMobile(),""+curOcrResult.getPie_id(),stationId);
                Response<String> response = NoHttp.startRequestSync(request);

                if(response.isSucceed()){
                    String data = response.get();
                    LogUtil.d("nohttp_", data);
                    BaseResult<String> result = JSON.parseObject(data, new TypeReference<BaseResult<String>>(){});
                    if(result.isSuccess()){
                        emitter.onNext(result);
                    }else {
                        emitter.onError(new Throwable(result.getMsg()));
                    }
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
    /**
     * 删除快递信息
     * @param pie_id
     * @return
     */
    public Observable<BaseResult<String>> httpStationSyncDelete(String pie_id){
        return Observable.create(new ObservableOnSubscribe<BaseResult<String>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<String>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.stationSyncDelete(stationId,pie_id);
                Response<String> response = NoHttp.startRequestSync(request);

                if(response.isSucceed()){
                    String data = response.get();
                    LogUtil.d("nohttp_", data);
                    BaseResult<String> result = JSON.parseObject(data, new TypeReference<BaseResult<String>>(){});
                    if(result.isSuccess()){
                        emitter.onNext(result);
                    }else {
                        emitter.onError(new Throwable(result.getMsg()));
                    }
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

}
