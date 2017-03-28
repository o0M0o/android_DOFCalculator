package wxm.dofcalculator.ui.start;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.dofcalculator.R;

import wxm.dofcalculator.ui.device.ACDevice;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
 * Created by wxm on 2017/3/11.
 */
public class FrgStart extends FrgUtilityBase {

    @BindView(R.id.bt_device_add)
    Button mBTDeviceAdd;

    @BindView(R.id.bt_device_select)
    Button mBTDeviceSelect;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgStart";
        View rootView = layoutInflater.inflate(R.layout.frg_start, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
    }

    @Override
    protected void loadUI() {
        mBTDeviceSelect.setVisibility(0 == ContextUtil.getDUDevice().getCount() ?
                View.GONE
                : View.VISIBLE);
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
