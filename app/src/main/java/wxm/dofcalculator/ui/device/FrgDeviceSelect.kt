package wxm.dofcalculator.ui.device

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ListView
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv
import wxm.androidutil.ViewHolder.ViewHolder
import wxm.androidutil.util.UtilFun
import wxm.dofcalculator.R
import wxm.dofcalculator.db.DBDataChangeEvent
import wxm.dofcalculator.define.CameraItem
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.ui.base.EventHelper
import wxm.dofcalculator.ui.base.MoreAdapter
import wxm.dofcalculator.ui.calculator.ACCalculator
import wxm.dofcalculator.utility.ContextUtil
import java.util.*

/**
 * frg for device select
 * Created by WangXM on2017/3/11.
 */
class FrgDeviceSelect : FrgSupportBaseAdv() {
    private val mLVDevice: ListView by bindView(R.id.lv_device)
    private val mBUSure: Button by bindView(R.id.bt_sure)
    private val mBUDelete: Button by bindView(R.id.bt_delete)
    private val mBUModify: Button by bindView(R.id.bt_edit)

    /**
     * 获取所有device信息
     * @return      结果
     */
    private val allDeviceInfo: List<HashMap<String, String>>
        get() {
            return LinkedList<HashMap<String, String>>().apply {
                ContextUtil.duDevice.allData.forEach {
                    val ci = it.camera!!
                    val li = it.lens!!

                    val hm = HashMap<String, String>()
                    hm[KEY_DEVICE_ID] = it.id.toString()
                    hm[KEY_DEVICE_NAME] = it.name!!
                    hm[KEY_CAMERA_INFO] = "${ci.filmName}"
                    hm[KEY_LENS_INFO] = "${li.minFocal}-${li.maxFocal}mm"

                    add(hm)
                }
            }
        }

    override fun getLayoutID(): Int {
        return R.layout.frg_device_select
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun initUI(savedInstanceState: Bundle?) {
        mBUSure.visibility = View.GONE
        mBUDelete.visibility = View.GONE
        mBUModify.visibility = View.GONE

        mLVDevice.adapter = AdapterDevice(activity, allDeviceInfo)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_sure, R.id.bt_delete, R.id.bt_edit),
                { v -> onDelOrSelect(v) })
    }

    /**
     * 过滤视图事件
     * @param event     事件
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: DBDataChangeEvent) {
        initUI(null)
    }

    private fun onDelOrSelect(v: View) {
        when (v.id) {
            R.id.bt_sure -> {
                val id = (mLVDevice.adapter as AdapterDevice).selectDeviceID
                if (GlobalDef.INVAILD_ID != id) {
                    Intent(activity, ACCalculator::class.java).let {
                        it.putExtra(ACCalculator.KEY_DEVICE_ID, id)
                        startActivity(it)
                    }
                }
            }

            R.id.bt_delete -> {
                val id = (mLVDevice.adapter as AdapterDevice).selectDeviceID
                if (GlobalDef.INVAILD_ID != id) {
                    val alDel = String.format(Locale.CHINA,
                            "是否删除设备'%s'", ContextUtil.duDevice.getData(id).name)

                    AlertDialog.Builder(activity).setMessage(alDel).setTitle("警告")
                            .setPositiveButton("确认") { _, _ -> ContextUtil.duDevice.removeData(id) }
                            .setNegativeButton("取消") { _, _ -> }
                            .create().show()
                }
            }

            R.id.bt_edit -> {
                val id = (mLVDevice.adapter as AdapterDevice).selectDeviceID
                if (GlobalDef.INVAILD_ID != id) {
                    Intent(activity, ACDevice::class.java).let {
                        it.putExtra(ACDevice.KEY_INVOKE_TYPE,  ACDevice.VAL_DEVICE_EDIT)
                        it.putExtra(ACDevice.KEY_DEVICE_ID,  id)
                        startActivity(it)
                    }
                }
            }
        }
    }

    /**
     * adapter for device
     * Created by WangXM on2017/1/23.
     */
    inner class AdapterDevice internal constructor(context: Context, data: List<Map<String, *>>) : MoreAdapter(context, data, R.layout.lv_device) {
        /**
         * 返回选中设备的ID
         * @return  若有选中设备返回其ID,否则返回 INVAILD_ID
         */
        internal val selectDeviceID: Int
            get() {
                var sId = GlobalDef.INVAILD_ID
                forEachChildView { v, pos ->
                    if (v.isSelected) {
                        @Suppress("UNCHECKED_CAST")
                        sId = Integer.valueOf((getItem(pos) as Map<String, String>)[KEY_DEVICE_ID])
                        false
                    } else {
                        true
                    }
                }

                return sId
            }

        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            if (null == vhHolder.getSelfTag(SELF_TAG_ID)) {
                vhHolder.setSelfTag(SELF_TAG_ID, SELF_TAG_VAL)

                vhHolder.convertView.isSelected = false
                vhHolder.convertView.setOnClickListener { v ->
                    val os = v.isSelected
                    if (!os) {
                        forEachChildView { view, _ ->
                            if (v !== view && view.isSelected) {
                                view.isSelected = false
                            }
                            true
                        }
                    }

                    v.isSelected = !os
                    (if (!os) View.VISIBLE else View.GONE).let {
                        mBUSure.visibility = it
                        mBUDelete.visibility = it
                        mBUModify.visibility = it
                    }
                }

                UtilFun.cast_t<Map<String, String>>(getItem(pos)).let {
                    vhHolder.setText(R.id.tv_device_name, it[KEY_DEVICE_NAME])
                    vhHolder.setText(R.id.tv_camera, it[KEY_CAMERA_INFO])
                    vhHolder.setText(R.id.tv_lens, it[KEY_LENS_INFO])
                    Unit
                }
            }
        }
    }

    companion object {
        private const val KEY_DEVICE_NAME = "device_name"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_CAMERA_INFO = "camera_info"
        private const val KEY_LENS_INFO = "lens_info"

        private const val SELF_TAG_ID = 1
        private const val SELF_TAG_VAL = "USED"
    }
}
