package wxm.dofcalculator.ui.calculator.view

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.dofcalculator.R
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.dialog.ObjectDistanceRange.DlgODRange
import wxm.dofcalculator.ui.calculator.ACCalculator
import wxm.dofcalculator.ui.calculator.event.AttrChangedEvent
import wxm.dofcalculator.ui.calculator.event.ObjectDistanceRangeChangeEvent
import wxm.dofcalculator.utility.ContextUtil
import wxm.uilib.TuneWheel.TuneWheel
import wxm.uilib.TwoStateButton.TwoStateButton
import java.util.*

/**
 * camera setting UI
 * Created by WangXM on2017/3/24.
 */
class VWCameraAdjust : ConstraintLayout {
    private val mTVLAVal: TextView by bindView(R.id.tv_la_val)
    private val mTWLATuneWheel: TuneWheel by bindView(R.id.tw_la_val)

    private val mTVLFVal: TextView by bindView(R.id.tv_lf_val)
    private val mTWLFTuneWheel: TuneWheel by bindView(R.id.tw_lf_val)

    private val mTVODVal: TextView by bindView(R.id.tv_od_val)
    private val mTWODTuneWheel: TuneWheel by bindView(R.id.tw_od_val)

    private val mSBODStep: TwoStateButton by bindView(R.id.sb_ob_step)

    private val TAG_DECIMETER: String = ContextUtil.getString(R.string.tag_decimeter)

    private var mAttrOrentation = VW_VERTICAL

    /**
     * min/max object distance, unit is m
     */
    private var mODMin = 0
    private var mODMax = 50

    val curLensFocal: String
        get() = mTVLFVal.text.toString()

    val curLensAperture: String
        get() = mTVLAVal.text.toString()

    val curObjectDistance: Int
        get() {
            return mTVODVal.text.toString().removeSuffix("m").toFloat().let {
                (1000 * it).toInt()
            }
        }

    constructor(context: Context) : super(context) {
        val vid = if (mAttrOrentation == VW_VERTICAL)
            R.layout.vw_camera_adjust_v
        else
            R.layout.vw_camera_adjust_h
        LayoutInflater.from(getContext()).inflate(vid, this)
        initUIComponent()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.VWCameraAdjust)
        try {
            mAttrOrentation = array.getInt(R.styleable.VWCameraAdjust_emCAOrientation, VW_VERTICAL)
        } finally {
            array.recycle()
        }

        val vid = if (mAttrOrentation == VW_VERTICAL)
            R.layout.vw_camera_adjust_v
        else
            R.layout.vw_camera_adjust_h
        LayoutInflater.from(getContext()).inflate(vid, this)
        initUIComponent()
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    /**
     * 初始化UI元件
     */
    private fun initUIComponent() {
        // for lens aperture
        mTWLATuneWheel.setValueChangeListener { _, valTag ->
            mTVLAVal.text = valTag
            EventBus.getDefault().post(AttrChangedEvent(0))
        }

        mTWLATuneWheel.setTranslateTag { tagVal -> LENS_APERTURE_ARR[tagVal] }

        // for lens focal
        mTWLFTuneWheel.setValueChangeListener { value, _ ->
            mTVLFVal.text = "${value}mm"
            EventBus.getDefault().post(AttrChangedEvent(0))
        }

        if (context is Activity) {
            (context as Activity).intent?.let {
                val dId = it.getIntExtra(ACCalculator.KEY_DEVICE_ID, GlobalDef.INVAILD_ID)
                if (GlobalDef.INVAILD_ID != dId) {
                    ContextUtil.duDevice.getData(dId)?.let {
                        mTWLFTuneWheel.adjustPara(HashMap<String, Any>().apply {
                            put(TuneWheel.PARA_VAL_MIN, it.lens!!.minFocal)
                            put(TuneWheel.PARA_VAL_MAX, it.lens!!.maxFocal)
                        })
                    }
                }
            }
        }

        // for object distance
        mTWODTuneWheel.setValueChangeListener { _, valTag ->
            mTVODVal.text = valTag
            EventBus.getDefault().post(AttrChangedEvent(0))
        }
        updateObjectDistanceRange(mSBODStep.curTxt == TAG_DECIMETER)
    }

    /**
     * 切换物距范围
     * @param v     参数
     */
    @OnClick(R.id.sb_ob_range)
    fun onChangeODRange(v: View) {
        DlgODRange().let {
            it.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                override fun onDialogPositiveResult(dialogFragment: DialogFragment) {
                    mODMin = it.odMin
                    mODMax = it.odMax
                    updateObjectDistanceRange(mSBODStep.curTxt == TAG_DECIMETER)

                    EventBus.getDefault().post(
                            ObjectDistanceRangeChangeEvent(mODMin, mODMax))
                }

                override fun onDialogNegativeResult(dialogFragment: DialogFragment) {}
            })

            it.show((context as AppCompatActivity).supportFragmentManager, "select object distance")
        }
    }

    /**
     * 切换object distance 步进值
     * @param v     para
     */
    @OnClick(R.id.sb_ob_step)
    fun onChangeODStep(v: View) {
        updateObjectDistanceRange(mSBODStep.curTxt == TAG_DECIMETER)
    }

    /**
     * 设置物距最小，最大值
     */
    private fun updateObjectDistanceRange(decimeter: Boolean) {
        mTWODTuneWheel.setTranslateTag(
                { tagVal ->
                    if (decimeter) {
                        when (tagVal) {
                            OB_MIN_VAL -> mODMin.toDouble()
                            OB_MAX_VAL -> mODMax.toDouble()
                            else -> mODMin + (mODMax - mODMin) * (Math.pow(INDEX_NUM, tagVal.toDouble()) / INDEX_MAX)
                        }
                    } else {
                        when (tagVal) {
                            0 -> mODMin.toDouble()
                            OB_MAX_VAL -> mODMax.toDouble()
                            else -> mODMin + (mODMax - mODMin) * (Math.pow(INDEX_NUM, tagVal.toDouble()) / INDEX_MAX)
                        }
                    }.let {
                        String.format(Locale.CHINA, if (it > 1) "%.00fm" else "%.02fm", it)
                    }
                })

        mTVODVal.text = mTWODTuneWheel.curValueTag
    }

    companion object {
        private const val VW_VERTICAL = 1
        private const val VW_HORIZONTAL = 2

        /**
         * 物距尺最大最小原始值
         */
        private const val OB_MIN_VAL = 0
        private const val OB_MAX_VAL = 10

        private const val INDEX_NUM = 1.8
        private val INDEX_MAX = Math.pow(INDEX_NUM, OB_MAX_VAL.toDouble())

        private val LENS_APERTURE_ARR = arrayOf("F1.0", "F1.4", "F2.0", "F2.8",
                "F4.0", "F5.6", "F8.0", "F11",
                "F16", "F22", "F32", "F45", "F64")
    }
}
