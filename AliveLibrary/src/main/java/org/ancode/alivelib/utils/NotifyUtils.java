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
public class NotifyUtils {


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


}
