package wxm.dofcalculator.ui.calculator.extend;

/**
 * 景深变化事件
 * Created by ookoo on 2017/3/20.
 */
public class DofChangedEvent {
    private float mNMFrontDof;
    private float mNMObjectDistance;
    private float mNMBackDof;

    public DofChangedEvent(float f, float o, float b)    {
        mNMFrontDof = f;
        mNMObjectDistance = o;
        mNMBackDof = b;
    }

    /**
     * 得到前景深
     * @return      前景深
     */
    public float getFrontDof()    {
        return mNMFrontDof;
    }

    /**
     * 得到后景深
     * @return      后景深
     */
    public float getBackDof()    {
        return mNMBackDof;
    }

    /**
     * 得到物距
     * @return      物距
     */
    public float getObjectDistance()    {
        return mNMObjectDistance;
    }
}
