package wxm.dofcalculator.dialog.ObjectDistanceRange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.View;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.dofcalculator.R;

/**
 * for select object distance
 * Created by WangXM on2016/11/1.
 */
public class DlgODRange extends DlgOKOrNOBase {
    @BindView(R.id.ti_od_min)
    TextInputEditText mTTODMin;

    @BindView(R.id.ti_od_max)
    TextInputEditText mTTODMax;

    @BindString(R.string.sz_error)
    String SZ_ERROR;

    @BindString(R.string.sz_sure)
    String SZ_SURE;

    /**
     * 得到最小物距（单位mm)
     * @return  最小物距
     */
    public int getODMin()   {
        String sz_min = mTTODMin.getText().toString();
        return UtilFun.StringIsNullOrEmpty(sz_min) ? 0 : Integer.valueOf(sz_min);
    }

    /**
     * 得到最大物距（单位mm)
     * @return  最大物距
     */
    public int getODMax()   {
        String sz_min = mTTODMax.getText().toString();
        return UtilFun.StringIsNullOrEmpty(sz_min) ? 0 : Integer.valueOf(sz_min);
    }

    @Override
    protected void initDlgView(@Nullable Bundle bundle) {
    }

    @Override
    protected View createDlgView(@Nullable Bundle bundle) {
        initDlgTitle("修改物距范围",  "接受", "放弃");
        return View.inflate(getActivity(), R.layout.dlg_od_range, null);
    }

    /**
     * 检查输入物距必须合法
     * @return  检查结果
     */
    @Override
    protected boolean checkBeforeOK() {
        String sz_min = mTTODMin.getText().toString();
        String sz_max = mTTODMax.getText().toString();
        if(UtilFun.StringIsNullOrEmpty(sz_min)) {
            new AlertDialog.Builder(getContext())
                    .setTitle(SZ_ERROR)
                    .setMessage("未设置最小物距")
                    .setPositiveButton(SZ_SURE, null)
                    .show();

            mTTODMin.requestFocus();
            return false;
        }

        if(UtilFun.StringIsNullOrEmpty(sz_max)) {
            new AlertDialog.Builder(getContext())
                    .setTitle(SZ_ERROR)
                    .setMessage("未设置最大物距")
                    .setPositiveButton(SZ_SURE, null)
                    .show();

            mTTODMax.requestFocus();
            return false;
        }

        int od_min = Integer.valueOf(sz_min);
        int od_max = Integer.valueOf(sz_max);
        if(od_min < 0)  {
            new AlertDialog.Builder(getContext())
                    .setTitle(SZ_ERROR)
                    .setMessage("最小物距必须大于0")
                    .setPositiveButton(SZ_SURE, null)
                    .show();

            mTTODMin.requestFocus();
            return false;
        }

        if(od_max <= od_min)    {
            new AlertDialog.Builder(getContext())
                    .setTitle(SZ_ERROR)
                    .setMessage("最大物距必须大于最小物距")
                    .setPositiveButton(SZ_SURE, null)
                    .show();

            mTTODMax.requestFocus();
            return false;
        }

        return true;
    }
}
