package com.shshcom.module_base.network;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * desc:
 * author: zhhli
 * 2020/1/13
 */
public class HttpLogInterceptor implements Interceptor {
    private static final String TAG = "HttpLog";
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String reqDetail = getRequestDetail(request);

        Response response = chain.proceed(request);

        String responseDetail = getResponseDetail(response);

        String detail =
                "HttpLogInterceptor \n发送请求url：" + request.url()
                        + "\n发送请求 method：" + request.method()
                        + "\n发送请求 body：\n" + reqDetail
                        + "\n收到响应 code:" + response.code()
                        + "\n响应结果: \n" + responseDetail;

        Log.d(TAG, detail);
        return response;
    }


    private String getRequestDetail(Request request) {
        RequestBody requestBody = request.newBuilder().build().body();
        if (requestBody == null) {
            return "";
        }


        String bodyDetail = "";

        if (isParseable(requestBody.contentType())) {
            Buffer buffer = new Buffer();
            try {
                requestBody.writeTo(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            bodyDetail = buffer.readString(charset);


        }


        return bodyDetail;


    }

    private String getResponseDetail(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return "";
        }


        String bodyDetail = "";
        if (isParseable(responseBody.contentType())) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }
            bodyDetail = buffer.clone().readString(charset);

            bodyDetail = decode(bodyDetail);


        }


        return bodyDetail;


    }

    static final String decode(final String in) {
        String working = in;
        int index;
        index = working.indexOf("\\u");
        while (index > -1) {
            int length = working.length();
            if (index > (length - 6)) break;
            int numStart = index + 2;
            int numFinish = numStart + 4;
            String substring = working.substring(numStart, numFinish);
            String stringStart = working.substring(0, index);
            String stringEnd = working.substring(numFinish);
            try {
                int number = Integer.parseInt(substring, 16);
                working = stringStart + ((char) number) + stringEnd;
                index = working.indexOf("\\u");
            } catch (Exception e) {
                index = working.indexOf("\\u", numFinish);
            }


        }
        return working.replace("\\", "");
    }


    /**
     * 是否可以解析
     *
     * @param mediaType {@link MediaType}
     * @return {@code true} 为可以解析
     */
    public static boolean isParseable(MediaType mediaType) {
        if (mediaType == null || mediaType.type() == null) {
            return false;
        }
        return isText(mediaType) || isPlain(mediaType)
                || isJson(mediaType) || isForm(mediaType)
                || isHtml(mediaType) || isXml(mediaType);
    }

    public static boolean isText(MediaType mediaType) {
        if (mediaType == null || mediaType.type() == null) {
            return false;
        }
        return "text".equals(mediaType.type());
    }

    public static boolean isPlain(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) {
            return false;
        }
        return mediaType.subtype().toLowerCase().contains("plain");
    }

    public static boolean isJson(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) {
            return false;
        }
        return mediaType.subtype().toLowerCase().contains("json");
    }

    public static boolean isXml(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) {
            return false;
        }
        return mediaType.subtype().toLowerCase().contains("xml");
    }

    public static boolean isHtml(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) {
            return false;
        }
        return mediaType.subtype().toLowerCase().contains("html");
    }

    public static boolean isForm(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) {
            return false;
        }
        return mediaType.subtype().toLowerCase().contains("x-www-form-urlencoded");
    }
}
