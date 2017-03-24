package wxm.dofcalculator.ui.calculator;

import android.content.Context;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.ui.calculator.extend.AttrChangedEvent;
import wxm.dofcalculator.ui.calculator.extend.TuneWheel;

/**
 * 相机设定视图
 * Created by ookoo on 2017/3/24.
 */
public class VWCameraAdjust extends ConstraintLayout {
    protected TextView  mTVLAVal;
    protected TuneWheel mTWLATuneWheel;

    protected TextView  mTVLFVal;
    protected TuneWheel mTWLFTuneWheel;

    protected TextView  mTVODVal;
    protected TuneWheel mTWODTuneWheel;

    public VWCameraAdjust(Context context) {
        super(context);
        initUIComponent();
    }

    public VWCameraAdjust(Context context, AttributeSet attrs){
        super(context, attrs);
        initUIComponent();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    public String getCurLensFocal() {
        return mTVLFVal.getText().toString();
    }

    public String getCurLensAperture() {
        return mTVLAVal.getText().toString();
    }

    public int getCurObjectDistance() {
        String val = mTVODVal.getText().toString();
        float v = Float.valueOf(val.substring(0, val.indexOf("m")));
        return (int) (1000 * v);
    }

    /**
     * 初始化UI元件
     */
    private void initUIComponent()  {
        LayoutInflater.from(getContext()).inflate(R.layout.vw_camera_adjust, this);

        // for lens aperture
        mTVLAVal = UtilFun.cast_t(findViewById(R.id.tv_la_val));
        mTWLATuneWheel = UtilFun.cast_t(findViewById(R.id.tw_la_val));

        mTWLATuneWheel.setValueChangeListener((value, valTag) -> {
            String tag = String.format(Locale.CHINA, "F/%s", valTag);

            mTVLAVal.setText(tag);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });

        mTWLATuneWheel.setTranslateTag(val -> String.format(Locale.CHINA, "%.01f", (float) val /10));

        // for lens focal
        mTVLFVal = UtilFun.cast_t(findViewById(R.id.tv_lf_val));
        mTWLFTuneWheel = UtilFun.cast_t(findViewById(R.id.tw_lf_val));

        mTWLFTuneWheel.setValueChangeListener((value, valTag) -> {
            String tag = String.valueOf((int)value) + "mm";

            mTVLFVal.setText(tag);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });

        // for object distance
        mTVODVal = UtilFun.cast_t(findViewById(R.id.tv_od_val));
        mTWODTuneWheel = UtilFun.cast_t(findViewById(R.id.tw_od_val));

        mTWODTuneWheel.setValueChangeListener((value, valTag) -> {
            String tag = valTag + "m";

            mTVODVal.setText(tag);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });
    }
}
