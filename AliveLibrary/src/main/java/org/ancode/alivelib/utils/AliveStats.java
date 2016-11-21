package org.ancode.alivelib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.ancode.alivelib.AliveHelper;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.http.HttpClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 保活统计
 * Created by andyliu on 16-11-7.
 */
public class AliveStats {
    private static final String TAG = AliveStats.class.getSimpleName();
    private boolean uploadingAlive = false;
    //文件写入
    private File aliveStatsfile = null;
    private FileWriter fileWriter = null;
    private BufferedWriter writer = null;
    private Context context = null;

    private ScheduledThreadPoolExecutor aliveStatsThreader = null;
    private AliveStatsTask aliveStatsTask = null;

    public AliveStats(Context context) {
        this.context = context;
    }

    public void openStatsLiveTimer() {
        if (aliveStatsThreader != null)
            closeStatsLiveTimer();

        if (aliveStatsThreader == null) {
            aliveStatsThreader = new ScheduledThreadPoolExecutor(1);
        }
        if (aliveStatsTask == null) {
            aliveStatsTask = new AliveStatsTask();
        }
        AliveLog.v(TAG, "---start Stats alive---");
        aliveStatsThreader.scheduleAtFixedRate(
                aliveStatsTask,
                2,
                HelperConfig.ALIVE_STATS_RATE,
                TimeUnit.SECONDS);
    }

    public void closeStatsLiveTimer() {
        if (aliveStatsThreader != null) {
            if (aliveStatsTask != null) {
                aliveStatsThreader.remove(aliveStatsTask);
                aliveStatsTask = null;
            }
            aliveStatsThreader.shutdown();
            aliveStatsThreader = null;
            AliveLog.v(TAG, "---close Stats alive---");
        }

    }

    public void clearAliveStats() {
        closeStatsLiveTimer();
        clearFileWriter();
    }

    private void aliveStats() {
        long nowTime = new Date().getTime();
        sendNotifyAliveStats(nowTime);
        try {
            checkFileWriter();

            //TODO[计算统计范围,超过一小时,将数据上传至服务器]
            //开始时间为0时重新赋值
            long startTime = AliveSPUtils.getInstance().getASBeginTime();
            if (startTime == 0) {
                AliveSPUtils.getInstance().setASBeginTime(nowTime);
                startTime = nowTime;
            }

            float differTime = AliveDateUtils.getDifferHours(startTime, nowTime);

            //TODO[统计Wifi/3G状态]
            String netStatus = NetUtils.getNetStatus(HelperConfig.CONTEXT);
            //写入文件
            writer.write(nowTime + " " + netStatus + "\r\n");
            writer.flush();
            AliveLog.v(TAG, "打点 =" + AliveDateUtils.timeFormat(nowTime, null) + " " + netStatus);

            if (differTime >= HelperConfig.UPLOAD_ALIVE_STATS_RATE && !uploadingAlive) {
                AliveLog.v(TAG, "距离第一次统计时间" + differTime + "小时,是否正在上传->," + uploadingAlive + "准备上传服务器");
                uploadingAlive = true;
                try {
                    HttpClient.uploadAliveStats("uploadAliveStats", handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    uploadingAlive = false;
                }
            } else {
                AliveLog.v(TAG, "距离第一次统计时间" + differTime + "小时 是否正在上传->," + uploadingAlive + "不上传服务器");
            }

        } catch (Exception e) {
            AliveLog.e(TAG, "write error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /***
     * 定时通知用户 查看在线成绩单(每天9点)
     *
     * @param nowTime
     */
    private void sendNotifyAliveStats(long nowTime) {
        try {
            long nextShowAsNotifyTime = AliveSPUtils.getInstance().getNextShowAsNotifyTime();
            if (nextShowAsNotifyTime == 0) {
                //设置明天9点
                long today9Point = AliveDateUtils.getTody9Point(nowTime);
                long nextDate = AliveDateUtils.getNextDayThisTime(today9Point);
                AliveSPUtils.getInstance().setNextShowAsNotifyTime(nextDate);

                if (!AliveSPUtils.getInstance().getIsRelease()) {
                    AliveLog.v(TAG, "第一次提示用户查看保活统计");
                    handler.sendEmptyMessage(SHOW_ALIVE_STATS_NOTIFY);
                }
            } else if (nowTime >= nextShowAsNotifyTime) {
                AliveLog.v(TAG, "到点了提示用户查看保活统计");
                //设置明天9点
                long nextDate = AliveDateUtils.getNextDayThisTime(nextShowAsNotifyTime);
                AliveSPUtils.getInstance().setNextShowAsNotifyTime(nextDate);
                handler.sendEmptyMessage(SHOW_ALIVE_STATS_NOTIFY);
            }
        } catch (Exception e) {
            Log.e(TAG, "提示用户查看在线成绩单失败");
            e.printStackTrace();
        }

    }

    private void checkFileWriter() throws Exception {
        if (aliveStatsfile == null) {

            aliveStatsfile = new File(context.getFilesDir(), HelperConfig.NEW_ALIVE_STATS_FILE_NAME);

            if (!aliveStatsfile.exists()) {
                try {
                    aliveStatsfile.createNewFile();
                } catch (IOException e) {
                    AliveLog.e(TAG, "create file error:" + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }

        if (fileWriter == null) {
            fileWriter = new FileWriter(aliveStatsfile, true);
        }
        if (writer == null) {
            writer = new BufferedWriter(fileWriter);
        }
    }

    private void clearFileWriter() {
        try {
            writer.close();
        } catch (Exception e) {
            AliveLog.e(TAG, "close error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        try {
            fileWriter.close();
        } catch (Exception e) {
            AliveLog.e(TAG, "close error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        aliveStatsfile = null;
        fileWriter = null;
        writer = null;
    }


    private static final int REOPEN_ALIVE_STATS = 0x102;
    public static final int RESET_ALIVE_STATS = 0x103;
    public static final int UPLOAD_ALIVE_STATS_FAILED = 0x104;
    public static final int SHOW_ALIVE_STATS_NOTIFY = 0x105;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REOPEN_ALIVE_STATS) {
                clearAliveStats();
                openStatsLiveTimer();
            } else if (msg.what == RESET_ALIVE_STATS) {
                AliveLog.v(TAG, "----上传数据成功----");
                //交互成功修
                uploadingAlive = false;
                context.deleteFile(HelperConfig.NEW_ALIVE_STATS_FILE_NAME);
                AliveSPUtils.getInstance().setASBeginTime(0);
                clearFileWriter();
                AliveLog.v(TAG, "----重置数据成功----");
            } else if (msg.what == UPLOAD_ALIVE_STATS_FAILED) {
                AliveLog.v(TAG, "----上传数据失败----");
                uploadingAlive = false;
            } else if (msg.what == SHOW_ALIVE_STATS_NOTIFY) {
                AliveHelper.getHelper().notifyAliveStats(2000);
            }
        }
    };


    private class AliveStatsTask implements Runnable {

        @Override
        public void run() {
            try {
                aliveStats();
            } catch (Exception e) {
                Log.e(TAG, "遇到错误重新开启保活统计");
                e.printStackTrace();
                handler.sendEmptyMessage(REOPEN_ALIVE_STATS);
            }
        }
    }
//    class AliveStatsTask extends TimerTask {
//
//        @Override
//        public void run() {
//            try {
//                aliveStats();
//            } catch (Exception e) {
//                e.printStackTrace();
//                handler.sendEmptyMessage(REOPEN_ALIVE_STATS);
//            }
//        }
//    }
}
