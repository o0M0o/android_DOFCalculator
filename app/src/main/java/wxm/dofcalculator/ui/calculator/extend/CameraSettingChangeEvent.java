package wxm.dofcalculator.ui.calculator.extend;

import java.math.BigDecimal;

/**
 * 相机设定调整事件
 * Created by ookoo on 2017/3/22.
 */
public class CameraSettingChangeEvent {
    private BigDecimal  mBDPixelArea;
    private int         mLFLensFocal;
    private BigDecimal  mBDlensAperture;

    private int         mODObjectDistance;


    public CameraSettingChangeEvent(BigDecimal pa, int lf, BigDecimal la, int od)   {
        mBDPixelArea = pa;
        mLFLensFocal = lf;
        mBDlensAperture = la;

        mODObjectDistance = od;
    }

    /**
     * 获取像素面积(单位 : 平方毫米)
     * @return      面积
     */
    public BigDecimal getPixelArea()    {
        return mBDPixelArea;
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
