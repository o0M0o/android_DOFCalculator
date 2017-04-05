package wxm.dofcalculator.ui.extend.SmallButton;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;


/**
 *  on-off button
 *
 * @author      wxm
 * @version create：2017/03/28
 */
public class SmallButton extends ConstraintLayout {
    private final static String LOG_TAG = "SmallButton";

    @BindView(R.id.tv_tag)
    TextView    mTVTag;

    /**
     * 可设置属性
     */
    private String mAttrTextOn;
    private String mAttrTextOff;

    private Drawable     mAttrBackGroundOn;
    private Drawable     mAttrBackGroundOff;

    private boolean mAttrIsOn;

    /**
     * 固定变量
     */
    private float DISPLAY_DENSITY;

    public SmallButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.vw_small_button, this);
        ButterKnife.bind(this);
        initCompent(context, attrs);
    }

    /**
     * 是否处于"on"
     * @return  is on ?
     */
    public boolean isInOk() {
        return mAttrIsOn;
    }

    /**
     * 得到当前的标签
     * @return  当前标签
     */
    public String getCurTxt()   {
        return mTVTag.getText().toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 初始化自身
     * @param context   上下文
     * @param attrs     配置
     */
    private void initCompent(Context context, AttributeSet attrs)  {
        // for parameter
        int text_color;
        int text_size = 12;
        int text_color_def;
        Resources res = context.getResources();
        DISPLAY_DENSITY = res.getDisplayMetrics().density;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources.Theme te = context.getTheme();
            text_color_def = res.getColor(R.color.text_fit, te);
        } else  {
            text_color_def = res.getColor(R.color.text_fit);
        }
        text_color = text_color_def;

        boolean b_ok = true;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SmallButton);
        try {
            text_size = array.getInt(R.styleable.SmallButton_sbTextSize, 12);
            text_color = array.getColor(R.styleable.SmallButton_sbTextColor, text_color_def);

            mAttrTextOn = array.getString(R.styleable.SmallButton_sbTextOn);
            mAttrTextOn = UtilFun.StringIsNullOrEmpty(mAttrTextOn) ? "on" : mAttrTextOn;

            mAttrTextOff = array.getString(R.styleable.SmallButton_sbTextOff);
            mAttrTextOff = UtilFun.StringIsNullOrEmpty(mAttrTextOff) ? "off" : mAttrTextOff;

            mAttrIsOn = array.getBoolean(R.styleable.SmallButton_sbIsOn, false);

            mAttrBackGroundOn = array.getDrawable(R.styleable.SmallButton_sbBackGroundOn);
            mAttrBackGroundOff = array.getDrawable(R.styleable.SmallButton_sbBackGroundOff);
        } catch (Exception ex)  {
            b_ok = false;
            Log.e(LOG_TAG, "catch ex : " + ex.toString());
        } finally {
            array.recycle();
        }

        if(b_ok) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Resources.Theme te = context.getTheme();
                if (null == mAttrBackGroundOn)
                    mAttrBackGroundOn = res.getDrawable(R.drawable.small_button_on_shape, te);

                if (null == mAttrBackGroundOff)
                    mAttrBackGroundOff = res.getDrawable(R.drawable.small_button_off_shape, te);
            } else {
                if (null == mAttrBackGroundOn)
                    mAttrBackGroundOn = res.getDrawable(R.drawable.small_button_on_shape);

                if (null == mAttrBackGroundOff)
                    mAttrBackGroundOff = res.getDrawable(R.drawable.small_button_off_shape);
            }

            mTVTag.setTextColor(text_color);
            mTVTag.setTextSize(text_size);
            mTVTag.setText(mAttrIsOn ? mAttrTextOn : mAttrTextOff);
            setBackground(mAttrIsOn ? mAttrBackGroundOn : mAttrBackGroundOff);
        }
    }

    @Override
    public boolean performClick()  {
        mAttrIsOn = !mAttrIsOn;

        setBackground(mAttrIsOn ? mAttrBackGroundOn : mAttrBackGroundOff);
        mTVTag.setText(mAttrIsOn ? mAttrTextOn : mAttrTextOff);
        return super.performClick();
    }
}
