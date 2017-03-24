package wxm.dofcalculator.ui.calculator.extend;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;


/**
 * 卷尺控件类。由于时间比较紧，只有下班后有时间，因此只实现了基本功能。<br>
 * 细节问题包括滑动过程中widget边缘的刻度显示问题等<br>
 * <p>
 * 周末有时间会继续更新<br>
 *
 * @author ttdevs
 * @version create：2014年8月26日
 */
@SuppressLint("ClickableViewAccessibility")
public class TuneWheel extends View {
    /**
     * 生成TuneWheel的标尺tag
     */
    public interface TagTranslate   {
        /**
         * 得到标尺显示tag
         * @param val       标尺值
         * @return          显示tag
         */
        String translateTWTag(int val);
    }

    /**
     * 值变动监听器
     */
    public interface OnValueChangeListener {
        /**
         * 值变动接口
         * @param value     当前数值
         * @param valTag    标尺刻度
         */
        void onValueChange(float value, String valTag);
    }

    public static final int MOD_TYPE_HALF = 2;
    public static final int MOD_TYPE_ONE = 10;

    private static final int ITEM_HALF_DIVIDER = 20;
    private static final int ITEM_ONE_DIVIDER = 10;

    private float mDensity;
    private int mModType = MOD_TYPE_HALF, mLineDivider = ITEM_HALF_DIVIDER;

    private int mLastX, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    /**
     * 可设置属性
     */
    private String  mAttrSZPostUnit;
    private String  mAttrSZPrvUnit;
    private int     mAttrValueStep;
    private int     mAttrMinValue;
    private int     mAttrMaxValue;
    private int     mAttrCurValue;

    private int     mAttrTextSize;
    private int     mAttrMaxHeight;
    private int     mAttrMinHeight;

    /**
     * 静态变量
     */
    private int TEXT_COLOR_HOT;
    private int TEXT_COLOR_NORMAL;
    private int LINE_COLOR_CURSOR;


    /**
     * 把值翻译为tag
     */
    private TagTranslate mTTTranslator = new TagTranslate() {
        @Override
        public String translateTWTag(int val) {
            return mAttrSZPrvUnit + String.valueOf(val) + mAttrSZPostUnit;
        }
    };


    public TuneWheel(Context context, AttributeSet attrs) {
        super(context, attrs);

        // for color
        TEXT_COLOR_NORMAL = Color.BLACK;

        Resources res = context.getResources();
        Resources.Theme te = context.getTheme();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TEXT_COLOR_HOT = res.getColor(R.color.firebrick, te);
            LINE_COLOR_CURSOR = res.getColor(R.color.trans_red, te);
        } else {
            TEXT_COLOR_HOT = res.getColor(R.color.firebrick);
            LINE_COLOR_CURSOR = res.getColor(R.color.trans_red);
        }

