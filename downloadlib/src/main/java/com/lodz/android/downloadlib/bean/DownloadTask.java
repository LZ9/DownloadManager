package com.lodz.android.downloadlib.bean;

import android.util.SparseArray;

import com.lodz.android.downloadlib.Constant;


/**
 * 下载任务
 * Created by zhouL on 2017/2/10.
 */
public class DownloadTask {

    /** 任务id */
    public int taskId;
    /** 下载地址 */
    public String url;
    /** 保存名称 */
    public String saveName;
    /** 保存地址 */
    public String savePath;
    /** 下载状态 */
    @Constant.DownloadStatus
    public int taskStatus = Constant.DownloadStatus.WAITING;
    /** 任务块 */
    public SparseArray<TaskBlock> blockSparseArray;

    public DownloadTask() {
        blockSparseArray = new SparseArray<>();
    }







}
