package wxm.dofcalculator.ui.extend.MeterView;


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
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;


/**
 *  表尺视图
 *
 * @author      wxm
 * @version create：2017/03/28
 */
public class MeterView extends View {
    public final static String PARA_VAL_MIN = "val_min";
    public final static String PARA_VAL_MAX = "val_max";

    /**
     * 生成表尺tag
     */
    public interface TagTranslate {
        /**
         * 得到标尺显示tag
         *
         * @param val 标尺值
         * @return 显示tag
         */
        String translateTWTag(int val);
    }

    private int mVWWidth;
    private int mVWHeight;


    /**
     * 可设置属性
     */
    private String mAttrSZPostUnit;
    private String mAttrSZPrvUnit;
    private int mAttrMinValue;
    private int mAttrMaxValue;

    private int mAttrTextSize;
    private int mAttrShortLineHeight;
    private int mAttrLongLineHeight;
    private int mAttrBaseLineWidth;
    private int mAttrModeType;
    private int mAttrLongLineCount;

    private int mAttrBaseLineBottomPadding;

    /**
     * 固定变量
     */
    private int TEXT_COLOR_NORMAL;
    private float DISPLAY_DENSITY;
    private float DISPLAY_TEXT_WIDH;

    /**
     *  标尺上游标
     */
    private ArrayList<MeterViewTag>     mALTags = new ArrayList<>();


    /**
     * 把值翻译为tag
     */
    private TagTranslate mTTTranslator = new TagTranslate() {
        @Override
        public String translateTWTag(int val) {
            return mAttrSZPrvUnit + String.valueOf(val) + mAttrSZPostUnit;
        }
    };


