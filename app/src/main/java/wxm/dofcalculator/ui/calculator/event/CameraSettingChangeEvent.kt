package wxm.dofcalculator.ui.calculator.event

import java.math.BigDecimal

import wxm.dofcalculator.define.DeviceItem

/**
 * camera setting changed
 * [lensFocal] for lens focal, unit is mm
 * [lensAperture] for lens aperture
 * [objectDistance] for object distance, unit is mm
 * [device] for camera
 * [cameraCOC] for camera cameraCOC
 * Created by WangXM on 2017/3/22.
 */
data class CameraSettingChangeEvent(
        val lensFocal: Int,
        val lensAperture: BigDecimal,
        val objectDistance: Int,
        val device: DeviceItem) {

    /**
     * 获取弥散圆直径(单位 : 毫米)
     * @return      弥散直径
     */
    val cameraCOC: BigDecimal
        get() = device.camera!!.cameraCOC

}