        // for others
        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        // for parameter
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TuneWheel);
        try {
            String sz_unit = array.getString(R.styleable.TuneWheel_twPostUnit);
            mAttrSZPostUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            sz_unit = array.getString(R.styleable.TuneWheel_twPrvUnit);
            mAttrSZPrvUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            mAttrValueStep = array.getInt(R.styleable.TuneWheel_twValueStep, 1);
            mAttrMinValue = array.getInt(R.styleable.TuneWheel_twMinValue, 0);
            mAttrMaxValue = array.getInt(R.styleable.TuneWheel_twMaxValue, 100);
            mAttrCurValue = array.getInt(R.styleable.TuneWheel_twValue, 50);

            mAttrTextSize = array.getInt(R.styleable.TuneWheel_twTextSize, 14);
            mAttrMaxHeight = array.getInt(R.styleable.TuneWheel_twMaxHeight, 24);
            mAttrMinHeight = array.getInt(R.styleable.TuneWheel_twMinHeight, 16);
        } finally {
            array.recycle();
        }
    }

    /**
     * 考虑可扩展，但是时间紧迫，只可以支持两种类型效果图中两种类型
     *
     * @param defaultValue    初始值
     * @param maxValue 最大值
     * @param model    刻度盘精度：<br>
     */
    public void initViewParam(int defaultValue, int maxValue, int model) {
        switch (model) {
            case MOD_TYPE_HALF:
                mModType = MOD_TYPE_HALF;
                mLineDivider = ITEM_HALF_DIVIDER;
                mAttrCurValue = defaultValue * 2;
                mAttrMaxValue = maxValue * 2;
                break;

            case MOD_TYPE_ONE:
                mModType = MOD_TYPE_ONE;
                mLineDivider = ITEM_ONE_DIVIDER;
                mAttrCurValue = defaultValue;
                mAttrMaxValue = maxValue;
                break;

            default:
                break;
        }
        invalidate();

        mLastX = 0;
        mMove = 0;
        notifyValueChange();
    }

    /**
     * 设置用于接收结果的监听器
     *
     * @param listener 监听器
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }


    /**
     * 设置标尺刻度翻译器
     * @param tt 翻译器
     */
    public void setTranslateTag(TagTranslate tt) {
        mTTTranslator = tt;
    }

    /**
     * 获取当前刻度值
     * @return   当前值
     */
    public float getValue() {
        return mAttrCurValue;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawScaleLine(canvas);
    }


    /**
     * 从中间往两边开始画刻度线
     * @param canvas    context
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(TEXT_COLOR_NORMAL);

        TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_normal.setTextSize(mAttrTextSize * mDensity);

        TextPaint tp_big = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_big.setTextSize((mAttrTextSize + 2) * mDensity);
        tp_big.setColor(TEXT_COLOR_HOT);

        int width = mWidth, drawCount = 0;
        float xPosition = 0;
        float textWidth = Layout.getDesiredWidth("0", tp_normal);

        //int text_top_pos = getTop() + getPaddingTop() + 2;

        //int left_h = getHeight() / 2 + (int)textWidth / 2;
        int left_h = getHeight() / 2 - (int) textWidth / 2;
        int ln_long_s_y = left_h - (int) mDensity * mAttrMaxHeight / 2;
        int ln_long_e_y = left_h + (int) mDensity * mAttrMaxHeight / 2;
        int ln_short_s_y = left_h - (int) mDensity * mAttrMinHeight / 2;
        int ln_short_e_y = left_h + (int) mDensity * mAttrMinHeight / 2;

        boolean b_skip = false;
        for (int i = 0; drawCount <= 4 * width; i++) {
            xPosition = (width / 2 - mMove) + i * mLineDivider * mDensity;
            if (xPosition + getPaddingRight() < mWidth) {
                int cur_v = mAttrCurValue + i * mAttrValueStep;
                if (cur_v <= mAttrMaxValue) {
                    String tw_tag = mTTTranslator.translateTWTag(cur_v);
                    if ((cur_v / 2) % mModType == 0) {
                        canvas.drawLine(xPosition, ln_long_s_y, xPosition, ln_long_e_y, linePaint);

                        if(!b_skip || 1 != i)
                            canvas.drawText(tw_tag, countLeftStart(tw_tag, xPosition, textWidth),
                                    getHeight() - textWidth, i == 0 ? tp_big : tp_normal);
                    } else {
                        canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);

                        if(0 == i)  {
                            canvas.drawText(tw_tag, countLeftStart(tw_tag, xPosition, textWidth),
                                    getHeight() - textWidth, tp_big);

                            b_skip = true;
                        }
                    }
                }
            }

            if(0 != i) {
                xPosition = (width / 2 - mMove) - i * mLineDivider * mDensity;
                if (xPosition > getPaddingLeft()) {
                    int cur_v = mAttrCurValue - i * mAttrValueStep;
                    if (cur_v >= mAttrMinValue) {
                        if ((cur_v / 2) % mModType == 0) {
                            canvas.drawLine(xPosition, ln_long_s_y, xPosition,
                                    ln_long_e_y, linePaint);

                            if (!b_skip || 1 != i)  {
                                String tw_tag = mTTTranslator.translateTWTag(cur_v);
                                canvas.drawText(tw_tag, countLeftStart(tw_tag, xPosition, textWidth),
                                    getHeight() - textWidth, tp_normal);
                            }
                        } else {
                            canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);
                        }
                    }
                }
            }

            drawCount += 2 * mLineDivider * mDensity;
        }

        canvas.restore();

        drawMiddleLine(canvas, ln_long_s_y, ln_long_e_y);
    }

    /**
     * 计算没有数字显示位置的辅助方法
     *
     * @param tag
     * @param xPosition
     * @param textWidth
     * @return
     */
    private float countLeftStart(String tag, float xPosition, float textWidth) {
        return xPosition - ((tag.length() * textWidth) / 2);
    }

    /**
     * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas, int s_y, int e_y) {
        // TOOD 常量太多，暂时放这，最终会放在类的开始，放远了怕很快忘记
        //int gap = 12, indexWidth = 12, indexTitleWidth = 24, indexTitleHight = 10, shadow = 6;
        //int pad_top = 24;
        //int pad_bottom = 24;
        int indexWidth = 12;

        canvas.save();

        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(indexWidth);
        redPaint.setColor(LINE_COLOR_CURSOR);
        canvas.drawLine(mWidth / 2, s_y, mWidth / 2, e_y, redPaint);

        /*
        Paint ovalPaint = new Paint();
        ovalPaint.setColor(Color.RED);
        ovalPaint.setStrokeWidth(indexTitleWidth);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, indexTitleHight, ovalPaint);
        canvas.drawLine(mWidth / 2, mHeight - indexTitleHight, mWidth / 2, mHeight, ovalPaint);

        Paint shadowPaint = new Paint();
        shadowPaint.setStrokeWidth(shadow);
        shadowPaint.setColor(Color.parseColor(color));
        canvas.drawLine(mWidth / 2 + gap, 0, mWidth / 2 + gap, mHeight, shadowPaint);
        */

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mScroller.forceFinished(true);

                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker(event);
                return false;
            // break;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    private void changeMoveAndValue() {
        int tValue = (int) (mMove / (mLineDivider * mDensity));
        if (Math.abs(tValue) > 0) {
            mAttrCurValue += tValue * mAttrValueStep;
            mMove -= tValue * mLineDivider * mDensity;
            if (mAttrCurValue <= mAttrMinValue || mAttrCurValue > mAttrMaxValue) {
                mAttrCurValue = mAttrCurValue <= mAttrMinValue ? mAttrMinValue : mAttrMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }
            notifyValueChange();
        }
        postInvalidate();
    }

    private void countMoveEnd() {
        int roundMove = Math.round(mMove / (mLineDivider * mDensity));
        mAttrCurValue = mAttrCurValue + roundMove * mAttrValueStep;
        mAttrCurValue = Math.min(Math.max(mAttrMinValue, mAttrCurValue), mAttrMaxValue);

        mLastX = 0;
        mMove = 0;

        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(mAttrCurValue, mTTTranslator.translateTWTag(mAttrCurValue));
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }
}
