package wxm.dofcalculator.ui.calculator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.dofcalculator.R;
import wxm.dofcalculator.ui.calculator.event.CameraSettingChangeEvent;
import wxm.dofcalculator.ui.calculator.event.DofChangedEvent;
import wxm.uilib.DistanceMeter.DistanceMeter;
import wxm.uilib.DistanceMeter.DistanceMeterTag;

/**
 * extend view for Dof result ui
 * Created by WangXM on2017/3/19.
 */
public class VWDof extends ConstraintLayout {
    private final static int        VW_VERTICAL     = 1;
    private final static int        VW_HORIZONTAL   = 2;

    private final static int        SETP_VAL = 5;

    protected DofChangedEvent mDENOFResult;
    protected CameraSettingChangeEvent mCSCameraSetting;

    @BindView(R.id.evw_meter)
    DistanceMeter mMVMeter;

    @BindView(R.id.cl_dof_info)
    ConstraintLayout      mCLDofInfo;

    @BindView(R.id.tv_front_dof)
    TextView      mTVFrontDof;

    @BindView(R.id.tv_drive)
    TextView      mTVDrive;

    @BindView(R.id.tv_objecet_distance)
    TextView      mTVObjectDistance;

    @BindView(R.id.tv_back_dof)
    TextView      mTVBackDof;

    @BindColor(R.color.rosybrown)
    int     mCRDOFFront;

    @BindColor(R.color.red_ff725f)
    int     mCRDOFObjectDistance;

    @BindColor(R.color.orangered)
    int     mCRDOFBack;

    @BindString(R.string.tag_front_point)
    String  mSZTagFrontPoint;

    @BindString(R.string.tag_object_distance)
    String  mSZTagObjectDistance;

    @BindString(R.string.tag_back_point)
    String  mSZTagBackPoint;

    private final static String DEF_SZ = "--";

    private int mAttrOrentation = VW_VERTICAL;

    public VWDof(Context context) {
        super(context);
        setWillNotDraw(false);

        int vid = mAttrOrentation == VW_VERTICAL ? R.layout.vw_dof_v : R.layout.vw_dof_h;
        LayoutInflater.from(context).inflate(vid, this);
        initUIComponent();
    }

    public VWDof(Context context, AttributeSet attrs){
        super(context, attrs);
        setWillNotDraw(false);

        //TypedArray是一个用来存放由context.obtainStyledAttributes获得的属性的数组
        //在使用完成后，一定要调用recycle方法
        //属性的名称是styleable中的名称+“_”+属性名称
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VWDof);
        try {
            mAttrOrentation = array.getInt(R.styleable.VWDof_emOrientation, VW_VERTICAL);
        } finally {
            array.recycle();
        }

