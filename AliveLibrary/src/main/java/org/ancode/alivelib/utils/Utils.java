package org.ancode.alivelib.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;


import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.callback.DelayCallBack;

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


}
