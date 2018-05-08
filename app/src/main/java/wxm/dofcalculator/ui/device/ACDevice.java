package wxm.dofcalculator.ui.device;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.Switcher.ACSwitcherActivity;
import wxm.androidutil.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.ui.base.IFFrgEdit;

/**
 * UI for device
 * Created by WangXM on2017/3/12.
 */
public class ACDevice extends ACSwitcherActivity<FrgSupportBaseAdv> {
    // for invoke parameter
    public final static String KEY_INVOKE_TYPE  = "invoke_type";
    public final static int VAL_DEVICE_ADD      = 1;
    public final static int VAL_DEVICE_SELECTED = 2;
    public final static int VAL_NULL            = -1;

    private int  mInvokeType = VAL_NULL;

    @Override
    protected void leaveActivity()  {
        int ret_data = GlobalDef.INSTANCE.getRET_CANCEL();

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void setupFragment(Bundle savedInstanceState)    {
        Intent it = getIntent();
        if(null == it)
            return;

        mInvokeType = it.getIntExtra(KEY_INVOKE_TYPE, VAL_NULL);
        switch (mInvokeType)    {
            case VAL_DEVICE_ADD :
                addFragment(new FrgDeviceAdd());
                break;

            case VAL_DEVICE_SELECTED :
                addFragment(new FrgDeviceSelect());
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        getHotFragment().reInitUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(VAL_DEVICE_ADD == mInvokeType) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.device_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                IFFrgEdit ie = UtilFun.cast_t(getHotFragment());
                if(ie.onAccept())   {
                    finish();
                }
            }
            break;

            case R.id.mi_giveup: {
                leaveActivity();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
