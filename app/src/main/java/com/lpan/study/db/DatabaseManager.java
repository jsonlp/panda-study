package com.lpan.study.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lpan.study.greendao.DaoMaster;
import com.lpan.study.greendao.DaoSession;

/**
 * Created by lpan on 2018/3/29.
 */

public class DatabaseManager {

    public static final String DB_NAME = "panda.db";

    private static DatabaseManager mDatabaseManager;

    private static DaoMaster.DevOpenHelper mDevOpenHelper;

    private static DaoMaster mDaoMaster;

    private static DaoSession mDaoSession;

    private DatabaseManager() {
//        Context context = AppContext.context;
        Context context = new GreenDaoContext();
        mDevOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        getDaoMaster();
        getDaoSession();
    }

    public static DatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (mDatabaseManager == null) {
                    mDatabaseManager = new DatabaseManager();
                }
            }
        }
        return mDatabaseManager;
    }

    public static DaoMaster getDaoMaster() {
        if (null == mDaoMaster) {
            synchronized (DatabaseManager.class) {
                if (null == mDaoMaster) {
                    mDaoMaster = new DaoMaster(getWritableDatabase());
                }
            }
        }
        return mDaoMaster;
    }

    public static DaoSession getDaoSession() {
        if (null == mDaoSession) {
            synchronized (DatabaseManager.class) {
                mDaoSession = getDaoMaster().newSession();
            }
        }

        return mDaoSession;
    }

    public static SQLiteDatabase getReadableDatabase() {
        if (null == mDevOpenHelper) {
            getInstance();
        }
        return mDevOpenHelper.getReadableDatabase();
    }

    public static SQLiteDatabase getWritableDatabase() {
        if (null == mDevOpenHelper) {
            getInstance();
        }

        return mDevOpenHelper.getWritableDatabase();
    }

}
