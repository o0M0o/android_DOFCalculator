package wxm.dofcalculator.utility

import android.content.Context
import wxm.dofcalculator.R
import java.util.*


/**
 * for preferences
 * Created by WangXM on2017/4/20.
 */
object PreferencesUtil {
    private val PROPERTIES_NAME = "DOFCalculator_properties"

    /// BEGIN PRIVATE
    /**
     * 把配置字符串解析成结果
     * @param pr        配置字符串
     * @return          结果
     */
    private fun parsePreferences(pr: String): List<String> {
        val ret = ArrayList<String>()
        Collections.addAll(ret, *pr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        return ret
    }

    /**
     * 把动作解析成配置字符串
     * @param acts      待解析动作
     * @return          配置字符串
     */
    private fun parseToPreferences(acts: List<String>): String {
        var ff = true
        val sb = StringBuilder()
        for (i in acts) {
            if (!ff) {
                sb.append(":")
            } else {
                ff = false
            }

            sb.append(i)
        }

        return sb.toString()
    }

    /**
     * 从配置字符串解析配置
     * @param cc    配置字符串
     * @return      解析配置
     */
    private fun parseChartColors(cc: String): HashMap<String, Int> {
        val ret = HashMap<String, Int>()
        cc.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().forEach {
            it.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().let {
                ret[it[0]] = Integer.parseInt(it[1])
            }
        }

        return ret
    }


    /**
     * 图表颜色串行化为字符串
     * @param hmCc  配置信息
     * @return      字符串
     */
    private fun parseChartColorsToString(hmCc: HashMap<String, Int>): String {
        val ret = StringBuilder()
        hmCc.forEach { key, value ->
            StringBuilder().let {
                it.append(key).append(":").append(value.toString())
                if (ret.isEmpty())
                    ret.append(it)
                else
                    ret.append(" ").append(it)
            }
        }

        return ret.toString()
    }
    /// END PRIVATE
}
