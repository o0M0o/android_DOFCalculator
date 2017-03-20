package wxm.dofcalculator.ui.calculator;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

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
import wxm.dofcalculator.ui.extend.SeekbarVW;
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

    // result ui
    @BindView(R.id.tv_front_focal_val)
    TextView    mTVFrontFocal;

    @BindView(R.id.tv_back_focal_val)
    TextView    mTVBackFocal;

    @BindView(R.id.tv_total_focal_val)
    TextView    mTVTotalFocal;

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
        mNMObjectDistance.put(15, "0.1m");
        mNMObjectDistance.put(30, "1.5m");
        mNMObjectDistance.put(45, "2m");
        mNMObjectDistance.put(60, "3m");
        mNMObjectDistance.put(75, "5m");
        mNMObjectDistance.put(90, "10m");
        mNMObjectDistance.put(100, "25m");
    }


    private SeekBar.OnSeekBarChangeListener mSBChangeListener = new SeekBar.OnSeekBarChangeListener()   {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            updateResultUI();
        }
    };

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

        // for extend listner
        mESBLensFocal.setSeekbarExtendListner(mSBChangeListener);
        mESBLensAperture.setSeekbarExtendListner(mSBChangeListener);
        mESBObjectDistance.setSeekbarExtendListner(mSBChangeListener);

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
        String sz_od_hot = mESBObjectDistance.getCurVal();
        String la_hot = mESBLensAperture.getCurVal();

        int lf_hot = Integer.valueOf(sz_lf_hot.substring(0, sz_lf_hot.indexOf("mm")));
        BigDecimal od_hot = new BigDecimal(sz_od_hot.substring(0, sz_od_hot.indexOf("m")));

        BigDecimal aperture = new BigDecimal(la_hot.substring(la_hot.indexOf("/") + 1));
        BigDecimal focal = new BigDecimal(lf_hot);

        //hyperFocal = (focal * focal) / (aperture * CoC) + focal;
        BigDecimal ff = new BigDecimal(lf_hot * lf_hot);
        BigDecimal ac = aperture.multiply(mDICurDevice.getCamera().getPixelArea());
        BigDecimal hyperFocal =  ff.divide(ac, RoundingMode.CEILING)
                                    .add(new BigDecimal(lf_hot));

        // change to unit mm
        BigDecimal od_mm = od_hot.multiply(new BigDecimal(1000));

        // dofNear = ((hyperFocal - focal) * distance) / (hyperFocal + distance - (2*focal));
        BigDecimal dofNear_f = hyperFocal.subtract(focal).multiply(od_mm);
        BigDecimal dofNear_b = hyperFocal.add(od_mm).subtract(focal.add(focal));
        BigDecimal dofNear = dofNear_f.divide(dofNear_b, RoundingMode.CEILING);

        // Prevent 'divide by zero' when calculating far distance.
        BigDecimal dofFar;
        if(Math.abs(hyperFocal.subtract(od_mm).floatValue()) <= 0.00001)    {
            dofFar = new BigDecimal(10000000);
        } else  {
            BigDecimal f = hyperFocal.subtract(focal).multiply(od_mm);
            BigDecimal b = hyperFocal.subtract(od_mm);
            dofFar = f.divide(b, RoundingMode.CEILING);
        }

        // change to unit m
        dofNear = dofNear.divide(new BigDecimal(1000), RoundingMode.CEILING);
        dofFar = dofFar.divide(new BigDecimal(1000), RoundingMode.CEILING);
        BigDecimal dofTotal = dofFar.subtract(dofNear);

        // updat ui
        mTVFrontFocal.setText(String.format(Locale.CHINA, "%.02f米", dofNear.floatValue()));
        mTVBackFocal.setText(String.format(Locale.CHINA, "%.02f米", dofFar.floatValue()));
        mTVTotalFocal.setText(String.format(Locale.CHINA, "%.02f米", dofTotal.floatValue()));
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
