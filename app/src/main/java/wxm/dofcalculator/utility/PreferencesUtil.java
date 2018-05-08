package wxm.dofcalculator.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import wxm.androidutil.util.UiUtil;
import wxm.androidutil.util.UtilFun;
import wxm.dofcalculator.R;


/**
 * for preferences
 * Created by WangXM on2017/4/20.
 */
public class PreferencesUtil {
    private final static String PROPERTIES_NAME = "DOFCalculator_properties";
    private final static String SET_CHART_COLOR = "chart_color";

    public final static String SET_PAY_COLOR    = "pay";
    public final static String SET_INCOME_COLOR = "income";
    public final static String SET_BUDGET_UESED_COLOR       = "budget_used";
    public final static String SET_BUDGET_BALANCE_COLOR     = "budget_balance";


    /// BEGIN
    /**
     * 加载chart颜色配置
     * @return   颜色配置
     */
    public static HashMap<String, Integer> loadChartColor()     {
        SharedPreferences param = ContextUtil.Companion.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        Context ct = ContextUtil.Companion.getInstance();
        String sb = SET_PAY_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.sienna)) +
                " " + SET_INCOME_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.teal)) +
                " " + SET_BUDGET_UESED_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.sienna)) +
                " " + SET_BUDGET_BALANCE_COLOR + ":" + String.valueOf(UiUtil.getColor(ct, R.color.teal));

        String load = param.getString(SET_CHART_COLOR, sb);
        return parseChartColors(load);
    }


    /**
     * 保存chart颜色配置
     * @param ccs  配色配置
     */
    public static void saveChartColor(HashMap<String, Integer> ccs)     {
        SharedPreferences param = ContextUtil.Companion.getInstance()
                .getSharedPreferences(PROPERTIES_NAME, Context.MODE_PRIVATE);

        String pr = parseChartColorsToString(ccs);
        param.edit().putString(SET_CHART_COLOR, pr).apply();
    }
    /// END


    /// BEGIN PRIVATE
    /**
     * 把配置字符串解析成结果
     * @param pr        配置字符串
     * @return          结果
     */
    private static List<String> parsePreferences(String pr)  {
        ArrayList<String> ret = new ArrayList<>();
        String[] pr_arr = pr.split(":");
        Collections.addAll(ret, pr_arr);

        return ret;
    }

    /**
     * 把动作解析成配置字符串
     * @param acts      待解析动作
     * @return          配置字符串
     */
    private static String parseToPreferences(List<String> acts)  {
        boolean ff = true;
        StringBuilder sb = new StringBuilder();
        for(String i : acts)    {
            if(!ff) {
                sb.append(":");
            }   else    {
                ff = false;
            }

            sb.append(i);
        }

        return sb.toString();
    }

    /**
     * 从配置字符串解析配置
     * @param cc    配置字符串
     * @return      解析配置
     */
    private static HashMap<String, Integer> parseChartColors(String cc)  {
        HashMap<String, Integer> ret = new HashMap<>();
        String[] cclns = cc.split(" ");
        for(String i : cclns)   {
            String[] iilns = i.split(":");

            ret.put(iilns[0], Integer.parseInt(iilns[1]));
        }

        return ret;
    }


    /**
     * 图表颜色串行化为字符串
     * @param hmcc  配置信息
     * @return      字符串
     */
    private static String parseChartColorsToString(HashMap<String, Integer> hmcc)  {
        StringBuilder ret = new StringBuilder();
        for(String i : hmcc.keySet())   {
            StringBuilder sb = new StringBuilder();
            sb.append(i).append(":").append(hmcc.get(i).toString());

            if(0 == ret.length())
                ret.append(sb);
            else
                ret.append(" ").append(sb);
        }

        return ret.toString();
    }
    /// END PRIVATE
}
