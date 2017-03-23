package wxm.dofcalculator.ui.calculator.extend;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * 滑动尺子
 * Created by ookoo on 2017/3/22.
 */
public class RadioRuler extends View {


    private Bitmap mRulerBg = null;

    private Bitmap mBgScale = null;

    private Bitmap mBgScaleCover = null;

    private Bitmap mPointer = null;

    private final int UNIT_WIDTH = 15;

    private Paint mFrePaint = null;

    private float mScrollDistance = 0;

    private float mStartViewPosition = 0;

    private float mStartX = 0;

    private final String TAG = "Ruler";

    private static final float START_SCALE_FM = 84;

    private static final float SCALE_UNIT_FM = 1;

    private static final float MAX_SCALE_FM = 110f;

    private static final float START_FREQ_FM = 83.5f;

    private static final float START_SCALE_AM = 500;

    private static final float START_FREQ_AM = 495;

    private static final float MAX_SCALE_AM = 1600;

    private static final float SCALE_UNIT_AM = 10;

    public static final int RULER_TYPE_FM = 1001;

    public static final int RULER_TYPE_AM = 1002;

    public static final int RULER_TYPE_NONE = 1000;

    private int mRUlerType = RULER_TYPE_NONE;

    private final int POINTER_POSITION = 405;

    private float mLastScale = 0;

    private Scroller mScroller = null;

    private ScrollListener mScrollListener = null;


    public RadioRuler(Context context, AttributeSet attrs) {
        super(context, attrs);


        mScroller = new Scroller(context);

        setFocusable(true);


        mFrePaint = new Paint();
        mFrePaint.setAntiAlias(true);
        mFrePaint.setColor(0xFFFFFFFF);
        mFrePaint.setTextSize(15);

        if (RULER_TYPE_NONE == mRUlerType) {
            setRulerType(RULER_TYPE_FM);
        }
    }

    public void initalSrc(Bitmap barScale, Bitmap bgscale, Bitmap cover, Bitmap point) {
        mRulerBg = barScale;
        mBgScale = bgscale;
        mBgScaleCover = cover;
        mPointer = point;
    }

