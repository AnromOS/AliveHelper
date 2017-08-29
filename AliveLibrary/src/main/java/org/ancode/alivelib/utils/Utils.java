package org.ancode.alivelib.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;


import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.callback.DelayCallBack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andyliu on 16-8-25.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();


    /***
     * 获取机型信息
     *
     * @return
     */
    public static Map<String, String> getProp() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tag",HelperConfig.APP_TAG);
        map.put("model", Build.MODEL);
        //ANDROID版本号
        // map.put("buildVersion", String.valueOf(Build.VERSION.RELEASE));
        //版本号
        map.put("version", String.valueOf(Build.DISPLAY));
        //编译版本号
//        map.put("buildVersion", String.valueOf(Build.VERSION.SDK_INT));
        AliveLog.v(TAG, "model=" + map.get("model").toString() + ",version=" + map.get("version"));
        return map;
    }

    /**
     * 通过包名获取应用程序的名称。
     */
    public static String getAppName() {
        PackageManager pm = HelperConfig.CONTEXT.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(HelperConfig.CONTEXT.getPackageName().toString(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }


    /***
     * 延迟操作
     *
     * @param afterTimer
     * @param delayCallBack
     */
    public static void delayCall(final long afterTimer, final DelayCallBack delayCallBack) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    delayCallBack.delayCall();
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(afterTimer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();

    }


    /**
     * 获取手机的版本信息
     *
     * @return
     */
    public static  String getVersionInfo() {
        try {
            PackageManager pm = HelperConfig.CONTEXT.getPackageManager();
            PackageInfo info = pm.getPackageInfo(HelperConfig.CONTEXT.getPackageName(), 0);
            return info.versionName + " - " + info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }


    /**
     * 获取手机的硬件信息
     *
     * @return
     */
    public static String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        // 通过反射获取系统的硬件信息
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                // 暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
