package wxm.dofcalculator.ui.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;

/**
 * for app setting
 * Created by ookoo on 2016/11/29.
 */
public class FrgSetting extends FrgUtilityBase {
    public static int    PAGE_IDX_MAIN;
    public static int    PAGE_IDX_CHECK_VERSION;


    @BindView(R.id.vp_pages)
    ViewPager mVPPage;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgSetting";
        View rootView = layoutInflater.inflate(R.layout.frg_setting, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        AppCompatActivity  a_ac = UtilFun.cast_t(getActivity());

        // for pages
        final PagerAdapter adapter = new PagerAdapter(a_ac.getSupportFragmentManager());
        mVPPage.setAdapter(adapter);
    }

    @Override
    protected void loadUI() {
        change_page(PAGE_IDX_MAIN);
    }


    /**
     * 切换到新页面
     * @param new_page 新页面postion
     */
    public void change_page(int new_page)  {
        mVPPage.setCurrentItem(new_page);
    }

    /**
     *  得到当前页
     * @return  当前页实例
     */
    public TFSettingBase getCurrentPage()   {
        PagerAdapter pa = UtilFun.cast_t(mVPPage.getAdapter());
        return UtilFun.cast_t(pa.getItem(mVPPage.getCurrentItem()));
    }

    public int getCurrentItem() {
        return mVPPage.getCurrentItem();
    }

    public void setCurrentItem(int idx) {
        mVPPage.setCurrentItem(idx);
    }



    /**
     * fragment adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        private int mNumOfFrags;
        private ArrayList<Fragment>  mFRFrags;

        PagerAdapter(FragmentManager fm) {
            super(fm);

            mFRFrags = new ArrayList<>();

            PAGE_IDX_MAIN = 0;
            mFRFrags.add(new TFSettingMain());

            PAGE_IDX_CHECK_VERSION = 1;
            mFRFrags.add(new TFSettingCheckVersion());

            mNumOfFrags = mFRFrags.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFRFrags.get(position);
        }

        @Override
        public int getCount() {
            return mNumOfFrags;
        }
    }
}
