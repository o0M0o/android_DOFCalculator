package wxm.dofcalculator.ui.calculator;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.ui.calculator.extend.CameraSettingChangeEvent;
import wxm.dofcalculator.ui.calculator.extend.DOFVW;
import wxm.dofcalculator.ui.calculator.extend.DofChangedEvent;
import wxm.dofcalculator.ui.calculator.extend.SeekbarChangedEvent;
import wxm.dofcalculator.ui.calculator.extend.SeekbarVW;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
 * Created by wxm on 2017/3/11.
 */
public class FrgCalculator extends FrgUtilityBase {
    // for seekbar
    private final static int    TAG_LENS_FOCAL      = 1;
    private final static int    TAG_LENS_APERTURE   = 2;
    private final static int    TAG_OBJECT_DISTANCE = 3;

    // seekbar ui
    @BindView(R.id.esb_lens_focal)
    SeekbarVW   mESBLensFocal;

    @BindView(R.id.esb_lens_aperture)
    SeekbarVW   mESBLensAperture;

    @BindView(R.id.esb_object_distance)
    SeekbarVW   mESBObjectDistance;

    @BindView(R.id.evw_dof)
    DOFVW       mEVWDOf;

    private DeviceItem mDICurDevice;

    private final static NavigableMap<Integer, String> mNMLensFocal = new TreeMap<>();
    private final static NavigableMap<Integer, String> mNMObjectDistance = new TreeMap<>();
    private final static NavigableMap<Integer, String> mNMLensAperture = new TreeMap<>();
    static {
        // for lens aperture
        mNMLensAperture.put(0, "F/1.2");
        mNMLensAperture.put(8, "F/1.4");
        mNMLensAperture.put(16, "F/2.0");
        mNMLensAperture.put(24, "F/2.8");
        mNMLensAperture.put(32, "F/4.0");
        mNMLensAperture.put(40, "F/5.6");
        mNMLensAperture.put(48, "F/8.0");
        mNMLensAperture.put(56, "F/11");
        mNMLensAperture.put(64, "F/16");
        mNMLensAperture.put(72, "F/22");
        mNMLensAperture.put(80, "F/32");
        mNMLensAperture.put(88, "F/44");
        mNMLensAperture.put(100, "F/64");

        // for object distance
        mNMObjectDistance.put(0, "0m");
        mNMObjectDistance.put(10, "0.4m");
        mNMObjectDistance.put(20, "6m");
        mNMObjectDistance.put(30, "8m");
        mNMObjectDistance.put(40, "12m");
        mNMObjectDistance.put(50, "20m");
        mNMObjectDistance.put(60, "30m");
        mNMObjectDistance.put(70, "40m");
        mNMObjectDistance.put(80, "50m");
        mNMObjectDistance.put(90, "75m");
        mNMObjectDistance.put(100, "100m");
    }

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
    public void onSeekBarChangeEvent(SeekbarChangedEvent event) {
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
        // for data
        mESBLensFocal.setSeekbarMap(mNMLensFocal);
        mESBLensAperture.setSeekbarMap(mNMLensAperture);
        mESBObjectDistance.setSeekbarMap(mNMObjectDistance);

        // for seekbar
        mESBLensFocal.getSeekBar().setTag(TAG_LENS_FOCAL);
        mESBLensAperture.getSeekBar().setTag(TAG_LENS_APERTURE);
        mESBObjectDistance.getSeekBar().setTag(TAG_OBJECT_DISTANCE);
    }

    @Override
    protected void loadUI() {
        updateResultUI();
    }


    /**
     * 更新结果UI
     */
    protected void updateResultUI() {
        String sz_lf_hot = mESBLensFocal.getCurVal();
        String la_hot = mESBLensAperture.getCurVal();

        int lf_hot = Integer.valueOf(sz_lf_hot.substring(0, sz_lf_hot.indexOf("mm")));
        BigDecimal pa = mDICurDevice.getCamera().getPixelArea();
        BigDecimal aperture = new BigDecimal(la_hot.substring(la_hot.indexOf("/") + 1));

        EventBus.getDefault().post(new CameraSettingChangeEvent(pa, lf_hot, aperture));
    }


    /**
     * 初始化镜头焦距步进值
     * @param minFocal      最小焦距
     * @param maxFocal      最大焦距
     */
    private void initLensFocalTMap(int minFocal, int maxFocal)  {
        mNMLensFocal.clear();
        int step_val = 10;
        int step_count = 10;
        int step_focal = (maxFocal - minFocal)/ step_count;
        for(int i = 0; i < step_count; ++i) {
            mNMLensFocal.put(step_val * i, Integer.toString(minFocal + step_focal * i) + "mm");
        }
    }
}
