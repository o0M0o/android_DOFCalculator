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

    private SeekBar     mSBLensFocal;
    private SeekBar     mSBLensAperture;
    private SeekBar     mSBObjectDistance;

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

    private final static NavigableMap<Integer, Integer> mNMLensFocal = new TreeMap<>();
    private final static NavigableMap<Integer, BigDecimal> mNMObjectDistance = new TreeMap<>();
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
        mNMObjectDistance.put(0, BigDecimal.ZERO);
        mNMObjectDistance.put(15, BigDecimal.valueOf(0.1));
        mNMObjectDistance.put(30, BigDecimal.valueOf(1.5));
        mNMObjectDistance.put(45, BigDecimal.valueOf(2));
        mNMObjectDistance.put(60, BigDecimal.valueOf(3));
        mNMObjectDistance.put(75, BigDecimal.valueOf(5));
        mNMObjectDistance.put(90, BigDecimal.valueOf(10));
        mNMObjectDistance.put(100, BigDecimal.valueOf(25));
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
            switch ((int)seekBar.getTag())    {
                case TAG_LENS_FOCAL         :
                    onLensFocal(seekBar);
                    break;

                case TAG_LENS_APERTURE      :
                    onLensAperture(seekBar);
                    break;

                case TAG_OBJECT_DISTANCE    :
                    onObjDistanc(seekBar);
                    break;
            }

            updateResultUI();
        }


        /**
         * 处理lens focal
         * @param sb  for seekbar
         */
        private void onLensFocal(SeekBar sb) {
            int p = sb.getProgress();
            updateLensFocal(p);
        }

        /**
         * 处理lens aperture
         * @param sb  for seekbar
         */
        private void onLensAperture(SeekBar sb)    {
            int p = sb.getProgress();
            updateLensAperture(p);
        }

        /**
         * 处理lens aperture
         * @param sb  for seekbar
         */
        private void onObjDistanc(SeekBar sb)    {
            int p = sb.getProgress();
            updateObjectDistance(p);
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
        // for min/max tag
        int f_min = mNMLensFocal.floorEntry(0).getValue();
        String sz_f_min = String.format(Locale.CHINA, "%dmm", f_min);

        int f_max = mNMLensFocal.floorEntry(100).getValue();
        String sz_f_max = String.format(Locale.CHINA, "%dmm", f_max);
        mESBLensFocal.setMinTag(sz_f_min);
        mESBLensFocal.setMaxTag(sz_f_max);

        String fa_min = mNMLensAperture.floorEntry(0).getValue();
        String fa_max = mNMLensAperture.floorEntry(100).getValue();
        mESBLensAperture.setMinTag(fa_min);
        mESBLensAperture.setMaxTag(fa_max);

        BigDecimal d_min = mNMObjectDistance.floorEntry(0).getValue();
        String sz_min = String.format(Locale.CHINA, "%.02fm", d_min.floatValue());

        BigDecimal d_max = mNMObjectDistance.floorEntry(100).getValue();
        String sz_max = String.format(Locale.CHINA, "%.02fm", d_max.floatValue());
        mESBObjectDistance.setMinTag(sz_min);
        mESBObjectDistance.setMaxTag(sz_max);

        // for seekbar
        mSBLensFocal = mESBLensFocal.getSeekBar();
        mSBLensAperture = mESBLensAperture.getSeekBar();
        mSBObjectDistance = mESBObjectDistance.getSeekBar();

        mSBLensFocal.setTag(TAG_LENS_FOCAL);
        mSBLensAperture.setTag(TAG_LENS_APERTURE);
        mSBObjectDistance.setTag(TAG_OBJECT_DISTANCE);

        mSBLensAperture.setOnSeekBarChangeListener(mSBChangeListener);
        mSBLensFocal.setOnSeekBarChangeListener(mSBChangeListener);
        mSBObjectDistance.setOnSeekBarChangeListener(mSBChangeListener);

        // init show
        updateLensFocal(0);
        updateLensAperture(0);
        updateObjectDistance(0);
    }

    @Override
    protected void loadUI() {
        updateResultUI();
    }


    /**
     * 更新结果UI
     */
    protected void updateResultUI() {
        int lf_hot = mNMLensFocal.floorEntry(mSBLensFocal.getProgress()).getValue();
        String la_hot = mNMLensAperture.floorEntry(mSBLensAperture.getProgress()).getValue();
        BigDecimal od_hot = mNMObjectDistance.floorEntry(mSBObjectDistance.getProgress()).getValue();

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
     * 更新lens focal
     * @param progress  进度
     */
    private void updateLensFocal(int progress)   {
        int f_hot = mNMLensFocal.floorEntry(progress).getValue();
        String sz_hot = String.format(Locale.CHINA, "%dmm", f_hot);
        mESBLensFocal.setCurVal(sz_hot);
    }

    /**
     * 更新lens aperture
     * @param progress  进度
     */
    private void updateLensAperture(int progress)   {
        String sz_hot = mNMLensAperture.floorEntry(progress).getValue();
        mESBLensAperture.setCurVal(sz_hot);
    }

    /**
     * 更新object distance
     * @param progress  进度
     */
    private void updateObjectDistance(int progress)   {
        BigDecimal d_hot = mNMObjectDistance.floorEntry(progress).getValue();
        String sz_hot = String.format(Locale.CHINA, "%.02fm", d_hot.floatValue());
        mESBObjectDistance.setCurVal(sz_hot);
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
            mNMLensFocal.put(step_val * i, minFocal + step_focal * i);
        }
    }
}
