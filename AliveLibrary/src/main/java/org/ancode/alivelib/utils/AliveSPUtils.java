package org.ancode.alivelib.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.ancode.alivelib.base.BaseSPUtils;
import org.ancode.alivelib.config.HelperConfig;

/**
 * Created by xf on 16-8-3.
 */
public class AliveSPUtils extends BaseSPUtils {
    private static final String TAG = AliveSPUtils.class.getSimpleName();
    private static final String SP_NAME = "alive_helper";

    private static AliveSPUtils spUtils;
    private SharedPreferences sp;

    @Override
    protected SharedPreferences getSharedPreferences() {
        return sp;
    }

    private AliveSPUtils() {
        sp = HelperConfig.CONTEXT.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS);
    }

    public static AliveSPUtils getInstance() {
        if (spUtils == null) {
            synchronized (AliveSPUtils.class) {
                if (spUtils == null) {
                    spUtils = new AliveSPUtils();
                }
            }
        }
        return spUtils;
    }


    /**
     * 统计开始时间
     */
    private static final String ALIVE_STATS_BEGIN_TIME = "alive_stats_begin_time";

    /**
     * 统计结束时间
     */
//    private static final String ALIVE_STATS_END_TIME = "alive_stats_end_time";
    /**
     * 终端信息（这里的格式不是确定，不同的app报名自己定义info格式）
     */
    private static final String ALIVE_STATS_UPLOAD_INFO = "alive_stats_upload_info";
    /**
     * 终端信息（这里的格式不是确定，不同的app报名自己定义info格式）
     */
    private static final String ALIVE_STATS_TAG = "alive_stats_tag";


    /**
     * 是否是外部版
     */
    private static final String IS_RELEASE = "is_release";


    /**
     * 下一次显示保活统计的时间
     */
    private static final String NEXT_SHOW_AS_NOTIFY_TIME = "NEXT_SHOW_NOTIFY_TIME";

    /**
     * 是否是外部版
     */
    private static final String ALIVE_STATS_FILE_NAME = "alive_stats_file_name";

    /**
     * 获取统计开始时间
     *
     * @return
     */
    public long getASBeginTime() {
        return getLong(ALIVE_STATS_BEGIN_TIME, 0);
    }

    /**
     * 设置统计开始时间
     */
    public void setASBeginTime(long asStartTime) {
        putLong(ALIVE_STATS_BEGIN_TIME, asStartTime);
    }

//    /**
//     * 获取统计结束时间
//     *
//     * @return
//     */
//    public long getASEndTime() {
//        return getLong(ALIVE_STATS_END_TIME, 0);
//    }
//
//
//    /**
//     * 设置统计结束时间
//     */
//    public void setASEndTime(long asEndTime) {
//        putLong(ALIVE_STATS_END_TIME, asEndTime);
//    }

    /***
     * 获取终端信息
     *
     * @return
     */
    public String getASUploadInfo() {
        return getString(ALIVE_STATS_UPLOAD_INFO, "");
    }

    /**
     * 设置终端信息
     *
     * @param uploadInfo
     */
    public void setASUploadInfo(String uploadInfo) {
        putString(ALIVE_STATS_UPLOAD_INFO, uploadInfo);
    }


    /***
     * 获取统计标示
     *
     * @return
     */
    public String getASTag() {
        return getString(ALIVE_STATS_TAG, "");
    }

    /**
     * 设置统计标示
     *
     * @param tag
     */
    public void setASTag(String tag) {
        putString(ALIVE_STATS_TAG, tag);
    }

    public void setIsRelease(boolean b) {
        putBoolean(IS_RELEASE, b);
    }

    public boolean getIsRelease() {
        return getBoolean(IS_RELEASE, true);
    }


    public void setNextShowAsNotifyTime(long time) {
        putLong(NEXT_SHOW_AS_NOTIFY_TIME, time);
    }

    public long getNextShowAsNotifyTime() {
        return getLong(NEXT_SHOW_AS_NOTIFY_TIME, 0);
    }


    /***
     * 保活统计文件名
     *
     * @return
     */
    public String getAliveStatsFileName() {
        return getString(ALIVE_STATS_FILE_NAME, "");
    }

    /**
     * 保活统计文件名
     *
     * @param fileName
     */
    public void setAliveStatsFileName(String fileName) {
        putString(ALIVE_STATS_FILE_NAME, fileName);
    }
}
