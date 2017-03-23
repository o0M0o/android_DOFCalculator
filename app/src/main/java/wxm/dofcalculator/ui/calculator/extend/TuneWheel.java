package wxm.dofcalculator.ui.calculator.extend;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
     * 值变动监听器
     */
    interface OnValueChangeListener {
        /**
         * 值变动接口
         * @param value     当前数值
         */
        void onValueChange(float value);
    }

    public static final int MOD_TYPE_HALF = 2;
    public static final int MOD_TYPE_ONE = 10;

    private static final int ITEM_HALF_DIVIDER = 28;
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
    private String  mAttrSZUnit;
    private int     mAttrValueStep;
    private int     mAttrMinValue;
    private int     mAttrMaxValue;
    private int     mAttrCurValue;

    private int     mAttrTextSize;
    private int     mAttrMaxHeight;
    private int     mAttrMinHeight;

    public TuneWheel(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;

        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        // setBackgroundResource(R.drawable.bg_wheel);
        //setBackgroundDrawable(createBackground());

        // for parameter
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TuneWheel);
        try {
            String sz_unit = array.getString(R.styleable.TuneWheel_twUnit);
            mAttrSZUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

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
     * @param listener
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前刻度值
     *
     * @return
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
        drawMiddleLine(canvas);
    }


    /**
     * 从中间往两边开始画刻度线
     * @param canvas    context
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.BLACK);

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(mAttrTextSize * mDensity);

        int width = mWidth, drawCount = 0;
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

        for (int i = 0; drawCount <= 4 * width; i++) {
            int numSize = String.valueOf(mAttrCurValue + i).length();
            int v_s = i * mAttrValueStep;

            xPosition = (width / 2 - mMove) + i * mLineDivider * mDensity;
            if (xPosition + getPaddingRight() < mWidth) {
                int cur_i = mAttrCurValue + v_s;
                int cur_v = cur_i;
                switch (mModType)   {
                    case MOD_TYPE_HALF :
                        cur_i /= 2;
                        break;
                }

                if (cur_i % mModType == 0) {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, mDensity * mAttrMaxHeight, linePaint);

                    if (cur_i <= mAttrMaxValue) {
                        switch (mModType) {
                            case MOD_TYPE_HALF:
                                canvas.drawText(String.valueOf(cur_v) + mAttrSZUnit, countLeftStart(mAttrCurValue + i, xPosition, textWidth), getHeight() - textWidth, textPaint);
                                break;
                            case MOD_TYPE_ONE:
                                canvas.drawText(String.valueOf(cur_v) + mAttrSZUnit, xPosition - (textWidth * numSize / 2), getHeight() - textWidth, textPaint);
                                break;

                            default:
                                break;
                        }
                    }
                } else {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, mDensity * mAttrMinHeight, linePaint);
                }
            }

            xPosition = (width / 2 - mMove) - i * mLineDivider * mDensity;
            if (xPosition > getPaddingLeft()) {
                int cur_i = mAttrCurValue - v_s;
                int cur_v = cur_i;
                switch (mModType)   {
                    case MOD_TYPE_HALF :
                        cur_i /= 2;
                        break;
                }

                if (cur_i % mModType == 0) {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, mDensity * mAttrMaxHeight, linePaint);

                    if (cur_i >= mAttrMinValue) {
                        switch (mModType) {
                            case MOD_TYPE_HALF:
                                canvas.drawText(String.valueOf(cur_v) + mAttrSZUnit, countLeftStart(mAttrCurValue - i, xPosition, textWidth), getHeight() - textWidth, textPaint);
                                break;
                            case MOD_TYPE_ONE:
                                canvas.drawText(String.valueOf(cur_v) + mAttrSZUnit, xPosition - (textWidth * numSize / 2), getHeight() - textWidth, textPaint);
                                break;

                            default:
                                break;
                        }
                    }
                } else {
                    canvas.drawLine(xPosition, getPaddingTop(), xPosition, mDensity * mAttrMinHeight, linePaint);
                }
            }

            drawCount += 2 * mLineDivider * mDensity;
        }

        canvas.restore();
    }

    /**
     * 计算没有数字显示位置的辅助方法
     *
     * @param value
     * @param xPosition
     * @param textWidth
     * @return
     */
    private float countLeftStart(int value, float xPosition, float textWidth) {
        float xp = 0f;
        if (value < 20) {
            xp = xPosition - (textWidth * 1 / 2);
        } else {
            xp = xPosition - (textWidth * 2 / 2);
        }
        return xp;
    }

    /**
     * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        // TOOD 常量太多，暂时放这，最终会放在类的开始，放远了怕很快忘记
        int gap = 12, indexWidth = 8, indexTitleWidth = 24, indexTitleHight = 10, shadow = 6;
        String color = "#66999999";

        canvas.save();

        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(indexWidth);
        redPaint.setColor(Color.RED);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, redPaint);

        Paint ovalPaint = new Paint();
        ovalPaint.setColor(Color.RED);
        ovalPaint.setStrokeWidth(indexTitleWidth);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, indexTitleHight, ovalPaint);
        canvas.drawLine(mWidth / 2, mHeight - indexTitleHight, mWidth / 2, mHeight, ovalPaint);

        // RectF ovalRectF = new RectF(mWidth / 2 - 10, 0, mWidth / 2 + 10, 4 *
        // mDensity); //TODO 椭圆
        // canvas.drawOval(ovalRectF, ovalPaint);
        // ovalRectF.set(mWidth / 2 - 10, mHeight - 8 * mDensity, mWidth / 2 +
        // 10, mHeight); //TODO

        Paint shadowPaint = new Paint();
        shadowPaint.setStrokeWidth(shadow);
        shadowPaint.setColor(Color.parseColor(color));
        canvas.drawLine(mWidth / 2 + gap, 0, mWidth / 2 + gap, mHeight, shadowPaint);

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
            mListener.onValueChange(mAttrCurValue);
            /*
            if (mModType == MOD_TYPE_ONE) {
                mListener.onValueChange(mAttrCurValue);
            }
            if (mModType == MOD_TYPE_HALF) {
                mListener.onValueChange(mAttrCurValue / 2f);
            }
            */
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
