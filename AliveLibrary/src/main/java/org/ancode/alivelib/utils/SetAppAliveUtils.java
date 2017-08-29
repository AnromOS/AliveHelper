package org.ancode.alivelib.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

/**
 * Created by andyliu on 17-2-15.
 */

public class SetAppAliveUtils {
    private static final String TAG = SetAppAliveUtils.class.getSimpleName();

    /***
     * 通过广播唤醒指定app
     *
     * @param context
     * @param packageName
     * @param className
     * @param action
     */
    public static void setAliveByBroadCast(final Context context, final String packageName, final String className, final String action, final String serviceName) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean send = true;
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(30); //30是最大值
                for (ActivityManager.RunningServiceInfo info : infos) {
                    if (info.service.getClassName().equals(serviceName)) {
                        send = false;
//                        AliveLog.v(APP_TAG, serviceName + "活着不发送广播");
                        break;
                    }

                }

                if (send) {
                    if (UiHelper.checkApkIsInstallByPk(context, packageName)) {
                        Intent intent = new Intent();
                        Context c = null;
                        try {
                            c = context.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
                            intent.setClassName(c, className);
                            intent.setAction(action);
                            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                            context.sendBroadcast(intent);
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.v(TAG, "setAliveByBroadCast error1");
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.v(TAG, "setAliveByBroadCast error2");
                            e.printStackTrace();
                        }
//                        AliveLog.v(APP_TAG, "发广播");
                    } else {
//                        AliveLog.v(APP_TAG, packageName + "未安装不发送广播");
                    }
                }

            }
        }).start();
    }


    /***
     * 唤醒指定应用servcie（尚待考证）
     *
     * @param context
     * @param packageName
     * @param serviceAllName
     */
    public static void setServiceAlive(Context context, String packageName, String serviceAllName, String action) {
        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName(packageName, serviceAllName));
        serviceIntent.setPackage(context.getPackageName());
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
        Log.v(TAG, "启动service");
    }
}
