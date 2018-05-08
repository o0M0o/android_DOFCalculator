package wxm.dofcalculator.ui.calculator

import android.content.res.Configuration
import android.os.Bundle
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.Switcher.ACSwitcherActivity
import wxm.dofcalculator.define.GlobalDef

/**
 * UI for device
 * Created by WangXM on2017/3/12.
 */
class ACCalculator : ACSwitcherActivity<FrgSupportBaseAdv>() {
    private val mFGPortraitHolder: FrgSupportBaseAdv = FrgCalculatorPortrait()
    private val mFGLandscapeHolder: FrgSupportBaseAdv = FrgCalculatorLandscape()

    override fun setupFragment(savedInstanceState: Bundle?) {
        intent?.let {
            val dId = it.getIntExtra(KEY_DEVICE_ID, GlobalDef.INVAILD_ID)
            if (GlobalDef.INVAILD_ID == dId)
                return

            Bundle().let {
                it.putInt(KEY_DEVICE_ID, dId)
                mFGLandscapeHolder.arguments = it
                mFGPortraitHolder.arguments = it
            }

            if (Configuration.ORIENTATION_LANDSCAPE == resources.configuration.orientation) {
                addFragment(mFGLandscapeHolder)
                addFragment(mFGPortraitHolder)
            } else {
                addFragment(mFGPortraitHolder)
                addFragment(mFGLandscapeHolder)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchToFragment(mFGLandscapeHolder)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            switchToFragment(mFGPortraitHolder)
        }
    }

    companion object {
        // for invoke parameter
        const val KEY_DEVICE_ID = "device_id"
    }
}
