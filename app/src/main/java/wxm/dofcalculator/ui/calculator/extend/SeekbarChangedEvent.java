package wxm.dofcalculator.ui.calculator.extend;

/**
 * seekbar progress
 * Created by ookoo on 2017/3/20.
 */
public class SeekbarChangedEvent {
    int mSBTag;

    public SeekbarChangedEvent(int tag)    {
        mSBTag = tag;
    }

    public int getSeekbarTag()  {
        return mSBTag;
    }
}
