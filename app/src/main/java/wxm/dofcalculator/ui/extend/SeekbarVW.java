package wxm.dofcalculator.ui.extend;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.TextView;


import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;

/**
 * extend view for seekbar
 * Created by ookoo on 2017/3/19.
 */
public class SeekbarVW extends ConstraintLayout {
    protected TextView  mTVTag;
    protected TextView  mTVVal;
    protected SeekBar   mSBBar;

    public SeekbarVW(Context context) {
        super(context);
        initUIComponent();
    }

    public SeekbarVW(Context context, AttributeSet attrs){
        super(context, attrs);
        initUIComponent();

        //TypedArray是一个用来存放由context.obtainStyledAttributes获得的属性的数组
        //在使用完成后，一定要调用recycle方法
        //属性的名称是styleable中的名称+“_”+属性名称
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SeekbarVW);
        String sz_tag = array.getString(R.styleable.SeekbarVW_szTag);
        sz_tag = UtilFun.StringIsNullOrEmpty(sz_tag) ? "tag" : sz_tag;

        String sz_val = array.getString(R.styleable.SeekbarVW_szVal);
        sz_val = UtilFun.StringIsNullOrEmpty(sz_val) ? "tag" : sz_val;

        mTVTag.setText(sz_tag);
        mTVVal.setText(sz_val);

        array.recycle();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    /**
     * 返回seekbar
     * @return      seekbar
     */
    public SeekBar getSeekBar() {
        return mSBBar;
    }

    /**
     * 设置当前seekbar值
     * @param val   当前值
     */
    public void setCurVal(String val)   {
        mTVVal.setText(val);
        invalidate();
        requestLayout();
    }


    /**
     * 初始化UI元件
     */
    private void initUIComponent()  {
        LayoutInflater.from(getContext()).inflate(R.layout.sb_item, this);

        mTVTag = UtilFun.cast_t(findViewById(R.id.tv_tag));
        mTVVal = UtilFun.cast_t(findViewById(R.id.tv_val));
        mSBBar = UtilFun.cast_t(findViewById(R.id.sb_bar));
    }
}
