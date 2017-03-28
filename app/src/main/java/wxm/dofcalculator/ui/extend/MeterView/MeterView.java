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
            private void drawBaseLine() {
                Paint basePaint = new Paint();
                basePaint.setStrokeWidth(mAttrBaseLineWidth);
                basePaint.setColor(TEXT_COLOR_NORMAL);

                int y = mVWHeight - mAttrBaseLineWidth / 2;
                canvas.drawLine(0, y, mVWWidth, y, basePaint);
            }

            private void drawLines() {
                int w_line = 2;
                int w_long_line = 4;
                // for paint
                Paint linePaint = new Paint();
                linePaint.setStrokeWidth(w_line);
                linePaint.setColor(TEXT_COLOR_NORMAL);

                Paint LongLinePaint = new Paint();
                LongLinePaint.setStrokeWidth(w_long_line);
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

                int unit_val = (mAttrMaxValue - mAttrMinValue) / mAttrLongLineCount;
                int w_long = mVWWidth / mAttrLongLineCount;
                int w_short = w_long / mAttrModeType;

                for(int i = 0; i <= mAttrLongLineCount; i++) {
                    int hot_x = 0 == i ?
                                w_long_line / 2
                                : (mAttrLongLineCount == i ?  mVWWidth - w_long_line / 2 : w_long * i);
                    canvas.drawLine(hot_x, ln_long_s_y, hot_x, ln_long_e_y, LongLinePaint);

                    String tw_tag = mTTTranslator.translateTWTag(mAttrMinValue + unit_val * i);
                    int x_start;
                    if(0 == i)  {
                        x_start = 0;
                    } else if (mAttrLongLineCount == i)   {
                        x_start = hot_x - (int)(tw_tag.length() * textWidth) - 4;
                    } else  {
                        x_start = hot_x - (int)(tw_tag.length() * textWidth) / 2;
                    }

                    canvas.drawText(tw_tag, x_start,
                            text_top_pos, tp_normal);

                    for(int j = 1; j < mAttrModeType; j++)  {
                        int cur_x = w_long * i + w_short * j;
                        canvas.drawLine(cur_x, ln_short_s_y, cur_x, ln_short_e_y, linePaint);
                    }
                }
            }
        }
        utility helper = new utility();

        canvas.save();
        helper.drawBaseLine();
        helper.drawLines();
        canvas.restore();
    }
}
