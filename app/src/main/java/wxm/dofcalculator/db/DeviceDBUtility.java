package wxm.dofcalculator.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import wxm.androidutil.DBHelper.DBUtilityBase;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * device db utility
 * Created by WangXM on 2017/03/13.
 */
public class DeviceDBUtility extends DBUtilityBase<DeviceItem, Integer> {
    public DeviceDBUtility()  {
        super();
    }

    public int getCount()   {
        return getAllData().size();
    }

    @Override
    protected RuntimeExceptionDao<DeviceItem, Integer> getDBHelper() {
        return ContextUtil.getDBHelper().getREDDevice();
    }


    @Override
    protected void onDataModify(List<Integer> md) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }

    @Override
    protected void onDataCreate(List<Integer> cd) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }

    @Override
    protected void onDataRemove(List<Integer> dd) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }
}
