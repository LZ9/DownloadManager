package com.lodz.android.downloadlib.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lodz.android.downloadlib.bean.DownloadTask;
import com.lodz.android.downloadlib.bean.TaskBlock;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.lodz.android.downloadlib.db.DbSql.RecordTable.TABLE_NAME;


/**
 * 数据库管理类
 * Created by zhouL on 2016/11/22.
 */
public class DatabaseManager implements DatabaseContract{

    private volatile static DatabaseManager mInstance;

    public static DatabaseManager get(Context context) {
        if (mInstance == null) {
            synchronized (DatabaseManager.class) {
                if (mInstance == null) {
                    mInstance = new DatabaseManager(context);
                }
            }
        }
        return mInstance;
    }

    /** 数据库助手 */
    private DbOpenHelper mDbOpenHelper;
    /** 对象锁 */
    private final Object databaseLock = new Object();
    /** 可读数据库 */
    private volatile SQLiteDatabase readableDatabase;
    /** 可写数据库 */
    private volatile SQLiteDatabase writableDatabase;

    private DatabaseManager(Context context) {
        mDbOpenHelper = new DbOpenHelper(context);
    }

    /** 关闭数据库 */
    public void closeDatabase() {
        synchronized (databaseLock) {
            readableDatabase = null;
            writableDatabase = null;
            mDbOpenHelper.close();
        }
    }

    /** 获取可写数据库 */
    private SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = writableDatabase;
        if (db == null) {
            synchronized (databaseLock) {
                db = writableDatabase;
                if (db == null) {
                    db = writableDatabase = mDbOpenHelper.getWritableDatabase();
                }
            }
        }
        return db;
    }

    /** 获取可读数据库 */
    private SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = readableDatabase;
        if (db == null) {
            synchronized (databaseLock) {
                db = readableDatabase;
                if (db == null) {
                    db = readableDatabase = mDbOpenHelper.getReadableDatabase();
                }
            }
        }
        return db;
    }


    @Override
    public Observable<Boolean> taskExists(DownloadTask task, TaskBlock block) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Cursor cursor = null;
                try {
//                    cursor = getReadableDatabase().query(TABLE_NAME, new String[]{COLUMN_ID}, "url=?", new String[]{url}, null, null, null);
//                    Cursor cursor = null;
//                    try {
//                        cursor = getReadableDatabase().query(TABLE_NAME, new String[]{COLUMN_ID}, "url=?",
//                                new String[]{url}, null, null, null);
//                        return cursor.getCount() == 0;
//                    } finally {
//                        if (cursor != null) {
//                            cursor.close();
//                        }
//                    }



                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onError(e);
                }finally {
                    if (cursor != null){
                        cursor.close();
                    }
                    emitter.onComplete();
                }
            }
        });
    }

    @Override
    public Observable<Boolean> insertTaskBlock(final DownloadTask task, final TaskBlock block) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    long isSuccess = getWritableDatabase().insert(TABLE_NAME, null, DbSql.RecordTable.insert(task, block));
                    emitter.onNext(isSuccess != -1);
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onError(e);
                }finally {
                    emitter.onComplete();
                }
            }
        });
    }


//    public boolean recordExists(String url) {
//        return !recordNotExists(url);
//    }
//
//    public boolean recordNotExists(String url) {
//        Cursor cursor = null;
//        try {
//            cursor = getReadableDatabase().query(TABLE_NAME, new String[]{COLUMN_ID}, "url=?",
//                    new String[]{url}, null, null, null);
//            return cursor.getCount() == 0;
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }
//
//    public long insertRecord(DownloadMission mission) {
//        return getWritableDatabase().insert(TABLE_NAME, null, insert(mission));
//    }
//
//    public long updateRecord(String url, DownloadStatus status) {
//        return getWritableDatabase().update(TABLE_NAME, update(status), "url=?", new String[]{url});
//    }
//
//    public long updateRecord(String url, int flag) {
//        return getWritableDatabase().update(TABLE_NAME, update(flag), "url=?", new String[]{url});
//    }
//
//    public int deleteRecord(String url) {
//        return getWritableDatabase().delete(TABLE_NAME, "url=?", new String[]{url});
//    }
//
//    public DownloadRecord readSingleRecord(String url) {
//        Cursor cursor = null;
//        try {
//            cursor = getReadableDatabase()
//                    .rawQuery("select * from " + TABLE_NAME + " where url=?", new String[]{url});
//            cursor.moveToFirst();
//            return Db.RecordTable.read(cursor);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }
//
//    public DownloadStatus readStatus(String url) {
//        Cursor cursor = null;
//        try {
//            cursor = getReadableDatabase().query(
//                    TABLE_NAME,
//                    new String[]{COLUMN_DOWNLOAD_SIZE, COLUMN_TOTAL_SIZE, COLUMN_IS_CHUNKED},
//                    "url=?", new String[]{url}, null, null, null);
//            if (cursor.getCount() == 0) {
//                return new DownloadStatus();
//            } else {
//                cursor.moveToFirst();
//                return Db.RecordTable.readStatus(cursor);
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }
//
//    public void closeDataBase() {
//        synchronized (databaseLock) {
//            readableDatabase = null;
//            writableDatabase = null;
//            mDbOpenHelper.close();
//        }
//    }
//
//    public Observable<List<DownloadRecord>> readAllRecords() {
//        return Observable.create(new ObservableOnSubscribe<List<DownloadRecord>>() {
//            @Override
//            public void subscribe(ObservableEmitter<List<DownloadRecord>> emitter)
//                    throws Exception {
//                Cursor cursor = null;
//                try {
//                    cursor = getReadableDatabase()
//                            .rawQuery("select * from " + TABLE_NAME, new String[]{});
//                    List<DownloadRecord> result = new ArrayList<>();
//                    while (cursor.moveToNext()) {
//                        result.add(Db.RecordTable.read(cursor));
//                    }
//                    emitter.onNext(result);
//                    emitter.onComplete();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public long repairErrorFlag() {
//        return getWritableDatabase().update(TABLE_NAME, update(DownloadFlag.PAUSED),
//                COLUMN_DOWNLOAD_FLAG + "=? or " + COLUMN_DOWNLOAD_FLAG + "=?",
//                new String[]{DownloadFlag.WAITING + "", DownloadFlag.STARTED + ""});
//    }
//
//    /**
//     * 获得url对应的下载记录
//     * <p>
//     * ps: 如果数据库中没有记录，则返回一个空的DownloadRecord.
//     *
//     * @param url url
//     *
//     * @return record
//     */
//    public Observable<DownloadRecord> readRecord(final String url) {
//        return Observable.create(new ObservableOnSubscribe<DownloadRecord>() {
//            @Override
//            public void subscribe(ObservableEmitter<DownloadRecord> emitter) throws Exception {
//                Cursor cursor = null;
//                try {
//                    cursor = getReadableDatabase().rawQuery("select * from " + TABLE_NAME +
//                            " where " + "url=?", new String[]{url});
//                    if (cursor.getCount() == 0) {
//                        emitter.onNext(new DownloadRecord());
//                    } else {
//                        cursor.moveToFirst();
//                        emitter.onNext(Db.RecordTable.read(cursor));
//                    }
//                    emitter.onComplete();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//    }
}