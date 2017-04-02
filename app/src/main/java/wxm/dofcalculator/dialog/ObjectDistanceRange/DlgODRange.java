package wxm.dofcalculator.dialog.ObjectDistanceRange;

import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;

/**
 * 物距范围选择对话框
 * Created by ookoo on 2016/11/1.
 */
public class DlgODRange extends DlgOKOrNOBase {
    @BindView(R.id.ti_od_min)
    TextInputEditText mTTODMin;

    @BindView(R.id.ti_od_max)
    TextInputEditText mTTODMax;

    @Override
    protected View InitDlgView() {
        InitDlgTitle("修改物距范围",  "接受", "放弃");
        View vw = View.inflate(getActivity(), R.layout.dlg_od_range, null);
        ButterKnife.bind(this, vw);
        return vw;
    }
}
