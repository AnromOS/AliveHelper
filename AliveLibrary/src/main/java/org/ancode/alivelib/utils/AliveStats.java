package org.ancode.alivelib.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
    long lastPoint = 0;

    public ScheduledThreadPoolExecutor aliveStatsThreader = null;
    public AliveStatsTask aliveStatsTask = null;

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
        AliveTestUtils.LogStats("打点线程被新建");
        //周期时间包括执行时间
//        aliveStatsThreader.scheduleAtFixedRate(
//                aliveStatsTask,
//                2,
//                HelperConfig.ALIVE_STATS_RATE,
//                TimeUnit.SECONDS);

        //周期时间不包括执行时间
        aliveStatsThreader.scheduleWithFixedDelay(
                aliveStatsTask,
                5,
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
            AliveTestUtils.LogStats("打点线程被销毁");
            AliveLog.v(TAG, "---close Stats alive---");
        }

    }

    public void clearAliveStats() {
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
            float differTime = AliveDateUtils.getDifferHours(startTime, nowTime);
            //TODO[统计Wifi/3G状态]
            String netStatus = NetUtils.getNetStatus(HelperConfig.CONTEXT);
            String pointStr = "";

            //TODO[丢失数据补救]

            if (lastPoint != 0) {
                if (differTime <= (HelperConfig.UPLOAD_ALIVE_STATS_RATE + 0.01f)) {

                    float loseDiffer = ((float) nowTime - lastPoint/* - (30 * 1000)*/) / 1000;
                    //丢失个数
                    int loseNumber = (int) Math.ceil(loseDiffer / HelperConfig.ALIVE_STATS_RATE);

//                    if (loseNumber > 0) {
//                        for (int i = 1; i <= loseNumber; i++) {
//                            long bqNumber = (HelperConfig.ALIVE_STATS_RATE * 1000 * i);
//                            long bqTime = lastPoint + bqNumber;
//                            float bqDiffer = ((float) (nowTime - bqTime)) / 1000;
//                            if (bqDiffer > 10) {
//                                pointStr = pointStr + bqTime + " " + netStatus + "\r\n";
//                                AliveLog.v(APP_TAG, "丢失数据补救=" + AliveDateUtils.timeFormat(bqTime, AliveDateUtils.DEFAULT_FORMAT));
//                                AliveTestUtils.LogBpoint(nowTime, "丢失数据补救=" + bqTime + " " + netStatus);
//                            } else {
//                                bqTime = bqTime - (HelperConfig.ALIVE_STATS_RATE / 2 * 1000);
//                                float bqDiffer2 = ((float) (nowTime - bqTime)) / 1000;
//                                if (bqDiffer2 > 10) {
//                                    pointStr = pointStr + bqTime + " " + netStatus + "\r\n";
//                                    AliveLog.v(APP_TAG, "丢失数据补救=" + AliveDateUtils.timeFormat(bqTime, AliveDateUtils.DEFAULT_FORMAT));
//                                    AliveTestUtils.LogBpoint(nowTime, "丢失数据补救=" + bqTime + " " + netStatus);
//                                }
//                            }
//                        }
//                    }
                    if (loseNumber > 0) {
                        loseNumber = loseNumber + 1;
                        for (int i = 1; i <= loseNumber; i++) {
                            long bqNumber = (HelperConfig.ALIVE_STATS_RATE * 1000 * i);
                            long bqTime = lastPoint + bqNumber;
                            float bqDiffer = ((float) (nowTime - bqTime)) / 1000;
                            if (bqDiffer > 0) {
                                pointStr = pointStr + bqTime + " " + netStatus + "\r\n";
                                AliveLog.v(TAG, "丢失数据补救=" + AliveDateUtils.timeFormat(bqTime, AliveDateUtils.DEFAULT_FORMAT));
                                AliveTestUtils.LogBpoint(nowTime, "丢失数据补救=" + bqTime + " " + netStatus);
                            }
                        }
                    }

                }
            } else {
                Log.v(TAG, "lastPoint被初始化为0");
                AliveTestUtils.LogStats("lastPoint被初始化为0");
            }
            //写入文件
            pointStr = pointStr + nowTime + " " + netStatus + "\r\n";

            writer.write(pointStr);
            writer.flush();
            AliveLog.v(TAG, "打点 =" + AliveDateUtils.timeFormat(nowTime, null) + " " + netStatus);

            lastPoint = nowTime;


            //超过1小时或者文件队列不为空，且无上传
            if ((differTime >= HelperConfig.UPLOAD_ALIVE_STATS_RATE ||
                    !TextUtils.isEmpty(AliveSPUtils.getInstance().getAliveStatsUploadFiles())) && !uploadingAlive) {

                if (differTime >= HelperConfig.UPLOAD_ALIVE_STATS_RATE) {
                    //将打点好的文件名放入队列
                    String aliveStasfn = AliveSPUtils.getInstance().getAliveStatsFileName();
                    putUploadFileNames(aliveStasfn);
                    //初始化开始时间
                    AliveSPUtils.getInstance().setASBeginTime(0);
                    //初始化当前统计文件名
                    AliveSPUtils.getInstance().setAliveStatsFileName("");
                    //清除当前流
                    clearFileWriter();
                }


                AliveLog.v(TAG, "距离第一次统计时间" + differTime + "小时,是否正在上传->," + uploadingAlive + "准备上传服务器");
                uploadingAlive = true;
                try {
                    HttpClient.uploadAliveStats("tag", handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    uploadingAlive = false;
                }
            } else {
                AliveLog.v(TAG, "距离第一次统计时间" + differTime + "小时 是否正在上传->," + uploadingAlive + "不上传服务器");
            }
            if (AliveNotifyUtils.checkShowASNotify(true, nowTime)) {
                handler.sendEmptyMessage(SHOW_ALIVE_STATS_NOTIFY);
            }

            //临时加入唤醒小秘
            SetAppAliveUtils.setAliveByBroadCast(context,
                    "org.ancode.secretary",
                    "org.ancode.secretary.receiver.AliveReceiver",
                    "org.ancode.secretary.receiver.AliveReceiver",
                    "org.ancode.secretary.service.SecService");
        } catch (Exception e) {
            AliveLog.e(TAG, "write error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    /**
     * 创建文件名
     *
     * @return
     */
    private String getAliveStatsFileName() {
        String fileName = "aliveStatsFile";
        fileName = "AiveStats_" + AliveDateUtils.timeFormat(new Date(), AliveDateUtils.ALIVE_STATS_FILE_NAME_FORMAT);
        return fileName;
    }

    private void putUploadFileNames(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        String fileNames = AliveSPUtils.getInstance().getAliveStatsUploadFiles();
        if (TextUtils.isEmpty(fileNames)) {
            AliveSPUtils.getInstance().setAliveStatsUploadFiles(fileName);
        } else {
            if (!fileNames.contains(fileName)) {

                fileNames = fileNames + "," + fileName;
                AliveSPUtils.getInstance().setAliveStatsUploadFiles(fileNames);
            }
        }
    }


    private void checkFileWriter() throws Exception {
        if (aliveStatsfile == null) {
            String aliveStasfn = AliveSPUtils.getInstance().getAliveStatsFileName();
            if (TextUtils.isEmpty(aliveStasfn)) {
                aliveStasfn = getAliveStatsFileName();
                AliveSPUtils.getInstance().setAliveStatsFileName(aliveStasfn);
            }
            aliveStatsfile = new File(context.getFilesDir(), aliveStasfn);

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
    public static final int UPLOAD_FILE_SUCCESS = 0x103;
    public static final int SHOW_ALIVE_STATS_NOTIFY = 0x105;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REOPEN_ALIVE_STATS) {
                clearAliveStats();
                openStatsLiveTimer();
            } else if (msg.what == UPLOAD_FILE_SUCCESS) {
                //交互成功修
                uploadingAlive = false;
            } else if (msg.what == SHOW_ALIVE_STATS_NOTIFY) {
                AliveHelper.getHelper().notifyAliveStats(2 * 1000);
            }
        }
    };


    private class AliveStatsTask implements Runnable {

        @Override
        public void run() {
            try {
                aliveStats();
            } catch (Exception e) {
                AliveLog.e(TAG, "遇到错误重新开启保活统计");
                e.printStackTrace();
                handler.sendEmptyMessage(REOPEN_ALIVE_STATS);
            }
        }
    }

}
