package wxm.dofcalculator.ui.calculator;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.ui.calculator.event.AttrChangedEvent;
import wxm.dofcalculator.ui.calculator.event.CameraSettingChangeEvent;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
 * Created by wxm on 2017/3/11.
 */
public class FrgCalculatorPortrait extends FrgUtilityBase {
    @BindView(R.id.evw_dof)
    VWDof           mEVWDof;

    @BindView(R.id.eca_adjust)
    VWCameraAdjust  mEVWCamera;

    private DeviceItem mDICurDevice;

    @Override
    protected void enterActivity()  {
        super.enterActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity()  {
        EventBus.getDefault().unregister(this);
        super.leaveActivity();
    }

    /**
     * 设置变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttrChangeEvent(AttrChangedEvent event) {
        updateResultUI();
    }


    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgCalculatorPortrait";
        View rootView = layoutInflater.inflate(R.layout.frg_calculator_portrait, viewGroup, false);
        ButterKnife.bind(this, rootView);

        int d_id = getArguments().getInt(ACCalculator.KEY_DEVICE_ID, GlobalDef.INT_INVAILED_ID);
        if(GlobalDef.INT_INVAILED_ID != d_id)   {
            mDICurDevice = ContextUtil.getDUDevice().getData(d_id);
        }

        if(null != mDICurDevice)    {
            LensItem li = mDICurDevice.getLens();
        }
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
    }

    @Override
    protected void loadUI() {
        //updateResultUI();
    }


    /**
     * 更新结果UI
     */
    protected void updateResultUI() {
        String sz_lf_hot = mEVWCamera.getCurLensFocal();
        String sz_la_hot = mEVWCamera.getCurLensAperture();
        int od = mEVWCamera.getCurObjectDistance();

        int lf_hot = Integer.valueOf(sz_lf_hot.substring(0, sz_lf_hot.indexOf("mm")));
        BigDecimal aperture = new BigDecimal(sz_la_hot.substring(sz_la_hot.indexOf("F") + 1));

        EventBus.getDefault().post(new CameraSettingChangeEvent(lf_hot, aperture, od, mDICurDevice));
    }
}