    public void setRulerType(int rulerTYpe) {
        switch (rulerTYpe) {
            case RULER_TYPE_AM:
                mRUlerType = RULER_TYPE_AM;
                break;

            default:
                mRUlerType = RULER_TYPE_FM;
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawRuler(canvas);
    }


    public void DrawRuler(Canvas canvas) {
        canvas.drawBitmap(mBgScale, 0, 0, null);
        Log.d(TAG, "startview position is " + mStartViewPosition);
        Log.d(TAG, "scroll distance position is " + mScrollDistance);

        mStartViewPosition = mScrollDistance % 300.0f;

        if (mStartViewPosition < -100) {
            canvas.drawBitmap(mRulerBg, 900 + mStartViewPosition, 20, null);
        }

        // mRulerBg 的长度为300，收音机标尺的长度为800，所以需要根据我们虚拟的位移来计算不同的应该画图的位置
        canvas.drawBitmap(mRulerBg, mStartViewPosition, 20, null);
        canvas.drawBitmap(mRulerBg, mStartViewPosition + 300, 20, null);
        canvas.drawBitmap(mRulerBg, mStartViewPosition + 600, 20, null);

        drawKeDu(canvas);

        canvas.drawBitmap(mPointer, 395, 20, null);

        canvas.drawBitmap(mBgScaleCover, 0, 0, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(800, 72);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mScroller != null && !mScroller.isFinished()) {
            return true;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:

                float movedistance = event.getX() - mStartX;
                mStartX = event.getX();
                drawWithDistance(movedistance);

                if (mScrollListener != null) {
                    mScrollListener.onScroll();
                }
                break;
            case MotionEvent.ACTION_UP:

                float offset = mScrollDistance % 15;
                moveToScrollDistance(-offset);

                if (mScrollListener != null) {
                    mScrollListener.onScrollEnd(getCurrentFreq());
                }
                break;


            default:
                break;
        }

        return true;
    }

    private void drawWithDistance(float moveInstance) {
        if (moveInstance < 0 && mLastScale > getMaxScale()) {
            return;
        }
        if (mScrollDistance + moveInstance <= 0) {
            mScrollDistance += moveInstance;
            mStartViewPosition = mScrollDistance % 300.0f;
            if (mStartViewPosition > 0) {
                mStartViewPosition = mStartViewPosition - 300;
            } else if (mStartViewPosition < -300) {
                mStartViewPosition = 300 + mStartViewPosition;
            }
            invalidate();
        } else {
            mScrollDistance = 0;
            mStartViewPosition = 0;
            invalidate();
        }
    }

    private void drawKeDu(Canvas canvas) {

        float KeduPosition = mScrollDistance + 68;

        int startInScreen = -1;
        while (KeduPosition <= 800) {

            startInScreen++;

            if (KeduPosition < 0) {
                KeduPosition += 150;
                continue;
            } else {
                switch (mRUlerType) {
                    case RULER_TYPE_AM:
                        mLastScale = START_SCALE_AM + startInScreen * SCALE_UNIT_AM;

                        break;

                    default:
                        mLastScale = START_SCALE_FM + startInScreen * SCALE_UNIT_FM;
                        break;
                }

                canvas.drawText(mLastScale + "", KeduPosition, 15, mFrePaint);
                KeduPosition += 150;
            }
        }
    }

    private float getMaxScale() {
        if (mRUlerType == RULER_TYPE_AM) {
            return MAX_SCALE_AM;
        } else {
            return MAX_SCALE_FM;
        }
    }

    private void moveToScrollDistance(float specialDistance) {
        mScroller.startScroll((int) mScrollDistance, 0, (int) specialDistance, 0, 100);
        while (mScroller.computeScrollOffset()) {
            mScrollDistance = mScroller.getCurrX();
            invalidate();
        }
    }

    public void setScrollListener(ScrollListener listener) {
        mScrollListener = listener;
    }

    public static interface ScrollListener {

        public void onScroll();

        public void onScrollEnd(float freq);

    }

    private float getCurrentFreq() {

        int unitCount = Math.abs((int) FloatUtility.divide(mScrollDistance, 15));

        float freq = 0;
        switch (mRUlerType) {
            case RULER_TYPE_FM:
                freq = FloatUtility.add(2.7f, FloatUtility.add(START_FREQ_FM, FloatUtility.mulitiply(unitCount, FloatUtility.divide(SCALE_UNIT_FM, 10f))));
                break;
            case RULER_TYPE_AM:
                freq = FloatUtility.add(27, FloatUtility.add(START_FREQ_AM, FloatUtility.mulitiply(unitCount, FloatUtility.divide(SCALE_UNIT_AM, 10f))));
                break;


            default:
                break;
        }

        return freq;
    }

    public void setFreq(int rulerType, float freq) {
        switch (rulerType) {
            case RULER_TYPE_FM:
                mRUlerType = RULER_TYPE_FM;
                float unitCount_FM = (freq - START_FREQ_FM) / 0.1f - POINTER_POSITION / UNIT_WIDTH;
                mScrollDistance = -unitCount_FM * 15;
                break;
            case RULER_TYPE_AM:
                mRUlerType = RULER_TYPE_AM;
                float unitCount_AM = (freq - START_FREQ_AM) / 1f - POINTER_POSITION / UNIT_WIDTH;
                mScrollDistance = -unitCount_AM * 15;
                break;

            default:
                break;
        }
    }

    public void smoothScrollToFreq(float freq) {
        float distance = 0;

        switch (mRUlerType) {
            case RULER_TYPE_FM:
                float unitCount_FM = (freq - START_FREQ_FM) / 0.1f - POINTER_POSITION / UNIT_WIDTH;
                distance = -unitCount_FM * 15;
                break;
            case RULER_TYPE_AM:
                float unitCount_AM = (freq - START_FREQ_AM) / 1f - POINTER_POSITION / UNIT_WIDTH;
                distance = -unitCount_AM * 15;
                break;

            default:
                break;
        }

        moveToScrollDistance(distance - mScrollDistance);
    }

    // 用于浮点数的精确计算
    public static class FloatUtility {

        public static float add(float f1, float f2) {
            BigDecimal mbd1 = new BigDecimal(Float.toString(f1));
            BigDecimal mbd2 = new BigDecimal(Float.toString(f2));
            return mbd1.add(mbd2).floatValue();
        }

        public static float mulitiply(float f1, float f2) {
            BigDecimal mbd1 = new BigDecimal(Float.toString(f1));
            BigDecimal mbd2 = new BigDecimal(Float.toString(f2));
            return mbd1.multiply(mbd2).floatValue();
        }

        public static float divide(float f1, float f2) {
            BigDecimal mbd1 = new BigDecimal(Float.toString(f1));
            BigDecimal mbd2 = new BigDecimal(Float.toString(f2));
            return mbd1.divide(mbd2).floatValue();
        }
    }


}