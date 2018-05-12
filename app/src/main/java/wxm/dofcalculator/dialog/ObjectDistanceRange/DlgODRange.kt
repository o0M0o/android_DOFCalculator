package wxm.dofcalculator.dialog.ObjectDistanceRange

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.View

import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.dofcalculator.R
import wxm.dofcalculator.utility.ContextUtil

/**
 * for select object distance
 * Created by WangXM on2016/11/1.
 */
class DlgODRange : DlgOKOrNOBase() {
    private lateinit var mTTODMin: TextInputEditText
    private lateinit var mTTODMax: TextInputEditText

    private val SZ_ERROR: String = ContextUtil.getString(R.string.error)
    private val SZ_SURE: String = ContextUtil.getString(R.string.sure)

    /**
     * 得到最小物距（单位mm)
     * @return  最小物距
     */
    val odMin: Int
        get() {
            val sz_min = mTTODMin.text.toString()
            return if (UtilFun.StringIsNullOrEmpty(sz_min)) 0 else Integer.valueOf(sz_min)
        }

    /**
     * 得到最大物距（单位mm)
     * @return  最大物距
     */
    val odMax: Int
        get() {
            val sz_min = mTTODMax.text.toString()
            return if (UtilFun.StringIsNullOrEmpty(sz_min)) 0 else Integer.valueOf(sz_min)
        }

    override fun initDlgView(bundle: Bundle?) {
        mTTODMin = findDlgChildView(R.id.ti_od_min)!!
        mTTODMax = findDlgChildView(R.id.ti_od_max)!!
    }

    override fun createDlgView(bundle: Bundle?): View {
        initDlgTitle("修改物距范围", "接受", "放弃")
        return View.inflate(activity, R.layout.dlg_od_range, null)
    }

    /**
     * 检查输入物距必须合法
     * @return  检查结果
     */
    override fun checkBeforeOK(): Boolean {
        val szMin = mTTODMin.text.toString()
        val szMax = mTTODMax.text.toString()
        if (UtilFun.StringIsNullOrEmpty(szMin)) {
            AlertDialog.Builder(context)
                    .setTitle(SZ_ERROR).setMessage("未设置最小物距")
                    .setPositiveButton(SZ_SURE, null)
                    .show()

            mTTODMin.requestFocus()
            return false
        }

        if (UtilFun.StringIsNullOrEmpty(szMax)) {
            AlertDialog.Builder(context)
                    .setTitle(SZ_ERROR).setMessage("未设置最大物距")
                    .setPositiveButton(SZ_SURE, null)
                    .show()

            mTTODMax.requestFocus()
            return false
        }

        val odMin = Integer.valueOf(szMin)
        val odMax = Integer.valueOf(szMax)
        if (odMin < 0) {
            AlertDialog.Builder(context)
                    .setTitle(SZ_ERROR).setMessage("最小物距必须大于0")
                    .setPositiveButton(SZ_SURE, null)
                    .show()

            mTTODMin.requestFocus()
            return false
        }

        if (odMax <= odMin) {
            AlertDialog.Builder(context)
                    .setTitle(SZ_ERROR).setMessage("最大物距必须大于最小物距")
                    .setPositiveButton(SZ_SURE, null)
                    .show()

            mTTODMax.requestFocus()
            return false
        }

        return true
    }
}
