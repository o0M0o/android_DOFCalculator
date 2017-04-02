package wxm.dofcalculator.ui.calculator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.ui.calculator.event.CameraSettingChangeEvent;
import wxm.dofcalculator.ui.calculator.event.DofChangedEvent;
import wxm.dofcalculator.ui.calculator.event.ObjectDistanceRangeChangeEvent;
import wxm.dofcalculator.ui.extend.MeterView.MeterView;
import wxm.dofcalculator.ui.extend.MeterView.MeterViewTag;
import wxm.dofcalculator.ui.extend.TuneWheel.TuneWheel;

/**
 * extend view for Dof result ui
 * Created by ookoo on 2017/3/19.
 */
public class VWDof extends ConstraintLayout {
    private final static String     LOG_TAG = "DOFVW";

    protected DofChangedEvent mDENOFResult;
    protected CameraSettingChangeEvent mCSCameraSetting;

    @BindView(R.id.evw_meter)
    MeterView   mMVMeter;

    @BindView(R.id.cl_dof_info)
    ConstraintLayout      mCLDofInfo;

    @BindView(R.id.tv_front_dof)
    TextView      mTVFrontDof;

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

    public VWDof(Context context) {
        super(context);
        setWillNotDraw(false);
        initUIComponent();
    }

    public VWDof(Context context, AttributeSet attrs){
        super(context, attrs);
        setWillNotDraw(false);
        initUIComponent();

        //TypedArray是一个用来存放由context.obtainStyledAttributes获得的属性的数组
        //在使用完成后，一定要调用recycle方法
        //属性的名称是styleable中的名称+“_”+属性名称
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VWDof);
        try {
        } finally {
            array.recycle();
        }
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

    /**
     * camera setting变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onObjectDistanceRangeChange(ObjectDistanceRangeChangeEvent event) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put(TuneWheel.PARA_VAL_MIN, event.getObjectDistanceMin());
        hm.put(TuneWheel.PARA_VAL_MAX, event.getObjectDistanceMax());
        mMVMeter.adjustPara(hm);

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
                setDofShow(View.INVISIBLE);
                mMVMeter.clearValueTag();
            }
        } else {
            setDofShow(View.VISIBLE);
            helper.drawRange();
            helper.drawBkg();
        }
    }

    /**
     * 初始化UI元件
     */
    private void initUIComponent()  {
        LayoutInflater.from(getContext()).inflate(R.layout.vw_dof, this);
        ButterKnife.bind(this);
    }

    /**
     * 刷新Dof
     */
    private void updateDof()    {
        if(null == mCSCameraSetting)    {
            mDENOFResult = null;
            return;
        }

        class utility   {
            void updateDofView()    {
                mMVMeter.clearValueTag();

                MeterViewTag mt_f = new MeterViewTag();
                mt_f.mSZTagName = mSZTagFrontPoint;
                mt_f.mCRTagColor = mCRDOFFront;
                mt_f.mTagVal = (int)mDENOFResult.getFrontDof();
                mMVMeter.addValueTag(mt_f);

                MeterViewTag mt_od = new MeterViewTag();
                mt_od.mSZTagName = mSZTagObjectDistance;
                mt_od.mCRTagColor = mCRDOFObjectDistance;
                mt_od.mTagVal = (int)mDENOFResult.getObjectDistance();
                mMVMeter.addValueTag(mt_od);

                MeterViewTag mt_b = new MeterViewTag();
                mt_b.mSZTagName = mSZTagBackPoint;
                mt_b.mCRTagColor = mCRDOFBack;
                mt_b.mTagVal = (int)mDENOFResult.getBackDof();
                mMVMeter.addValueTag(mt_b);
            }
        }
        utility helper = new utility();

        int lens_focal = mCSCameraSetting.getLensFocal();
        BigDecimal lens_aperture = mCSCameraSetting.getLensAperture();
        BigDecimal pixel_area = mCSCameraSetting.getCOC();
        int od = mCSCameraSetting.getObjectDistance();
        float f_od = (float)od;

        //hyperFocal = (focal * focal) / (aperture * CoC) + focal;
        BigDecimal ff = new BigDecimal(lens_focal * lens_focal);
        BigDecimal ac = lens_aperture.multiply(pixel_area);
        BigDecimal hyperFocal =  ff.divide(ac, RoundingMode.CEILING)
                                    .add(new BigDecimal(lens_focal));

        // change to unit mm
        BigDecimal od_mm = new BigDecimal(od);

        // dofNear = ((hyperFocal - focal) * distance) / (hyperFocal + distance - (2*focal));
        BigDecimal focal = new BigDecimal(lens_focal);
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

        mDENOFResult = new DofChangedEvent(dofNear.floatValue(), f_od / 1000, dofFar.floatValue());
        mTVFrontDof.setText(String.format(Locale.CHINA, "%.02fm", dofNear.floatValue()));
        mTVObjectDistance.setText(String.format(Locale.CHINA, "%.02fm", f_od / 1000));
        mTVBackDof.setText(String.format(Locale.CHINA, "%.02fm", dofFar.floatValue()));

        // updat ui
        //invalidate();
        helper.updateDofView();
    }

    /**
     * 设置是否显示Dof
     * @param v     gone or visibility
     */
    private void setDofShow(int v) {
        mCLDofInfo.setVisibility(v);
    }

    /**
     * 触摸监听类
    private class TouchListener implements OnTouchListener {
        int lastX;
        int lastY;
        int screenWidth;
        int screenHeight;

        TouchListener() {
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            Log.d(LOG_TAG, "screen width =" + screenWidth + ",screen height="
                    + screenHeight);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(LOG_TAG, "TouchListener -- onTouch");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    Log.d(LOG_TAG, "down x=" + lastX + ", y=" + lastY);
                    break;

                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    Log.d(LOG_TAG, "move dx=" + dx + ",  dy=" + dy);

                    int left = v.getLeft() + dx;
                    int top = v.getTop();
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom();
                    Log.d(LOG_TAG, "view  left=" + left + ", top=" + top + ", right="
                            + right + ",bottom=" + bottom);

                    // set bound
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }
                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    v.layout(left, top, right, top + v.getHeight());

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_UP:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    Log.d(LOG_TAG, "up x=" + lastX + ", y=" + lastY);

                    int mid_x = v.getWidth() / 2 + v.getLeft();
                    float progress = (float)mid_x / (float)screenWidth;
                    EventBus.getDefault().post(new ObjectDistanceChangedEvent(progress * 100));
                    break;
            }
            return true;
        }
    }
     */
}
