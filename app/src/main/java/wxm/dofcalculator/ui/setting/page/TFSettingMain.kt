package wxm.dofcalculator.ui.setting.page


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wxm.dofcalculator.R
import wxm.dofcalculator.ui.base.EventHelper
import wxm.dofcalculator.ui.setting.ACSetting

/**
 * 设置主页面
 * Created by WangXM on2016/10/10.
 */
class TFSettingMain : TFSettingBase() {

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.page_setting_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (null != view) {
            EventHelper.setOnClickOperator(view, intArrayOf(R.id.rl_check_version),
                    { v ->
                        when (v.id) {
                            R.id.rl_check_version -> {
                                (activity as ACSetting).switchToCheckVersionPage()
                            }
                        }
                    })
        }
    }

    override fun updateSetting() {
        if (isSettingDirty) {
            isSettingDirty = false
        }
    }
}
