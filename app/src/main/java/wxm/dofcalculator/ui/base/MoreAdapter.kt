package wxm.dofcalculator.ui.base

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.SparseArray
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
    private val mVWChild : Array<View?> = arrayOfNulls(data.size)

    override fun getViewTypeCount(): Int {
        return count.let { if (it < 1) 1 else it }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return ViewHolder.get(context, convertView, getChildViewLayout(position)).let {
            loadView(position, it)
            mVWChild[position] = it.convertView

            it.convertView
        }
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        mVWChild.fill(null)
    }

    override fun notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated()
        mVWChild.fill(null)
    }

    /**
     * load childView at [pos] with holder [vhHolder]
     */
    protected abstract fun loadView(pos: Int, vhHolder: ViewHolder)

    /**
     * get childView layout-id at [pos]
     */
    @Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
    @LayoutRes
    protected fun getChildViewLayout(pos: Int): Int {
        return mLRSelfDef
    }

    /**
     * do [funOperator] for each childView until it return false
     */
    protected fun forEachChildView(funOperator: (view:View, pos:Int) -> Boolean)    {
        mVWChild.filterNotNull().forEach{
            if(!funOperator(it, mVWChild.indexOf(it)))
                return@forEach
        }
    }
}
