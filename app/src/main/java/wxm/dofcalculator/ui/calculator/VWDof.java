package wxm.dofcalculator.ui.calculator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.ui.calculator.extend.CameraSettingChangeEvent;
import wxm.dofcalculator.ui.calculator.extend.DofChangedEvent;

/**
 * extend view for Dof result ui
 * Created by ookoo on 2017/3/19.
 */
public class VWDof extends ConstraintLayout {
    private final static String     LOG_TAG = "DOFVW";

    protected ConstraintLayout      mCLDofView;

    protected ConstraintLayout      mCLDofInfo;
    protected TextView              mTVFrontDof;
    protected TextView              mTVObjectDistance;
    protected TextView              mTVBackDof;

    protected DofChangedEvent mDENOFResult;
    protected CameraSettingChangeEvent mCSCameraSetting;

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
            TextView tv;
            String sz_min = array.getString(R.styleable.VWDof_szDofMinTag);
            sz_min = UtilFun.StringIsNullOrEmpty(sz_min) ? "0m" : sz_min;
            tv = UtilFun.cast_t(findViewById(R.id.tv_min_tag));
            tv.setText(sz_min);

            String sz_middle1 = array.getString(R.styleable.VWDof_szDofMiddle1Tag);
            sz_middle1 = UtilFun.StringIsNullOrEmpty(sz_middle1) ? "25m" : sz_middle1;
            tv = UtilFun.cast_t(findViewById(R.id.tv_middle1_tag));
            tv.setText(sz_middle1);

            String sz_middle2 = array.getString(R.styleable.VWDof_szDofMiddle2Tag);
            sz_middle2 = UtilFun.StringIsNullOrEmpty(sz_middle2) ? "50m" : sz_middle2;
            tv = UtilFun.cast_t(findViewById(R.id.tv_middle2_tag));
            tv.setText(sz_middle2);

            String sz_middle3 = array.getString(R.styleable.VWDof_szDofMiddle3Tag);
            sz_middle3 = UtilFun.StringIsNullOrEmpty(sz_middle3) ? "75m" : sz_middle3;
            tv = UtilFun.cast_t(findViewById(R.id.tv_middle3_tag));
            tv.setText(sz_middle3);

            String sz_max = array.getString(R.styleable.VWDof_szDofMaxTag);
            sz_max = UtilFun.StringIsNullOrEmpty(sz_max) ? "100m" : sz_max;
            tv = UtilFun.cast_t(findViewById(R.id.tv_max_tag));
            tv.setText(sz_max);
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
    public void onCameraSettingChangeEvent(CameraSettingChangeEvent event) {
        mCSCameraSetting = event;
        updateDof();
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        class utility   {
            void drawRange()    {
                float front_dof =  mDENOFResult.getFrontDof();
                float object_distance =  mDENOFResult.getObjectDistance();
                float back_dof =  mDENOFResult.getBackDof();

                Paint p_f = new Paint();
                p_f.setColor(Color.CYAN);
                p_f.setStrokeJoin(Paint.Join.ROUND);
                p_f.setStrokeCap(Paint.Cap.ROUND);
                p_f.setStrokeWidth(3);

                Paint p_b = new Paint();
                p_b.setColor(Color.RED);
                p_b.setStrokeJoin(Paint.Join.ROUND);
                p_b.setStrokeCap(Paint.Cap.ROUND);
                p_b.setStrokeWidth(3);

                canvas.save();

                float om_w = mCLDofView.getWidth() / 100;
                int h_top = mCLDofInfo.getHeight();
                int h = mCLDofView.getHeight() + h_top;
                int mid_x = (int)(object_distance * om_w);

                int f_x = (int) (om_w * front_dof);
                canvas.drawRect(f_x, h_top, mid_x, h, p_f);

                int b_x = (int) (back_dof * om_w);
                canvas.drawRect(mid_x, h_top, b_x, h, p_b);

                canvas.restore();
            }

            void drawBkg()  {
                Paint p_bkg = new Paint();
                p_bkg.setColor(Color.YELLOW);
                p_bkg.setStrokeJoin(Paint.Join.ROUND);
                p_bkg.setStrokeCap(Paint.Cap.ROUND);
                p_bkg.setStrokeWidth(3);

                int h_top = mCLDofInfo.getHeight();
                int h = mCLDofView.getHeight();
                int l = getWidth();
                int h_middle = h_top + h / 2;
                int h_bottom = h_middle + h / 2;

                Path pt_u = new Path();
                pt_u.moveTo(0, h_top);
                pt_u.lineTo(0, h_middle);
                pt_u.lineTo(l, h_top);
                pt_u.lineTo(0, h_top);

                Path pt_d = new Path();
                pt_d.moveTo(0, h_middle);
                pt_d.lineTo(0, h_bottom);
                pt_d.lineTo(l, h_bottom);
                pt_d.lineTo(0, h_middle);

                canvas.save();
                canvas.drawPath(pt_u, p_bkg);
                canvas.drawPath(pt_d, p_bkg);
                canvas.restore();
            }
        }

        utility helper = new utility();
        if(null == mDENOFResult)    {
            helper.drawBkg();
            if(!isInEditMode()) {
                setDofShow(View.GONE);
            }
        } else {
            setDofShow(View.VISIBLE);
            helper.drawRange();
            helper.drawBkg();
        }
    }

    /**
     * 调整Dof view(仅做平移)
     * @param v     view
     * @param x     x coords for view
     * @param w     width for view
     * @param h     height for view
     */
    public void adjustDofView(View v, int x, int w, int h) {
        v.layout(x, 0, x + w, h);
    }


    /**
     * 初始化UI元件
     */
    private void initUIComponent()  {
        LayoutInflater.from(getContext()).inflate(R.layout.vw_dof, this);

        mCLDofView = UtilFun.cast_t(findViewById(R.id.cl_vw));

        mCLDofInfo = UtilFun.cast_t(findViewById(R.id.cl_dof_info));
        mTVFrontDof = UtilFun.cast_t(findViewById(R.id.tv_front_dof));
        mTVObjectDistance = UtilFun.cast_t(findViewById(R.id.tv_objecet_distance));
        mTVBackDof = UtilFun.cast_t(findViewById(R.id.tv_back_dof));
    }

    /**
     * 刷新Dof
     */
    private void updateDof()    {
        if(null == mCSCameraSetting)    {
            mDENOFResult = null;
            return;
        }

        int lens_focal = mCSCameraSetting.getLensFocal();
        BigDecimal lens_aperture = mCSCameraSetting.getLensAperture();
        BigDecimal pixel_area = mCSCameraSetting.getPixelArea();
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
        invalidate();
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
