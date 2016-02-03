package com.example.hexi.canvastest.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by hexi on 15/3/11.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "tracker.db";
    private static final String TAG = "DBHelper";

    private static volatile DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table log_data " +
                        "(id integer PRIMARY KEY AUTOINCREMENT" +
                        ", content text not null" +
                        ", createAt Long not null)"
        );
        db.execSQL(
                "create table user " +
                        "(id integer PRIMARY KEY AUTOINCREMENT" +
                        ", username text not null" +
                        ", nickname text not null)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS log_data");
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public boolean save(String[] logDatas) {
        Log.d(TAG, "===into save, time:" + DateTime.now());
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "===start save, time:" + DateTime.now());
        for (String logData : logDatas) {
            String sql = "insert into log_data(content, createAt) values (?, ?);";
            Object[] bindArgs = new Object[]{logData, new Date().getTime()};
            db.execSQL(sql, bindArgs);
        }
        Log.d(TAG, "===end save, time:" + DateTime.now());
        return true;
    }

    public boolean saveWithTransaction(String[] logDatas) {
        Log.d(TAG, "===into saveWithTransaction, time:" + DateTime.now());
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "===start saveWithTransaction, time:"+ DateTime.now());
        try {
            db.beginTransaction();
            for (String logData : logDatas) {
                String sql = "insert into log_data(content, createAt) values (?, ?);";
                Object[] bindArgs = new Object[]{logData, new Date().getTime()};
                db.execSQL(sql, bindArgs);
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "===end saveWithTransaction, time:"+DateTime.now());
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public Integer getLastLogDataId() {
        Log.d(TAG, String.format("===%s into getLastLogId, time:%s",
                Thread.currentThread().getName(),
                DateTime.now()));
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, String.format("===%s start getLastLogId, time:%s",
                Thread.currentThread().getName(),
                DateTime.now()));

        String sql = "select id from log_data order by id DESC limit 1";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return null;
            }
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            Log.d(TAG, String.format("===%s end getLastLogId, time:%s",
                    Thread.currentThread().getName(),
                    DateTime.now()));
        }
    }

    public Integer getLastLogDataIdSleep() {
        Log.d(TAG, String.format("===%s into getLastLogId, time:%s",
                Thread.currentThread().getName(),
                DateTime.now()));
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, String.format("===%s start getLastLogId, time:%s",
                Thread.currentThread().getName(),
                DateTime.now()));

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String sql = "select id from log_data order by id DESC limit 1";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return null;
            }
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            Log.d(TAG, String.format("===%s end getLastLogId, time:%s",
                    Thread.currentThread().getName(),
                    DateTime.now()));
        }
    }

    public Integer countUser() {
        Log.d(TAG, String.format("===%s into countUser, time:%s",
                Thread.currentThread().getName(),
                DateTime.now()));
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, String.format("===%s start countUser, time:%s",
                Thread.currentThread().getName(),
                DateTime.now()));
        String sql = "select count(1) from user";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "===count of user:"+count);
                return count;
            } else {
                return 0;
            }
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            Log.d(TAG, String.format("===%s end countUser, time:%s",
                    Thread.currentThread().getName(),
                    DateTime.now()));
        }
    }
}
