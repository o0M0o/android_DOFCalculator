package wxm.dofcalculator.ui.start

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.dofcalculator.R
import wxm.dofcalculator.dialog.UsrMessage.DlgUsrMessage
import wxm.dofcalculator.ui.help.ACHelp
import wxm.dofcalculator.ui.setting.ACSetting


/**
 * UI for App start
 * Created by WangXM on2017/3/12.
 */
class ACStart : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mFGStart: FrgStart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_start)

        initComponent(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        mFGStart!!.reInitUI()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_help -> {
                val intent = Intent(this, ACHelp::class.java)
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_START)

                startActivityForResult(intent, 1)
            }

            R.id.nav_setting -> {
                val intent = Intent(this, ACSetting::class.java)
                startActivityForResult(intent, 1)
            }

            R.id.nav_share_app -> {
                Toast.makeText(this, "invoke share!", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_contact_writer -> {
                sendMessage()
            }
        }

        findViewById<DrawerLayout>(R.id.ac_start).closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * 初始化activity
     * @param savedInstanceState  onclick的动作
     */
    private fun initComponent(savedInstanceState: Bundle?) {
        // set nav view
        val tb = findViewById<Toolbar>(R.id.ac_navw_toolbar)
        setSupportActionBar(tb)

        findViewById<DrawerLayout>(R.id.ac_start).let {
            val toggle = ActionBarDrawerToggle(this, it, tb,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            it.addDrawerListener(toggle)
            toggle.syncState()
        }

        findViewById<NavigationView>(R.id.start_nav_view).setNavigationItemSelectedListener(this)

        // load fragment
        if (null == savedInstanceState) {
            mFGStart = FrgStart()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.fl_holder, mFGStart)
            ft.commit()
        }
    }

    /**
     * 发送留言
     */
    private fun sendMessage() {
        val dlg = DlgUsrMessage()
        dlg.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
            override fun onDialogPositiveResult(dialogFragment: DialogFragment) {}

            override fun onDialogNegativeResult(dialogFragment: DialogFragment) {}
        })

        dlg.show(supportFragmentManager, "send message")
    }
}
