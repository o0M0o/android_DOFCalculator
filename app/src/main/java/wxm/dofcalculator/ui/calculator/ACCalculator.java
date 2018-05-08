package wxm.dofcalculator.ui.calculator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.Switcher.ACSwitcherActivity;
import wxm.dofcalculator.define.GlobalDef;

/**
 * UI for device
 * Created by WangXM on2017/3/12.
 */
public class ACCalculator extends ACSwitcherActivity<FrgSupportBaseAdv> {
    // for invoke parameter
    public final static String KEY_DEVICE_ID  = "device_id";

    private FrgSupportBaseAdv  mFGPortraitHolder;
    private FrgSupportBaseAdv  mFGLandscapeHolder;

    @Override
    protected void setupFragment(Bundle savedInstanceState)    {
        Intent it = getIntent();
        if(null == it)
            return;

        int d_id = it.getIntExtra(KEY_DEVICE_ID, GlobalDef.INSTANCE.getINVAILD_ID());
        if(GlobalDef.INSTANCE.getINVAILD_ID() == d_id)
            return;

        mFGPortraitHolder = new FrgCalculatorPortrait();
        mFGLandscapeHolder = new FrgCalculatorLandscape();
        Bundle arg = new Bundle();
        arg.putInt(KEY_DEVICE_ID, d_id);
        mFGLandscapeHolder.setArguments(arg);
        mFGPortraitHolder.setArguments(arg);

        if(Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            addFragment(mFGLandscapeHolder);
            addFragment(mFGPortraitHolder);
        } else  {
            addFragment(mFGPortraitHolder);
            addFragment(mFGLandscapeHolder);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        getHotFragment().reInitUI();

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchToFragment(mFGLandscapeHolder);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            switchToFragment(mFGPortraitHolder);
        }
    }
}
