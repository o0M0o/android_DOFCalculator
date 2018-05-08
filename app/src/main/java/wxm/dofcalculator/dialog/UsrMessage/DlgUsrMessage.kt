package wxm.dofcalculator.dialog.UsrMessage

import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.READ_SMS
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.PackageUtil
import wxm.androidutil.util.SIMCardUtil
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.WRMsgHandler
import wxm.dofcalculator.R
import wxm.dofcalculator.define.GlobalDef
import wxm.dofcalculator.utility.ContextUtil
import java.io.IOException

/**
 * send user message
 * Created by WangXM on2017/1/9.
 */
class DlgUsrMessage : DlgOKOrNOBase() {
    private lateinit var mETUsrMessage: TextInputEditText

    private var mProgressStatus = 0
    private var mHDProgress: LocalMsgHandler? = null
    private var mPDDlg: ProgressDialog? = null

    private val mSZUrlPost: String = ContextUtil.getString(R.string.url_post_send_message)
    private val mSZColUsr: String = ContextUtil.getString(R.string.col_usr)
    private val mSZColMsg: String = ContextUtil.getString(R.string.col_message)
    private val mSZColAppName: String = ContextUtil.getString(R.string.col_app_name)
    private val mSZColValAppName: String = ContextUtil.getString(R.string.col_val_app_name)
    private val mSZUsrMessage: String = ContextUtil.getString(R.string.cn_usr_message)
    private val mSZAccept: String = ContextUtil.getString(R.string.cn_accept)
    private val mSZGiveUp: String = ContextUtil.getString(R.string.cn_giveup)

    override fun initDlgView(bundle: Bundle?) {
        mETUsrMessage = findDlgChildView(R.id.et_usr_message)!!

        mHDProgress = LocalMsgHandler(this)
        mPDDlg = ProgressDialog(context)
    }

    override fun createDlgView(bundle: Bundle?): View {
        initDlgTitle(mSZUsrMessage, mSZAccept, mSZGiveUp)
        return View.inflate(activity, R.layout.dlg_send_message, null)
    }

    override fun checkBeforeOK(): Boolean {
        val msg = mETUsrMessage.text.toString()
        if (UtilFun.StringIsNullOrEmpty(msg)) {
            AlertDialog.Builder(context)
                    .setTitle("警告").setMessage("消息不能为空")
                    .create().show()
            return false
        }

        val usr = if (ContextCompat.checkSelfPermission(ContextUtil.instance, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ContextUtil.instance, READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            SIMCardUtil(context).nativePhoneNumber
        } else "null"

        return sendMsgByHttpPost(usr ?: "null", msg)
    }

    /**
     * 通过http post发送消息
     *
     * @param usr 用户
     * @param msg 消息
     * @return 发送成功返回true
     */
    private fun sendMsgByHttpPost(usr: String, msg: String): Boolean {
        val ht = HttpPostTask(usr, msg)
        ht.execute()
        return true
    }

    /**
     * for send http post
     */
    inner class HttpPostTask internal constructor(private val mSZUsr: String, private val mSZMsg: String) : AsyncTask<Void, Void, Boolean>() {
        override fun onPreExecute() {
            super.onPreExecute()

            mProgressStatus = 0

            mPDDlg!!.max = 100
            // 设置对话框的标题
            mPDDlg!!.setTitle("发送消息")
            // 设置对话框 显示的内容
            mPDDlg!!.setMessage("发送进度")
            // 设置对话框不能用“取消”按钮关闭
            mPDDlg!!.setCancelable(true)
            mPDDlg!!.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialogInterface, i -> }

            // 设置对话框的进度条风格
            mPDDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mPDDlg!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            // 设置对话框的进度条是否显示进度
            mPDDlg!!.isIndeterminate = false

            mPDDlg!!.incrementProgressBy(-mPDDlg!!.progress)
            mPDDlg!!.show()
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        override fun doInBackground(vararg params: Void): Boolean? {
            val client = OkHttpClient()
            try {
                // set param
                val param = JSONObject()
                param.put(mSZColUsr, mSZUsr)
                param.put(mSZColMsg, mSZMsg)
                param.put(mSZColAppName,
                        mSZColValAppName + "-"
                                + PackageUtil.getVerName(context, GlobalDef.PACKAGE_NAME))

                val body = RequestBody.create(JSON, param.toString())

                mProgressStatus = 50
                val m = Message()
                m.what = MSG_PROGRESS_UPDATE
                mHDProgress!!.sendMessage(m)

                val request = Request.Builder()
                        .url(mSZUrlPost!!).post(body).build()
                client.newCall(request).execute()

                mProgressStatus = 100
                val m1 = Message()
                m1.what = MSG_PROGRESS_UPDATE
                mHDProgress!!.sendMessage(m1)
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return true
        }


        override fun onPostExecute(bret: Boolean?) {
            super.onPostExecute(bret)
            mPDDlg!!.dismiss()
        }
    }

    /**
     * safe message hanlder
     */
    private class LocalMsgHandler internal constructor(ac: DlgUsrMessage) : WRMsgHandler<DlgUsrMessage>(ac) {
        override fun processMsg(m: Message, home: DlgUsrMessage) {
            when (m.what) {
                MSG_PROGRESS_UPDATE -> {
                    home.mPDDlg!!.progress = home.mProgressStatus
                }

                else -> Log.e(LOG_TAG, String.format("msg(%s) can not process", m.toString()))
            }
        }
    }

    companion object {
        private var LOG_TAG = ::LocalMsgHandler.javaClass.simpleName

        // for progress dialog when send http post
        private val PROGRESS_DIALOG = 0x112
        private val MSG_PROGRESS_UPDATE = 0x111

        // for http post
        private val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}
