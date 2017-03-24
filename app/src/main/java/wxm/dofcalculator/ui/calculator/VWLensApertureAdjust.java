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
 * extend view for seekbar
 * Created by ookoo on 2017/3/19.
 */
public class VWLensApertureAdjust extends ConstraintLayout {
    protected TextView  mTVTag;
    protected TextView  mTVVal;
    protected TuneWheel mTuneWheel;


    public VWLensApertureAdjust(Context context) {
        super(context);
        initUIComponent();
    }

    public VWLensApertureAdjust(Context context, AttributeSet attrs){
        super(context, attrs);
        initUIComponent();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    /**
     * 获取当前seekbar的值
     * @return  当前值
     */
    public String getCurVal()   {
        return mTVVal.getText().toString();
    }


    /**
     * 初始化UI元件
     */
    private void initUIComponent()  {
        LayoutInflater.from(getContext()).inflate(R.layout.vw_lens_aperture_adjust, this);

        mTVTag = UtilFun.cast_t(findViewById(R.id.tv_tag));
        mTVVal = UtilFun.cast_t(findViewById(R.id.tv_val));
        mTuneWheel = UtilFun.cast_t(findViewById(R.id.tw_val));

        mTuneWheel.setValueChangeListener(value -> {
            String val = String.format(Locale.CHINA, "F/%.01f", value / 10);
            mTVVal.setText(val);
            EventBus.getDefault().post(new AttrChangedEvent(0));
        });

        mTuneWheel.setTranslateTag(val -> String.format(Locale.CHINA, "F/%.01f", (float) val /10));
    }
}
