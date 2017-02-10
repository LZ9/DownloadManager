package com.lodz.android.downloadlib.db;

import com.lodz.android.downloadlib.bean.DownloadTask;
import com.lodz.android.downloadlib.bean.TaskBlock;

import io.reactivex.Observable;

/**
 * 数据库操作接口
 * Created by zhouL on 2017/2/10.
 */
public interface DatabaseContract {

    /**
     * 下载任务块是否存在
     * @param task 下载任务
     * @param block 任务分块
     * @return 任务块是否存在
     */
    Observable<Boolean> taskExists(DownloadTask task, TaskBlock block);

    /**
     * 插入下载任务块
     * @param task 下载任务
     * @param block 任务分块
     * @return 插入是否成功
     */
    Observable<Boolean> insertTaskBlock(DownloadTask task, TaskBlock block);




}
