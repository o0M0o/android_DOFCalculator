package wxm.dofcalculator.ui.calculator


import android.os.Bundle

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.math.BigDecimal

import butterknife.BindView
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.dofcalculator.R
import wxm.dofcalculator.define.DeviceItem
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.ui.calculator.event.AttrChangedEvent
import wxm.dofcalculator.ui.calculator.event.CameraSettingChangeEvent
import wxm.dofcalculator.ui.calculator.view.VWCameraAdjust
import wxm.dofcalculator.ui.calculator.view.VWDof
import wxm.dofcalculator.utility.ContextUtil

/**
 * first frg for app
 * Created by WangXM on2017/3/11.
 */
class FrgCalculatorLandscape : FrgSupportBaseAdv() {
    @BindView(R.id.evw_dof)
    internal var mEVWDof: VWDof? = null

    @BindView(R.id.eca_adjust)
    internal var mEVWCamera: VWCameraAdjust? = null

    private var mDICurDevice: DeviceItem? = null

    /**
     * 设置变化处理器
     * @param event     事件参数
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAttrChangeEvent(event: AttrChangedEvent) {
        if (isVisible) {
            updateResultUI()
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.frg_calculator_landscape
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun initUI(savedInstanceState: Bundle?) {
        val d_id = arguments.getInt(ACCalculator.KEY_DEVICE_ID, GlobalDef.INVAILD_ID)
        if (GlobalDef.INVAILD_ID != d_id) {
            mDICurDevice = ContextUtil.duDevice.getData(d_id)
        }
    }

    override fun loadUI(savedInstanceState: Bundle?) {
        updateResultUI()
    }

    /**
     * 更新结果UI
     */
    protected fun updateResultUI() {
        val sz_lf_hot = mEVWCamera!!.curLensFocal
        val sz_la_hot = mEVWCamera!!.curLensAperture
        val od = mEVWCamera!!.curObjectDistance

        val lf_hot = Integer.valueOf(sz_lf_hot.substring(0, sz_lf_hot.indexOf("mm")))
        val aperture = BigDecimal(sz_la_hot.substring(sz_la_hot.indexOf("F") + 1))

        EventBus.getDefault().post(CameraSettingChangeEvent(lf_hot, aperture, od, mDICurDevice!!))
    }
}
