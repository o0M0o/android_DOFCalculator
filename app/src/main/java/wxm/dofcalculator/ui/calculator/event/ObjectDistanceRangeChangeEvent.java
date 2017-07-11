package wxm.dofcalculator.ui.calculator.event;

/**
 * 物距范围调整事件
 * Created by ookoo on 2017/3/22.
 */
public class ObjectDistanceRangeChangeEvent {
    private int         mODMin;
    private int         mODMax;

    public ObjectDistanceRangeChangeEvent(int min, int max)   {
        mODMin = min;
        mODMax = max;
    }

    /**
     * 获取最小物距(单位 : 米)
     * @return     最小物距
     */
    public int getObjectDistanceMin()   {
        return mODMin;
    }

    /**
     * 获取最大物距(单位 : 米)
     * @return      最大物距
     */
    public int getObjectDistanceMax()   {
        return mODMax;
    }
}
