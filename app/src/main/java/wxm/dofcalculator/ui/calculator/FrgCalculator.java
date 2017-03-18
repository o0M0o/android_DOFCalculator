package wxm.dofcalculator.ui.calculator;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.ui.device.ACDevice;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
 * Created by wxm on 2017/3/11.
 */
public class FrgCalculator extends FrgUtilityBase {

    @BindView(R.id.sb_lens_focal)
    SeekBar     mSBLensFocal;

    @BindView(R.id.sb_lens_aperture)
    SeekBar     mSBLensAperture;

    @BindView(R.id.sb_object_distance)
    SeekBar     mSBObjectDistance;

    @BindView(R.id.tv_lens_aperture_val)
    TextView    mTVLensAperture;

    @BindView(R.id.tv_object_distance_val)
    TextView    mTVObjectDistance;

    @BindView(R.id.tv_lens_focal_val)
    TextView    mTVLensFocal;

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
            switch (seekBar.getId())    {
                case R.id.sb_lens_focal :
                    onLensFocal(seekBar);
                    break;

                case R.id.sb_lens_aperture :
                    onLensAperture(seekBar);
                    break;

                case R.id.sb_object_distance :
                    onObjDistanc(seekBar);
                    break;
            }
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
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        mSBLensAperture.setOnSeekBarChangeListener(mSBChangeListener);
        mSBLensFocal.setOnSeekBarChangeListener(mSBChangeListener);
        mSBObjectDistance.setOnSeekBarChangeListener(mSBChangeListener);

        int d_id = getArguments().getInt(ACCalculator.KEY_DEVICE_ID, GlobalDef.INT_INVAILED_ID);
        if(GlobalDef.INT_INVAILED_ID != d_id)   {
            mDICurDevice = ContextUtil.getDUDevice().getData(d_id);
        }

        if(null != mDICurDevice)    {
            LensItem li = mDICurDevice.getLens();
            initLensFocalTMap(li.getMinFocal(), li.getMaxFocal());
        }

        // init show
        updateLensFocal(0);
        updateLensAperture(0);
        updateObjectDistance(0);
    }

    @Override
    protected void loadUI() {
    }


    /**
     * 更新lens focal
     * @param progress  进度
     */
    private void updateLensFocal(int progress)   {
        int f_hot = mNMLensFocal.floorEntry(progress).getValue();
        String sz_hot = String.format(Locale.CHINA, "%dmm", f_hot);
        mTVLensFocal.setText(sz_hot);
    }

    /**
     * 更新lens aperture
     * @param progress  进度
     */
    private void updateLensAperture(int progress)   {
        String sz_hot = mNMLensAperture.floorEntry(progress).getValue();
        mTVLensAperture.setText(sz_hot);
    }

    /**
     * 更新object distance
     * @param progress  进度
     */
    private void updateObjectDistance(int progress)   {
        BigDecimal d_hot = mNMObjectDistance.floorEntry(progress).getValue();
        String sz_hot = String.format(Locale.CHINA, "%.02fm", d_hot.floatValue());
        mTVObjectDistance.setText(sz_hot);
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
