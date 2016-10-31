package org.ancode.alivelib.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import org.ancode.alivelib.R;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.notification.AliveNotification;

/**
 * Created by andyliu on 16-10-31.
 */
public class NotifyUtils {

    protected static final int SHOW_FLAG = 0x01;


    /***
     * 防杀指南通知
     * @param aftertime
     */
    public static void showAliveGuideNotify(long aftertime) {
        AliveNotification  baseNotification = new AliveNotification();
        Bitmap lageIcon = UiHelper.getAppIcon();
        String appName = Utils.getAppName();
        String title = String.format(HelperConfig.CONTEXT.getString(R.string.alive_activity_title), appName);
        String text = String.format(HelperConfig.CONTEXT.getString(R.string.alive_notify_text), appName);
        baseNotification.setTitle(title)
                .setText(text)
                .setTickerText(title)
                .setLargeIcon(lageIcon);
        if (HelperConfig.SMALL_ICON_ID <= -1) {
            baseNotification.setSmallIcon(HelperConfig.CONTEXT.getApplicationInfo().icon);
        } else {
            baseNotification.setSmallIcon(HelperConfig.SMALL_ICON_ID);
        }
        delayCallNotification(baseNotification, aftertime);
    }
    /***
     * 延迟操作
     *
     * @param afterTimer
     * @param handler
     */
    protected static void delayCall(final long afterTimer, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(afterTimer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(SHOW_FLAG);
            }
        }).start();
    }

    private static void delayCallNotification(final AliveNotification baseNotification, long aftertime) {

        delayCall(aftertime, new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == SHOW_FLAG) {
//                    Log.v("应用","应用名称他是  -----"+appName);
                    baseNotification.show(IntentUtils.getNotifyActivity(null, HelperConfig.THEME_COLOR_ID));
                }
            }
        });
    }


}
