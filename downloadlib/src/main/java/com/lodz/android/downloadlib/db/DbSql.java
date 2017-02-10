package com.lodz.android.downloadlib.db;

import android.content.ContentValues;

import com.lodz.android.downloadlib.Constant;
import com.lodz.android.downloadlib.bean.DownloadTask;
import com.lodz.android.downloadlib.bean.TaskBlock;

import java.util.Date;

/**
 * 数据库配置
 * Created by zhouL on 2017/2/10.
 */
class DbSql {

    /** 下载记录表 */
    static class RecordTable {

        /** 表名 */
        static final String TABLE_NAME = "download_record";

        private static final String COLUMN_ID = "id";// 主键id，自增
        private static final String COLUMN_TASK_ID = "task_id";// 下载任务id
        private static final String COLUMN_BLOCK_ID = "block_id";// 块序号
        private static final String COLUMN_BLOCK_SUM = "block_sum";// 块总数
        private static final String COLUMN_URL = "url";// 下载地址
        private static final String COLUMN_SAVE_NAME = "save_name";// 保存名字
        private static final String COLUMN_SAVE_PATH = "save_path";// 保存地址
        private static final String COLUMN_DOWNLOAD_SIZE = "download_size";// 已下载大小
        private static final String COLUMN_BLOCK_SIZE = "block_size";// 块大小
        private static final String COLUMN_BLOCK_STATUS = "block_status";// 块状态
        private static final String COLUMN_DATE = "date";// 下载时间毫秒时间戳

        /** 建表 */
        static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_TASK_ID + " INTEGER," +
                        COLUMN_BLOCK_ID + " INTEGER," +
                        COLUMN_BLOCK_SUM + " INTEGER," +
                        COLUMN_URL + " TEXT NOT NULL," +
                        COLUMN_SAVE_NAME + " TEXT," +
                        COLUMN_SAVE_PATH + " TEXT," +
                        COLUMN_DOWNLOAD_SIZE + " TEXT DEFAULT 0," +
                        COLUMN_BLOCK_SIZE + " TEXT DEFAULT 0," +
                        COLUMN_BLOCK_STATUS + " INTEGER DEFAULT " + Constant.DownloadStatus.WAITING + "," +
                        COLUMN_DATE + " TEXT NOT NULL " +
                        " )";

        /**
         * 插入下载任务块
         * @param task 下载任务
         * @param block 任务分块
         */
        static ContentValues insert(DownloadTask task, TaskBlock block) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TASK_ID, task.taskId);
            values.put(COLUMN_BLOCK_ID, block.blockId);
            values.put(COLUMN_BLOCK_SUM, block.blockSum);
            values.put(COLUMN_URL, task.url);
            values.put(COLUMN_SAVE_NAME, task.saveName);
            values.put(COLUMN_SAVE_PATH, task.savePath);
            values.put(COLUMN_DOWNLOAD_SIZE, block.downloadSize);
            values.put(COLUMN_BLOCK_SIZE, block.blockSize);
            values.put(COLUMN_BLOCK_STATUS, block.blockStatus);
            values.put(COLUMN_DATE, new Date().getTime());
            return values;
        }




    }


}
