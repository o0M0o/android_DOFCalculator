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
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.ui.calculator.event.AttrChangedEvent;
import wxm.dofcalculator.ui.calculator.event.CameraSettingChangeEvent;
import wxm.dofcalculator.ui.calculator.view.VWCameraAdjust;
import wxm.dofcalculator.ui.calculator.view.VWDof;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
 * Created by WangXM on2017/3/11.
 */
public class FrgCalculatorPortrait extends FrgUtilitySupportBase {
    @BindView(R.id.evw_dof)
    VWDof mEVWDof;

    @BindView(R.id.eca_adjust)
    VWCameraAdjust mEVWCamera;

    private DeviceItem mDICurDevice;

    public FrgCalculatorPortrait() {
        super();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 设置变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAttrChangeEvent(AttrChangedEvent event) {
        if(isVisible()) {
            updateResultUI();
        }
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.frg_calculator_portrait, viewGroup, false);

        int d_id = getArguments().getInt(ACCalculator.KEY_DEVICE_ID, GlobalDef.INT_INVAILED_ID);
        if(GlobalDef.INT_INVAILED_ID != d_id)   {
            mDICurDevice = ContextUtil.getDUDevice().getData(d_id);
        }

        return rootView;
    }

    @Override
    protected void loadUI(Bundle savedInstanceState) {
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
