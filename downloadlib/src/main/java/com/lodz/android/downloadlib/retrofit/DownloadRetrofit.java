package com.lodz.android.downloadlib.retrofit;


import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 接口管理器
 * Created by zhouL on 2016/10/31.
 */
public class DownloadRetrofit {

    private static DownloadRetrofit mInstance;

    public static DownloadRetrofit get() {
        if (mInstance == null) {
            synchronized (DownloadRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new DownloadRetrofit();
                }
            }
        }
        return mInstance;
    }

    /** 数据标签 */
    private static final String TAG = "resultValue";
    /** 基地址 */
    private static final String BASE_URL = "http://example.com/api/";

    private Retrofit mRetrofit;

    private DownloadRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .build();
    }

    /** 获取一个OkHttpClient */
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(getHttpLoggingInterceptor())
                .build();
    }

    private Interceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    public void setRetrofit(@NonNull Retrofit retrofit){
        this.mRetrofit = retrofit;
    }

    /**
     * 创建对应的ApiService
     * @param service 接口类
     */
    public <T> T create(final Class<T> service){
        return mRetrofit.create(service);
    }
}
