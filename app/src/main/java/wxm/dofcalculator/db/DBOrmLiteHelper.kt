package wxm.dofcalculator.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.RuntimeExceptionDao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

import java.sql.SQLException

import wxm.dofcalculator.BuildConfig
import wxm.dofcalculator.define.CameraItem
import wxm.dofcalculator.define.DeviceItem
import wxm.dofcalculator.define.LensItem

/**
 * db helper
 * Created by WangXM on2016/8/5.
 */
class DBOrmLiteHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class.
     * It will create it or just give the cached value.
     * RuntimeExceptionDao only throw RuntimeExceptions.
     */
    val redCamera: RuntimeExceptionDao<CameraItem, Int> = getRuntimeExceptionDao(CameraItem::class.java)
    val redLens: RuntimeExceptionDao<LensItem, Int> = getRuntimeExceptionDao(LensItem::class.java)
    val redDevice: RuntimeExceptionDao<DeviceItem, Int> = getRuntimeExceptionDao(DeviceItem::class.java)

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    override fun onCreate(db: SQLiteDatabase, connectionSource: ConnectionSource) {
        createAndInitTable()
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Suppress("RedundantOverride")
    override fun onUpgrade(db: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Suppress("RedundantOverride")
    override fun close() {
        super.close()
    }

    @Suppress("ConstantConditionIf")
    private fun createAndInitTable() {
        try {
            TableUtils.createTable(connectionSource, CameraItem::class.java)
            TableUtils.createTable(connectionSource, LensItem::class.java)
            TableUtils.createTable(connectionSource, DeviceItem::class.java)
        } catch (e: SQLException) {
            Log.e(LOG_TAG, "Can't create database", e)
            throw RuntimeException(e)
        }

        if (BuildConfig.FILL_TESTDATA)
            addTestData()
    }

    /**
     * add test data(when test)
     */
    private fun addTestData() {}

    companion object {
        private val LOG_TAG = ::DBOrmLiteHelper.javaClass.simpleName

        // name of the database file for your application
        // change to something appropriate for your app
        private const val DATABASE_NAME = "AppLocal.db"

        // any time you make changes to your database objects,
        // you may have to increase the database version
        private const val DATABASE_VERSION = 1
    }
}
