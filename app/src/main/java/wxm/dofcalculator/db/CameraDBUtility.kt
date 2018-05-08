package wxm.dofcalculator.db

import com.j256.ormlite.dao.RuntimeExceptionDao


import org.greenrobot.eventbus.EventBus

import wxm.androidutil.DBHelper.DBUtilityBase
import wxm.dofcalculator.define.CameraItem
import wxm.dofcalculator.utility.ContextUtil

/**
 * camera db utility
 * Created by WangXM on 2017/03/13.
 */
class CameraDBUtility : DBUtilityBase<CameraItem, Int>() {

    override fun getDBHelper(): RuntimeExceptionDao<CameraItem, Int> {
        return ContextUtil.dbHelper.redCamera
    }


    override fun onDataModify(md: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataCreate(cd: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataRemove(dd: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }
}
