package wxm.dofcalculator.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import wxm.androidutil.DBHelper.DBUtilityBase;
import wxm.dofcalculator.define.CameraItem;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * CameraItem数据工具类
 * Created by WangXM on 2017/03/13.
 */
public class CameraDBUtility extends DBUtilityBase<CameraItem, Integer> {
    public CameraDBUtility()  {
        super();
    }

    @Override
    protected RuntimeExceptionDao<CameraItem, Integer> getDBHelper() {
        return ContextUtil.getDBHelper().getREDCamera();
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
