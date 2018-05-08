package wxm.dofcalculator.ui.setting.page


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.Locale

import kotterknife.bindView
import wxm.androidutil.util.PackageUtil
import wxm.dofcalculator.R
import wxm.dofcalculator.define.GlobalDef

/**
 * 检查版本设置页面
 * Created by WangXM on2016/10/10.
 */
class TFSettingCheckVersion : TFSettingBase() {
    private val mTVVerNumber: TextView by bindView(R.id.tv_ver_number)
    private val mTVVerName: TextView by bindView(R.id.tv_ver_name)

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.page_setting_version, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (null != view) {
            mTVVerNumber.text = String.format(Locale.CHINA, "当前版本号 : %d",
                    PackageUtil.getVerCode(context, GlobalDef.PACKAGE_NAME))

            mTVVerName.text = String.format(Locale.CHINA, "当前版本名 : %s",
                    PackageUtil.getVerName(context, GlobalDef.PACKAGE_NAME))
        }
    }

    override fun updateSetting() {
        if (isSettingDirty) {
            isSettingDirty = false
        }
    }
}
