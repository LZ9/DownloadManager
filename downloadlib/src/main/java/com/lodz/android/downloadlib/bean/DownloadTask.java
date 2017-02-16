package com.lodz.android.downloadlib.bean;

import java.util.HashMap;
import java.util.Map;


/**
 * 下载任务
 * Created by zhouL on 2017/2/10.
 */
public class DownloadTask {

    /** 任务id */
    public String taskId = "";
    /** 下载地址 */
    public String url = "";
    /** 保存名称 */
    public String saveName = "";
    /** 保存地址 */
    public String savePath = "";
    /** 临时文件地址 */
    public String tempFilePath = "";
    /** 文件长度 */
    public long contentLength = 0;
    /** 任务块 */
    public Map<String, TaskBlock> taskBlockMap;

    public DownloadTask() {
        taskBlockMap = new HashMap<>();
    }
}
