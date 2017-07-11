package wxm.dofcalculator.ui.calculator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import wxm.androidutil.ExActivity.BaseAppCompatActivity;
import wxm.androidutil.FrgUtility.FrgUtilityBase;
import wxm.dofcalculator.define.GlobalDef;

/**
 * UI for device
 * Created by wxm on 2017/3/12.
 */
public class ACCalculator extends BaseAppCompatActivity {
    // for invoke parameter
    public final static String KEY_DEVICE_ID  = "device_id";

    private FrgUtilityBase  mFGPortraitHolder;
    private FrgUtilityBase  mFGLandscapeHolder;

    @Override
    protected void leaveActivity() {
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACCalculator";

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

        mFGHolder = mFGPortraitHolder;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        ((FrgUtilityBase)mFGHolder).refreshUI();

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            swapFrg(mFGLandscapeHolder);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            swapFrg(mFGPortraitHolder);
        }
    }
}
