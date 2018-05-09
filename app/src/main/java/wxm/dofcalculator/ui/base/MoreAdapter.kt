package wxm.dofcalculator.ui.base

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter

import wxm.androidutil.ViewHolder.ViewHolder

/**
 * fix some issues when use SimpleAdapter
 * @author WangXM
 * @version create：2018/5/8
 */
abstract class MoreAdapter(protected val context: Context, data: List<Map<String, *>>,
                           @param:LayoutRes @field:LayoutRes private val mLRSelfDef: Int)
    : SimpleAdapter(context, data, mLRSelfDef, arrayOfNulls(0), IntArray(0)) {

    override fun getViewTypeCount(): Int {
        return count.let { if (it < 1) 1 else it }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return ViewHolder.get(context, convertView, getChildViewLayout(position)).let {
            loadView(position, it)
            it.convertView
        }
    }

    /**
     * load child view
     * @param pos           child view pos
     * @param vhHolder      view helper
     */
    protected abstract fun loadView(pos: Int, vhHolder: ViewHolder)

    /**
     * get child view layout-id
     * @param pos           child view pos
     * @return              layout-id
     */
    @Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
    @LayoutRes
    protected fun getChildViewLayout(pos: Int): Int {
        return mLRSelfDef
    }
}
