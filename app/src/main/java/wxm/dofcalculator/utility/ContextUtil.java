package wxm.dofcalculator.utility;

import android.app.Application;

import wxm.dofcalculator.db.CameraDBUtility;
import wxm.dofcalculator.db.DBOrmLiteHelper;
import wxm.dofcalculator.db.DeviceDBUtility;
import wxm.dofcalculator.db.LensDBUtility;

/**
 * 获取全局context
 * 获取全局辅助类
 * Created by 123 on 2016/5/7.
 */
public class ContextUtil extends Application {
    private static final String TAG = "ContextUtil";
    private static ContextUtil instance;

    // mainly for sqlite
    private DBOrmLiteHelper mDBHelper;
    private CameraDBUtility mDUCamera;
    private LensDBUtility   mDULens;
    private DeviceDBUtility mDUDevice;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance    = this;

        // for db
        mDBHelper = new DBOrmLiteHelper(getInstance());
        mDUCamera = new CameraDBUtility();
        mDULens   = new LensDBUtility();
        mDUDevice = new DeviceDBUtility();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // for db
        if(null != mDBHelper) {
            mDBHelper.close();
            mDBHelper = null;
        }

        mDUCamera = null;
        mDULens   = null;
        mDUDevice = null;
    }

    /**
     * 获取全局context
     * @return  全局context
     */
    public static ContextUtil getInstance() {
        return instance;
    }

    /**
     * 获得数据库辅助类
     * @return 数据库辅助类
     */
    public static DBOrmLiteHelper getDBHelper()    {
        return getInstance().mDBHelper;
    }

    /**
     * 获得camera辅助类
     * @return  辅助类
     */
    public static CameraDBUtility getDUCamera()  {
        return  getInstance().mDUCamera;
    }

    /**
     * 获得lens辅助类
     * @return  辅助类
     */
    public static LensDBUtility getDULens()  {
        return  getInstance().mDULens;
    }

    /**
     * 获得device辅助类
     * @return  辅助类
     */
    public static DeviceDBUtility getDUDevice()  {
        return  getInstance().mDUDevice;
    }
}
