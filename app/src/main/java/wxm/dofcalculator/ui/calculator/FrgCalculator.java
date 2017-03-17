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

import java.util.NavigableMap;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.dofcalculator.R;
import wxm.dofcalculator.ui.device.ACDevice;

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


    private final static NavigableMap<Integer, String> mNMLensAperture = new TreeMap<>();
    static {
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
        private void onLensFocal(SeekBar sb)    {
        }

        /**
         * 处理lens aperture
         * @param sb  for seekbar
         */
        private void onLensAperture(SeekBar sb)    {
            int p = sb.getProgress();
            String sz_hot = mNMLensAperture.floorEntry(p).getValue();
            mTVLensAperture.setText(sz_hot);
        }

        /**
         * 处理lens aperture
         * @param sb  for seekbar
         */
        private void onObjDistanc(SeekBar sb)    {
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
    }

    @Override
    protected void loadUI() {
    }



}
