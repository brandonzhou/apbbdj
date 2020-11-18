package com.shshcom.station.storage.http.bean


import com.google.gson.annotations.SerializedName

data class OSSConfig(
        @SerializedName("authServerUrl")
        val authServerUrl: String, // https://express.shshcom.com/qrcode/bbapi/submit2/querySts
        @SerializedName("bucketName")
        val bucketName: String, // bbsh-com
        @SerializedName("endpoint")
        val endpoint: String, // https://oss-cn-beijing.aliyuncs.com
        @SerializedName("path")
        val path: String // picture/ruku
)