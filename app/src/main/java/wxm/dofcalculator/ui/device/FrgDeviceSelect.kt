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

    private val mSASensorSize: Array<String> = ContextUtil.getStrArray(R.array.sensor_size)

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
                    hm[KEY_LENS_INFO] = "${li.minFocal}-${li.maxFocal}mm 焦距"

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

        val ap = AdapterDevice(activity, allDeviceInfo)
        mLVDevice.adapter = ap
        ap.notifyDataSetChanged()

        mBUSure.setOnClickListener { v ->
            val id = ap.selectDeviceID
            if (GlobalDef.INVAILD_ID != id) {
                val di = ContextUtil.duDevice.getData(id)
                val it = Intent(activity, ACCalculator::class.java)
                it.putExtra(ACCalculator.KEY_DEVICE_ID, di.id)
                startActivity(it)
            }
        }

        mBUDelete.setOnClickListener { _ ->
            val id = ap.selectDeviceID
            if (GlobalDef.INVAILD_ID != id) {
                val di = ContextUtil.duDevice.getData(id)
                val al_del = String.format(Locale.CHINA,
                        "是否删除设备'%s'", di.name)

                val builder = AlertDialog.Builder(activity)
                builder.setMessage(al_del).setTitle("警告")
                        .setPositiveButton("确认") { dialog, which -> ContextUtil.duDevice.removeData(id) }
                        .setNegativeButton("取消") { dlg, which -> }
                val dlg = builder.create()
                dlg.show()
            }
        }
    }

    /**
     * 过滤视图事件
     * @param event     事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: DBDataChangeEvent) {
        loadUI(null)
    }

    /**
     * 获取相机传感器尺寸的名字
     * @param ci        cameraitem
     * @return          名字
     */
    private fun getCameraSensorSizeName(ci: CameraItem): String {
        var ret = ""
        val arr = resources.getStringArray(R.array.sensor_size)
        when (ci.filmSize) {
            135 -> ret = arr[0]
            85 -> ret = arr[1]
            65 -> ret = arr[2]
            55 -> ret = arr[3]
            35 -> ret = arr[4]
        }

        return ret
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
                for (i in 0 until mLVDevice.childCount) {
                    if (mLVDevice.getChildAt(i).isSelected) {
                        @Suppress("UNCHECKED_CAST")
                        return Integer.valueOf((getItem(i) as Map<String, String>)[KEY_DEVICE_ID])
                    }
                }

                return GlobalDef.INVAILD_ID

            }

        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            vhHolder.convertView.setOnClickListener { v ->
                val os = v.isSelected
                for (i in 0 until mLVDevice.childCount) {
                    val vc = mLVDevice.getChildAt(i)
                    if (v !== vc)
                        mLVDevice.getChildAt(i).isSelected = false
                }

                v.isSelected = !os
                mBUSure.visibility = if (!os) View.VISIBLE else View.GONE
                mBUDelete.visibility = if (!os) View.VISIBLE else View.GONE
            }

            val hm = UtilFun.cast_t<Map<String, String>>(getItem(pos))
            vhHolder.setText(R.id.tv_device_name, hm[KEY_DEVICE_NAME])
            vhHolder.setText(R.id.tv_camera, hm[KEY_CAMERA_INFO])
            vhHolder.setText(R.id.tv_lens, hm[KEY_LENS_INFO])
        }
    }

    companion object {
        private const val KEY_DEVICE_NAME = "device_name"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_CAMERA_INFO = "camera_info"
        private const val KEY_LENS_INFO = "lens_info"
    }
}
