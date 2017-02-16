package com.lodz.android.downloadlib.utils;

import com.lodz.android.core.utils.FileUtils;
import com.lodz.android.downloadlib.bean.DownloadTask;
import com.lodz.android.downloadlib.bean.TaskBlock;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import io.reactivex.ObservableEmitter;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;


/**
 * 分块帮助类
 * Created by zhouL on 2017/2/16.
 */
public class RangeUtils {

    public static TaskBlock getRange(TaskBlock taskBlock, String tempFilePath, int index, long contentLength, int count) throws  Exception{
        FileUtils.createNewFile(tempFilePath);
        File tempFile = FileUtils.createFile(tempFilePath);
        if (!FileUtils.isFileExists(tempFilePath) || tempFile == null){// 文件不存在
            throw new NullPointerException("temp file not exists");
        }
        BigDecimal bigDecimal = new BigDecimal(contentLength / count);
        long blockSize = bigDecimal.setScale(0, BigDecimal.ROUND_UP).longValue();// 算出块大小
        if (blockSize <= 0){// 文件不存在
            throw new IllegalArgumentException("blockSize less than 0");
        }

        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, "rws");
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, index * blockSize, (index + 1) * blockSize);
            long startByte = buffer.getLong();
            long endByte = buffer.getLong();
            if (endByte > contentLength){// 如果算出的endByte大于文件总长度 则取文件总长度
                endByte = contentLength;
            }
            taskBlock.rangeStart = startByte;
            taskBlock.rangeEnd = endByte;
            if (startByte > endByte){
                throw new IllegalArgumentException("startByte Less than endByte");
            }
            taskBlock.blockSize = endByte - startByte;
            return taskBlock;
        } finally {
            Util.closeQuietly(channel);
            Util.closeQuietly(record);
        }
    }

    public static void rangeSave(ObservableEmitter<DownloadTask> emitter, DownloadTask downloadTask,
            TaskBlock taskBlock, ResponseBody response) {

//        RandomAccessFile record = null;
//        FileChannel recordChannel = null;
//        RandomAccessFile save = null;
//        FileChannel saveChannel = null;
//        InputStream inStream = null;
//        try {
//            FileUtils.createNewFile(downloadTask.tempFilePath);
//            File tempFile = FileUtils.createFile(downloadTask.tempFilePath);
//            if (!FileUtils.isFileExists(downloadTask.tempFilePath) || tempFile == null){// 文件不存在
//                throw new NullPointerException("temp file not exists");
//            }
//            try {
//                int readLen;
//                byte[] buffer = new byte[8192];
//
//                record = new RandomAccessFile(tempFile, "rws");
//                recordChannel = record.getChannel();
//                MappedByteBuffer recordBuffer = recordChannel
//                        .map(FileChannel.MapMode.READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);
//
//                long totalSize = recordBuffer.getLong(RECORD_FILE_TOTAL_SIZE - 8) + 1;
//                status.setTotalSize(totalSize);
//
//                save = new RandomAccessFile(saveFile, "rws");
//                saveChannel = save.getChannel();
//                MappedByteBuffer saveBuffer = saveChannel.map(FileChannel.MapMode.READ_WRITE, start, end - start + 1);
//                inStream = response.byteStream();
//
//                while ((readLen = inStream.read(buffer)) != -1) {
//                    saveBuffer.put(buffer, 0, readLen);
//                    recordBuffer.putLong(i * EACH_RECORD_SIZE,
//                            recordBuffer.getLong(i * EACH_RECORD_SIZE) + readLen);
//                    status.setDownloadSize(totalSize - getResidue(recordBuffer));
//                    emitter.onNext(status);
//                }
//                emitter.onComplete();
//            } finally {
//                Util.closeQuietly(record);
//                Util.closeQuietly(recordChannel);
//                Util.closeQuietly(save);
//                Util.closeQuietly(saveChannel);
//                Util.closeQuietly(inStream);
//                Util.closeQuietly(response);
//            }
//        } catch (Exception e) {
//            emitter.onError(e);
//        }
    }
}
