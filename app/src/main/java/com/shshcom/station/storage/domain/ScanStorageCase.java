package com.shshcom.station.storage.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mt.bbdj.baseconfig.db.PickupCode;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.db.UserConfig;
import com.mt.bbdj.baseconfig.db.core.DbUserUtil;
import com.mt.bbdj.baseconfig.db.core.GreenDaoUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.RxFileTool;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.shshcom.station.base.ICaseBack;
import com.shshcom.station.imageblurdetection.ImageDetectionUseCase;
import com.shshcom.station.imageblurdetection.OpenCVData;
import com.shshcom.station.storage.http.ApiStorageRequest;
import com.shshcom.station.storage.http.bean.BaseResult;
import com.shshcom.station.storage.http.bean.ExpressCompany;
import com.shshcom.station.storage.http.bean.OcrResult;
import com.shshcom.station.storage.http.bean.StationOrcResult;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
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

    public String getBatchNo() {
        UserConfig userConfig = DbUserUtil.getUserConfig();
        return userConfig.getBatchNo();
    }

    /**
     * 更新批次号
     */
    public void updateBatchNo(){
        UserConfig userConfig = DbUserUtil.getUserConfig();
        String batchNo = System.currentTimeMillis()+"";
        userConfig.setBatchNo(batchNo);
        DbUserUtil.saveUserConfig(userConfig);
    }

    public PickupCode getCurrentPickCode(){
        return GreenDaoUtil.getPickCodeLast();

    }

    public void updatePickCode(PickupCode pickupCode){
        GreenDaoUtil.updatePickCode(pickupCode);
    }


    public ScanImage getLastScanImage(){
        return GreenDaoUtil.getLastScanImage();
    }



    public List<ScanImage> getScanImageList(ScanImage.State state){
        return GreenDaoUtil.listScanImage(state, getBatchNo());
    }

    public boolean isAllImageUploaded(){
        int size = getScanImageList(ScanImage.State.upload_fail).size()
                + getScanImageList(ScanImage.State.uploading).size();

        return size==0;
    }

    public int getCurrentImageSize(){
        return GreenDaoUtil.listScanImage(getBatchNo()).size();
    }

    public ScanImage searchScanImageFromDb(String eId){
        return GreenDaoUtil.findScanImage(eId);
    }


    public Observable<OpenCVData> getBitmap(byte[] imageData){
        return Observable.just(imageData)
                .map(new Function<byte[], Bitmap>() {
                    @Override
                    public Bitmap apply(byte[] bytes) throws Exception {
                        SHCameraHelp shCameraHelp = new SHCameraHelp();

                        return shCameraHelp.getImageBitmap(bytes);
                    }
                }).map(new Function<Bitmap, OpenCVData>() {
                    @Override
                    public OpenCVData apply(Bitmap bitmap) throws Exception {

                        double score = ImageDetectionUseCase.INSTANCE.getSharpnessScoreFromOpenCV(bitmap);
                        return new OpenCVData(bitmap, score);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void saveScanImage(String eId, PickupCode pickCode, OpenCVData cvData, String mobile, String expressCompanyId, ICaseBack<String> iCaseBack) {
        String stationId = GreenDaoUtil.getStationId();

        ScanImage image = new ScanImage();
        image.setStationId(stationId);
        image.setEId(eId);
        image.setExpressCompanyId(expressCompanyId);
        image.setPhone(mobile);
        image.setBatchNo(getBatchNo());
        image.setBlurScore(cvData.getScore());

        // 根据规则，生成真正的取件码
        String strPickCode = pickCode.createRealPickCode(eId);
        image.setPickCode(strPickCode);
        GreenDaoUtil.updateScanImage(image);

        Disposable disposable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                SHCameraHelp shCameraHelp = new SHCameraHelp();
                String file = shCameraHelp.saveImage(context, image.getEId(), cvData.getBitmap());

                image.setLocalPath(file);
                image.setState(ScanImage.State.uploading.name());
                GreenDaoUtil.updateScanImage(image);

                BaseResult baseResult;
                if(TextUtils.isEmpty(image.getPhone())){
                     baseResult = uploadImage(image);
                }else {
                    // 手动输入快递信息
                    baseResult = stationInputUploadExpress(image);
                }
                if(baseResult == null){
                    image.setState(ScanImage.State.upload_fail.name());
                    GreenDaoUtil.updateScanImage(image);
                    emitter.onError(new Throwable("网络错误"));
                    return;
                }

                image.setState(ScanImage.State.upload_success.name());
                RxFileTool.deleteFile(file);

                if (baseResult.isSuccess()) {
                    GreenDaoUtil.updateScanImage(image);
                    emitter.onNext("success");
                } else {
                    GreenDaoUtil.deleteScanImage(image.getEId());
                    emitter.onError(new Throwable(baseResult.getMsg()));
                }


            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                }, e -> {
                    if (iCaseBack != null) {
                        iCaseBack.onError(e.getMessage());
                    }
                });
    }

    public void retryUploadImage(List<ScanImage> list){
        for (ScanImage image : list) {
            image.setState(ScanImage.State.uploading.name());

        }
        GreenDaoUtil.updateScanImageList(list);


        for (ScanImage image : list) {
            Disposable disposable = Observable.just(image)
                    .flatMap(new Function<ScanImage, ObservableSource<String>>() {
                        @Override
                        public ObservableSource<String> apply(ScanImage image) throws Exception {
                            BaseResult baseResult;
                            if(TextUtils.isEmpty(image.getPhone())){
                                baseResult = uploadImage(image);
                            }else {
                                // 手动输入快递信息
                                baseResult = stationInputUploadExpress(image);
                            }
                            if(baseResult == null){
                                image.setState(ScanImage.State.upload_fail.name());
                                GreenDaoUtil.updateScanImage(image);
                                throw new Exception("网络错误");
                            }

                            image.setState(ScanImage.State.upload_success.name());
                            RxFileTool.deleteFile(image.getLocalPath());
                            GreenDaoUtil.updateScanImage(image);

                            if(baseResult.isSuccess()){
                                return Observable.just("success");
                            }else {
                                throw new Exception(baseResult.getMsg());
                            }

                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {}, throwable -> ToastUtil.showShort(throwable.getMessage()));
        }


    }

    private BaseResult uploadImage(ScanImage image) {
        Request<String> request = ApiStorageRequest.stationUploadExpressImg3(image);
        //Request<String> request = ApiStorageRequest.stationOcrResult(stationId);
        request.setConnectTimeout(3 * 1000);
        request.setReadTimeout(3 * 1000);
        Response<String> response = NoHttp.startRequestSync(request);

        if (response.isSucceed()) {
            String data = response.get();
            LogUtil.d("nohttp_", data);
            BaseResult result = JSONObject.parseObject(data, BaseResult.class);
            // 5002 运单已存在等
            return result;
        }


        return null;

    }

    private BaseResult stationInputUploadExpress(ScanImage image) {
        Request<String> request = ApiStorageRequest.stationInputUploadExpress(image);
        request.setConnectTimeout(30 * 1000);
        request.setReadTimeout(30 * 1000);
        Response<String> response = NoHttp.startRequestSync(request);

        if (response.isSucceed()) {
            String data = response.get();
            LogUtil.d("nohttp_", data);
            BaseResult result = JSONObject.parseObject(data, BaseResult.class);
            return result;
        }
        return null;
    }


    public StationOrcResult getOrcResult() {
        return orcResult;
    }

    public void setOrcResult(StationOrcResult orcResult) {
        this.orcResult = orcResult;
    }

    public Observable<BaseResult<StationOrcResult>> httpOcrResult(){
        return Observable.create(new ObservableOnSubscribe<BaseResult<StationOrcResult>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<StationOrcResult>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.stationOcrResult(stationId, getBatchNo());

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
                    //BaseResult<ArrayList<ExpressCompany>> result = JSON.parseObject(data, new TypeReference<BaseResult<ArrayList<ExpressCompany>>>(){});
                    Gson gson = new Gson();
                    BaseResult result = gson.fromJson(data , new TypeToken<BaseResult<List<ExpressCompany>>>(){}.getType());

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
    public Observable<BaseResult<Object>> httpStationUpdatePie(OcrResult curOcrResult){
        return Observable.create(new ObservableOnSubscribe<BaseResult<Object>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<Object>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.stationUpdatePie(curOcrResult.getNumber(),curOcrResult.getCode(),""+curOcrResult.getExpress_id(),
                        curOcrResult.getMobile(),""+curOcrResult.getPie_id(),stationId);
                Response<String> response = NoHttp.startRequestSync(request);

                if(response.isSucceed()){
                    String data = response.get();
                    LogUtil.d("nohttp_", data);
                    BaseResult<Object> result = JSON.parseObject(data, new TypeReference<BaseResult<Object>>(){});
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
    public Observable<BaseResult<Object>> httpStationSyncDelete(String pie_id){
        return Observable.create(new ObservableOnSubscribe<BaseResult<Object>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<Object>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.stationSyncDelete(stationId,pie_id);
                Response<String> response = NoHttp.startRequestSync(request);

                if(response.isSucceed()){
                    String data = response.get();
                    LogUtil.d("nohttp_", data);
                    BaseResult<Object> result = JSON.parseObject(data, new TypeReference<BaseResult<Object>>(){});
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


    public Observable<BaseResult<ExpressCompany>> httpQueryExpress(String barCode){

        return Observable.create(new ObservableOnSubscribe<BaseResult<ExpressCompany>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResult<ExpressCompany>> emitter) throws Exception {
                String stationId = GreenDaoUtil.getStationId();
                Request<String> request = ApiStorageRequest.queryExpress(stationId,barCode);
                Response<String> response = NoHttp.startRequestSync(request);

                if(response.isSucceed()){
                    String data = response.get();
                    LogUtil.d("nohttp_", data);
                    Gson gson = new Gson();
                    //BaseResult<ExpressCompany> result = JSON.parseObject(data, new TypeReference<BaseResult<ExpressCompany>>(){});
                    BaseResult<ExpressCompany> result = gson.fromJson(data , new TypeToken<BaseResult<ExpressCompany>>(){}.getType());
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
