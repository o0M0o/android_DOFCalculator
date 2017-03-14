package wxm.dofcalculator.ui.device;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import cn.wxm.andriodutillib.util.FastViewHolder;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.CameraItem;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * frg for device select
 * Created by wxm on 2017/3/11.
 */
public class FrgDeviceSelect
        extends FrgUtilityBase  {
    private final static String  KEY_DEVICE_NAME = "device_name";
    private final static String  KEY_CAMERA_INFO = "camera_info";
    private final static String  KEY_LENS_INFO   = "lens_info";

    @BindView(R.id.lv_device)
    ListView    mLVDevice;

    @BindView(R.id.bt_sure)
    Button      mBUSure;


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

        AdapterDevice ap = new AdapterDevice(getActivity(), getAllDeviceInfo(),
                new String[]{}, new int[]{});
        mLVDevice.setAdapter(ap);
        ap.notifyDataSetChanged();

        mLVDevice.setOnItemClickListener((parent, view1, position, id) -> {
            boolean os = view1.isSelected();
            for(int i = 0; i < mLVDevice.getChildCount(); ++i)  {
                mLVDevice.getChildAt(i).setSelected(false);
            }

            view1.setSelected(!os);
            mBUSure.setVisibility(!os ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void loadUI() {
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
            hm.put(KEY_DEVICE_NAME, di.getName());
            hm.put(KEY_CAMERA_INFO,
                    String.format(Locale.CHINA, "%s,%d万像素",
                            getCameraSensorSizeName(ci), ci.getPixelCount()));
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
            FastViewHolder vh = FastViewHolder.get(getContext(), convertView,
                    R.layout.lv_device);

            Map<String, String> hm = UtilFun.cast_t(getItem(position));
            vh.setText(R.id.tv_device_name, hm.get(KEY_DEVICE_NAME));
            vh.setText(R.id.tv_camera, hm.get(KEY_CAMERA_INFO));
            vh.setText(R.id.tv_lens, hm.get(KEY_LENS_INFO));
            return vh.getConvertView();
        }
    }
}
