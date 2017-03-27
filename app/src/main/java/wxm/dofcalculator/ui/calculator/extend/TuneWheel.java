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
    public interface TagTranslate {
        /**
         * 得到标尺显示tag
         *
         * @param val 标尺值
         * @return 显示tag
         */
        String translateTWTag(int val);
    }

    /**
     * 值变动监听器
     */
    public interface OnValueChangeListener {
        /**
         * 值变动接口
         *
         * @param value  当前数值
         * @param valTag 标尺刻度
         */
        void onValueChange(int value, String valTag);
    }

    public static final int MOD_TYPE_HALF = 2;

    private static final int ITEM_HALF_DIVIDER = 20;
    private static final int ITEM_ONE_DIVIDER = 10;


    private int mLastX, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    /**
     * 可设置属性
     */
    private String mAttrSZPostUnit;
    private String mAttrSZPrvUnit;
    private int mAttrMinValue;
    private int mAttrMaxValue;
    private int mAttrCurValue;

    private int mAttrTextSize;
    private int mAttrMaxHeight;
    private int mAttrMinHeight;
    private int mAttrModeType;

    /**
     * 固定变量
     */
    private int TEXT_COLOR_HOT;
    private int TEXT_COLOR_NORMAL;
    private int LINE_COLOR_CURSOR;
    private float DISPLAY_DENSITY;
    private int LINE_DIVIDER = ITEM_HALF_DIVIDER;


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
        DISPLAY_DENSITY = getContext().getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        // for parameter
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TuneWheel);
        try {
            String sz_unit = array.getString(R.styleable.TuneWheel_twPostUnit);
            mAttrSZPostUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            sz_unit = array.getString(R.styleable.TuneWheel_twPrvUnit);
            mAttrSZPrvUnit = UtilFun.StringIsNullOrEmpty(sz_unit) ? "" : sz_unit;

            mAttrModeType = array.getInt(R.styleable.TuneWheel_twModeType, 2);
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
     * 设置用于接收结果的监听器
     *
     * @param listener 监听器
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
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
     * 获取当前刻度值
     *
     * @return 当前值
     */
    public float getCurValue() {
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
     *
     * @param canvas context
     */
    private void drawScaleLine(Canvas canvas) {
        class utility {
            /**
             * 计算显示位置
             * @param tag           tag
             * @param xPosition     起始x坐标
             * @param textWidth     字体宽度
             * @return 偏移坐标
             */
            private float countLeftStart(String tag, float xPosition, float textWidth) {
                return xPosition - ((tag.length() * textWidth) / 2);
            }

            /**
             * 中间的红色指示线
             * @param canvas     画布
             * @param s_y        起始Y坐标
             * @param e_y        结束Y坐标
             */
            private void drawMiddleLine(Canvas canvas, int s_y, int e_y) {
                int indexWidth = 12;

                Paint redPaint = new Paint();
                redPaint.setStrokeWidth(indexWidth);
                redPaint.setColor(LINE_COLOR_CURSOR);
                canvas.drawLine(mWidth / 2, s_y, mWidth / 2, e_y, redPaint);
            }
        }
        utility helper = new utility();


        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(TEXT_COLOR_NORMAL);

        TextPaint tp_normal = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_normal.setTextSize(mAttrTextSize * DISPLAY_DENSITY);

        TextPaint tp_big = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp_big.setTextSize((mAttrTextSize + 2) * DISPLAY_DENSITY);
        tp_big.setColor(TEXT_COLOR_HOT);

        int width = mWidth, drawCount = 0;
        float xPosition;
        float textWidth = Layout.getDesiredWidth("0", tp_normal);

        int text_top_pos = getTop() + getPaddingTop() - 2;
        int text_bottom_pos = getHeight() - (int) textWidth;

        int left_h = getHeight() / 2;
        int ln_long_s_y = left_h - (int) DISPLAY_DENSITY * mAttrMaxHeight / 2;
        int ln_long_e_y = left_h + (int) DISPLAY_DENSITY * mAttrMaxHeight / 2;
        int ln_short_s_y = left_h - (int) DISPLAY_DENSITY * mAttrMinHeight / 2;
        int ln_short_e_y = left_h + (int) DISPLAY_DENSITY * mAttrMinHeight / 2;

        for (int i = 0; drawCount <= 4 * width; i++) {
            xPosition = (width / 2 - mMove) + i * LINE_DIVIDER * DISPLAY_DENSITY;
            if (xPosition + getPaddingRight() < mWidth) {
                int cur_v = mAttrCurValue + i;
                if (cur_v <= mAttrMaxValue) {
                    String tw_tag = mTTTranslator.translateTWTag(cur_v);
                    if ((cur_v - mAttrMinValue) % mAttrModeType == 0) {
                        canvas.drawLine(xPosition, ln_long_s_y, xPosition, ln_long_e_y, linePaint);

                        canvas.drawText(tw_tag,
                                helper.countLeftStart(tw_tag, xPosition, textWidth),
                                text_bottom_pos, tp_normal);

                        if (0 == i)
                            canvas.drawText(tw_tag,
                                    helper.countLeftStart(tw_tag, xPosition, textWidth),
                                    text_top_pos, tp_big);
                    } else {
                        canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);

                        if (0 == i) {
                            canvas.drawText(tw_tag,
                                    helper.countLeftStart(tw_tag, xPosition, textWidth),
                                    text_top_pos, tp_big);
                        }
                    }
                }
            }

            if (0 != i) {
                xPosition = (width / 2 - mMove) - i * LINE_DIVIDER * DISPLAY_DENSITY;
                if (xPosition > getPaddingLeft()) {
                    int cur_v = mAttrCurValue - i;
                    if (cur_v >= mAttrMinValue) {
                        if ((cur_v - mAttrMinValue) % mAttrModeType == 0) {
                            String tw_tag = mTTTranslator.translateTWTag(cur_v);
                            canvas.drawLine(xPosition, ln_long_s_y, xPosition,
                                    ln_long_e_y, linePaint);

                            canvas.drawText(tw_tag,
                                    helper.countLeftStart(tw_tag, xPosition, textWidth),
                                    text_bottom_pos, tp_normal);
                        } else {
                            canvas.drawLine(xPosition, ln_short_s_y, xPosition, ln_short_e_y, linePaint);
                        }
                    }
                }
            }

            drawCount += 2 * LINE_DIVIDER * DISPLAY_DENSITY;
        }

        helper.drawMiddleLine(canvas, ln_long_s_y, ln_long_e_y);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TouchUtility helper = new TouchUtility();
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
                helper.changeMoveAndValue();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                helper.countMoveEnd();
                helper.countVelocityTracker(event);
                return false;

            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        TouchUtility helper = new TouchUtility();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                helper.countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                helper.changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }

    /**
     * 数据变化后调用监听器
     */
    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(mAttrCurValue, mTTTranslator.translateTWTag(mAttrCurValue));
        }
    }

    /**
     * 触摸功能辅助类
     */
    private class TouchUtility   {
        private void countVelocityTracker(MotionEvent event) {
            mVelocityTracker.computeCurrentVelocity(1000);
            float xVelocity = mVelocityTracker.getXVelocity();
            if (Math.abs(xVelocity) > mMinVelocity) {
                mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            }
        }

        private void changeMoveAndValue() {
            int tValue = (int) (mMove / (LINE_DIVIDER * DISPLAY_DENSITY));
            if (Math.abs(tValue) > 0) {
                mAttrCurValue += tValue;
                mMove -= tValue * LINE_DIVIDER * DISPLAY_DENSITY;
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
            int roundMove = Math.round(mMove / (LINE_DIVIDER * DISPLAY_DENSITY));
            mAttrCurValue = mAttrCurValue + roundMove;
            mAttrCurValue = Math.min(Math.max(mAttrMinValue, mAttrCurValue), mAttrMaxValue);

            mLastX = 0;
            mMove = 0;

            notifyValueChange();
            postInvalidate();
        }
    }
}