        int vid = mAttrOrentation == VW_VERTICAL ? R.layout.vw_dof_v : R.layout.vw_dof_h;
        LayoutInflater.from(context).inflate(vid, this);
        initUIComponent();
    }

    @Override
    protected void onAttachedToWindow() {
        EventBus.getDefault().register(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }


    /**
     * camera setting变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCameraSettingChange(CameraSettingChangeEvent event) {
        mCSCameraSetting = event;
        updateDof();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        class utility   {
            void drawRange()    {
            }

            void drawBkg()  {
            }
        }

        utility helper = new utility();
        if(null == mDENOFResult)    {
            helper.drawBkg();
            if(!isInEditMode()) {
                mMVMeter.clearCursor();

                mTVFrontDof.setText(DEF_SZ);
                mTVObjectDistance.setText(DEF_SZ);
                mTVBackDof.setText(DEF_SZ);

                mTVDrive.setText(mCSCameraSetting == null ?
                            DEF_SZ : mCSCameraSetting.getDevice().getName());
            }
        } else {
            helper.drawRange();
            helper.drawBkg();
        }
    }

    /**
     * 初始化UI元件
     */
    private void initUIComponent()  {
        ButterKnife.bind(this);
    }

    /**
     * update view
     */
    private void updateDof()    {
        if(null == mCSCameraSetting)    {
            mDENOFResult = null;
            return;
        }

        class utility   {
            /**
             * update dof info in header view
             */
            void updateDofInfo()    {
                mTVFrontDof.setText(String.format(Locale.CHINA, "%.02fm",
                                        mDENOFResult.getFrontDof() / 1000));
                mTVObjectDistance.setText(String.format(Locale.CHINA, "%.02fm",
                                        mDENOFResult.getObjectDistance() / 1000));
                mTVBackDof.setText(String.format(Locale.CHINA, "%.02fm",
                                        mDENOFResult.getBackDof() / 1000));

                mTVDrive.setText(mCSCameraSetting.getDevice().getName());
            }


            /**
             * add cursor and adjust value range
             */
            void updateDofView()    {
                mMVMeter.clearCursor();

                float valBackDof = mDENOFResult.getBackDof() / 1000;
                float valFrontDof = mDENOFResult.getFrontDof() / 1000;
                float valObjectDistance = mDENOFResult.getObjectDistance() / 1000;

                // adjust min-max value
                int cur_max = (((int)valBackDof) / SETP_VAL + 2) * SETP_VAL;
                int cur_min = Math.max(0, (((int)valFrontDof) / SETP_VAL - 2) * SETP_VAL);
                HashMap<String, Object> hm = new HashMap<>();
                hm.put(DistanceMeter.PARA_VAL_MAX, cur_max);
                hm.put(DistanceMeter.PARA_VAL_MIN, cur_min);
                mMVMeter.adjustAttribute(hm);

                // add cursors
                DistanceMeterTag mt_f = new DistanceMeterTag();
                mt_f.mSZTagName = mSZTagFrontPoint;
                mt_f.mCRTagColor = mCRDOFFront;
                mt_f.mTagVal = valFrontDof;

                DistanceMeterTag mt_od = new DistanceMeterTag();
                mt_od.mSZTagName = mSZTagObjectDistance;
                mt_od.mCRTagColor = mCRDOFObjectDistance;
                mt_od.mTagVal = valObjectDistance;

                DistanceMeterTag mt_b = new DistanceMeterTag();
                mt_b.mSZTagName = mSZTagBackPoint;
                mt_b.mCRTagColor = mCRDOFBack;
                mt_b.mTagVal = valBackDof;

                mMVMeter.addCursor(mt_f, mt_od, mt_b);
            }
        }
        utility helper = new utility();

        int lens_focal = mCSCameraSetting.getLensFocal();
        BigDecimal lens_aperture = mCSCameraSetting.getLensAperture();
        BigDecimal pixel_area = mCSCameraSetting.getCOC();
        float f_od = (float)mCSCameraSetting.getObjectDistance();

        //hyperFocal = (focal * focal) / (aperture * CoC) + focal;
        BigDecimal ff = new BigDecimal(lens_focal * lens_focal);
        BigDecimal ac = lens_aperture.multiply(pixel_area);
        BigDecimal hyperFocal =  ff.divide(ac, RoundingMode.CEILING)
                                    .add(new BigDecimal(lens_focal));

        // change to unit mm
        BigDecimal od_mm = new BigDecimal(f_od);

        // dofNear = ((hyperFocal - focal) * distance) / (hyperFocal + distance - (2*focal));
        BigDecimal focal = new BigDecimal(lens_focal);
        BigDecimal dofNear_f = hyperFocal.subtract(focal).multiply(od_mm);
        BigDecimal dofNear_b = hyperFocal.add(od_mm).subtract(focal.add(focal));
        float dofNear = dofNear_f.divide(dofNear_b, RoundingMode.CEILING).floatValue();

        // Prevent 'divide by zero' when calculating far distance.
        float dofFar;
        if(Math.abs(hyperFocal.subtract(od_mm).floatValue()) <= 0.00001)    {
            dofFar = f_od + 200000;
        } else  {
            BigDecimal f = hyperFocal.subtract(focal).multiply(od_mm);
            BigDecimal b = hyperFocal.subtract(od_mm);
            dofFar = f.divide(b, RoundingMode.CEILING).floatValue();
            if(dofFar <= f_od)
                dofFar = f_od + 200000;
        }

        float dof_n = Math.min(dofNear, f_od);
        float dof_f = Math.max(dofFar, f_od);
        mDENOFResult = new DofChangedEvent(dof_n, f_od, dof_f);

        // updat ui
        helper.updateDofInfo();
        helper.updateDofView();
    }
}
