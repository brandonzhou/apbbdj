package com.shshcom.station.storage.domain;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.mt.bbdj.baseconfig.db.ScanImage;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.shshcom.module_base.utils.Utils;
import com.shshcom.station.storage.http.bean.OSSConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * oss 上传图片
 *
 * @author: zhhli
 * @date: 2020/11/17
 */
public class OSSWrapper {

    private static final OSSWrapper WRAPPER = new OSSWrapper();


    OSSClient mClient;


    private OSSConfig config;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);

    private OSSWrapper() {

        String bucketName = "bbsh-com";
        String path = "picture/ruku";

        String STS_INFO_URL = "https://express.shshcom.com/qrcode/bbapi/submit2/querySts";
        String OSS_ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";

        config = new OSSConfig(STS_INFO_URL, bucketName, OSS_ENDPOINT, path);

        OSSAuthCredentialsProvider authCredentialsProvider = new OSSAuthCredentialsProvider(config.getAuthServerUrl());
        mClient = new OSSClient(Utils.getContext(), config.getEndpoint(), authCredentialsProvider);

        OSSLog.enableLog();
    }

    public static OSSWrapper sharedWrapper() {
        return WRAPPER;
    }

    public OSSWrapper setConfig(OSSConfig config) {
        this.config = config;

        OSSAuthCredentialsProvider authCredentialsProvider = new OSSAuthCredentialsProvider(config.getAuthServerUrl());
        mClient = new OSSClient(Utils.getContext(), config.getEndpoint(), authCredentialsProvider);


        return this;
    }

    public String asyncPutImage(ScanImage scanImage, String localFile) {
        final long upload_start = System.currentTimeMillis();
        OSSLog.logDebug("upload start");


        File file = new File(localFile);
        if (!file.exists()) {
            LogUtil.w("AsyncPutImage", "FileNotExist");
            LogUtil.w("LocalFile", localFile);
            return "";
        }

        String object = createRemoteAbsPath(scanImage, file);

        // 构造上传请求
        OSSLog.logDebug("create PutObjectRequest ");
        PutObjectRequest put = new PutObjectRequest(config.getBucketName(), object, localFile);
        put.setCRC64(OSSRequest.CRC64Config.YES);


        OSSLog.logDebug(" asyncPutObject ");
        try {
            PutObjectResult putResult = mClient.putObject(put);

            LogUtil.d("PutObject", "UploadSuccess");

            String remoteUrl = createRemoteUrl(object);

            LogUtil.d("onSuccess", remoteUrl);

            long upload_end = System.currentTimeMillis();
            LogUtil.d("onSuccess", "upload cost: " + (upload_end - upload_start) / 1000f);

            return remoteUrl;
        } catch (Exception e) {
            // 本地异常，如网络异常等。
            e.printStackTrace();
            String info = e.getMessage();
            ToastUtil.showLong(scanImage.getEId() + ": " + info);
        }

        return "";


//        OSSAsyncTask taskAsy = mClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                LogUtil.d("PutObject", "UploadSuccess");
//
//
//
//                long upload_end = System.currentTimeMillis();
//                OSSLog.logDebug("upload cost: " + (upload_end - upload_start) / 1000f);
//                //mDisplayer.uploadComplete();
//                LogUtil.d("onSuccess", "Bucket: " + BUCKET_NAME
//                        + "\nObject: " + request.getObjectKey()
//                        + "\nETag: " + result.getETag()
//                        + "\nRequestId: " + result.getRequestId()
//                        + "\nCallback: " + result.getServerCallbackReturnBody());
//                LogUtil.d("onSuccess", createRemoteUrl(object));
//
//            }
//
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                String info = "";
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                    info = clientExcepion.toString();
//                }
//                if (serviceException != null) {
//                    // 服务异常
//                    LogUtil.e("ErrorCode", serviceException.getErrorCode());
//                    LogUtil.e("RequestId", serviceException.getRequestId());
//                    LogUtil.e("HostId", serviceException.getHostId());
//                    LogUtil.e("RawMessage", serviceException.getRawMessage());
//                    info = serviceException.toString();
//                }
//                ToastUtil.showShort(scanImage.getEId()+": " + info);
//            }
//        });
    }


    /**
     * 拼接图片地址：https://bbsh-com.oss-cn-beijing.aliyuncs.com/picture/ruku/20201116/d2b2f8537b2503aed0cb1742315c1aeb757e515e.jpeg
     *
     * @param absPath
     * @return
     */
    private String createRemoteUrl(String absPath) {

        StringBuilder buffer = new StringBuilder(config.getEndpoint());
        int index = buffer.indexOf("://");
        buffer.insert(index + 3, config.getBucketName() + ".");
        buffer.append("/");
        buffer.append(absPath);
        return buffer.toString();
    }

    /**
     * 拼接服务器存储路径：picture/ruku/20201116/d2b2f8537b2503aed0cb1742315c1aeb757e515e.jpeg
     *
     * @param scanImage
     * @param file
     * @return
     */
    private String createRemoteAbsPath(ScanImage scanImage, File file) {
        String day = sdf.format(new Date());
        return config.getPath() + "/" + day
                + "/" + scanImage.getStationId()
                + "/" + file.getName();
    }


}
