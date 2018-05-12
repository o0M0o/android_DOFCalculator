package wxm.dofcalculator.ui.calculator.view

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.dofcalculator.R
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.dialog.ObjectDistanceRange.DlgODRange
import wxm.dofcalculator.ui.base.EventHelper
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
    private val mTVLenAperture: TextView by bindView(R.id.tv_la_val)
    private val mTWLenAperture: TuneWheel by bindView(R.id.tw_la_val)

    private val mTVFocalLength: TextView by bindView(R.id.tv_lf_val)
    private val mTWFocalLength: TuneWheel by bindView(R.id.tw_lf_val)

    private val mTVObjectDistance: TextView by bindView(R.id.tv_od_val)
    private val mTWObjectDistance: TuneWheel by bindView(R.id.tw_od_val)

    private val mSBObjectDistanceStep: TwoStateButton by bindView(R.id.sb_ob_step)

    private lateinit var TAG_DECIMETER: String

    private var mAttrOrientation = VW_VERTICAL

    /**
     * default min/max object distance, unit is m
     */
    private var mODMin = 0
    private var mODMax = 50

    val curLensFocal: String
        get() = mTVFocalLength.text.toString()

    val curLensAperture: String
        get() = mTVLenAperture.text.toString()

    val curObjectDistance: Int
        get() {
            return mTVObjectDistance.text.toString().removeSuffix("m").toFloat().let {
                (1000 * it).toInt()
            }
        }

    constructor(context: Context) : super(context) {
        initUIComponent()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.VWCameraAdjust)
        try {
            mAttrOrientation = array.getInt(R.styleable.VWCameraAdjust_emCAOrientation, VW_VERTICAL)
        } finally {
            array.recycle()
        }

        initUIComponent()
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    /**
     * 初始化UI元件
     */
    private fun initUIComponent() {
        (if (mAttrOrientation == VW_VERTICAL) R.layout.vw_camera_adjust_v
        else R.layout.vw_camera_adjust_h).let {
            LayoutInflater.from(context).inflate(it, this)
        }

        TAG_DECIMETER = context.getString(R.string.tag_decimeter)

        // for lens aperture
        mTWLenAperture.setValueChangeListener(object : TuneWheel.OnValueChangeListener {
            override fun onValueChange(value: Int, valTag: String) {
                if (valTag != mTVLenAperture.text) {
                    mTVLenAperture.text = valTag
                    EventBus.getDefault().post(AttrChangedEvent(0))
                }
            }
        })

        mTWLenAperture.setTranslateTag(object : TuneWheel.TagTranslate {
            override fun translateTWTag(tagVal: Int): String {
                return LENS_APERTURE_ARR[tagVal]
            }
        })

        // for lens focal
        mTWFocalLength.setValueChangeListener(object : TuneWheel.OnValueChangeListener {
            override fun onValueChange(value: Int, valTag: String) {
                val tag = value.toString()
                if (tag != mTVFocalLength.text.removeSuffix("mm")) {
                    mTVFocalLength.text = String.format(Locale.CHINA, "%smm", tag)
                    EventBus.getDefault().post(AttrChangedEvent(0))
                }
            }
        })

        mTWFocalLength.setTranslateTag(object : TuneWheel.TagTranslate {
            override fun translateTWTag(tagVal: Int): String = String.format(Locale.CHINA, "%d", tagVal)
        })

        if (context is Activity) {
            (context as Activity).intent?.let {
                val dId = it.getIntExtra(ACCalculator.KEY_DEVICE_ID, GlobalDef.INVAILD_ID)
                if (GlobalDef.INVAILD_ID != dId) {
                    ContextUtil.duDevice.getData(dId)?.let {
                        mTWFocalLength.adjustPara(HashMap<String, Any>().apply {
                            put(TuneWheel.PARA_VAL_MIN, it.lens!!.minFocal)
                            put(TuneWheel.PARA_VAL_MAX, it.lens!!.maxFocal)
                        })
                    }
                }
            }
        }

        // for object distance
        mTWObjectDistance.setValueChangeListener(object : TuneWheel.OnValueChangeListener {
            override fun onValueChange(value: Int, valTag: String) {
                if (valTag != mTVObjectDistance.text) {
                    mTVObjectDistance.text = valTag
                    EventBus.getDefault().post(AttrChangedEvent(0))
                }
            }
        })
        mTWObjectDistance.setTranslateTag(object : TuneWheel.TagTranslate {
            override fun translateTWTag(tagVal: Int): String {
                return when (tagVal) {
                    0 -> mODMin.toDouble()
                    OB_MAX_VAL -> mODMax.toDouble()
                    else -> (mODMin + tagVal * ((
                            (mODMax - mODMin).toDouble() / (mTWObjectDistance.attrMaxValue - mTWObjectDistance.attrMinValue))))
                            .let {
                                if (mSBObjectDistanceStep.curTxt == TAG_DECIMETER) it / 10 else it
                            }
                }.let {
                    String.format(Locale.CHINA, if (0.1f < it.rem(1f)) "%.01fm" else "%.00fm", it)
                }
            }
        })

        EventHelper.setOnClickOperator(this,
                intArrayOf(R.id.sb_ob_range, R.id.sb_ob_step),
                { v ->
                    when (v.id) {
                        R.id.sb_ob_range -> onChangeODRange()
                        R.id.sb_ob_step -> onChangeODStep()
                    }
                })
    }

    /**
     * 切换物距范围
     */
    private fun onChangeODRange() {
        DlgODRange().let {
            it.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                override fun onDialogPositiveResult(dialogFragment: DialogFragment) {
                    mODMin = it.odMin
                    mODMax = it.odMax
                    mTWObjectDistance.invalidate()

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
     */
    private fun onChangeODStep() {
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
