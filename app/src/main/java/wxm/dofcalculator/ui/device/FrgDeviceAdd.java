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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.define.CameraItem;
import wxm.dofcalculator.define.DeviceItem;
import wxm.dofcalculator.define.LensItem;
import wxm.dofcalculator.ui.base.IFFrgEdit;
import wxm.dofcalculator.utility.ContextUtil;

/**
 * first frg for app
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

    @BindView(R.id.sp_sensor_pixel_count)
    Spinner             mSPSensorPixelCount;

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

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgUsrAdd";
        View rootView = layoutInflater.inflate(R.layout.frg_device_add, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        // for sensor size
        ArrayAdapter<CharSequence> ap_sensor_size = ArrayAdapter.createFromResource(getActivity(),
                R.array.sensor_size, android.R.layout.simple_spinner_item);
        ap_sensor_size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSPSensorSize.setAdapter(ap_sensor_size);

        // for sensor size
        ArrayAdapter<CharSequence> ap_sensor_pc = ArrayAdapter.createFromResource(getActivity(),
                R.array.sensor_pixel_count, android.R.layout.simple_spinner_item);
        ap_sensor_pc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSPSensorPixelCount.setAdapter(ap_sensor_pc);
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
        int sp_idx = mSPSensorPixelCount.getSelectedItemPosition();

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

        if(AdapterView.INVALID_POSITION == sp_idx)  {
            mSPSensorPixelCount.requestFocus();

            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setMessage(mSZNeedCameraSensorPixelCount).setTitle(mSZWarn);

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
        ci.setFilmSize(getSensorSize());
        ci.setPixelCount(getSensorPixelCount());
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
        li.setMinFocal(Integer.valueOf(mETLensMaxFocal.getText().toString()));
        return li;
    }

    /**
     * 获取传感器尺寸
     * @return  传感器尺寸
     */
    private int getSensorSize() {
        int idx = mSPSensorSize.getSelectedItemPosition();
        int ret = -1;
        switch (idx)    {
            case 0 :
                ret = 135;
                break;

            case 1 :
                ret = 85;
                break;

            case 2 :
                ret = 65;
                break;

            case 3 :
                ret = 55;
                break;

            case 4 :
                ret = 35;
                break;

        }

        return ret;
    }

    /**
     * 获取传感器像素数
     * @return  传感器像素数
     */
    private int getSensorPixelCount() {
        int idx = mSPSensorPixelCount.getSelectedItemPosition();
        int ret = -1;
        switch (idx)    {
            case 0 :
                ret = 2400;
                break;

            case 1 :
                ret = 1600;
                break;

            case 2 :
                ret = 1200;
                break;

            case 3 :
                ret = 800;
                break;

        }

        return ret;
    }
}
