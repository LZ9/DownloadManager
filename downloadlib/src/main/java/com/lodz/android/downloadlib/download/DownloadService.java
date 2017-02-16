package com.lodz.android.downloadlib.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lodz.android.downloadlib.bean.DownloadTask;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 下载服务
 * Created by zhouL on 2017/2/13.
 */
public class DownloadService extends Service{

    private DownloadBinder mDownloadBinder;

    private Queue<DownloadTask> mWaitingList;

    private Map<Integer, DownloadTask> mLoadingMap;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadBinder = new DownloadBinder();
        mWaitingList = new LinkedList<>();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadBinder;
    }

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
