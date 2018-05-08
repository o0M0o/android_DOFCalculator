package wxm.dofcalculator.ui.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

import wxm.androidutil.ViewHolder.ViewHolder;

/**
 * fix some issues when use SimpleAdapter
 * @author WangXM
 * @version create：2018/5/8
 */
public abstract class MoreAdapter extends SimpleAdapter {
    private Context    mCTSelf;
    private @LayoutRes int  mLRSelfDef;

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     */
    public MoreAdapter(Context context, List<? extends Map<String, ?>> data, @LayoutRes int resource) {
        super(context, data, resource, new String[0], new int[0]);
        mCTSelf = context;
        mLRSelfDef = resource;
    }

    @Override
    public int getViewTypeCount() {
        int org_ct = getCount();
        return org_ct < 1 ? 1 : org_ct;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = ViewHolder.get(getContext(), convertView, getChildViewLayout(position));
        loadView(position, vh);
        return vh.getConvertView();
    }

    protected Context getContext()  {
        return mCTSelf;
    }

    /**
     * load child view
     * @param pos           child view pos
     * @param vhHolder      view helper
     */
    protected abstract void loadView(int pos, ViewHolder vhHolder);

    /**
     * get child view layout-id
     * @param pos           child view pos
     * @return              layout-id
     */
    protected @LayoutRes int getChildViewLayout(int pos)    {
        return mLRSelfDef;
    }
}
