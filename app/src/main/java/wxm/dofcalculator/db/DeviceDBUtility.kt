package wxm.dofcalculator.db

import com.j256.ormlite.dao.RuntimeExceptionDao

import org.greenrobot.eventbus.EventBus

import wxm.androidutil.DBHelper.DBUtilityBase
import wxm.dofcalculator.define.DeviceItem
import wxm.dofcalculator.utility.ContextUtil

/**
 * device db utility
 * Created by WangXM on 2017/03/13.
 */
class DeviceDBUtility : DBUtilityBase<DeviceItem, Int>() {

    val count: Int
        get() = allData.size

    override fun getDBHelper(): RuntimeExceptionDao<DeviceItem, Int> {
        return ContextUtil.dbHelper.redDevice
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
