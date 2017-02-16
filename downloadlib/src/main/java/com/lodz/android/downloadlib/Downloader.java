package com.lodz.android.downloadlib;

import android.text.TextUtils;

import com.lodz.android.downloadlib.bean.DownloadTask;
import com.lodz.android.downloadlib.bean.ErrorBean;
import com.lodz.android.downloadlib.bean.TaskBlock;
import com.lodz.android.downloadlib.retrofit.DownloadApiService;
import com.lodz.android.downloadlib.retrofit.DownloadRetrofit;
import com.lodz.android.downloadlib.utils.HttpUtils;
import com.lodz.android.downloadlib.utils.RangeUtils;

import java.io.File;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * 下载器
 * Created by zhouL on 2017/2/14.
 */
public class Downloader {

    /** 监听器 */
    private Listener mListener;
    /** 保存路径 */
    private String mSavePath = "";
    /** 每个任务的分块数 */
    private int mTaskBlockCount = 0;
    /** 保存名称 */
    private String mSaveName = "";
    /** 下载接口 */
    private DownloadApiService mApiService;

    /** 创建下载器 */
    public static Downloader create(){
        Downloader downloader = new Downloader();
        downloader.mApiService = DownloadRetrofit.get().create(DownloadApiService.class);
        return downloader;
    }

    /**
     * 设置监听器
     * @param listener 监听器
     */
    public Downloader setListener(Listener listener){
        this.mListener = listener;
        return this;
    }

    /**
     * 设置保存路径
     * @param savePath 保存路径
     */
    public Downloader setSavePath(String savePath){
        this.mSavePath = savePath;
        return this;
    }

    /**
     * 设置保存名称
     * @param saveName 保存名称
     */
    public Downloader setSaveName(String saveName){
        this.mSaveName = saveName;
        return this;
    }

    /**
     * 每个任务的分块数
     * @param taskBlockCount 任务分块数
     */
    public Downloader setTaskBlockCount(int taskBlockCount) {
        this.mTaskBlockCount = taskBlockCount;
        return this;
    }

    /**
     * 开始下载
     * @param url 下载地址
     */
    public void download(String url){
        checkParams();
        downloadPrepare(url);
    }

    /** 校验参数 */
    private void checkParams() {
        if (TextUtils.isEmpty(mSavePath)){
            mSavePath = DownloadConfig.get().getBuilder().getSavePath();
        }
        if (TextUtils.isEmpty(mSaveName)){
            mSaveName = System.currentTimeMillis()+"";
        }
        if (mTaskBlockCount <= 0){
            mTaskBlockCount = DownloadConfig.get().getBuilder().getTaskBlockCount();
        }
        if (mTaskBlockCount > 6){// 单任务最大分块数不超过6块
            mTaskBlockCount = 6;
        }
    }

    /**
     * 下载准备
     * @param url 下载地址
     */
    private void downloadPrepare(String url) {
        if (isFileExists(getFilePath(mSavePath, mSaveName))){// 如果文件已经存在或已下载完成
            if (mListener != null){
                mListener.onComplete(getFilePath(mSavePath, mSaveName));
            }
            return;
        }

        if (isInDownloadList(url)){// 已经在下载队列中无需重复添加
            if (mListener != null){
                mListener.onError(new ErrorBean(ErrorBean.ErrorType.ALREADY_IN_DOWNLOAD_LIST));
            }
            return;
        }
        DownloadTask task = createDownloadTask(url);
        if (hasRecord(url)){// 存在下载记录
            task = readRecord(task);// 读取下载记录
            startDownload(task);
            return;
        }
        divideBlock(task);
    }

