package org.ancode.alivelib;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.ancode.alivelib.activity.AliveGuideActivity;
import org.ancode.alivelib.activity.AliveStatsActivity;
import org.ancode.alivelib.base.BaseAliveHelper;
import org.ancode.alivelib.bean.BaseStatsInfo;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.notification.AliveNotification;
import org.ancode.alivelib.service.AliveHelperService;
import org.ancode.alivelib.utils.IntentUtils;
import org.ancode.alivelib.utils.NotifyUtils;

/**
 * Created by andyliu on 16-8-25.
 */
public class AliveHelper extends BaseAliveHelper {


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
        } else {

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

    /***
     * 设置主题颜色
     *
     * @param themeColorId
     * @return
     */
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
            helper.closeAliveStats();
            helper.closeAliveWarning();
            helper.cancelNotification();
            helper = null;
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        if (HelperConfig.CONTEXT != null) {
            HelperConfig.CONTEXT = null;
        }
    }

    /***
     * 开启保活统计服务
     *
     * @param baseStatsInfo
     */
    public void openAliveStats(BaseStatsInfo baseStatsInfo) {
        baseStatsInfo.setDevice(Build.MODEL); //设备名称
        baseStatsInfo.setOs(Build.DISPLAY);//系统版本号(显示版本号)
        setAliveInfo(baseStatsInfo.getStatsInfo().toString());
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

    /***
     * 直接获取使用指南的url
     *
     * @param cb
     */
    public void getUseGuideUrl(StringCallBack cb) {
        HttpClient.getAliveGuideUrl(cb);
    }

    /****
     * 显示防杀指南
     */
    public void showAliveUseGuide() {
        HelperConfig.CONTEXT.startActivity(IntentUtils.getNormalActivity(AliveGuideActivity.class, true));
    }

    /***
     * 显示保活统计
     */
    public void showAliveStats() {
        HelperConfig.CONTEXT.startActivity(IntentUtils.getNormalActivity(AliveStatsActivity.class, true));
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
     * 显示保活指南的通知
     *
     * @param afterTime 延迟时间显示时间
     */
    public void notifyAliveUseGuide(long afterTime) {
        new NotifyUtils().showAliveGuideNotify(afterTime);
    }


    /**
     * 显示保活统计的通知
     *
     * @param afterTime 延迟时间显示时间
     */
    public void notifyAliveStats(long afterTime) {
        new NotifyUtils().showAliveStatsNotify(afterTime);
    }


    /***
     * 取消方杀助手的提示
     */
    public void cancelNotification() {
        new AliveNotification().cancelAll();
    }


}
