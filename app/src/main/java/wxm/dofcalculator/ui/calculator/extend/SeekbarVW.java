package wxm.dofcalculator.ui.calculator.extend;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;

import java.util.NavigableMap;

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

    protected TextView  mTVMinTag;
    protected TextView  mTVMiddle1Tag;
    protected TextView  mTVMiddle2Tag;
    protected TextView  mTVMaxTag;

    protected NavigableMap<Integer, String>     mNMSBData = null;
    protected SeekBar.OnSeekBarChangeListener   mSBCLExtend = null;

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
        sz_val = UtilFun.StringIsNullOrEmpty(sz_val) ? "val" : sz_val;

        String sz_min = array.getString(R.styleable.SeekbarVW_szMinTag);
        sz_min = UtilFun.StringIsNullOrEmpty(sz_min) ? "0" : sz_min;

        String sz_middle1 = array.getString(R.styleable.SeekbarVW_szMiddle1Tag);
        sz_middle1 = UtilFun.StringIsNullOrEmpty(sz_middle1) ? "1/3" : sz_middle1;

        String sz_middle2 = array.getString(R.styleable.SeekbarVW_szMiddle2Tag);
        sz_middle2 = UtilFun.StringIsNullOrEmpty(sz_middle2) ? "2/3" : sz_middle2;

        String sz_max = array.getString(R.styleable.SeekbarVW_szMaxTag);
        sz_max = UtilFun.StringIsNullOrEmpty(sz_max) ? "1" : sz_max;

        mTVTag.setText(sz_tag);
        mTVVal.setText(sz_val);
        mTVMinTag.setText(sz_min);
        mTVMiddle1Tag.setText(sz_middle1);
        mTVMiddle2Tag.setText(sz_middle2);
        mTVMaxTag.setText(sz_max);

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
     * 获取当前seekbar的值
     * @return  当前值
     */
    public String getCurVal()   {
        return mTVVal.getText().toString();
    }

    /**
     * 设置seekbar的数据
     * @param nm_data   数据
     */
    public void setSeekbarMap(NavigableMap<Integer, String> nm_data)    {
        mNMSBData = nm_data;

        mTVMinTag.setText(mNMSBData.floorEntry(0).getValue());
        mTVMiddle1Tag.setText(mNMSBData.floorEntry(30).getValue());
        mTVMiddle2Tag.setText(mNMSBData.floorEntry(70).getValue());
        mTVMaxTag.setText(mNMSBData.floorEntry(100).getValue());

        String sz_h = mNMSBData.floorEntry(mSBBar.getProgress()).getValue();
        mTVVal.setText(sz_h);
    }

    /**
     * 设置seekbar的扩展监听器
     * @param sl        扩展监听器
     */
    public void setSeekbarExtendListner(SeekBar.OnSeekBarChangeListener sl) {
        mSBCLExtend = sl;
    }


    /**
     * 初始化UI元件
     */
    private void initUIComponent()  {
        LayoutInflater.from(getContext()).inflate(R.layout.vw_seekbar, this);

        mTVTag = UtilFun.cast_t(findViewById(R.id.tv_tag));
        mTVVal = UtilFun.cast_t(findViewById(R.id.tv_val));
        mSBBar = UtilFun.cast_t(findViewById(R.id.sb_bar));

        mTVMinTag = UtilFun.cast_t(findViewById(R.id.tv_min_tag));
        mTVMiddle1Tag = UtilFun.cast_t(findViewById(R.id.tv_middle1_tag));
        mTVMiddle2Tag = UtilFun.cast_t(findViewById(R.id.tv_middle2_tag));
        mTVMaxTag = UtilFun.cast_t(findViewById(R.id.tv_max_tag));

        mSBBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(null != mSBCLExtend) {
                    mSBCLExtend.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(null != mSBCLExtend) {
                    mSBCLExtend.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(null != mNMSBData)   {
                    String sz_h = mNMSBData.floorEntry(seekBar.getProgress()).getValue();
                    mTVVal.setText(sz_h);
                }

                if(null != mSBCLExtend) {
                    mSBCLExtend.onStopTrackingTouch(seekBar);
                }

                EventBus.getDefault().post(new SeekbarChangedEvent((int)seekBar.getTag()));
            }
        });
    }
}
