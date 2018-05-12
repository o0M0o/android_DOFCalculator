package wxm.dofcalculator.ui.device


import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotterknife.bindView
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.dofcalculator.R
import wxm.dofcalculator.define.CameraItem
import wxm.dofcalculator.define.DeviceItem
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.define.LensItem
import wxm.dofcalculator.ui.base.IFFrgEdit
import wxm.dofcalculator.utility.ContextUtil
import java.math.BigDecimal

/**
 * frg for device add
 * Created by WangXM on2017/3/11.
 */
class FrgDeviceEdit : FrgSupportBaseAdv(), IFFrgEdit {
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

    // for edit data
    private lateinit var mEditData: DeviceItem

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
            return mSPSensorSize.selectedItemPosition.let {
                if (AdapterView.INVALID_POSITION != it) {
                    getSensorSize(mSASensorSize[it])
                } else {
                    BigDecimal.ZERO
                }
            }
        }

    /**
     * 获得传感器名
     * @return  传感器名
     */
    private val filmName: String
        get() {
            return mSPSensorSize.selectedItemPosition.let {
                if (AdapterView.INVALID_POSITION != it) {
                    return getSensorName(mSASensorSize[it])
                } else ""
            }
        }

    override fun getLayoutID(): Int {
        return R.layout.frg_device_add
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(savedInstanceState: Bundle?) {
        activity.intent.getIntExtra(ACDevice.KEY_DEVICE_ID, GlobalDef.INVAILD_ID).let {
            mEditData = ContextUtil.duDevice.getData(it)
        }

        // for sensor size
        ArrayList<String>(mSASensorSize.size).apply {
            mSASensorSize.forEach {
                add(getSensorName(it))
            }
        }.let {
            ArrayAdapter<CharSequence>(activity, android.R.layout.simple_spinner_item,
                    it.toTypedArray()).let {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mSPSensorSize.adapter = it
            }
        }

        // init value
        mETDeviceName.setText(mEditData.name)

        mETCameraName.setText(mEditData.camera!!.name!!)
        for (i in 0 until mSPSensorSize.childCount) {
            if (getSensorName(mSASensorSize[i]) == mEditData.camera!!.filmName!!) {
                mSPSensorSize.setSelection(i)
                break
            }
        }

        mETLensName.setText(mEditData.lens!!.name)
        mETLensMinFocal.setText((mEditData.lens!!.minFocal).toString())
        mETLensMaxFocal.setText((mEditData.lens!!.maxFocal).toString())

        view!!.let {
            { v: View, hasFocus: Boolean ->
                val vwHome = it
                it.scrollY = if (hasFocus) {
                    val vwHomeTop = Rect().let {
                        vwHome.getGlobalVisibleRect(it)
                        it.top
                    }

                    Rect().let {
                        v.getGlobalVisibleRect(it)
                        it.top - vwHomeTop - SCROLL_TOP_MARGIN
                    }
                } else 0
            }.let {
                mETLensName.setOnFocusChangeListener(it)
                mETLensMinFocal.setOnFocusChangeListener(it)
                mETLensMaxFocal.setOnFocusChangeListener(it)
                Unit
            }
        }

        view!!.setOnClickListener { v ->
            if (0 != v.scrollY) {
                activity.currentFocus?.clearFocus()
            }
        }
    }


    override fun onAccept(): Boolean {
        if (!(checkDeviceParameter() && checkCameraParameter() && checkLensParameter()))
            return false

        val ci = cameraItem
        ci.id = mEditData.camera!!.id

        val li = lensItem
        li.id = mEditData.lens!!.id

        if (ContextUtil.duCamera.modifyData(ci) && ContextUtil.duLens.modifyData(li)) {
            DeviceItem().let {
                it.id = mEditData.id
                it.name = mETDeviceName.text.toString()
                it.camera = ci
                it.lens = li
                return ContextUtil.duDevice.modifyData(it)
            }
        }

        return false
    }

    private fun getSensorName(sz: String): String {
        return sz.substring(0, sz.indexOf("|"))
    }

    private fun getSensorSize(sz: String): BigDecimal {
        return sz.let {
            BigDecimal(it.substring(it.indexOf("|") + 1))
        }
    }

    private fun showWarnAlert(szInfo: String) {
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

    companion object {
        private const val SCROLL_TOP_MARGIN = 60
    }
}
