package wxm.dofcalculator.ui.calculator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.androidutil.Switcher.ACRxSwitcherActivity;
import wxm.androidutil.Switcher.ACSwitcherActivity;
import wxm.dofcalculator.define.GlobalDef;

/**
 * UI for device
 * Created by WangXM on2017/3/12.
 */
public class ACCalculator extends ACSwitcherActivity<FrgUtilitySupportBase> {
    // for invoke parameter
    public final static String KEY_DEVICE_ID  = "device_id";

    private FrgUtilitySupportBase  mFGPortraitHolder;
    private FrgUtilitySupportBase  mFGLandscapeHolder;

    @Override
    protected void setupFragment(Bundle savedInstanceState)    {
        Intent it = getIntent();
        if(null == it)
            return;

        int d_id = it.getIntExtra(KEY_DEVICE_ID, GlobalDef.INT_INVAILED_ID);
        if(GlobalDef.INT_INVAILED_ID == d_id)
            return;

        mFGPortraitHolder = new FrgCalculatorPortrait();
        mFGLandscapeHolder = new FrgCalculatorLandscape();
        Bundle arg = new Bundle();
        arg.putInt(KEY_DEVICE_ID, d_id);
        mFGLandscapeHolder.setArguments(arg);
        mFGPortraitHolder.setArguments(arg);

        addFragment(mFGPortraitHolder);
        addFragment(mFGLandscapeHolder);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        getHotFragment().refreshUI();

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchToFragment(mFGLandscapeHolder);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            switchToFragment(mFGPortraitHolder);
        }
    }
}
