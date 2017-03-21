package wxm.dofcalculator.ui.calculator.extend;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;

/**
 * extend view for Dof result ui
 * Created by ookoo on 2017/3/19.
 */
public class DOFVW extends ConstraintLayout {
    protected ConstraintLayout      mCLDofView;
    protected View                  mVWFrontDOF;
    protected View                  mVWBackDOF;

    protected ConstraintLayout      mCLDofInfo;
    protected TextView              mTVFrontDof;
    protected TextView              mTVObjectDistance;
    protected TextView              mTVBackDof;

    protected DofChangedEvent     mDEEvent;

    public DOFVW(Context context) {
        super(context);
        setWillNotDraw(false);
        initUIComponent();
    }

    public DOFVW(Context context, AttributeSet attrs){
        super(context, attrs);
        setWillNotDraw(false);
        initUIComponent();

        //TypedArray是一个用来存放由context.obtainStyledAttributes获得的属性的数组
        //在使用完成后，一定要调用recycle方法
        //属性的名称是styleable中的名称+“_”+属性名称
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DOFVW);
        try {
            TextView tv;
            String sz_min = array.getString(R.styleable.DOFVW_szDofMinTag);
            sz_min = UtilFun.StringIsNullOrEmpty(sz_min) ? "0m" : sz_min;
            tv = UtilFun.cast_t(findViewById(R.id.tv_min_tag));
            tv.setText(sz_min);

            String sz_middle1 = array.getString(R.styleable.DOFVW_szDofMiddle1Tag);
            sz_middle1 = UtilFun.StringIsNullOrEmpty(sz_middle1) ? "25m" : sz_middle1;
            tv = UtilFun.cast_t(findViewById(R.id.tv_middle1_tag));
            tv.setText(sz_middle1);

            String sz_middle2 = array.getString(R.styleable.DOFVW_szDofMiddle2Tag);
            sz_middle2 = UtilFun.StringIsNullOrEmpty(sz_middle2) ? "50m" : sz_middle2;
            tv = UtilFun.cast_t(findViewById(R.id.tv_middle2_tag));
            tv.setText(sz_middle2);

            String sz_middle3 = array.getString(R.styleable.DOFVW_szDofMiddle3Tag);
            sz_middle3 = UtilFun.StringIsNullOrEmpty(sz_middle3) ? "50m" : sz_middle3;
            tv = UtilFun.cast_t(findViewById(R.id.tv_middle3_tag));
            tv.setText(sz_middle3);

            String sz_max = array.getString(R.styleable.DOFVW_szDofMaxTag);
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
     * SeekBar变化处理器
     * @param event     事件参数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDofChangeEvent(DofChangedEvent event) {
        mDEEvent = event;

        invalidate();
        requestLayout();
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(null == mDEEvent)    {
            if(!isInEditMode()) {
                setDofShow(View.GONE);
            }
        } else {
            setDofShow(View.VISIBLE);

            float om_w = mCLDofView.getWidth() / 100;
            int h = mCLDofView.getHeight();

            int f_x = (int) (om_w * mDEEvent.getFrontDof());
            int f_w = (int) ((mDEEvent.getObjectDistance() - mDEEvent.getFrontDof()) * om_w);
            adjustDofView(mVWFrontDOF, f_x, f_w, h);

            int b_x = f_x + f_w;
            int b_w = (int) ((mDEEvent.getBackDof() - mDEEvent.getObjectDistance()) * om_w);
            adjustDofView(mVWBackDOF, b_x, b_w, h);

            mTVFrontDof.setText(String.format(Locale.CHINA, "%.02fm", mDEEvent.getFrontDof()));
            mTVObjectDistance.setText(String.format(Locale.CHINA, "%.02fm", mDEEvent.getObjectDistance()));
            mTVBackDof.setText(String.format(Locale.CHINA, "%.02fm", mDEEvent.getBackDof()));
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
        mVWFrontDOF = UtilFun.cast_t(findViewById(R.id.vw_front_dof));
        mVWBackDOF = UtilFun.cast_t(findViewById(R.id.vw_back_dof));

        mCLDofInfo = UtilFun.cast_t(findViewById(R.id.cl_dof_info));
        mTVFrontDof = UtilFun.cast_t(findViewById(R.id.tv_front_dof));
        mTVObjectDistance = UtilFun.cast_t(findViewById(R.id.tv_objecet_distance));
        mTVBackDof = UtilFun.cast_t(findViewById(R.id.tv_back_dof));

        if(!isInEditMode()) {
            setDofShow(View.GONE);
        }
    }

    /**
     * 设置是否显示Dof
     * @param v     gone or visibility
     */
    private void setDofShow(int v) {
        mVWFrontDOF.setVisibility(v);
        mVWBackDOF.setVisibility(v);

        mCLDofInfo.setVisibility(v);
    }
}