    public MeterView(Context context, AttributeSet attrs) {
        super(context, attrs);

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

    /**
     * 设置标尺刻度翻译器
     *
     * @param tt 翻译器
     */
    public void setTranslateTag(TagTranslate tt) {
        mTTTranslator = tt;
    }


    /**
     * 清理标尺游标
     */
    public void clearValueTag()     {
        mALTags.clear();
        invalidate();
        requestLayout();
    }

    /**
     * 添加标尺游标
     */
    public void addValueTag(MeterViewTag vt)    {
        mALTags.add(vt);
        invalidate();
        requestLayout();
    }


    /**
     * 调整参数
     * @param m_paras      新参数
     */
    public void adjustPara(Map<String, Object> m_paras)  {
        for(String k : m_paras.keySet())     {
            if(k.equals(PARA_VAL_MIN))  {
                mAttrMinValue = (int)m_paras.get(k);
            } else if(k.equals(PARA_VAL_MAX))   {
                mAttrMaxValue = (int)m_paras.get(k);
            }
        }
        
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mVWWidth = getWidth();
        mVWHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
    }


    /**
     * 从中间往两边开始画刻度线
     *
     * @param canvas context
     */
    private void drawScaleLine(Canvas canvas) {
        class utility {
            private int mTotalValue;

            private int mShortLineWidth = 2;
            private int mLongLineWidth = 4;

            private float mBigWidthUnit;
            private float mSmallWidthUnit;

            private int mBigUnitVal;
            private int mSmallUnitVal;

            public utility()    {
                mTotalValue = mAttrMaxValue - mAttrMinValue;

                mBigWidthUnit = mVWWidth / mAttrLongLineCount;
                mSmallWidthUnit = mBigWidthUnit / mAttrModeType;

                mBigUnitVal = mTotalValue / mAttrLongLineCount;
                mSmallUnitVal = mBigUnitVal / mAttrModeType;
            }

            private void drawBaseLine() {
                Paint basePaint = new Paint();
                basePaint.setStrokeWidth(mAttrBaseLineWidth);
                basePaint.setColor(TEXT_COLOR_NORMAL);

                float y = mVWHeight - DISPLAY_DENSITY * mAttrBaseLineBottomPadding;
                canvas.drawLine(0, y, mVWWidth, y, basePaint);
            }

            private void drawLines() {
                // for paint
                Paint linePaint = new Paint();
                linePaint.setStrokeWidth(mShortLineWidth);
                linePaint.setColor(TEXT_COLOR_NORMAL);

                Paint LongLinePaint = new Paint();
                LongLinePaint.setStrokeWidth(mLongLineWidth);
                LongLinePaint.setColor(TEXT_COLOR_NORMAL);

                TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                tp_normal.setColor(getResources().getColor(R.color.text_fit));
                tp_normal.setTextSize(mAttrTextSize * DISPLAY_DENSITY);

                // for axis
                float b_start = mVWHeight - DISPLAY_DENSITY * mAttrBaseLineBottomPadding;
                float ln_long_s_y = b_start;
                float ln_long_e_y = b_start
                            - (int) DISPLAY_DENSITY * (mAttrBaseLineWidth + mAttrLongLineHeight);
                float ln_short_s_y = b_start;
                float ln_short_e_y = b_start
                            - (int) DISPLAY_DENSITY * (mAttrBaseLineWidth + mAttrShortLineHeight);
                float text_top_pos = ln_long_e_y - 8;

                for(int i = 0; i <= mAttrLongLineCount; i++) {
                    float hot_x = RulerValueToXPosition(i, 0, 0);
                    canvas.drawLine(hot_x, ln_long_s_y, hot_x, ln_long_e_y, LongLinePaint);

                    String tw_tag = mTTTranslator.translateTWTag(mAttrMinValue + mBigUnitVal * i);
                    float x_start;
                    if(0 == i)  {
                        x_start = 0;
                    } else if (mAttrLongLineCount == i)   {
                        x_start = hot_x - tw_tag.length() * DISPLAY_TEXT_WIDH - 4;
                    } else  {
                        x_start = hot_x - tw_tag.length() * DISPLAY_TEXT_WIDH / 2;
                    }

                    canvas.drawText(tw_tag, x_start, text_top_pos, tp_normal);

                    for(int j = 1; j < mAttrModeType; j++)  {
                        float cur_x = RulerValueToXPosition(i, j, 0);
                        canvas.drawLine(cur_x, ln_short_s_y, cur_x, ln_short_e_y, linePaint);
                    }
                }
            }

            private void drawTags() {
                Paint linePaint = new Paint();

                float ln_long_s_y = mVWHeight - DISPLAY_DENSITY * (mAttrBaseLineBottomPadding - 2);
                float ln_long_e_y = ln_long_s_y + DISPLAY_DENSITY * 8;
                float text_top_pos = ln_long_e_y + DISPLAY_TEXT_WIDH + DISPLAY_DENSITY * 4;

                TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);

                for(MeterViewTag mt : mALTags)  {
                    float x = MeterValueToXPosition(mt.mTagVal);

                    linePaint.setColor(mt.mCRTagColor);
                    Path p = new Path();
                    p.moveTo(x, ln_long_s_y);
                    p.lineTo(x - 8, ln_long_e_y);
                    p.lineTo(x + 8, ln_long_e_y);
                    p.lineTo(x, ln_long_s_y);
                    canvas.drawPath(p, linePaint);

                    tp_normal.setColor(mt.mCRTagColor);
                    tp_normal.setTextSize(mAttrTextSize * DISPLAY_DENSITY);
                    canvas.drawText(mt.mSZTagName,
                            x - mt.mSZTagName.length() * DISPLAY_TEXT_WIDH / 2,
                            text_top_pos, tp_normal);
                }
            }

            /**
             * 通过值获得标尺x坐标
             * @param val       值
             * @return          标尺X坐标
             */
            private float MeterValueToXPosition(int val)    {
                int big = val / mBigUnitVal;
                int small = val % mBigUnitVal / mSmallUnitVal;
                int left = val % mSmallUnitVal;

                return RulerValueToXPosition(big, small, left);
            }

            /**
             * 尺度值获得X坐标
             * @param big       大值
             * @param small     小值
             * @param left      剩余值
             * @return          x坐标
             */
            private float RulerValueToXPosition(int big, int small, int left) {
                float x_big = 0 == big ?
                                mLongLineWidth / 2
                                : (mAttrLongLineCount == big ?
                                        mVWWidth - mLongLineWidth / 2
                                        : mBigWidthUnit * big - mLongLineWidth / 2 );

                float x_small = mSmallWidthUnit * small;
                float x_left = mSmallWidthUnit * left / mSmallUnitVal;
                return x_big + x_small + x_left;
            }
        }
        utility helper = new utility();

        canvas.save();
        helper.drawBaseLine();
        helper.drawLines();
        helper.drawTags();
        canvas.restore();
    }
}
