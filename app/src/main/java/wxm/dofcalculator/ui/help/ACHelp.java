package wxm.dofcalculator.ui.help;


import android.os.Bundle;

import wxm.androidutil.Switcher.ACSwitcherActivity;

/**
 * help UI
 */
public class ACHelp extends ACSwitcherActivity<FrgHelp> {
    public static final String STR_HELP_TYPE            = "HELP_TYPE";
    public static final String STR_HELP_START           = "help_start";

    @Override
    protected void initUi(Bundle savedInstanceState)    {
        super.initUi(savedInstanceState);
        addFragment(new FrgHelp());
    }
}