    /**
     * 划分任务块
     * @param downloadTask 下载任务
     */
    private void divideBlock(final DownloadTask downloadTask) {
        mApiService.HEAD(DownloadApiService.TEST_RANGE_SUPPORT, downloadTask.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> response) {
                        downloadTask.contentLength = HttpUtils.getContentLength(response);
                        if (downloadTask.contentLength <= 0){
                            onError(new IllegalArgumentException("contentLength less than 0"));
                            return;
                        }
                        if (HttpUtils.isSupportRange(response)){//支持分段下载
                            if (mTaskBlockCount > 1){
                                rangeDownload(downloadTask, mTaskBlockCount);
                                return;
                            }
                        }
                        normalDownload(downloadTask);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (mListener != null){
                            ErrorBean errorBean = new ErrorBean(ErrorBean.ErrorType.GET_HEAD_ERROR);
                            errorBean.setThrowable(e);
                            mListener.onError(errorBean);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 分块下载
     * @param downloadTask 下载任务
     * @param blockCount 分块数
     */
    private void rangeDownload(DownloadTask downloadTask, int blockCount) {
        for (int i = 0; i < blockCount; i++) {
            TaskBlock block = new TaskBlock();
            block.blockId = downloadTask.taskId + "_" + i;
            block.blockStatus = Constant.DownloadStatus.WAITING;
            block.blockSum = blockCount;
            try {
                block = RangeUtils.getRange(block, downloadTask.tempFilePath, i, downloadTask.contentLength, blockCount);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorBean errorBean = new ErrorBean(ErrorBean.ErrorType.DIVIDE_BLOCK_ERROR);
                errorBean.setThrowable(e);
                if (mListener != null){
                    mListener.onError(errorBean);
                }
                return;
            }
            downloadTask.taskBlockMap.put(block.blockId, block);
        }
        startDownload(downloadTask);
    }

    /**
     * 单块下载
     * @param downloadTask 下载任务
     */
    private void normalDownload(DownloadTask downloadTask) {
        TaskBlock block = new TaskBlock();
        block.blockId = downloadTask.taskId + "_0";
        block.blockStatus = Constant.DownloadStatus.WAITING;
        block.blockSum = 1;
        block.blockSize = downloadTask.contentLength;
        block.rangeStart = 0;
        block.rangeEnd = downloadTask.contentLength;
        downloadTask.taskBlockMap.put(block.blockId, block);
        startDownload(downloadTask);
    }

    /**
     * 开始下载
     * @param downloadTask 下载任务
     */
    private void startDownload(DownloadTask downloadTask) {
        Iterator<String> iterator = downloadTask.taskBlockMap.keySet().iterator();
        while (iterator.hasNext()) {
            downloadTask.taskBlockMap.get(iterator.next()).blockStatus = Constant.DownloadStatus.STARTED;
        }
        if (mListener != null){
            mListener.onStart(downloadTask);
        }
        if (downloadTask.taskBlockMap.size() > 1){// 多任务下载
            startRangeDownload(downloadTask);
        }else {
            startNormalDownload(downloadTask);
        }
    }

    private void startRangeDownload(DownloadTask downloadTask) {
        Iterator<String> iterator = downloadTask.taskBlockMap.keySet().iterator();
        while (iterator.hasNext()) {
            TaskBlock taskBlock = downloadTask.taskBlockMap.get(iterator.next());
            rangeProcess(downloadTask, taskBlock);
        }
    }

    private void rangeProcess(final DownloadTask downloadTask, final TaskBlock taskBlock){
        String rangeStr = "bytes=" + taskBlock.rangeStart + "-" + taskBlock.rangeEnd;
        mApiService.download(rangeStr, downloadTask.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Response<ResponseBody>, Observable<DownloadTask>>() {
                    @Override
                    public Observable<DownloadTask> apply(final Response<ResponseBody> resp) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<DownloadTask>() {
                            @Override
                            public void subscribe(ObservableEmitter<DownloadTask> emitter) throws Exception {
                                RangeUtils.rangeSave(emitter, downloadTask, taskBlock, resp.body());
                            }
                        });
                    }
                })
                .subscribe(new Observer<DownloadTask>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DownloadTask task) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

//                .subscribe(new Observer<Response<ResponseBody>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Response<ResponseBody> resp) {
//                        RangeUtils.rangeSave(downloadTask, taskBlock, resp.body());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mListener != null){
//                            ErrorBean errorBean = new ErrorBean(ErrorBean.ErrorType.DOWNLOADING_ERROR);
//                            errorBean.setThrowable(e);
//                            mListener.onError(errorBean);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    private void startNormalDownload(DownloadTask downloadTask) {

    }

    /**
     * 读取下载记录
     * @param downloadTask 下载任务
     */
    private DownloadTask readRecord(DownloadTask downloadTask) {
        return downloadTask;
    }

    /**
     * 是否存在下载记录
     * @param url 下载地址
     */
    private boolean hasRecord(String url) {
        return false;
    }

    /**
     * 下载文件是否已经存在
     * @param filePath 文件路径
     */
    private boolean isFileExists(String filePath) {
        return false;
    }

    /**
     * 是否已经在下载列表中
     * @param url 下载地址
     */
    private boolean isInDownloadList(String url) {
        return false;
    }

    /**
     * 获取下载文件完整路径
     * @param savePath 保存路径
     * @param saveName 保存名称
     */
    private String getFilePath(String savePath, String saveName){
        if (!savePath.endsWith(File.separator)){
            savePath += File.separator;
        }
        return savePath + saveName;
    }

    /**
     * 创建下载任务
     * @param url 下载地址
     */
    private DownloadTask createDownloadTask(String url) {
        DownloadTask task = new DownloadTask();
        task.taskId = System.currentTimeMillis()+"";
        task.url = url;
        task.saveName = mSaveName;
        task.savePath = mSavePath;
        task.tempFilePath = getFilePath(mSavePath, mSaveName)+"_temp";
        return task;
    }

    public interface Listener{
        /** 进度回调 */
        void onProgress();
        /** 下载完成 */
        void onComplete(String filePath);
        /** 开始下载 */
        void onStart(DownloadTask downloadTask);
        /** 暂停/停止 */
        void onPause();
        /** 错误回调 */
        void onError(ErrorBean errorBean);
    }

}
