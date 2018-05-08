package wxm.dofcalculator.ui.start;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.dofcalculator.R;

import wxm.dofcalculator.ui.device.ACDevice;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
 * Created by WangXM on2017/3/11.
 */
public class FrgStart extends FrgSupportBaseAdv {
    @BindView(R.id.bt_device_add)
    Button mBTDeviceAdd;

    @BindView(R.id.bt_device_select)
    Button mBTDeviceSelect;

    @Override
    protected int getLayoutID() {
        return R.layout.frg_start;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle) {
        mBTDeviceSelect.setVisibility(0 == ContextUtil.Companion.getDuDevice().getCount()
                        ? View.GONE : View.VISIBLE);
    }

    @OnClick({R.id.bt_device_add, R.id.bt_device_select})
    public void onBTClickForDevice(View v) {
        int vid = v.getId();
        Intent it = new Intent(getActivity(), ACDevice.class);
        switch (vid) {
            case R.id.bt_device_add: {
                it.putExtra(ACDevice.KEY_INVOKE_TYPE, ACDevice.VAL_DEVICE_ADD);
            }
            break;

            case R.id.bt_device_select: {
                it.putExtra(ACDevice.KEY_INVOKE_TYPE, ACDevice.VAL_DEVICE_SELECTED);
            }
            break;
        }

        startActivity(it);
    }
}
