package com.mt.bbdj.baseconfig.view.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rxfeature.module.scaner.CameraConfigurationManager;

/**
 * @Author : ZSK
 * @Date : 2019/9/28
 * @Description :
 */
public final class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = com.rxfeature.module.scaner.PreviewCallback.class.getSimpleName();

    private final CameraHelper cameraHelper;
    private final boolean useOneShotPreviewCallback;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(CameraHelper cameraHelper, boolean useOneShotPreviewCallback) {
        this.cameraHelper = cameraHelper;
        this.useOneShotPreviewCallback = useOneShotPreviewCallback;
    }

    public void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }


    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Size cameraResolution = cameraHelper.getPreviewViewSize();
        if (!useOneShotPreviewCallback) {
            camera.setPreviewCallback(null);
        }
        if (previewHandler != null) {
            Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.width,
                    cameraResolution.height, data);
            message.sendToTarget();
            previewHandler = null;
        } else {
            Log.d(TAG, "Got preview callback, but no handler for it");
        }
    }

}
