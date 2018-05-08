package wxm.dofcalculator.db

import com.j256.ormlite.dao.RuntimeExceptionDao

import org.greenrobot.eventbus.EventBus

import wxm.androidutil.DBHelper.DBUtilityBase
import wxm.dofcalculator.define.LensItem
import wxm.dofcalculator.utility.ContextUtil

/**
 * lens db utility
 * Created by WangXM on 2017/03/13.
 */
class LensDBUtility : DBUtilityBase<LensItem, Int>() {
    override fun getDBHelper(): RuntimeExceptionDao<LensItem, Int> {
        return ContextUtil.dbHelper.redLens
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
