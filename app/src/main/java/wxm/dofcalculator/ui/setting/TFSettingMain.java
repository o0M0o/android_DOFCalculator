package wxm.dofcalculator.ui.setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.dofcalculator.R;

/**
 * 设置主页面
 * Created by 123 on 2016/10/10.
 */
public class TFSettingMain extends TFSettingBase {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_setting_main, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick({R.id.rl_check_version })
    public void onIVClick(View v) {
        switch (v.getId()) {
            case R.id.rl_check_version: {
                toPageByIdx(FrgSetting.PAGE_IDX_CHECK_VERSION);
            }
            break;
        }
    }

    @Override
    public void updateSetting() {
        if (mBSettingDirty) {
            mBSettingDirty = false;
        }
    }
}
