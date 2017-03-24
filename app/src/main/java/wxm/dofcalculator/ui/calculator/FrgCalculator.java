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
import wxm.dofcalculator.ui.calculator.extend.AttrChangedEvent;
import wxm.dofcalculator.ui.calculator.extend.CameraSettingChangeEvent;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
 * Created by wxm on 2017/3/11.
 */
public class FrgCalculator extends FrgUtilityBase {
    // for seekbar
    private final static int    TAG_LENS_FOCAL      = 1;
    private final static int    TAG_LENS_APERTURE   = 2;

    @BindView(R.id.evw_dof)
    VWDof mEVWDOf;

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
     * SeekBar变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSeekBarChangeEvent(AttrChangedEvent event) {
        updateResultUI();
    }


    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgCalculator";
        View rootView = layoutInflater.inflate(R.layout.frg_calculator, viewGroup, false);
        ButterKnife.bind(this, rootView);

        int d_id = getArguments().getInt(ACCalculator.KEY_DEVICE_ID, GlobalDef.INT_INVAILED_ID);
        if(GlobalDef.INT_INVAILED_ID != d_id)   {
            mDICurDevice = ContextUtil.getDUDevice().getData(d_id);
        }

        if(null != mDICurDevice)    {
            LensItem li = mDICurDevice.getLens();
            initLensFocalTMap(li.getMinFocal(), li.getMaxFocal());
        }
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
    }

    @Override
    protected void loadUI() {
        updateResultUI();
    }


    /**
     * 更新结果UI
     */
    protected void updateResultUI() {
        String sz_lf_hot = mEVWCamera.getCurLensFocal();
        String sz_la_hot = mEVWCamera.getCurLensAperture();

        int lf_hot = Integer.valueOf(sz_lf_hot.substring(0, sz_lf_hot.indexOf("mm")));
        BigDecimal pa = mDICurDevice.getCamera().getPixelArea();
        BigDecimal aperture = new BigDecimal(sz_la_hot.substring(sz_la_hot.indexOf("/") + 1));

        EventBus.getDefault().post(new CameraSettingChangeEvent(pa, lf_hot, aperture));
    }


    /**
     * 初始化镜头焦距步进值
     * @param minFocal      最小焦距
     * @param maxFocal      最大焦距
     */
    private void initLensFocalTMap(int minFocal, int maxFocal)  {
        /*
        mNMLensFocal.clear();
        int step_val = 10;
        int step_count = 10;
        int step_focal = (maxFocal - minFocal)/ step_count;
        for(int i = 0; i < step_count; ++i) {
            mNMLensFocal.put(step_val * i, Integer.toString(minFocal + step_focal * i) + "mm");
        }
        */
    }
}
