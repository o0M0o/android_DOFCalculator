package wxm.dofcalculator.ui.calculator.event;

/**
 * seekbar progress
 * Created by WangXM on2017/3/20.
 */
public class AttrChangedEvent {
    int mSBTag;

    public AttrChangedEvent(int tag)    {
        mSBTag = tag;
    }

    public int getSeekbarTag()  {
        return mSBTag;
    }
}
