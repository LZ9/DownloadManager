package com.lodz.android.downloadlib.bean;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 异常实体
 * Created by zhouL on 2017/2/15.
 */
public class ErrorBean {

    @IntDef({ErrorType.ALREADY_IN_DOWNLOAD_LIST, ErrorType.GET_HEAD_ERROR, ErrorType.DIVIDE_BLOCK_ERROR, ErrorType.DOWNLOADING_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorType {
        /** 已经在下载列表中 */
        public static final int ALREADY_IN_DOWNLOAD_LIST = 1;
        /** 头信息获取失败 */
        public static final int GET_HEAD_ERROR = 2;
        /** 文件分块失败 */
        public static final int DIVIDE_BLOCK_ERROR = 3;
        /** 文件下载失败 */
        public static final int DOWNLOADING_ERROR = 4;
    }

    @ErrorType
    private int mErrorType = 0;

    private Throwable mThrowable;


    public ErrorBean(@ErrorType int errorType) {
        mErrorType = errorType;
    }

    public int getErrorType() {
        return mErrorType;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public void setThrowable(Throwable t) {
        this.mThrowable = t;
    }
}
