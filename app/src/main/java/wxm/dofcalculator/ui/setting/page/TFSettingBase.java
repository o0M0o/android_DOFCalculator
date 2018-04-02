package wxm.dofcalculator.ui.setting.page;

import android.content.Context;
import android.support.v4.app.Fragment;

import wxm.androidutil.util.UtilFun;

/**
 * 设置页面基础类
 * Created by WangXM on2016/10/10.
 */
public abstract class TFSettingBase extends Fragment {
    protected boolean   mBSettingDirty = false;

    /**
     * 页面所管理的配置是否更改
     * @return  若配置已经更改则返回true, 否则返回false
     */
    public boolean isSettingDirty() {
        return mBSettingDirty;
    }

    /**
     * 保存页面所管理的配置
     */
    public abstract void updateSetting();
}
