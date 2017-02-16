package com.lodz.android.downloadlib;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.lodz.android.downloadlib.retrofit.DownloadRetrofit;

import retrofit2.Retrofit;

/**
 * 下载管理器
 * Created by zhouL on 2017/2/13.
 */
public class DownloadConfig {

    private static DownloadConfig mInstance;

    public static DownloadConfig get() {
        if (mInstance == null) {
            synchronized (DownloadConfig.class) {
                if (mInstance == null) {
                    mInstance = new DownloadConfig();
                }
            }
        }
        return mInstance;
    }

    /** 构建对象 */
    private Builder mBuilder;

    /** 设置构建对象 */
    public Builder newBuilder(){
        mBuilder = null;
        mBuilder = new Builder();
        return mBuilder;
    }

    /** 获取构建对象 */
    public Builder getBuilder(){
        if (mBuilder == null){
            mBuilder = newBuilder();
        }
        return mBuilder;
    }

    public class Builder {

        /** 每个任务的分块数 */
        private int taskBlockCount = 1;
        /** 保存地址（默认在根目录的Downloads） */
        private String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        public Builder setTaskBlockCount(int taskBlockCount) {
            this.taskBlockCount = taskBlockCount;
            return this;
        }

        public Builder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public void setRetrofit(@NonNull Retrofit retrofit) {
            DownloadRetrofit.get().setRetrofit(retrofit);
        }

        public int getTaskBlockCount() {
            return taskBlockCount;
        }

        public String getSavePath() {
            return savePath;
        }

        public void build(){

        }
    }
}
