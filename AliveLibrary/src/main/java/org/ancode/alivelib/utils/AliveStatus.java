package org.ancode.alivelib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.http.HttpClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 保活统计
 * Created by andyliu on 16-11-7.
 */
public class AliveStatus {
    private static final String TAG = AliveStatus.class.getSimpleName();
    public Timer aliveStatsTimer = null;
    private boolean uploadingAlive = false;
    //文件写入
    private File aliveStatsfile = null;
    private FileWriter fileWriter = null;
    private BufferedWriter writer = null;
    private Context context = null;
    private AliveStatsTask aliveStatsTask = null;

    public AliveStatus(Context context) {
        this.context = context;
    }

    public void openStatsLiveTimer() {
        if (aliveStatsTimer != null) {
            closeStatsLiveTimer();
        }
        if (aliveStatsTimer == null) {
            AliveLog.v(TAG, "---start Stats alive---");
            aliveStatsTimer = new Timer();
            aliveStatsTask = new AliveStatsTask();
            aliveStatsTimer.schedule(aliveStatsTask, 2000, HelperConfig.ALIVE_STATS_RATE);
        }
    }

    public void closeStatsLiveTimer() {
        if (aliveStatsTask != null) {
            aliveStatsTask.cancel();
        }
        if (aliveStatsTimer != null) {

            aliveStatsTimer.purge();
            aliveStatsTimer.cancel();
            aliveStatsTimer = null;
            AliveLog.v(TAG, "---close Stats alive---");
        }
    }

    public void clearAliveStatus() {
        closeStatsLiveTimer();
        clearFileWriter();
    }

    private void aliveStats() {
        long nowTime = new Date().getTime();
        try {
            checkFileWriter();


            //TODO[计算统计范围,超过一小时,将数据上传至服务器]
            //开始时间为0时重新赋值
            long startTime = AliveSPUtils.getInstance().getASBeginTime();
            if (startTime == 0) {
                AliveSPUtils.getInstance().setASBeginTime(nowTime);
                startTime = nowTime;
            }


            //TODO[如果发现当前统计数据跨天,则提交]
            if (AliveDateUtils.getDifferDayOnly(startTime, nowTime) >= 1) {
                AliveLog.v(TAG, "已经跨天了,准备上传服务器");
                AliveLog.v(TAG, "本地不在打点了");
                uploadingAlive = true;
                try {
                    HttpClient.uploadAliveStats("uploadAliveStats", handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    uploadingAlive = false;
                }
            } else {
                float differTime = AliveDateUtils.getDifferHours(startTime, nowTime);
                //TODO[统计Wifi/3G状态]
                String netStatus = NetUtils.getNetStatus(HelperConfig.CONTEXT);
                //写入文件
                writer.write(nowTime + " " + netStatus + "\r\n");
                writer.flush();
                AliveLog.v(TAG, "insert time =" + AliveDateUtils.timeFormat(nowTime, null));

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
            }

        } catch (FileNotFoundException e) {
            AliveLog.e(TAG, "write error:" + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            AliveLog.e(TAG, "write error:" + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkFileWriter() throws Exception {
        if (aliveStatsfile == null) {

            aliveStatsfile = new File(context.getFilesDir(), HelperConfig.ALIVE_STATS_FILE_NAME);

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
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REOPEN_ALIVE_STATS) {
                clearAliveStatus();
                openStatsLiveTimer();
            } else if (msg.what == RESET_ALIVE_STATS) {
                AliveLog.v(TAG, "----上传数据成功----");
                //交互成功修
                uploadingAlive = false;
                context.deleteFile(HelperConfig.ALIVE_STATS_FILE_NAME);
                AliveSPUtils.getInstance().setASBeginTime(0);
                clearFileWriter();
                AliveLog.v(TAG, "----重置数据成功----");
            } else if (msg.what == UPLOAD_ALIVE_STATS_FAILED) {
                uploadingAlive = false;
            }
        }
    };


    class AliveStatsTask extends TimerTask {

        @Override
        public void run() {
            try {
                aliveStats();
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(REOPEN_ALIVE_STATS);
            }
        }
    }
}
