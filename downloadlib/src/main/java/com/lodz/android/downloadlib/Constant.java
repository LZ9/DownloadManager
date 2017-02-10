package com.lodz.android.downloadlib;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 常量
 * Created by zhouL on 2017/2/10.
 */
public interface Constant {

    @IntDef({DownloadStatus.WAITING, DownloadStatus.STARTED, DownloadStatus.PAUSEED, DownloadStatus.STOPED, DownloadStatus.COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DownloadStatus {
        public static final int WAITING = 0;// 等待中
        public static final int STARTED = 1;// 已开始
        public static final int PAUSEED = 2;// 已暂停
        public static final int STOPED = 3;// 已停止
        public static final int COMPLETED = 4;// 已完成

    }

}
