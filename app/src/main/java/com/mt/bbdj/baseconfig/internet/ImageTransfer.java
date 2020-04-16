package com.mt.bbdj.baseconfig.internet;

import android.util.Log;

import com.mt.bbdj.baseconfig.db.ExpressImage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressImageDao;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LoadDialogUtils;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * @Author : ZSK
 * @Date : 2019/12/3
 * @Description :
 */
public class ImageTransfer {

    private static RequestQueue requestQueue = NoHttp.newRequestQueue();
    private static ResponseListener responseListener = new ResponseListener();
    private static int REQUEST_UPLOAD_PICTURE = 1001;   //上传图片
    private static String picturePath;


    public static void uploadImage(){

        //有图片正在上传中、无法上传下一个图片
        if (Constant.syncImageAccomplish ==false){
            return;
        }

        //查询一下是否有未同步的数据
        DaoSession daoSession =  GreenDaoManager.getInstance().getSession();
        ExpressImageDao expressImageDao = daoSession.getExpressImageDao();
        List<ExpressImage> expressImageList = expressImageDao.queryBuilder().where(ExpressImageDao.Properties.IsSync.notEq(1)).list();

        //全部同步完成 结束Service
        if (expressImageList.size() == 0) {
            Constant.isBegin = false;
            return;
        }

        ExpressImage expressImage= expressImageList.get(0);   //找出第一张  判断是否有效
        if (!isImageEffect(expressImage)){
            return;
        }
        //确定图片有效、开始单个上传
        Constant.syncImageAccomplish = false;
        Request<String> request = NoHttpRequest.commitPannelPictureRequest(expressImage.getImagePath(),
                expressImage.getUser_id(), expressImage.getUuid(),expressImage.getExpress_id());
        requestQueue.add(REQUEST_UPLOAD_PICTURE,request,responseListener);
    }

    private static boolean isImageEffect(ExpressImage expressImage) {
        //文件不存在或者损坏
        String imagePaht = expressImage.getImagePath();
        if (imagePaht == null || "".equals(imagePaht)) {
            return false;
        }
        if (!new File(imagePaht).exists()){
            return false;
        }
        return true;
    }

    private static class ResponseListener<String> implements OnResponseListener<String> {

        @Override
        public void onStart(int what) {
            Log.e("图片", "开始");
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == REQUEST_UPLOAD_PICTURE){
                handleTransferImage(response);
            }
        }

        private void handleTransferImage(Response<String> response) {
            LogUtil.i("图片===", "ImageTransfer::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get().toString().trim());
                java.lang.String code = jsonObject.get("code").toString();
                java.lang.String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    updateLocalState(jsonObject);
                } else {
                    Constant.syncImageAccomplish = true;   //上传失败重新上传
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Constant.syncImageAccomplish = true;   //上传失败重新上传
            }
            LoadDialogUtils.cannelLoadingDialog();
        }

        private void updateLocalState(JSONObject jsonObject) throws JSONException {
            JSONObject data = jsonObject.getJSONObject("data");
            java.lang.String uuid = data.getString("uuid");
            DaoSession daoSession =  GreenDaoManager.getInstance().getSession();
            ExpressImageDao expressImageDao = daoSession.getExpressImageDao();
            List<ExpressImage> localData = expressImageDao.queryBuilder().where(ExpressImageDao.Properties.Uuid.eq(uuid)).list();
            if (localData.size() != 0){
                ExpressImage expressImage = localData.get(0);
                expressImage.setIsSync(1);
                expressImageDao.update(expressImage);
                java.lang.String picturePath = expressImage.getImagePath();
                File file = new File(picturePath);
                if (file.exists()){
                    file.delete();
                }
            }
            Constant.syncImageAccomplish = true;   //上传完成
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            Log.e("图片", "失败"+response.get());
            Constant.syncImageAccomplish = true;
        }

        @Override
        public void onFinish(int what) {

        }
    }
}
