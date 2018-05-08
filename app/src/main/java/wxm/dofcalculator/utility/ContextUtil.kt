package wxm.dofcalculator.utility

import android.app.Application
import android.graphics.drawable.Drawable
import android.support.annotation.*

import wxm.dofcalculator.db.CameraDBUtility
import wxm.dofcalculator.db.DBOrmLiteHelper
import wxm.dofcalculator.db.DeviceDBUtility
import wxm.dofcalculator.db.LensDBUtility

/**
 * 获取全局context
 * 获取全局辅助类
 * Created by WangXM on2016/5/7.
 */
class ContextUtil : Application() {
    // sqlite
    private lateinit var mDBHelper: DBOrmLiteHelper
    private lateinit var mDUCamera: CameraDBUtility
    private lateinit var mDULens: LensDBUtility
    private lateinit var mDUDevice: DeviceDBUtility

    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()
        appSelf = this

        // for db
        mDBHelper = DBOrmLiteHelper(instance)
        mDUCamera = CameraDBUtility()
        mDULens = LensDBUtility()
        mDUDevice = DeviceDBUtility()
    }

    override fun onTerminate() {
        super.onTerminate()
        mDBHelper.close()
    }

    companion object {
        private var appSelf: ContextUtil? = null

        /**
         * 获取全局context
         * @return  全局context
         */
        val instance: ContextUtil
            get() = appSelf!!

        /**
         * 获得数据库辅助类
         * @return 数据库辅助类
         */
        val dbHelper: DBOrmLiteHelper
            get() = instance.mDBHelper

        /**
         * 获得camera辅助类
         * @return  辅助类
         */
        val duCamera: CameraDBUtility
            get() = instance.mDUCamera

        /**
         * 获得lens辅助类
         * @return  辅助类
         */
        val duLens: LensDBUtility
            get() = instance.mDULens

        /**
         * 获得device辅助类
         * @return  辅助类
         */
        val duDevice: DeviceDBUtility
            get() = instance.mDUDevice


        /**
         * get res string
         */
        fun getString(@StringRes resId : Int): String   {
            return instance.getString(resId)
        }

        /**
         * get res color
         */
        @ColorInt
        fun getColor(@ColorRes resId : Int): Int  {
            return instance.getColor(resId)
        }

        /**
         * get res drawable
         */
        fun getDrawable(@DrawableRes resId : Int): Drawable {
            return instance.getDrawable(resId)
        }

        /**
         * get dimension by pixel
         */
        fun getDimension(@DimenRes resId : Int): Float   {
            return instance.resources.getDimension(resId)
        }
    }
}
