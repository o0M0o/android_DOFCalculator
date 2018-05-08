package wxm.dofcalculator.ui.setting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.androidutil.Switcher.ACSwitcherActivity;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.ui.setting.page.TFSettingBase;
import wxm.dofcalculator.ui.setting.page.TFSettingCheckVersion;
import wxm.dofcalculator.ui.setting.page.TFSettingMain;

/**
 * for app setting
 */
public class ACSetting extends ACSwitcherActivity<TFSettingBase> {
    private TFSettingCheckVersion   mTFCheckVersion = new TFSettingCheckVersion();
    private TFSettingMain mTFMain         = new TFSettingMain();

    @Override
    protected void leaveActivity() {
        if(mTFMain != getHotFragment())    {
            switchToFragment(mTFMain);
        } else {
            int ret_data = GlobalDef.INSTANCE.getRET_CANCEL();
            Intent data = new Intent();
            setResult(ret_data, data);
            finish();
        }
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addFragment(mTFMain);
        addFragment(mTFCheckVersion);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_giveup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if(mTFMain != getHotFragment())    {
                    final TFSettingBase tb = getHotFragment();
                    if(tb.isSettingDirty()) {
                        Dialog alertDialog = new AlertDialog.Builder(this).
                                setTitle("配置已经更改").
                                setMessage("是否保存更改的配置?").
                                setPositiveButton("是", (dialog, which) -> {
                                    tb.updateSetting();
                                    switchToFragment(mTFMain);
                                }).
                                setNegativeButton("否", (dialog, which) -> switchToFragment(mTFMain)).
                                create();
                        alertDialog.show();
                    } else  {
                        switchToFragment(mTFMain);
                    }
                } else  {
                    int ret_data = GlobalDef.INSTANCE.getRET_SURE();
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup:    {
                leaveActivity();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    public void switchToCheckVersionPage()  {
        switchToFragment(mTFCheckVersion);
    }
}
