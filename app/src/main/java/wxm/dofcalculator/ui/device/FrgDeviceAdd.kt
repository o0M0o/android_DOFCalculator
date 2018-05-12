package wxm.dofcalculator.ui.device


import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotterknife.bindView
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.dofcalculator.R
import wxm.dofcalculator.define.CameraItem
import wxm.dofcalculator.define.DeviceItem
import wxm.dofcalculator.define.LensItem
import wxm.dofcalculator.ui.base.IFFrgEdit
import wxm.dofcalculator.utility.ContextUtil
import java.math.BigDecimal

/**
 * frg for device add
 * Created by WangXM on2017/3/11.
 */
class FrgDeviceAdd : FrgSupportBaseAdv(), IFFrgEdit {
    // for device
    private val mETDeviceName: TextInputEditText by bindView(R.id.et_device_name)

    // for camera
    private val mETCameraName: TextInputEditText by bindView(R.id.et_camera_name)
    private val mSPSensorSize: Spinner by bindView(R.id.sp_sensor_size)

    // for lens
    private val mETLensName: TextInputEditText by bindView(R.id.et_lens_name)
    private val mETLensMinFocal: TextInputEditText by bindView(R.id.et_lens_min_focal)
    private val mETLensMaxFocal: TextInputEditText by bindView(R.id.et_lens_max_focal)

    // for device sz
    private val mSZNeedDeviceName: String = ContextUtil.getString(R.string.error_need_device_name)

    // for camera sz
    private val mSZWarn: String = ContextUtil.getString(R.string.warn)
    private val mSZNeedCameraName: String = ContextUtil.getString(R.string.error_need_camera_name)
    private val mSZNeedCameraSensorSize: String = ContextUtil.getString(R.string.error_need_camera_sensor_size)

    // for lens sz
    private val mSZNeedLensName: String = ContextUtil.getString(R.string.error_need_lens_name)
    private val mSZFocalRangeMess: String = ContextUtil.getString(R.string.error_focal_range_mess)
    private val mSZNeedMinFocal: String = ContextUtil.getString(R.string.error_need_min_focal)
    private val mSZNeedMaxFocal: String = ContextUtil.getString(R.string.error_need_max_focal)

    // for sensor
    private val mSASensorSize: Array<String> = ContextUtil.getStrArray(R.array.sensor_size)

    /**
     * 根据录入数据生成Camraitem
     * !!不检查数据合法性
     * @return  结果
     */
    private val cameraItem: CameraItem
        get() {
            val ci = CameraItem()

            ci.name = mETCameraName.text.toString()
            ci.filmSize = Math.round(sensorSize.toFloat())
            ci.filmName = filmName
            return ci
        }

    /**
     * 根据录入数据生成Lensitem
     * !!不检查数据合法性
     * @return  结果
     */
    private val lensItem: LensItem
        get() {
            val li = LensItem()

            li.name = mETLensName.text.toString()
            li.minFocal = Integer.valueOf(mETLensMinFocal.text.toString())
            li.maxFocal = Integer.valueOf(mETLensMaxFocal.text.toString())
            return li
        }

    /**
     * 获取传感器对角线尺寸
     * @return  传感器尺寸
     */
    private val sensorSize: BigDecimal
        get() {
            val idx = mSPSensorSize.selectedItemPosition
            if (AdapterView.INVALID_POSITION != idx) {
                val sel_ss = mSASensorSize[idx]
                return BigDecimal(sel_ss.substring(sel_ss.indexOf("|") + 1))
            }

            return BigDecimal.ZERO
        }

    /**
     * 获得传感器名
     * @return  传感器名
     */
    private val filmName: String
        get() {
            val idx = mSPSensorSize.selectedItemPosition
            if (AdapterView.INVALID_POSITION != idx) {
                val org = mSASensorSize[idx]
                return org.substring(0, org.indexOf("|"))
            }

            return ""
        }

    override fun getLayoutID(): Int {
        return R.layout.frg_device_add
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(savedInstanceState: Bundle?) {
        val arrSensor = ArrayList<String>(mSASensorSize.size).apply {
            mSASensorSize.forEach {
                add(it.substring(0, it.indexOf("|")))
            }
        }

        // for sensor size
        ArrayAdapter<CharSequence>(activity, android.R.layout.simple_spinner_item,
                arrSensor.toTypedArray()).let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSPSensorSize.adapter = it
        }
    }


    override fun onAccept(): Boolean {
        if (!(checkDeviceParameter() && checkCameraParameter() && checkLensParameter()))
            return false

        val ci = cameraItem
        val li = lensItem
        if (ContextUtil.duCamera.createData(ci) && ContextUtil.duLens.createData(li)) {
            val di = DeviceItem()
            di.name = mETDeviceName.text.toString()
            di.camera = ci
            di.lens = li

            return ContextUtil.duDevice.createData(di)
        }

        return false
    }

    private fun showWarnAlert(szInfo: String)   {
        AlertDialog.Builder(activity).setTitle(mSZWarn).setMessage(szInfo)
                .create().show()
    }

    /**
     * 检查设备参数
     * @return      检查结果
     */
    private fun checkDeviceParameter(): Boolean {
        if (UtilFun.StringIsNullOrEmpty(mETDeviceName.text.toString())) {
            mETDeviceName.error = mSZNeedDeviceName
            mETDeviceName.requestFocus()

            showWarnAlert(mSZNeedDeviceName)
            return false
        }

        return true
    }

    /**
     * 检查相机参数
     * @return      检查结果
     */
    private fun checkCameraParameter(): Boolean {
        if (UtilFun.StringIsNullOrEmpty(mETCameraName.text.toString())) {
            mETCameraName.error = mSZNeedCameraName
            mETCameraName.requestFocus()

            showWarnAlert(mSZNeedCameraName)
            return false
        }

        if (AdapterView.INVALID_POSITION == mSPSensorSize.selectedItemPosition) {
            mSPSensorSize.requestFocus()

            showWarnAlert(mSZNeedCameraSensorSize)
            return false
        }

        return true
    }

    /**
     * 检查相机参数
     * @return      检查结果
     */
    private fun checkLensParameter(): Boolean {
        if (UtilFun.StringIsNullOrEmpty(mETLensName.text.toString())) {
            mETLensName.error = mSZNeedLensName
            mETLensName.requestFocus()

            showWarnAlert(mSZNeedLensName)
            return false
        }

        val cMin = mETLensMinFocal.text.toString()
        val cMax = mETLensMaxFocal.text.toString()
        if (UtilFun.StringIsNullOrEmpty(cMin)) {
            mETLensMinFocal.error = mSZNeedMinFocal
            mETLensMinFocal.requestFocus()

            showWarnAlert(mSZNeedMinFocal)
            return false
        }

        if (UtilFun.StringIsNullOrEmpty(cMax)) {
            mETLensMaxFocal.error = mSZNeedMaxFocal
            mETLensMaxFocal.requestFocus()

            showWarnAlert(mSZNeedMaxFocal)
            return false
        }

        if (Integer.valueOf(cMin) > Integer.valueOf(cMax)) {
            mETLensMinFocal.requestFocus()

            showWarnAlert(mSZFocalRangeMess)
            return false
        }

        return true
    }
}
