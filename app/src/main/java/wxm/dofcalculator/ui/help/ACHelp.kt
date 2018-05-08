package wxm.dofcalculator.ui.help


import android.os.Bundle

import wxm.androidutil.Switcher.ACSwitcherActivity

/**
 * help UI
 */
class ACHelp : ACSwitcherActivity<FrgHelp>() {
    override fun setupFragment(bundle: Bundle?) {
        addFragment(FrgHelp())
    }

    companion object {
        const val STR_HELP_TYPE = "HELP_TYPE"
        const val STR_HELP_START = "help_start"
    }
}
