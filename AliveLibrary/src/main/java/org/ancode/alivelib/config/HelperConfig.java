package org.ancode.alivelib.config;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by andyliu on 16-8-31.
 */
public class HelperConfig {
    public static Context CONTEXT = null;
    public static boolean DEBUG = false;
    public static boolean USE_ANET = false;

    /***
     * 状态栏是否使用主题颜色
     */
    public static final String APPLY_STATUS_COLOR = "apply_status_color";
    public static int THEME_COLOR_ID = -1;


    //统计数据相关配置
    /***
     * APP存活统计频率
     */
    public static final int ALIVE_STATS_RATE = 30 * 1000;
    /**
     * 时间相差有效值
     */
    public static final int CHECK_STATS_DIFFER = 35 * 1000;

    /***
     * aliveStats存储文件名称(旧文件)
     */
    public static final String OLD_ALIVE_STATS_FILE_NAME = "alive_cout_file";
    /***
     * aliveStats存储文件名称(新文件)
     */
    public static final String NEW_ALIVE_STATS_FILE_NAME = "alive_stats_file_2";
    /***
     * 应用默认警告点
     * 0.0-1.0
     */
    public static float WARNING_POINT = 0.8f;
    /***
     * 小图标id
     */
    public static int SMALL_ICON_ID = -1;

    /**
     * aliveStats上传数据频率 单位/小时
     */
    public static final int UPLOAD_ALIVE_STATS_RATE = 1;


}
