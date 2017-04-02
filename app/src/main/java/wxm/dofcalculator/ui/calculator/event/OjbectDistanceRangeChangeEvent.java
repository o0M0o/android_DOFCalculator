package wxm.dofcalculator.ui.calculator.event;

import java.math.BigDecimal;

/**
 * 物距范围调整事件
 * Created by ookoo on 2017/3/22.
 */
public class OjbectDistanceRangeChangeEvent {
    private int         mODMin;
    private int         mODMax;

    public OjbectDistanceRangeChangeEvent(int min, int max)   {
        mODMin = min;
        mODMax = max;
    }

    /**
     * 获取最小物距(单位 : 毫米)
     * @return      焦距
     */
    public int getObjectDistanceMin()   {
        return mODMin;
    }

    /**
     * 获取最大物距(单位 : 毫米)
     * @return      焦距
     */
    public int getObjectDistanceMax()   {
        return mODMax;
    }
}
