package wxm.dofcalculator.ui.setting.page

import android.content.Context
import android.support.v4.app.Fragment

import wxm.androidutil.util.UtilFun

/**
 * 设置页面基础类
 * Created by WangXM on2016/10/10.
 */
abstract class TFSettingBase : Fragment() {
    /**
     * 页面所管理的配置是否更改
     * @return  若配置已经更改则返回true, 否则返回false
     */
    var isSettingDirty = false
        protected set

    /**
     * 保存页面所管理的配置
     */
    abstract fun updateSetting()
}
