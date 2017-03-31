package wxm.dofcalculator.ui.device;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import cn.wxm.andriodutillib.util.FastViewHolder;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.db.DBDataChangeEvent;
import wxm.dofcalculator.define.CameraItem;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.GlobalDef;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.ui.calculator.ACCalculator;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * frg for device select
 * Created by wxm on 2017/3/11.
 */
public class FrgDeviceSelect
        extends FrgUtilityBase  {
    private final static String  KEY_DEVICE_NAME = "device_name";
    private final static String  KEY_DEVICE_ID   = "device_id";
    private final static String  KEY_CAMERA_INFO = "camera_info";
    private final static String  KEY_LENS_INFO   = "lens_info";

    @BindView(R.id.lv_device)
    ListView    mLVDevice;

    @BindView(R.id.bt_sure)
    Button      mBUSure;

    @BindView(R.id.bt_delete)
    Button      mBUDelete;

    @BindArray(R.array.sensor_size)
    String[]    mSASensorSize;

    @Override
    protected void enterActivity()  {
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity()  {
        EventBus.getDefault().unregister(this);

        super.leaveActivity();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgDeviceSelect";
        View rootView = layoutInflater.inflate(R.layout.frg_device_select, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        mBUSure.setVisibility(View.GONE);
        mBUDelete.setVisibility(View.GONE);

        AdapterDevice ap = new AdapterDevice(getActivity(), getAllDeviceInfo(),
                new String[]{}, new int[]{});
        mLVDevice.setAdapter(ap);
        ap.notifyDataSetChanged();

        mBUSure.setOnClickListener(v -> {
            int id = ap.getSelectDeviceID();
            if(GlobalDef.INT_INVAILED_ID != id) {
                DeviceItem di = ContextUtil.getDUDevice().getData(id);
                Intent it = new Intent(getActivity(), ACCalculator.class);
                it.putExtra(ACCalculator.KEY_DEVICE_ID, di.getID());
                startActivity(it);
            }
        });

        mBUDelete.setOnClickListener(v -> {
            int id = ap.getSelectDeviceID();
            if(GlobalDef.INT_INVAILED_ID != id) {
                DeviceItem di = ContextUtil.getDUDevice().getData(id);
                String al_del = String.format(Locale.CHINA,
                                    "是否删除设备'%s'", di.getName());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(al_del).setTitle("警告")
                        .setPositiveButton("确认", (dialog, which) -> ContextUtil.getDUDevice().removeData(id))
                        .setNegativeButton("取消", (dlg, which) -> {});
                AlertDialog dlg = builder.create();
                dlg.show();
            }
        });
    }

    @Override
    protected void loadUI() {
    }

    /**
     * 过滤视图事件
     * @param event     事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(DBDataChangeEvent event) {
        initUiComponent(getView());
    }

    /**
     * 获取所有device信息
     * @return      结果
     */
    private List<HashMap<String, String>> getAllDeviceInfo()    {
        LinkedList<HashMap<String, String>> ls_hm = new LinkedList<>();
        List<DeviceItem> ls_device = ContextUtil.getDUDevice().getAllData();
        for(DeviceItem di : ls_device)  {
            CameraItem ci = di.getCamera();
            LensItem   li = di.getLens();

            HashMap<String, String> hm = new HashMap<>();
            hm.put(KEY_DEVICE_ID, String.valueOf(di.getID()));
            hm.put(KEY_DEVICE_NAME, di.getName());
            hm.put(KEY_CAMERA_INFO,
                    String.format(Locale.CHINA, "%s,%d万像素",
                            ci.getFilmName(), ci.getPixelCount()));
            hm.put(KEY_LENS_INFO,
                    String.format(Locale.CHINA, "%d-%dmm 焦距",
                            li.getMinFocal(), li.getMaxFocal()));

            ls_hm.add(hm);
        }

        return ls_hm;
    }

    /**
     * 获取相机传感器尺寸的名字
     * @param ci        cameraitem
     * @return          名字
     */
    private String getCameraSensorSizeName(CameraItem ci)   {
        String ret = "";
        String[] arr = getResources().getStringArray(R.array.sensor_size);
        switch (ci.getFilmSize())   {
            case 135 :
                ret = arr[0];
                break;

            case 85 :
                ret = arr[1];
                break;

            case 65 :
                ret = arr[2];
                break;

            case 55 :
                ret = arr[3];
                break;

            case 35 :
                ret = arr[4];
                break;
        }

        return ret;
    }


    /**
     * adapter for device
     * Created by ookoo on 2017/1/23.
     */
    public class AdapterDevice extends SimpleAdapter {
        private final static String LOG_TAG = "AdapterDevice";
        private View.OnClickListener mCLItem = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean os = v.isSelected();
                for(int i = 0; i < mLVDevice.getChildCount(); ++i)  {
                    View v_c = mLVDevice.getChildAt(i);
                    if(v != v_c)
                        mLVDevice.getChildAt(i).setSelected(false);
                }

                v.setSelected(!os);
                mBUSure.setVisibility(!os ? View.VISIBLE : View.GONE);
                mBUDelete.setVisibility(!os ? View.VISIBLE : View.GONE);
            }
        };


        public AdapterDevice(Context context, List<? extends Map<String, ?>> data,
                                 String[] from, int[] to) {
            super(context, data, R.layout.lv_device, from, to);
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


        public View getView(int position, View convertView, ViewGroup parent) {
            FastViewHolder vh = FastViewHolder.get(getActivity(), convertView,
                    R.layout.lv_device);
            View rv = vh.getConvertView();
            rv.setOnClickListener(mCLItem);

            Map<String, String> hm = UtilFun.cast_t(getItem(position));
            vh.setText(R.id.tv_device_name, hm.get(KEY_DEVICE_NAME));
            vh.setText(R.id.tv_camera, hm.get(KEY_CAMERA_INFO));
            vh.setText(R.id.tv_lens, hm.get(KEY_LENS_INFO));
            return rv;
        }

        /**
         * 返回选中设备的ID
         * @return  若有选中设备返回其ID,否则返回 INT_INVAILED_ID
         */
        public int getSelectDeviceID()  {
            int hot_pos = -1;
            for(int i = 0; i < mLVDevice.getChildCount(); ++i)  {
                if(mLVDevice.getChildAt(i).isSelected())    {
                    hot_pos = i;
                    break;
                }
            }

            if(-1 == hot_pos)
                return GlobalDef.INT_INVAILED_ID;

            Map<String, String> hm = UtilFun.cast_t(getItem(hot_pos));
            return Integer.valueOf(hm.get(KEY_DEVICE_ID));
        }
    }
}
