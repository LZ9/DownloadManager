package com.lodz.android.downloadlib.bean;


import com.lodz.android.downloadlib.Constant;

/**
 * 下载任务块
 * Created by zhouL on 2017/2/10.
 */
public class TaskBlock {

    /** 块序号 */
    public String blockId = "";
    /** 块总数 */
    public int blockSum = 0;
    /** 块大小 */
    public long blockSize = 0;
    /** 已下载大小 */
    public long downloadSize = 0;
    /** 块状态 */
    @Constant.DownloadStatus
    public int blockStatus = Constant.DownloadStatus.WAITING;
    /** 分块起始位置 */
    public long rangeStart = 0;
    /** 分块结束位置 */
    public long rangeEnd = 0;
}
