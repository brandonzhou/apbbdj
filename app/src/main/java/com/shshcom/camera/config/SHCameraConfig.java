package com.shshcom.camera.config;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;

import com.king.mlkit.vision.camera.config.CameraConfig;

public class SHCameraConfig extends CameraConfig {

    @NonNull
    public ImageCapture options(@NonNull ImageCapture.Builder builder){
        return builder.build();
    }
}
