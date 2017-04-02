package wxm.dofcalculator.ui.device;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.CameraItem;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.ui.base.IFFrgEdit;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * frg for device add
 * Created by wxm on 2017/3/11.
 */
public class FrgDeviceAdd
        extends FrgUtilityBase
        implements IFFrgEdit {

    // for device
    @BindView(R.id.et_device_name)
    TextInputEditText   mETDeviceName;

    // for camera
    @BindView(R.id.et_camera_name)
    TextInputEditText   mETCamraName;

    @BindView(R.id.sp_sensor_size)
    Spinner             mSPSensorSize;


    // for lens
    @BindView(R.id.et_lens_name)
    TextInputEditText   mETLensName;

    @BindView(R.id.et_lens_min_focal)
    TextInputEditText   mETLensMinFocal;

    @BindView(R.id.et_lens_max_focal)
    TextInputEditText   mETLensMaxFocal;

    // for device sz
    @BindString(R.string.error_need_device_name)
    String              mSZNeedDeviceName;

    // for camera sz
    @BindString(R.string.sz_warn)
    String              mSZWarn;

    @BindString(R.string.error_need_camera_name)
    String              mSZNeedCameraName;

    @BindString(R.string.error_need_camera_sensor_size)
    String              mSZNeedCameraSensorSize;

    @BindString(R.string.error_need_camera_sensor_pixel_count)
    String              mSZNeedCameraSensorPixelCount;

    // for lens sz
    @BindString(R.string.error_need_lens_name)
    String              mSZNeedLensName;

    @BindString(R.string.error_focal_range_mess)
    String              mSZFocalRangeMess;

    @BindString(R.string.error_need_min_focal)
    String              mSZNeedMinFocal;

    @BindString(R.string.error_need_max_focal)
    String              mSZNeedMaxFocal;

    @BindArray(R.array.sensor_size)
    String[]    mSASensorSize;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgDeviceAdd";
        View rootView = layoutInflater.inflate(R.layout.frg_device_add, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        String[] org_arr = new String[mSASensorSize.length];
        for(int i = 0; i < org_arr.length; ++i) {
            String org = mSASensorSize[i];
            org_arr[i] = org.substring(0, org.indexOf("|"));
        }

        // for sensor size
        ArrayAdapter<CharSequence> ap_sensor_size = new ArrayAdapter<>(getActivity(),
                                                android.R.layout.simple_spinner_item, org_arr);
        ap_sensor_size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSPSensorSize.setAdapter(ap_sensor_size);
    }

    @Override
    protected void loadUI() {
    }


    @Override
    public boolean onAccept() {
        if(!(checkDeviceParameter() && checkCameraParameter() && checkLensParameter()))
            return false;

        CameraItem ci = getCameraItem();
        LensItem   li = getLensItem();
        if(ContextUtil.getDUCamera().createData(ci)
                && ContextUtil.getDULens().createData(li))  {
            DeviceItem di = new DeviceItem();
            di.setName(mETDeviceName.getText().toString());
            di.setCamera(ci);
            di.setLens(li);

            return ContextUtil.getDUDevice().createData(di);
        }

        return false;
    }

    /**
     * 检查设备参数
     * @return      检查结果
     */
    private boolean checkDeviceParameter()  {
        String d_name = mETDeviceName.getText().toString();
        Activity ac = getActivity();
        if(UtilFun.StringIsNullOrEmpty(d_name)) {
            mETDeviceName.setError(mSZNeedDeviceName);
            mETDeviceName.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZNeedDeviceName).setTitle(mSZWarn);

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }

    /**
     * 检查相机参数
     * @return      检查结果
     */
    private boolean checkCameraParameter()   {
        String c_name = mETCamraName.getText().toString();
        int ss_idx = mSPSensorSize.getSelectedItemPosition();

        Activity ac = getActivity();
        if(UtilFun.StringIsNullOrEmpty(c_name)) {
            mETCamraName.setError(mSZNeedCameraName);
            mETCamraName.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZNeedCameraName).setTitle(mSZWarn);

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(AdapterView.INVALID_POSITION == ss_idx)  {
            mSPSensorSize.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZNeedCameraSensorSize).setTitle(mSZWarn);

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }

    /**
     * 检查相机参数
     * @return      检查结果
     */
    private boolean checkLensParameter()   {
        String c_name = mETLensName.getText().toString();
        String c_min = mETLensMinFocal.getText().toString();
        String c_max = mETLensMaxFocal.getText().toString();

        Activity ac = getActivity();
        if(UtilFun.StringIsNullOrEmpty(c_name)) {
            mETLensName.setError(mSZNeedLensName);
            mETLensName.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZNeedLensName).setTitle(mSZWarn);

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(c_min)) {
            mETLensMinFocal.setError(mSZNeedMinFocal);
            mETLensMinFocal.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZNeedMinFocal).setTitle(mSZWarn);

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(c_max)) {
            mETLensMaxFocal.setError(mSZNeedMaxFocal);
            mETLensMaxFocal.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZNeedMaxFocal).setTitle(mSZWarn);

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        int f_min = Integer.valueOf(c_min);
        int f_max = Integer.valueOf(c_max);
        if(f_min > f_max)   {
            mETLensMinFocal.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZFocalRangeMess).setTitle(mSZWarn);

            AlertDialog dlg = builder.create();
            dlg.show();
            return false;
        }

        return true;
    }

    /**
     * 根据录入数据生成Camraitem
     * !!不检查数据合法性
     * @return  结果
     */
    private CameraItem getCameraItem()  {
        CameraItem ci = new CameraItem();

        ci.setName(mETCamraName.getText().toString());
        ci.setFilmSize(Math.round(getSensorSize().floatValue()));
        ci.setFilmName(getFilmName());
        return ci;
    }

    /**
     * 根据录入数据生成Lensitem
     * !!不检查数据合法性
     * @return  结果
     */
    private LensItem getLensItem()  {
        LensItem li = new LensItem();

        li.setName(mETLensName.getText().toString());
        li.setMinFocal(Integer.valueOf(mETLensMinFocal.getText().toString()));
        li.setMaxFocal(Integer.valueOf(mETLensMaxFocal.getText().toString()));
        return li;
    }

    /**
     * 获取传感器对角线尺寸
     * @return  传感器尺寸
     */
    private BigDecimal getSensorSize() {
        int idx = mSPSensorSize.getSelectedItemPosition();
        if(AdapterView.INVALID_POSITION != idx) {
            String sel_ss = mSASensorSize[idx];
            return  new BigDecimal(sel_ss.substring(sel_ss.indexOf("|") + 1));
        }

        return BigDecimal.ZERO;
    }

    /**
     * 获得传感器名
     * @return  传感器名
     */
    private String getFilmName()    {
        int idx = mSPSensorSize.getSelectedItemPosition();
        if(AdapterView.INVALID_POSITION != idx) {
            String org = mSASensorSize[idx];
            return org.substring(0, org.indexOf("|"));
        }

        return "";
    }
}
