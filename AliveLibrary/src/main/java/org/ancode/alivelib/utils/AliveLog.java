
package org.ancode.alivelib.utils;

import org.ancode.alivelib.config.HelperConfig;

public class AliveLog {
    public static final String LOG_TAG = "AliveLibrary:";


    public static int DEBUG_LEVEL = 5;// 1:error,2:warning,3:info,4:debug,5:verbose

    public static void v(String tag, String msg) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 5)
            android.util.Log.v(LOG_TAG + tag, msg);
    }



    public static void v(String tag, String msg, Throwable tr) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 5)
            android.util.Log.v(LOG_TAG + tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 4)
            android.util.Log.v(LOG_TAG + tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 4)
            android.util.Log.d(LOG_TAG + tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 3)
            android.util.Log.i(LOG_TAG + tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 3)
            android.util.Log.i(LOG_TAG + tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 2)
            android.util.Log.w(LOG_TAG + tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 2)
            android.util.Log.w(LOG_TAG + tag, msg, tr);
    }

    public static void w(String tag, Throwable tr) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 2)
            android.util.Log.w(LOG_TAG + tag, tr);
    }

    public static void e(String tag, String msg) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 1)
            android.util.Log.e(LOG_TAG + tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (HelperConfig.DEBUG && DEBUG_LEVEL >= 1)
            android.util.Log.e(LOG_TAG + tag, msg, tr);
    }
}
