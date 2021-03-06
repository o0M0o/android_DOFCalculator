package wxm.dofcalculator.ui.calculator.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.dofcalculator.R
import wxm.dofcalculator.ui.calculator.event.CameraSettingChangeEvent
import wxm.dofcalculator.ui.calculator.event.DofChangedEvent
import wxm.uilib.DistanceMeter.DistanceMeter
import wxm.uilib.DistanceMeter.DistanceMeterTag
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * extend view for Dof result ui
 * Created by WangXM on2017/3/19.
 */
class VWDof : ConstraintLayout {
    private var mDENOFResult: DofChangedEvent? = null
    private var mCSCameraSetting: CameraSettingChangeEvent? = null

    private val mMVMeter: DistanceMeter by bindView(R.id.evw_meter)
    private val mCLDofInfo: ConstraintLayout by bindView(R.id.cl_dof_info)
    private val mTVFrontDof: TextView by bindView(R.id.tv_front_dof)
    private val mTVDrive: TextView by bindView(R.id.tv_drive)
    private val mTVFocalLength: TextView by bindView(R.id.tv_focal_length)
    private val mTVBackDof: TextView by bindView(R.id.tv_back_dof)

    private var mCRDOFFront: Int = Color.BLACK
    private var mCRDOFObjectDistance: Int = Color.BLACK
    private var mCRDOFBack: Int = Color.BLACK

    private lateinit var mSZTagFrontPoint: String
    private lateinit var mSZTagObject: String
    private lateinit var mSZTagBackPoint: String

    private var mAttrOrentation = VW_VERTICAL

    constructor(context: Context) : super(context) {
        setWillNotDraw(false)
        initUI(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setWillNotDraw(false)
        initUI(context, attrs)
    }

    override fun onAttachedToWindow() {
        EventBus.getDefault().register(this)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }


    /**
     * camera setting变化处理器
     * @param event     事件参数
     */
    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCameraSettingChange(event: CameraSettingChangeEvent) {
        mCSCameraSetting = event
        updateDof()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (null == mDENOFResult) {
            if (!isInEditMode) {
                mMVMeter.clearCursor()

                mTVFrontDof.text = DEF_SZ
                mTVFocalLength.text = DEF_SZ
                mTVBackDof.text = DEF_SZ

                mTVDrive.text = if (mCSCameraSetting == null)
                    DEF_SZ
                else
                    mCSCameraSetting!!.device.name
            }
        }
    }

    private fun initUI(context: Context, attrs: AttributeSet?)    {
        val array = context.obtainStyledAttributes(attrs, R.styleable.VWDof)
        try {
            mAttrOrentation = array.getInt(R.styleable.VWDof_emOrientation, VW_VERTICAL)
        } finally {
            array.recycle()
        }

        val vid = if (mAttrOrentation == VW_VERTICAL) R.layout.vw_dof_v else R.layout.vw_dof_h
        LayoutInflater.from(context).inflate(vid, this)

        mCRDOFFront = context.getColor(R.color.teal)
        mCRDOFObjectDistance = context.getColor(R.color.salmon)
        mCRDOFBack = context.getColor(R.color.teal)

        mSZTagFrontPoint = context.getString(R.string.tag_front_point)
        mSZTagObject = context.getString(R.string.tag_object)
        mSZTagBackPoint = context.getString(R.string.tag_back_point)
    }

    /**
     * update view
     */
    private fun updateDof() {
        mDENOFResult = null
        mCSCameraSetting?.let {
            val lensFocal = it.lensFocal
            val lensAperture = it.lensAperture
            val pixelArea = it.cameraCOC
            val fOd = it.objectDistance.toFloat()

            //hyperFocal = (focal * focal) / (aperture * CoC) + focal;
            val ff = BigDecimal(lensFocal * lensFocal)
            val ac = lensAperture.multiply(pixelArea)
            val hyperFocal = ff.divide(ac, RoundingMode.CEILING)
                    .add(BigDecimal(lensFocal))

            // change to unit mm
            val odMM = BigDecimal(fOd.toDouble())

            // dofNear = ((hyperFocal - focal) * distance) / (hyperFocal + distance - (2*focal));
            val focal = BigDecimal(lensFocal)
            val dofNearF = hyperFocal.subtract(focal).multiply(odMM)
            val dofNearB = hyperFocal.add(odMM).subtract(focal.add(focal))
            val dofNear = dofNearF.divide(dofNearB, RoundingMode.CEILING).toFloat()

            // Prevent 'divide by zero' when calculating far distance.
            val dofFar: Float = if (Math.abs(hyperFocal.subtract(odMM).toFloat()) <= 0.00001) {
                fOd + 200000
            } else {
                val f = hyperFocal.subtract(focal).multiply(odMM)
                val b = hyperFocal.subtract(odMM)
                val ret = f.divide(b, RoundingMode.CEILING).toFloat()
                if (ret <= fOd)
                    fOd + 200000
                else
                    ret
            }

            mDENOFResult = DofChangedEvent(Math.min(dofNear, fOd), fOd, Math.max(dofFar, fOd))
        }

        updateDofInfo()
        updateDofView()
    }

    /**
     * update dof info in header view
     */
    private fun updateDofInfo() {
        mDENOFResult?.let {
            mTVFrontDof.text = String.format(Locale.CHINA, "%.02fm",
                    it.frontDofInMeter)
            mTVFocalLength.text = String.format(Locale.CHINA, "%.02fm",
                    it.focalLengthInMeter)
            mTVBackDof.text = String.format(Locale.CHINA, "%.02fm",
                    it.backDofInMeter)
        }

        mCSCameraSetting?.let {
            mTVDrive.text = it.device.name
        }
    }

    /**
     * add cursor and adjust value range
     */
    private fun updateDofView() {
        mMVMeter.clearCursor()

        mDENOFResult?.let {
            val backDof = it.backDofInMeter
            val frontDof = it.frontDofInMeter
            val objectDistance = it.objectDistanceInMeter

            // adjust min-max value
            mMVMeter.adjustAttribute(HashMap<String, Any>().apply {
                put(DistanceMeter.PARA_VAL_MAX,
                        (backDof.toInt() / SETP_VAL + 2) * SETP_VAL)
                put(DistanceMeter.PARA_VAL_MIN,
                        Math.max(0, (frontDof.toInt() / SETP_VAL - 2) * SETP_VAL))
            })

            // add cursors
            mMVMeter.addCursor(
                    DistanceMeterTag(mSZTagFrontPoint, frontDof).apply {
                        mCRTagColor = mCRDOFFront
                    },
                    DistanceMeterTag(mSZTagObject, objectDistance).apply {
                        mCRTagColor = mCRDOFObjectDistance
                    },
                    DistanceMeterTag(mSZTagBackPoint, backDof).apply {
                        mCRTagColor = mCRDOFBack
                    })
        }
    }

    companion object {
        private const val VW_VERTICAL = 1
        private const val VW_HORIZONTAL = 2

        private const val SETP_VAL = 5

        private const val DEF_SZ = "--"
    }
}
