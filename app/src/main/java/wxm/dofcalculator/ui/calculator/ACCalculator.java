package wxm.dofcalculator.ui.calculator;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.ExActivity.BaseAppCompatActivity;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.ui.base.IFFrgEdit;
import wxm.dofcalculator.ui.device.FrgDeviceAdd;
import wxm.dofcalculator.ui.device.FrgDeviceSelect;

/**
 * UI for device
 * Created by wxm on 2017/3/12.
 */
public class ACCalculator extends BaseAppCompatActivity {
    // for invoke parameter
    public final static String KEY_DEVICE_ID  = "device_id";

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

        Bundle arg = new Bundle();
        arg.putInt(KEY_DEVICE_ID, d_id);
        mFGHolder = new FrgCalculator();
        mFGHolder.setArguments(arg);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        ((FrgUtilityBase)mFGHolder).refreshUI();
    }
}
