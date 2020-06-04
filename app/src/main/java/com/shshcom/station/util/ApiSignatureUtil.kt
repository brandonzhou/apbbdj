package com.shshcom.station.util

import com.mt.bbdj.baseconfig.model.Constant
import com.mt.bbdj.baseconfig.utls.EncryptUtil
import com.mt.bbdj.baseconfig.utls.MD5Util
import java.util.*

/**
 * desc:
 * author: zhhli
 * 2020/6/3
 */
object ApiSignatureUtil {

    /**
     * 1.1签名说明
     * 需每个接口都要传递签名值
     * Ⅰ、将所传参数按首字母大小顺序排序
     * 例：station_id=12&code=1234，结果为code1234station_id12
     * Ⅱ、将Ⅰ 生成的字符串后面拼接密钥secret
     * 例：code1234station_id12 + secret
     * Ⅲ、将Ⅱ 生成的字符串进行sha1 加密
     * Ⅳ、将 Ⅲ 生成的字符串进行md5 加密
     * Ⅴ、将 Ⅳ 生成的字符串转为大写
     *
     * secret密钥值：take2019bbdj
     *
     * @param map
     * @return
     */
    fun addSignature(map: MutableMap<String, Any>) {
        // 构造签名键值对的格式
        val sb = StringBuilder()

        val list  = map.entries.sortedBy { it.key }.toList()
        list.forEach {
            sb.append(it.key)
            sb.append(it.value)
        }

        sb.append(Constant.key)
        var result = EncryptUtil.getSha1(sb.toString())
        //进行MD5加密
        result = MD5Util.toMD5(result)
        val sign = result.toUpperCase(Locale.US)
        map["signature"] = sign

    }
}