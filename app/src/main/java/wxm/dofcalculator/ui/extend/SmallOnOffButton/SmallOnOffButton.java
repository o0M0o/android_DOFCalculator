package wxm.dofcalculator.ui.extend.SmallOnOffButton;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import butterknife.ButterKnife;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.ui.extend.MeterView.MeterViewTag;


/**
 *  on-off button
 *
 * @author      wxm
 * @version create：2017/03/28
 */
public class SmallOnOffButton extends View {

    /**
     * 可设置属性
     */
    private int mAttrOnText;
    private int mAttrOffText;

    private int mAttrTextSize;
    private boolean mAttrIsOn;

    /**
     * 固定变量
     */
    private float DISPLAY_DENSITY;


    public SmallOnOffButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this);

        // for normal setting
        Resources res = context.getResources();
        DISPLAY_DENSITY = res.getDisplayMetrics().density;
        TEXT_COLOR_NORMAL = Color.BLACK;

        // for parameter
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MeterView);
        try {
            String sz_unit = array.getString(R.styleable.MeterView_mvPostUnit);
            mAttrSZPostUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            sz_unit = array.getString(R.styleable.MeterView_mvPrvUnit);
            mAttrSZPrvUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            mAttrLongLineCount = array.getInt(R.styleable.MeterView_mvLongLineCount, 5);
            mAttrModeType = array.getInt(R.styleable.MeterView_mvModeType, 2);
            mAttrMinValue = array.getInt(R.styleable.MeterView_mvMinValue, 0);
            mAttrMaxValue = array.getInt(R.styleable.MeterView_mvMaxValue, 100);

            mAttrTextSize = array.getInt(R.styleable.MeterView_mvTextSize, 14);
            mAttrShortLineHeight = array.getInt(R.styleable.MeterView_mvShortLineHeight, 4);
            mAttrLongLineHeight = array.getInt(R.styleable.MeterView_mvLongLineHeight, 8);
            mAttrBaseLineWidth = array.getInt(R.styleable.MeterView_mvBaseLineWidth, 4);

            mAttrBaseLineBottomPadding = array.getInt(R.styleable.MeterView_mvBaseLineBottomPadding,
                                    24);

            // get other
            TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            tp_normal.setTextSize(mAttrTextSize * DISPLAY_DENSITY);
            DISPLAY_TEXT_WIDH = Layout.getDesiredWidth("0", tp_normal);
        } finally {
            array.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
