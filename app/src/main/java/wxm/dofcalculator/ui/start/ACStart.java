package wxm.dofcalculator.ui.start;

import android.content.res.Configuration;

import cn.wxm.andriodutillib.ExActivity.BaseAppCompatActivity;

/**
 * UI for App start
 * Created by wxm on 2017/3/12.
 */
public class ACStart extends BaseAppCompatActivity {
    private FrgStart    mFGStart = new FrgStart();

    @Override
    protected void leaveActivity() {
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACStart";
        mFGHolder = mFGStart;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        mFGStart.refreshUI();
    }
}
