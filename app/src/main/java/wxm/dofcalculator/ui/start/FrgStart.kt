package wxm.dofcalculator.ui.start


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotterknife.bindView
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.dofcalculator.R
import wxm.dofcalculator.ui.base.EventHelper
import wxm.dofcalculator.ui.device.ACDevice
import wxm.dofcalculator.utility.ContextUtil

/**
 * first frg for app
 * Created by WangXM on2017/3/11.
 */
class FrgStart : FrgSupportBaseAdv() {
    private val mBTDeviceSelect: Button by bindView(R.id.bt_device_select)

    override fun getLayoutID(): Int {
        return R.layout.frg_start
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        mBTDeviceSelect.visibility = if (0 == ContextUtil.duDevice.count) View.GONE
        else View.VISIBLE

        EventHelper.setOnClickOperator(view!!, intArrayOf(R.id.bt_device_add, R.id.bt_device_select),
                { v ->
                    Intent(activity, ACDevice::class.java).let {
                        when (v.id) {
                            R.id.bt_device_add -> {
                                it.putExtra(ACDevice.KEY_INVOKE_TYPE, ACDevice.VAL_DEVICE_ADD)
                            }

                            R.id.bt_device_select -> {
                                it.putExtra(ACDevice.KEY_INVOKE_TYPE, ACDevice.VAL_DEVICE_SELECTED)
                            }
                        }

                        startActivity(it)
                    }
                })
    }
}
