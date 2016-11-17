package org.ancode.alivelib;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.ancode.alivelib.activity.AliveGuideActivity;
import org.ancode.alivelib.activity.AliveStatsActivity;
import org.ancode.alivelib.base.BaseAliveHelper;
import org.ancode.alivelib.bean.BaseStatsInfo;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.notification.AliveNotification;
import org.ancode.alivelib.service.AliveHelperService;
import org.ancode.alivelib.utils.AliveLog;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.IntentUtils;
import org.ancode.alivelib.utils.NotifyUtils;
import org.ancode.alivelib.utils.PatchUtils;

/**
 * Created by andyliu on 16-8-25.
 */
public class AliveHelper extends BaseAliveHelper {


    private static AliveHelper helper = null;
    private static final String TAG = AliveHelper.class.getSimpleName();

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
            doPatch();
        } else {

        }
//        initCrash(context);
        return helper;
    }

    /***
     * 打补丁
     */
    private static void doPatch() {
        PatchUtils.deleteOldAsFile();
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
     * 是否是外部版本 默认是外部版本
     *
     * @param b
     * @return
     */
    public static AliveHelper isRelease(boolean b) {
        AliveSPUtils.getInstance().setIsRelease(b);
        AliveLog.v("AliveHelper", "接收到isRelease信息 isRelease = " + b);
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

    public static void killAliveHelper() {
        if (helper != null) {
            helper.closeAliveStats();
            helper.closeAliveWarning();
            helper.cancelNotification();
            helper = null;
        }

        if (HelperConfig.CONTEXT != null) {
            AliveSPUtils.getInstance().setIsRelease(true);
            HelperConfig.CONTEXT = null;
        }

//        android.os.Process.killProcess(android.os.Process.myPid());
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
    public void notifyAliveStats(final long afterTime) {
        if (AliveSPUtils.getInstance().getIsRelease()) {

            Log.v(TAG, "发现当前设置为外部版,请求服务器是否显示notification");
            isEnableShowNotify(new StringCallBack() {
                @Override
                public void onResponse(String response) {
                    if ("0".equals(response)) {
                        new NotifyUtils().showAliveStatsNotify(afterTime);
                        Log.v(TAG, "isEnableShowNotify 服务器返回" + response + "显示notification");
                    } else {
                        Log.v(TAG, "isEnableShowNotify 服务器返回" + response + "不显示notification");
                    }
                }

                @Override
                public void error(String error) {
                    Log.e(TAG, "请求是否显示notification接口失败 error = " + error);
                }
            });
        } else {
            new NotifyUtils().showAliveStatsNotify(afterTime);
            Log.v(TAG, "发现当前设置为内部版,直接显示notification");
        }

    }


    /***
     * 是否显示notification
     *
     * @param stringCallBack
     */
    public void isEnableShowNotify(StringCallBack stringCallBack) {
        HttpClient.isEnableShowNotify(stringCallBack);
    }

    /***
     * 取消方杀助手的提示
     */
    public void cancelNotification() {
        new AliveNotification().cancelAll();
    }


}
