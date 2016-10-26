package org.ancode.alivelib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import org.ancode.alivelib.AliveHelper;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.listener.StringCallBack;
import org.ancode.alivelib.utils.AliveStatsUtils;
import org.ancode.alivelib.utils.DateTimeUtils;
import org.ancode.alivelib.http.HttpUtils;
import org.ancode.alivelib.utils.Log;
import org.ancode.alivelib.utils.NetUtils;
import org.ancode.alivelib.utils.AliveSPUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by andyliu on 16-10-8.
 */
public class AliveHelperService extends Service {
    private static final String TAG = AliveHelperService.class.getSimpleName();

    public static final String ACTION = "action";

    public static final String OPEN_ALIVE_STATS_SERVICE_ACTION = "org.ancode.alivelib.service.OPEN_ALIVE_STATS_SERVICE";
    public static final String OPEN_ALIVE_WARNING_SERVICE_ACTION = "org.ancode.alivelib.service.OPEN_ALIVE_WARNING_SERVICE";

    public static final String CLOSE_ALIVE_STATS_SERVICE_ACTION = "org.ancode.alivelib.service.CLOSE_ALIVE_STATS_SERVICE_ACTION";
    public static final String CLOSE_ALIVE_WARNING_SERVICE_ACTION = "org.ancode.alivelib.service.CLOSE_ALIVE_WARNING_SERVICE";

    private final int WARNING_TIME = 1000 * 60 * 30;

    private Timer aliveStatsTimer = null;

    //文件写入
    private File aliveStatsfile = null;
    private FileWriter fileWriter = null;
    private BufferedWriter writer = null;
    private boolean isFirstStats = true;


    //定时弹出使用指南
    //是否已经提示
    private boolean isNotify = false;
    private Timer warningTimer = null;

    private boolean uploadingAlive = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = null;
        try {
            action = intent.getStringExtra(ACTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action != null) {
            switch (action) {
                case OPEN_ALIVE_STATS_SERVICE_ACTION:
                    openStatsLiveTimer();
                    break;
                case OPEN_ALIVE_WARNING_SERVICE_ACTION:
                    openWarningTimer();
                    break;
                case CLOSE_ALIVE_STATS_SERVICE_ACTION:
                    closeStatsLiveTimer();
                    break;
                case CLOSE_ALIVE_WARNING_SERVICE_ACTION:
                    closeWarningTimer();
                    break;
            }
        }

        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void openStatsLiveTimer() {
        final Object mLock = new Object();
        if (aliveStatsTimer == null) {
            Log.v(TAG, "---start Stats alive---");
            aliveStatsTimer = new Timer();
            aliveStatsTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        aliveStats();
                        isFirstStats = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(REOPEN_ALIVE_STATS);
                    }
                }
            }, 2000, HelperConfig.ALIVE_STATS_RATE);
        }
    }

    private void closeStatsLiveTimer() {
        if (aliveStatsTimer != null) {
            aliveStatsTimer.purge();
            aliveStatsTimer.cancel();
            aliveStatsTimer = null;
            Log.v(TAG, "---close Stats alive---");
        }
    }

    private void openWarningTimer() {

        if (warningTimer == null) {
            Log.v(TAG, "---open notification timer---");
            warningTimer = new Timer();
            warningTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (isNotify) {
                            Log.v(TAG, "allready notificaton,do return");
                            return;
                        }
                        while (isFirstStats) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        float percent = AliveStatsUtils.getAlivePercent();
                        if (percent >= 0) {
                            if (percent <= HelperConfig.WARNING_POINT) {
                                handler.sendEmptyMessage(SHOW_NOTIFICATION);
                                isNotify = true;
                            }
                        } else {
                            isNotify = true;
                            Log.e(TAG, "no Stats yesterday, no warning!");
                        }
                        Log.v(TAG, "alive percent=" + percent + ",warning point=" + HelperConfig.WARNING_POINT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000, WARNING_TIME);
        }
    }

    private void closeWarningTimer() {
        if (warningTimer != null) {
            warningTimer.purge();
            warningTimer.cancel();
            warningTimer = null;
            Log.v(TAG, "---close notification alert---");
        }

    }

    private static final int SHOW_NOTIFICATION = 0x101;
    private static final int REOPEN_ALIVE_STATS = 0x102;
    public static final int RESET_ALIVE_STATS = 0x103;
    public static final int UPLOAD_ALIVE_STATS_FAILED = 0x104;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SHOW_NOTIFICATION) {
                AliveHelper.getHelper().notification(0);
            } else if (msg.what == REOPEN_ALIVE_STATS) {
                closeStatsLiveTimer();
                openStatsLiveTimer();
            } else if (msg.what == RESET_ALIVE_STATS) {
                Log.v(TAG, "----上传数据成功----");
                //交互成功修
                uploadingAlive = false;
                AliveSPUtils.getInstance().setASBeginTime(0);
                deleteFile(HelperConfig.ALIVE_STATS_FILE_NAME);
                clearFileWriter();
                Log.v(TAG, "----重置数据成功----");
            } else if (msg.what == UPLOAD_ALIVE_STATS_FAILED) {
                uploadingAlive = false;
            }
        }
    };


    private void aliveStats() {
        long nowTime = new Date().getTime();
        try {
            checkFileWriter();

            //TODO[统计Wifi/3G状态]
            String netStatus = NetUtils.getNetStatus(HelperConfig.CONTEXT);
            //写入文件
            writer.write(nowTime + " " + netStatus + "\r\n");
            writer.flush();
            Log.v(TAG, "insert time =" + DateTimeUtils.timeFormat(nowTime, null));

            //TODO[计算统计范围,超过一小时,将数据上传至服务器]
            //开始时间为0时重新赋值
            long startTime = AliveSPUtils.getInstance().getASBeginTime();
            if (startTime == 0) {
                AliveSPUtils.getInstance().setASBeginTime(nowTime);
                startTime = nowTime;
            }

            //结束时间实时赋值
            //AliveSPUtils.getInstance().setASEndTime(nowTime);

            float differTime = DateTimeUtils.getDifferHours(startTime, nowTime);
            if (differTime >= HelperConfig.UPLOAD_ALIVE_STATS_RATE && !uploadingAlive) {
                Log.v(TAG, "距离第一次统计时间" + differTime + "小时,是否正在上传->," + uploadingAlive + "准备上传服务器");
                uploadingAlive = true;
                try {
                    HttpClient.uploadAliveStats("uploadAliveStats", handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    uploadingAlive = false;
                }
            } else {
                Log.v(TAG, "距离第一次统计时间" + differTime + "小时 是否正在上传->," + uploadingAlive + "不上传服务器");
            }


        } catch (FileNotFoundException e) {
            Log.e(TAG, "write error:" + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "write error:" + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkFileWriter() throws Exception {
        if (aliveStatsfile == null) {

            aliveStatsfile = new File(getFilesDir(), HelperConfig.ALIVE_STATS_FILE_NAME);

            if (!aliveStatsfile.exists()) {
                try {
                    aliveStatsfile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "create file error:" + e.getLocalizedMessage());
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

    public void clearFileWriter() {
        try {
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "close error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        try {
            fileWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "close error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        aliveStatsfile = null;
        fileWriter = null;
        writer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "AliveHelperService onDestroy");
        closeStatsLiveTimer();
        closeWarningTimer();
        clearFileWriter();
    }
}
