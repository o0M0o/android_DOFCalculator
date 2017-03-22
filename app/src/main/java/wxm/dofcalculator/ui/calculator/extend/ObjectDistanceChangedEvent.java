package wxm.dofcalculator.ui.calculator.extend;

/**
 * 物距变化事件
 * Created by ookoo on 2017/3/22.
 */
public class ObjectDistanceChangedEvent {
    private float   mODObjectDistance;

    public ObjectDistanceChangedEvent(float od) {
        mODObjectDistance = od;
    }

    /**
     * 获取物距(单位 : m)
     * @return  物距
     */
    public float getObjectDistance()    {
        return mODObjectDistance;
    }
}
