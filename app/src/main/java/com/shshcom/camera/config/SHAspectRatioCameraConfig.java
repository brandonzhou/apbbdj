package com.shshcom.camera.config;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;

import com.king.mlkit.vision.camera.util.LogUtils;

import org.jetbrains.annotations.NotNull;

public class SHAspectRatioCameraConfig extends SHCameraConfig{
    private int mAspectRatio;

    public SHAspectRatioCameraConfig(Context context) {
        super();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        mAspectRatio = aspectRatio(width, height);
        LogUtils.d("aspectRatio:" + mAspectRatio);

    }

    private int aspectRatio(float width, float height){
        float ratio = Math.max(width, height) / Math.min(width, height);
        if (Math.abs(ratio - 4.0f / 3.0f) < Math.abs(ratio - 16.0f / 9.0f)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    @NonNull
    @Override
    public Preview options(@NonNull Preview.Builder builder) {
        return super.options(builder);
    }

    @NonNull
    @Override
    public CameraSelector options(@NonNull CameraSelector.Builder builder) {
        return super.options(builder);
    }

    @NonNull
    @Override
    public ImageAnalysis options(@NonNull ImageAnalysis.Builder builder) {
        builder.setTargetAspectRatio(mAspectRatio);
        return super.options(builder);
    }

    @NotNull
    @Override
    public ImageCapture options(@NotNull ImageCapture.Builder builder) {
        builder.setTargetAspectRatio(mAspectRatio);
        return super.options(builder);
    }
}
