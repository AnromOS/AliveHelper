package org.ancode.alivelib;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.ancode.alivelib.activity.AliveHelperActivity;
import org.ancode.alivelib.activity.AliveStatsActivity;
import org.ancode.alivelib.bean.BaseStatsInfo;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.listener.StringCallBack;
import org.ancode.alivelib.notification.AliveNotification;
import org.ancode.alivelib.service.AliveHelperService;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.IntentUtils;
import org.ancode.alivelib.utils.Log;
import org.ancode.alivelib.utils.NotifyUtils;

/**
 * Created by andyliu on 16-8-25.
 */
public class AliveHelper extends  BaseAliveHelper{


    private static AliveHelper helper = null;

    /****
     * 初始化library
     *
     * @param context
     * @return
     */
    public static AliveHelper init(Context context) {
        if (helper == null) {
            HelperConfig.CONTEXT = context;
            helper = new AliveHelper();
        }
//        initCrash(context);
        return helper;
    }


//    /***
//     * 初始化crash
//     *
//     * @param context
//     */
//    private static void initCrash(Context context) {
//        AliveHelperCrash handler = AliveHelperCrash.getInstance();
//        handler.init(context);
//        Thread.setDefaultUncaughtExceptionHandler(handler);
//    }


    /**
     * 是否使用原网地址访问数据
     *
     * @param b
     * @return
     */
    public static AliveHelper useAnet(boolean b) {
        HelperConfig.USE_ANET = b;
        return helper;
    }


    /**
     * log开关
     *
     * @param debug
     * @return
     */
    public static AliveHelper setDebug(boolean debug) {
        HelperConfig.DEBUG = debug;
        return helper;
    }

    public static AliveHelper setThemeColor(int themeColorId) {
        HelperConfig.THEME_COLOR_ID = themeColorId;
        return helper;
    }

    /***
     * 设置notification小图标id
     * <p>取值范围是[0-1]</p>
     *
     * @param iconId
     * @return
     */
    public static AliveHelper setNotifySmallIcon(int iconId) {
        if (iconId < 0) {
            throw new RuntimeException("setting small icon id error");
        }
        HelperConfig.SMALL_ICON_ID = iconId;
        return helper;
    }

    public static AliveHelper getHelper() {
        if (helper == null) {
            throw new NullPointerException("未初始化helper，请先在Application中初始化helper。");
        } else {
            return helper;
        }
    }

    public static void release() {
        if (helper != null) {
            helper = null;
        }
        if (HelperConfig.CONTEXT != null) {
            HelperConfig.CONTEXT = null;
        }
    }

    /****
     * 开启保活统计服务
     *
     * @param info
     * @param tag
     */
    @Deprecated
    public void openAliveStats(String info, String tag) {
        if (TextUtils.isEmpty(info)) {
            throw new IllegalArgumentException("info is null,you should set a json string");
        }

        AliveSPUtils.getInstance().setASUploadInfo(info);
        Log.v("AliveHelper", "接收到info信息 info = " + info);
        setAliveTag(tag);
        openAliveStats();
    }

    /***
     * 开启保活统计服务
     *
     * @param baseStatsInfo
     */
    public void openAliveStats(BaseStatsInfo baseStatsInfo) {
        if (TextUtils.isEmpty(baseStatsInfo.getStatsInfo().toString())) {
            throw new IllegalArgumentException("info is null,you should set a json string");
        }

        AliveSPUtils.getInstance().setASUploadInfo(baseStatsInfo.getStatsInfo().toString());
        Log.v("AliveHelper", "接收到info信息 info = " + baseStatsInfo.getStatsInfo().toString());
        setAliveTag(baseStatsInfo.getTag());
        openAliveStats();
    }

    /***
     * 关闭保活统计服务
     */
    public void closeAliveStats() {
        Intent intent = new Intent(HelperConfig.CONTEXT, AliveHelperService.class);
        intent.putExtra(AliveHelperService.ACTION, AliveHelperService.CLOSE_ALIVE_STATS_SERVICE_ACTION);
        HelperConfig.CONTEXT.startService(intent);
    }


    public void check(StringCallBack cb) {
        HttpClient.getAliveGuideUrl(cb);
    }

    /****
     * 显示防杀指南
     */
    public void showAliveUseGuide() {
        HelperConfig.CONTEXT.startActivity(IntentUtils.getNormalActivity(AliveHelperActivity.class, HelperConfig.THEME_COLOR_ID, true));
    }

    /***
     * 显示保活统计
     */
    public void showAliveStats() {
        HelperConfig.CONTEXT.startActivity(IntentUtils.getNormalActivity(AliveStatsActivity.class, HelperConfig.THEME_COLOR_ID, true));
    }


    /***
     * 开启保活警告
     * <p>警告点,取值范围是[0-1]</p>
     *
     * @param warningPoint
     */
    public void openAliveWarning(float warningPoint) {
        if (warningPoint < 0 || warningPoint > 1) {
            throw new RuntimeException("warning point range of [0-1]. your settings are" + warningPoint);
        }
        HelperConfig.WARNING_POINT = warningPoint;
        //启动统计alivetime服务
        Intent intent = new Intent(HelperConfig.CONTEXT, AliveHelperService.class);
        intent.putExtra(AliveHelperService.ACTION, AliveHelperService.OPEN_ALIVE_WARNING_SERVICE_ACTION);
        HelperConfig.CONTEXT.startService(intent);

    }

    /****
     * 设置警告点
     *
     * @param warningPoint
     * @return
     */
    public AliveHelper setWarnPoint(float warningPoint) {
        if (warningPoint < 0 || warningPoint > 1) {
            throw new RuntimeException("warning point range of [0-1]. your settings are" + warningPoint);
        }
        HelperConfig.WARNING_POINT = warningPoint;
        return this;
    }

    /****
     * 获取警告点
     *
     * @return
     */
    public float getWarnPoint() {
        return HelperConfig.WARNING_POINT;
    }

    /***
     * 关闭保活警告
     */
    public void closeAliveWarning() {
        Intent intent = new Intent(HelperConfig.CONTEXT, AliveHelperService.class);
        intent.putExtra(AliveHelperService.ACTION, AliveHelperService.CLOSE_ALIVE_WARNING_SERVICE_ACTION);
        HelperConfig.CONTEXT.startService(intent);
    }


    /**
     * 显示notification
     *
     * @param afterTime 延迟时间显示时间
     */
    public void aliveNotify(long afterTime) {
        new NotifyUtils().showAliveGuideNotify(afterTime);
    }


    /***
     * 取消方杀助手的提示
     */
    public void cancelNotification() {
        new AliveNotification().cancelAliveHelper();
    }


}
