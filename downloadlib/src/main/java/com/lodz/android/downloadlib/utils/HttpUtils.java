package com.lodz.android.downloadlib.utils;

import android.text.TextUtils;

import okhttp3.internal.http.HttpHeaders;
import retrofit2.Response;

/**
 * Http相关帮助类
 * Created by zhouL on 2017/2/15.
 */
public class HttpUtils {

    /**
     * 是否支持分块下载
     * @param response 头信息数据
     */
    public static boolean isSupportRange(Response<?> response) {
        return !TextUtils.isEmpty(response.headers().get("Content-Range"))
                && getContentLength(response) != -1;
    }

    /**
     * 获取文件长度
     * @param response 文件长度
     */
    public static long getContentLength(Response<?> response) {
        return HttpHeaders.contentLength(response.headers());
    }
}
