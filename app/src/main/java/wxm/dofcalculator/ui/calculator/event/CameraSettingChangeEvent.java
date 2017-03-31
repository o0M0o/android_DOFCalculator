package wxm.dofcalculator.ui.calculator.event;

import java.math.BigDecimal;

/**
 * 相机设定调整事件
 * Created by ookoo on 2017/3/22.
 */
public class CameraSettingChangeEvent {
    private BigDecimal  mBDCOC;
    private int         mLFLensFocal;
    private BigDecimal  mBDlensAperture;

    private int         mODObjectDistance;


    public CameraSettingChangeEvent(BigDecimal pa, int lf, BigDecimal la, int od)   {
        mBDCOC = pa;
        mLFLensFocal = lf;
        mBDlensAperture = la;

        mODObjectDistance = od;
    }

    /**
     * 获取弥散圆直径(单位 : 毫米)
     * @return      弥散直径
     */
    public BigDecimal getCOC()    {
        return mBDCOC;
    }

    /**
     * 获取镜头焦距(单位 : 毫米)
     * @return      焦距
     */
    public int getLensFocal()   {
        return mLFLensFocal;
    }

    /**
     * 获取镜头光圈
     * @return      光圈
     */
    public BigDecimal getLensAperture() {
        return mBDlensAperture;
    }

    /**
     * 获取物距(单位 : 豪米)
     * @return      物距
     */
    public int getObjectDistance()   {
        return mODObjectDistance;
    }
}
