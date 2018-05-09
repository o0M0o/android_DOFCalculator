package wxm.dofcalculator.ui.calculator


import android.os.Bundle
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.dofcalculator.R
import wxm.dofcalculator.define.DeviceItem
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.ui.calculator.event.AttrChangedEvent
import wxm.dofcalculator.ui.calculator.event.CameraSettingChangeEvent
import wxm.dofcalculator.ui.calculator.view.VWCameraAdjust
import wxm.dofcalculator.ui.calculator.view.VWDof
import wxm.dofcalculator.utility.ContextUtil
import java.math.BigDecimal

/**
 * first frg for app
 * Created by WangXM on2017/3/11.
 */
class FrgCalculatorPortrait : FrgSupportBaseAdv() {
    private val mEVWDof: VWDof by bindView(R.id.evw_dof)
    private val mEVWCamera: VWCameraAdjust by bindView(R.id.eca_adjust)

    private lateinit var mDICurDevice: DeviceItem

    /**
     * 设置变化处理器
     * @param event     事件参数
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAttrChangeEvent(event: AttrChangedEvent) {
        if (isVisible) {
            loadUI(null)
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.frg_calculator_portrait
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun initUI(savedInstanceState: Bundle?) {
        val dId = arguments.getInt(ACCalculator.KEY_DEVICE_ID, GlobalDef.INVAILD_ID)
        if (GlobalDef.INVAILD_ID != dId) {
            mDICurDevice = ContextUtil.duDevice.getData(dId)
        }

        loadUI(savedInstanceState)
    }

    override fun loadUI(savedInstanceState: Bundle?) {
        EventBus.getDefault().post(
                CameraSettingChangeEvent(
                        Integer.valueOf(mEVWCamera.curLensFocal.removeSuffix("mm")),
                        BigDecimal(mEVWCamera.curLensAperture.removePrefix("F")),
                        mEVWCamera.curObjectDistance, mDICurDevice))
    }
}
