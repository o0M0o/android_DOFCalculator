package wxm.dofcalculator.ui.start;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.dofcalculator.R;
import wxm.dofcalculator.dialog.UsrMessage.DlgUsrMessage;
import wxm.dofcalculator.ui.setting.ACSetting;


/**
 * UI for App start
 * Created by wxm on 2017/3/12.
 */
public class ACStart extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {
    private FrgStart    mFGStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_start);

        init_component(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        mFGStart.refreshUI();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_help: {
                /*
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_START);

                startActivityForResult(intent, 1);
                */
            }
            break;

            case R.id.nav_setting: {
                Intent intent = new Intent(this, ACSetting.class);
                startActivityForResult(intent, 1);
            }
            break;

            case R.id.nav_share_app: {
                Toast.makeText(this, "invoke share!", Toast.LENGTH_SHORT).show();
            }
            break;

            case R.id.nav_contact_writer: {
                sendMessage();
            }
            break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.ac_start);
        assert null != drawer;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 初始化activity
     * @param savedInstanceState  onclick的动作
     */
    private void init_component(Bundle savedInstanceState) {
        // set nav view
        Toolbar tb = UtilFun.cast(findViewById(R.id.ac_navw_toolbar));
        setSupportActionBar(tb);

        DrawerLayout drawer = UtilFun.cast(findViewById(R.id.ac_start));
        assert null != drawer;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nv = UtilFun.cast(findViewById(R.id.start_nav_view));
        assert null != nv;
        nv.setNavigationItemSelectedListener(this);

        // load fragment
        if(null == savedInstanceState)  {
            mFGStart = new FrgStart();
            FragmentTransaction ft =  getFragmentManager().beginTransaction();
            ft.add(R.id.fl_holder, mFGStart);
            ft.commit();
        }
    }

    /**
     * 发送留言
     */
    private void sendMessage() {
        DlgUsrMessage dlg = new DlgUsrMessage();
        dlg.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
            @Override
            public void onDialogPositiveResult(DialogFragment dialogFragment) {
            }

            @Override
            public void onDialogNegativeResult(DialogFragment dialogFragment) {
            }
        });

        dlg.show(getSupportFragmentManager(), "send message");
    }
}
