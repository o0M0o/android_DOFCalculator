package wxm.dofcalculator.ui.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.ui.calculator.event.AttrChangedEvent;
import wxm.dofcalculator.ui.extend.TuneWheel.TuneWheel;
import wxm.dofcalculator.utility.ContextUtil;

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

    private final static String[] LENS_APERTURE_ARR = {
            "F1.0", "F1.4", "F2.0", "F2.8", "F4.0",
            "F5.6", "F8.0", "F11", "F16", "F22",
            "F32", "F45", "F64"};

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
            mTVLAVal.setText(valTag);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });

        mTWLATuneWheel.setTranslateTag(val -> LENS_APERTURE_ARR[val]);

        // for lens focal
        mTVLFVal = UtilFun.cast_t(findViewById(R.id.tv_lf_val));
        mTWLFTuneWheel = UtilFun.cast_t(findViewById(R.id.tw_lf_val));

        mTWLFTuneWheel.setValueChangeListener((value, valTag) -> {
            String tag = String.valueOf(value) + "mm";

            mTVLFVal.setText(tag);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });

        Context ct = getContext();
        if(ct instanceof Activity)  {
            Intent it = ((Activity)ct).getIntent();
            if(null != it) {
                int d_id = it.getIntExtra(ACCalculator.KEY_DEVICE_ID, GlobalDef.INT_INVAILED_ID);
                if(GlobalDef.INT_INVAILED_ID != d_id)   {
                    DeviceItem di = ContextUtil.getDUDevice().getData(d_id);
                    if(null != di)  {
                        Map<String, Object> mp = new HashMap<>();
                        mp.put(TuneWheel.PARA_VAL_MIN, di.getLens().getMinFocal());
                        mp.put(TuneWheel.PARA_VAL_MAX, di.getLens().getMaxFocal());
                        mTWLFTuneWheel.adjustPara(mp);
                    }
                }
            }
        }

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
