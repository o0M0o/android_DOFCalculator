package wxm.dofcalculator.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.wxm.andriodutillib.DBHelper.DBUtilityBase;
import wxm.dofcalculator.define.CameraItem;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * CameraItem数据工具类
 * Created by WangXM on 2017/03/13.
 */
public class LensDBUtility extends DBUtilityBase<LensItem, Integer> {
    public LensDBUtility()  {
        super();
    }

    @Override
    protected RuntimeExceptionDao<LensItem, Integer> getDBHelper() {
        return ContextUtil.getDBHelper().getREDLens();
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
