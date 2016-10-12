package org.ancode.alivelib.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.ancode.alivelib.base.BaseSPUtils;
import org.ancode.alivelib.config.HelperConfig;

/**
 * Created by xf on 16-8-3.
 */
public class SPUtils extends BaseSPUtils {
    private static final String TAG = SPUtils.class.getSimpleName();
    private static final String SP_NAME = "alive_helper";

    private static SPUtils spUtils;
    private SharedPreferences sp;

    @Override
    protected SharedPreferences getSharedPreferences() {
        return sp;
    }

    private SPUtils() {
        sp = HelperConfig.CONTEXT.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS);
    }

    public static SPUtils getInstance() {
        if (spUtils == null) {
            synchronized (SPUtils.class) {
                if (spUtils == null) {
                    spUtils = new SPUtils();
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
    private static final String ALIVE_STATS_END_TIME = "alive_stats_end_time";
    /**
     * 终端信息（这里的格式不是确定，不同的app报名自己定义info格式）
     */
    private static final String ALIVE_STATS_UPLOAD_INFO = "alive_stats_upload_info";

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
     *
     * @return
     */
    public void setASBeginTime(long asStartTime) {
        putLong(ALIVE_STATS_BEGIN_TIME, asStartTime);
    }

    /**
     * 获取统计结束时间
     *
     * @return
     */
    public long getASEndTime() {
        return getLong(ALIVE_STATS_END_TIME, 0);
    }


    /**
     * 设置统计结束时间
     *
     * @return
     */
    public void setASEndTime(long asEndTime) {
        putLong(ALIVE_STATS_END_TIME, asEndTime);
    }

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
}
