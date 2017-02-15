package org.ancode.alivelib.utils;

import android.graphics.Bitmap;

import org.ancode.alivelib.R;
import org.ancode.alivelib.activity.AliveGuideActivity;
import org.ancode.alivelib.activity.AliveStatsActivity;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.callback.DelayCallBack;
import org.ancode.alivelib.notification.AliveNotification;

/**
 * Created by andyliu on 16-10-31.
 */
public class AliveNotifyUtils {
    public static final String TAG = AliveNotifyUtils.class.getSimpleName();

    /***
     * 防杀指南通知
     *
     * @param aftertime
     */
    public static void showAliveGuideNotify(long aftertime) {
        final AliveNotification baseNotification = new AliveNotification();
        Bitmap lageIcon = UiHelper.getAppIcon();
        String appName = Utils.getAppName();
        String title = String.format(HelperConfig.CONTEXT.getString(R.string.alive_activity_title), appName);
        String text = String.format(HelperConfig.CONTEXT.getString(R.string.alive_notify_text), appName);
        baseNotification.setTitle(title)
                .setText(text)
                .setTickerText(title)
                .setLargeIcon(lageIcon)
                .setNotifyFlag(AliveNotification.ALIVE_HELPER_NOTIFY_FLAG);
        if (HelperConfig.SMALL_ICON_ID <= -1) {
            baseNotification.setSmallIcon(HelperConfig.CONTEXT.getApplicationInfo().icon);
        } else {
            baseNotification.setSmallIcon(HelperConfig.SMALL_ICON_ID);
        }

        Utils.delayCall(aftertime, new DelayCallBack() {
            @Override
            public void delayCall() {
                baseNotification.show(IntentUtils.getNotifyActivity(AliveGuideActivity.class));
            }
        });
    }


    /***
     * 保活统计
     *
     * @param aftertime
     */
    public static void showAliveStatsNotify(long aftertime) {
        final AliveNotification baseNotification = new AliveNotification();
        Bitmap lageIcon = UiHelper.getAppIcon();
        String appName = Utils.getAppName();
        String title = String.format(HelperConfig.CONTEXT.getString(R.string.alive_stats_title), appName);
        String text = String.format(HelperConfig.CONTEXT.getString(R.string.alive_stats_notify_text), appName);
        baseNotification.setTitle(title)
                .setText(text)
                .setTickerText(title)
                .setLargeIcon(lageIcon)
                .setNotifyFlag(AliveNotification.ALIVE_STATS_NOTIFY_FLAG);
        if (HelperConfig.SMALL_ICON_ID <= -1) {
            baseNotification.setSmallIcon(HelperConfig.CONTEXT.getApplicationInfo().icon);
        } else {
            baseNotification.setSmallIcon(HelperConfig.SMALL_ICON_ID);
        }

        Utils.delayCall(aftertime, new DelayCallBack() {
            @Override
            public void delayCall() {
                baseNotification.show(IntentUtils.getNotifyActivity(AliveStatsActivity.class));
            }
        });
    }


    public static boolean isCheckShowASNotify = false;

    /***
     * 检测是否显示在线成绩单通知
     * inTimer=true定时提示模式，inTimer=false是手动点开模式
     *
     * @param inTimer
     * @param time
     * @return
     */
    public static boolean checkShowASNotify(boolean inTimer, long time) {
        if (isCheckShowASNotify) {
            AliveLog.v(TAG, "有地方正在检测是否显示在线成绩单通知");
            return false;
        }
        isCheckShowASNotify = true;
        try {
            if (inTimer) {
                long nextShowAsNotifyTime = AliveSPUtils.getInstance().getNextShowAsNotifyTime();
                if (nextShowAsNotifyTime == 0) {
                    //设置明天9点
                    long today9Point = AliveDateUtils.getTody9Point(time);
                    long nextDate = AliveDateUtils.getNextDay9Point();
                    AliveSPUtils.getInstance().setNextShowAsNotifyTime(nextDate);

                    if (!AliveSPUtils.getInstance().getIsRelease()) {
                        AliveLog.v(TAG, "第一次提示用户查看保活统计");
                        return true;
                    }
                } else if (time >= nextShowAsNotifyTime) {
                    AliveLog.v(TAG, "到点了提示用户查看保活统计");
                    //设置明天9点
                    long nextDate = AliveDateUtils.getNextDay9Point();
                    AliveSPUtils.getInstance().setNextShowAsNotifyTime(nextDate);
                    return true;
                }
            } else {
                AliveLog.v(TAG, "Activity检测提示在线成绩单通知");
                long nextShowAsNotifyTime = AliveSPUtils.getInstance().getNextShowAsNotifyTime();
                if (nextShowAsNotifyTime > 0) {
                    long nowStartTime = AliveDateUtils.getToDayStartTime();
                    long nextStratTime = AliveDateUtils.getThisDayStartTime(nextShowAsNotifyTime);
                    if (nowStartTime == nextStratTime) {
                        long nextShowTime = AliveDateUtils.getNextDay9Point();
                        AliveSPUtils.getInstance().setNextShowAsNotifyTime(nextShowTime);
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            AliveLog.e(TAG, "检测是否提示在线成绩单通知出错");
            e.printStackTrace();
        } finally {
            isCheckShowASNotify = false;
        }
        AliveLog.v(TAG, "在线成绩单检测，不需要弹提示");
        return false;

    }


}
