package wxm.dofcalculator.db;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import wxm.dofcalculator.BuildConfig;
import wxm.dofcalculator.define.CameraItem;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.LensItem;

/**
 * db ormlite helper
 * Created by 123 on 2016/8/5.
 */
public class DBOrmLiteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DBOrmLiteHelper";


    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "AppLocal.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the SimpleData table
    private RuntimeExceptionDao<CameraItem, Integer>    mRDCamera   = null;
    private RuntimeExceptionDao<LensItem, Integer>      mRDLens     = null;
    private RuntimeExceptionDao<DeviceItem, Integer>    mRDDevice   = null;

    public DBOrmLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        CreateAndInitTable();
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<CameraItem, Integer> getREDCamera() {
        if (mRDCamera == null) {
            mRDCamera = getRuntimeExceptionDao(CameraItem.class);
        }

        return mRDCamera;
    }

    public RuntimeExceptionDao<LensItem, Integer> getREDLens() {
        if (mRDLens == null) {
            mRDLens = getRuntimeExceptionDao(LensItem.class);
        }

        return mRDLens;
    }

    public RuntimeExceptionDao<DeviceItem, Integer> getREDDevice() {
        if (mRDDevice == null) {
            mRDDevice = getRuntimeExceptionDao(DeviceItem.class);
        }

        return mRDDevice;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        mRDCamera   = null;
        mRDLens     = null;
        mRDDevice   = null;
    }


    /**
     * 创建表并完成初始化工作
     */
    private void CreateAndInitTable()   {
        try {
            TableUtils.createTable(connectionSource, CameraItem.class);
            TableUtils.createTable(connectionSource, LensItem.class);
            TableUtils.createTable(connectionSource, DeviceItem.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }

        if(BuildConfig.FILL_TESTDATA)
            AddTestData();
    }


    /**
     * 填充测试数据
     */
    private void AddTestData()  {
    }
}