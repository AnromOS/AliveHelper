package org.ancode.alivelib.utils;

import android.util.Log;

import org.ancode.alivelib.config.HelperConfig;

import java.io.File;

/**
 * 打补丁相关
 * Created by andyliu on 16-11-16.
 */
public class PatchUtils {
    public static final String TAG = PatchUtils.class.getSimpleName();

    /***
     * 删除旧的保活统计文件
     */
    public static void deleteOldAsFile() {
        try {
            //****删除旧保活统计文件补丁****//
            File aliveStatsfile = new File(HelperConfig.CONTEXT.getFilesDir(), HelperConfig.OLD_ALIVE_STATS_FILE_NAME);
            if (aliveStatsfile.exists()) {
                AliveSPUtils.getInstance().setASBeginTime(0);
                HelperConfig.CONTEXT.deleteFile(HelperConfig.OLD_ALIVE_STATS_FILE_NAME);
                aliveStatsfile = null;
                Log.v(TAG, "保活统计有旧的文件,执行删除操作,并初始化数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
