package wxm.dofcalculator.ui.device

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.Switcher.ACSwitcherActivity
import wxm.androidutil.util.UtilFun
import wxm.dofcalculator.R
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.ui.base.IFFrgEdit

/**
 * UI for device
 * Created by WangXM on2017/3/12.
 */
class ACDevice : ACSwitcherActivity<FrgSupportBaseAdv>() {
    private var mInvokeType = VAL_NULL

    override fun leaveActivity() {
        setResult(GlobalDef.RET_CANCEL, Intent())
        finish()
    }

    override fun setupFragment(savedInstanceState: Bundle?) {
        intent?.let {
            mInvokeType = it.getIntExtra(KEY_INVOKE_TYPE, VAL_NULL)
            when (mInvokeType) {
                VAL_DEVICE_ADD -> addFragment(FrgDeviceAdd())
                VAL_DEVICE_SELECTED -> addFragment(FrgDeviceSelect())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (VAL_DEVICE_ADD == mInvokeType) {
            val inflater = menuInflater
            inflater.inflate(R.menu.device_edit, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_save -> {
                val ie = UtilFun.cast_t<IFFrgEdit>(hotFragment)
                if (ie.onAccept()) {
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

    companion object {
        // for invoke parameter
        const val KEY_INVOKE_TYPE = "invoke_type"
        const val VAL_DEVICE_ADD = 1
        const val VAL_DEVICE_SELECTED = 2

        private const val VAL_NULL = -1
    }
}
