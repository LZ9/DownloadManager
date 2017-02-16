package com.snxun.downloaddemo;

import android.app.Application;

import com.lodz.android.downloadlib.DownloadConfig;
import com.snxun.downloaddemo.utils.FileManager;

/**
 * 应用
 * Created by zhouL on 2017/2/13.
 */
public class App extends Application{

    private static Application sInstance;

    public static Application get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initFile();
        initDownload();
    }

    private void initFile() {
        FileManager.init();
    }

    private void initDownload() {
        DownloadConfig.get().newBuilder()
                .setTaskBlockCount(1)
                .setSavePath(FileManager.getDownloadFolderPath())
                .build();
    }
}
