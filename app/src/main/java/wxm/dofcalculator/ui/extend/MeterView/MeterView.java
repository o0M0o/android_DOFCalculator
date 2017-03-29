package wxm.dofcalculator.ui.extend.MeterView;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    /**
     * 固定变量
     */
    private int TEXT_COLOR_NORMAL;
    private float DISPLAY_DENSITY;

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

            public utility()    {
                mTotalValue = mAttrMaxValue - mAttrMinValue;

                mBigWidthUnit = mVWWidth / mAttrLongLineCount;
                mSmallWidthUnit = mBigWidthUnit / mAttrModeType;
            }

            private void drawBaseLine() {
                Paint basePaint = new Paint();
                basePaint.setStrokeWidth(mAttrBaseLineWidth);
                basePaint.setColor(TEXT_COLOR_NORMAL);

                int y = mVWHeight - mAttrBaseLineWidth / 2;
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
                float textWidth = Layout.getDesiredWidth("0", tp_normal);

                // for axis
                int ln_long_s_y = mVWHeight;
                int ln_long_e_y = mVWHeight
                        - (int) DISPLAY_DENSITY * (mAttrBaseLineWidth + mAttrLongLineHeight);
                int ln_short_s_y = mVWHeight;
                int ln_short_e_y = mVWHeight
                        - (int) DISPLAY_DENSITY * (mAttrBaseLineWidth + mAttrShortLineHeight);
                int text_top_pos = ln_long_e_y - 8;

                int unit_val = mTotalValue / mAttrLongLineCount;
                for(int i = 0; i <= mAttrLongLineCount; i++) {
                    float hot_x = RulerValueToXPosition(i, 0, 0);
                    canvas.drawLine(hot_x, ln_long_s_y, hot_x, ln_long_e_y, LongLinePaint);

                    String tw_tag = mTTTranslator.translateTWTag(mAttrMinValue + unit_val * i);
                    float x_start;
                    if(0 == i)  {
                        x_start = 0;
                    } else if (mAttrLongLineCount == i)   {
                        x_start = hot_x - tw_tag.length() * textWidth - 4;
                    } else  {
                        x_start = hot_x - tw_tag.length() * textWidth / 2;
                    }

                    canvas.drawText(tw_tag, x_start, text_top_pos, tp_normal);

                    for(int j = 1; j < mAttrModeType; j++)  {
                        float cur_x = RulerValueToXPosition(i, j, 0);
                        canvas.drawLine(cur_x, ln_short_s_y, cur_x, ln_short_e_y, linePaint);
                    }
                }
            }

            private void drawTags() {
                int w_line = 2;
                Paint linePaint = new Paint();
                linePaint.setStrokeWidth(w_line);
                linePaint.setColor(Color.RED);

                int ln_long_s_y = mVWHeight;
                int ln_long_e_y = mVWHeight
                        - (int) DISPLAY_DENSITY * (mAttrBaseLineWidth + mAttrLongLineHeight);
                int text_top_pos = ln_long_e_y - 8;

                TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);

                for(MeterViewTag mt : mALTags)  {
                    float x = MeterValueToXPosition(mt.mTagVal);

                    linePaint.setColor(mt.mCRTagColor);
                    canvas.drawLine(x, ln_long_s_y, x, ln_long_e_y, linePaint);

                    tp_normal.setColor(mt.mCRTagColor);
                    tp_normal.setTextSize(mAttrTextSize * DISPLAY_DENSITY);
                    canvas.drawText(mt.mSZTagName, x, text_top_pos, tp_normal);
                }
            }

            /**
             * 通过值获得标尺x坐标
             * @param val       值
             * @return          标尺X坐标
             */
            private float MeterValueToXPosition(int val)    {
                int unit_val = mTotalValue / mAttrLongLineCount;
                int small_unit_val = unit_val / mAttrModeType;

                int big = val / unit_val;
                int small = val % unit_val / small_unit_val;
                int left = val % unit_val % small_unit_val;

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
                int unit_val = mTotalValue / mAttrLongLineCount;
                int small_unit_val = unit_val / mAttrModeType;

                float x_big = 0 == big ?
                                mLongLineWidth / 2
                                : (mAttrLongLineCount == big ?
                                        mVWWidth - mLongLineWidth / 2
                                        : mBigWidthUnit * big - mLongLineWidth / 2 );

                float x_small = mSmallWidthUnit * small;
                float x_left = mSmallWidthUnit * (left / small_unit_val);
                return x_big + x_small + x_left;
            }
        }
        utility helper = new utility();

        canvas.save();
        /*
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), p);
        */

        helper.drawBaseLine();
        helper.drawLines();
        helper.drawTags();
        canvas.restore();
    }
}
