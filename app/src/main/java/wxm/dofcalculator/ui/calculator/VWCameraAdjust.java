package wxm.dofcalculator.ui.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.preference.TwoStatePreference;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.dialog.ObjectDistanceRange.DlgODRange;
import wxm.dofcalculator.ui.calculator.event.AttrChangedEvent;
import wxm.dofcalculator.ui.calculator.event.ObjectDistanceRangeChangeEvent;
import wxm.dofcalculator.utility.ContextUtil;
import wxm.uilib.TuneWheel.TuneWheel;
import wxm.uilib.TwoStateButton.TwoStateButton;

/**
 * 相机设定视图
 * Created by ookoo on 2017/3/24.
 */
public class VWCameraAdjust extends ConstraintLayout {
    private final static int        VW_VERTICAL     = 1;
    private final static int        VW_HORIZONTAL   = 2;

    @BindView(R.id.tv_la_val)
    TextView  mTVLAVal;
    @BindView(R.id.tw_la_val)
    TuneWheel mTWLATuneWheel;

    @BindView(R.id.tv_lf_val)
    TextView  mTVLFVal;
    @BindView(R.id.tw_lf_val)
    TuneWheel mTWLFTuneWheel;

    @BindView(R.id.tv_od_val)
    TextView  mTVODVal;
    @BindView(R.id.tw_od_val)
    TuneWheel mTWODTuneWheel;

    @BindView(R.id.sb_ob_step)
    TwoStateButton mSBODStep;

    @BindString(R.string.tag_decimeter)
    String  TAG_DECIMETER;

    private int mAttrOrentation = VW_VERTICAL;

    /**
     * 物距尺最大最小原始值
     */
    private final static int OB_MIN_VAL = 0;
    private final static int OB_MAX_VAL = 10;

    private final static double INDEX_NUM = 1.8;
    private final static double INDEX_MAX = Math.pow(INDEX_NUM, OB_MAX_VAL);

    /**
     * 最小和最大物距
     * 单位m
     */
    private int     mODMin = 0;
    private int     mODMax = 50;

    private final static String[] LENS_APERTURE_ARR = {
            "F1.0", "F1.4", "F2.0", "F2.8", "F4.0",
            "F5.6", "F8.0", "F11", "F16", "F22",
            "F32", "F45", "F64"};

    public VWCameraAdjust(Context context) {
        super(context);

        int vid = mAttrOrentation == VW_VERTICAL ? R.layout.vw_camera_adjust_v
                        : R.layout.vw_camera_adjust_h;
        LayoutInflater.from(getContext()).inflate(vid, this);
        initUIComponent();
    }

    public VWCameraAdjust(Context context, AttributeSet attrs){
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VWCameraAdjust);
        try {
            mAttrOrentation = array.getInt(R.styleable.VWCameraAdjust_emCAOrientation, VW_VERTICAL);
        } finally {
            array.recycle();
        }

        int vid = mAttrOrentation == VW_VERTICAL ? R.layout.vw_camera_adjust_v
                        : R.layout.vw_camera_adjust_h;
        LayoutInflater.from(getContext()).inflate(vid, this);
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
        ButterKnife.bind(this);

        // for lens aperture
        mTWLATuneWheel.setValueChangeListener((value, valTag) -> {
            mTVLAVal.setText(valTag);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });

        mTWLATuneWheel.setTranslateTag(val -> LENS_APERTURE_ARR[val]);

        // for lens focal
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
        mTWODTuneWheel.setValueChangeListener((value, valTag) -> {
            mTVODVal.setText(valTag);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });
        updateObjectDistanceRange(mSBODStep.getCurTxt().equals(TAG_DECIMETER));
    }

    /**
     * 切换物距范围
     * @param v     参数
     */
    @OnClick({R.id.sb_ob_range})
    public void onChangeODRange(View v) {
        DlgODRange dlg_od = new DlgODRange();
        dlg_od.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
            @Override
            public void onDialogPositiveResult(DialogFragment dialogFragment) {
                mODMin = dlg_od.getODMin();
                mODMax = dlg_od.getODMax();
                updateObjectDistanceRange(mSBODStep.getCurTxt().equals(TAG_DECIMETER));

                EventBus.getDefault().post(
                        new ObjectDistanceRangeChangeEvent(mODMin, mODMax));
            }

            @Override
            public void onDialogNegativeResult(DialogFragment dialogFragment) {
            }
        });

        dlg_od.show(((AppCompatActivity)getContext()).getSupportFragmentManager()
                ,"select object distance");
    }

    /**
     *  切换object distance 步进值
     * @param v     para
     */
    @OnClick({R.id.sb_ob_step})
    public void onChangeODStep(View v) {
        updateObjectDistanceRange(mSBODStep.getCurTxt().equals(TAG_DECIMETER));
    }

    /**
     * 设置物距最小，最大值
     */
    private void updateObjectDistanceRange(boolean decimeter)    {
        mTWODTuneWheel.setTranslateTag(decimeter ?
                (TuneWheel.TagTranslate) val -> {
                    double m_v;
                    if(OB_MIN_VAL == val)
                        m_v = mODMin;
                    else if(OB_MAX_VAL == val)
                        m_v = mODMax;
                    else
                        m_v = mODMin + (mODMax - mODMin) * (Math.pow(INDEX_NUM, val) / INDEX_MAX);

                    return String.format(Locale.CHINA, m_v > 1 ? "%.00fm" : "%.02fm", m_v);
                }

                : (TuneWheel.TagTranslate) val ->   {
                    double m_v;
                    if(0 == val)
                        m_v = mODMin;
                    else if(OB_MAX_VAL == val)
                        m_v = mODMax;
                    else
                        m_v = mODMin + (mODMax - mODMin) * (Math.pow(INDEX_NUM, val) / INDEX_MAX);

                    return String.format(Locale.CHINA, m_v > 1 ? "%.00fm" : "%.02fm", m_v);
                });

        mTVODVal.setText(mTWODTuneWheel.getCurValueTag());
    }
}
