package com.shshcom.station.setting.http.bean

import com.google.gson.annotations.SerializedName


/**
 * desc:
 * author: zhhli
 * 2020/7/7
 */


data class CustomSMSTemplateData(
        @SerializedName("custom_account")
        var phone: String,
        @SerializedName("custom_address")
        var customAddress: String,
        @SerializedName("custom_sms_template")
        val customSmsTemplate: SmsTemplate
)

data class SmsTemplate(
        @SerializedName("content")
        val content: String,
        @SerializedName("list")
        val list: List<TemplateKey>
)

data class TemplateKey(
        @SerializedName("key")
        val key: String,
        @SerializedName("val")
        var valX: String
)