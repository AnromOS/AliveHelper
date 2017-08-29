package org.ancode.alivelib.utils;

import android.os.Environment;
import android.util.Log;

import org.ancode.alivelib.config.HelperConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by andyliu on 16-11-21.
 */
public class AliveTestUtils {
    public static final String TAG = AliveTestUtils.class.getSimpleName();
    private static final boolean  SHOW_LOG = false;
    /***
     * 统计数据备份
     *
     * @param fileName
     * @param data
     */
    public static void backUpUploadData(final String fileName, final long startTime, final JSONObject data) {
        if (!AliveSPUtils.getInstance().getIsRelease() && SHOW_LOG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String beginTime = AliveDateUtils.timeFormat(startTime, AliveDateUtils.DEFAULT_FORMAT);
                    String endTime = AliveDateUtils.timeFormat(new Date(), AliveDateUtils.DEFAULT_FORMAT);
                    String title = null;
                    String tag = null;
                    try {
                        tag = data.getString("tag");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    title = "文件名" + fileName + ",APP_TAG=" + tag + ",开始时间=" + beginTime + "," + ",结束时间=" + endTime;
                    String myData = "=============================================\n"
                            + title + "\n\n"
                            + data.toString() + "\n=============================================\n";
                    FileOutputStream fos = null;
                    String dirStr = HelperConfig.BACK_UP_FILE_PATH + Utils.getAppName() + File.separator;
                    File dir = new File(dirStr);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String backFileName = dirStr + HelperConfig.ALIVE_STATS_BACK_UP_FILE_NAME;
                    File file = new File(backFileName);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            Log.e(TAG, "创建备份文件失败");
                            e.printStackTrace();
                        }
                    }
                    try {
                        fos = new FileOutputStream(file, true);
                        fos.write(myData.getBytes());
                        Log.v(TAG, "备份AliveStats数据成功");
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "未找到文件");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(TAG, "IO操作失败!");
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (Exception e) {
                            Log.e(TAG, "关闭文件失败");
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public static void LogBpoint(final long datetime, final String s) {
        if (!AliveSPUtils.getInstance().getIsRelease() && SHOW_LOG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String date = AliveDateUtils.timeFormat(datetime, AliveDateUtils.DEFAULT_FORMAT);
                    String tag = AliveSPUtils.getInstance().getASTag();
                    String myData = "APP_TAG=" + tag + " time=" + date + " :" + s + "\n";
                    FileOutputStream fos = null;
                    String dirStr = HelperConfig.BACK_UP_FILE_PATH + Utils.getAppName() + File.separator;
                    File dir = new File(dirStr);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String backFileName = dirStr + HelperConfig.ALIVE_BQPOINT_LOG_FILE_NAME;
                    File file = new File(backFileName);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            Log.e(TAG, "create log file failed!");
                            e.printStackTrace();
                        }
                    }
                    try {
                        fos = new FileOutputStream(file, true);
                        fos.write(myData.getBytes());
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "do not find file");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(TAG, "IO操作失败!");
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (Exception e) {
                            Log.e(TAG, "关闭文件失败");
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    public static void LogBpoint(final String s) {

        LogBpoint(new Date().getTime(), s);
    }


    public static void LogStats(final String s) {
        if (!AliveSPUtils.getInstance().getIsRelease() && SHOW_LOG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String date = AliveDateUtils.timeFormat(new Date(), AliveDateUtils.DEFAULT_FORMAT);
                    String tag = AliveSPUtils.getInstance().getASTag();
                    String myData = "APP_TAG=" + tag + " time=" + date + " :" + s + "\n";
                    FileOutputStream fos = null;
                    String dirStr = HelperConfig.BACK_UP_FILE_PATH + Utils.getAppName() + File.separator;
                    File dir = new File(dirStr);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String backFileName = dirStr + HelperConfig.ALIVE_STATS_LOG_FILE_NAME;
                    File file = new File(backFileName);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            Log.e(TAG, "create log file failed!");
                            e.printStackTrace();
                        }
                    }
                    try {
                        fos = new FileOutputStream(file, true);
                        fos.write(myData.getBytes());
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "do not find file");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(TAG, "IO操作失败!");
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (Exception e) {
                            Log.e(TAG, "关闭文件失败");
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

    }
}
