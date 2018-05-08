package wxm.dofcalculator.ui.help

import android.os.Bundle

import wxm.androidutil.FrgWebView.FrgSupportWebView

/**
 * for help
 * Created by WangXM on2016/11/29.
 */

class FrgHelp : FrgSupportWebView() {
    override fun loadUI(bundle: Bundle?) {
        loadPage("file:///android_asset/help_main.html", null)
    }
}
