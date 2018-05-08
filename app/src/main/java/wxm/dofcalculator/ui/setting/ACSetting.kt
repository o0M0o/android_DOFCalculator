package wxm.dofcalculator.ui.setting

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem

import wxm.androidutil.Switcher.ACSwitcherActivity
import wxm.dofcalculator.R
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.ui.setting.page.TFSettingBase
import wxm.dofcalculator.ui.setting.page.TFSettingCheckVersion
import wxm.dofcalculator.ui.setting.page.TFSettingMain

/**
 * for app setting
 */
class ACSetting : ACSwitcherActivity<TFSettingBase>() {
    private val mTFCheckVersion = TFSettingCheckVersion()
    private val mTFMain = TFSettingMain()

    override fun leaveActivity() {
        if (mTFMain !== hotFragment) {
            switchToFragment(mTFMain)
        } else {
            setResult(GlobalDef.RET_CANCEL, Intent())
            finish()
        }
    }

    override fun setupFragment(bundle: Bundle?) {
        addFragment(mTFMain)
        addFragment(mTFCheckVersion)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.save_giveup, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_save -> {
                if (mTFMain !== hotFragment) {
                    val tb = hotFragment
                    if (tb.isSettingDirty) {
                        AlertDialog.Builder(this)
                                .setTitle("配置已经更改").setMessage("是否保存更改的配置?")
                                .setPositiveButton("是") { _, _ ->
                                    tb.updateSetting()
                                    switchToFragment(mTFMain)
                                }
                                .setNegativeButton("否") { _, _ ->
                                    switchToFragment(mTFMain)
                                }
                                .create().show()
                    } else {
                        switchToFragment(mTFMain)
                    }
                } else {
                    setResult(GlobalDef.RET_SURE, Intent())
                    finish()
                }
            }

            R.id.mi_giveup -> {
                leaveActivity()
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    fun switchToCheckVersionPage() {
        switchToFragment(mTFCheckVersion)
    }
}
