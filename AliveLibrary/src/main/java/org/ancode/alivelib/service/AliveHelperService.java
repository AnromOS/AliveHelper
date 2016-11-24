package org.ancode.alivelib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.ancode.alivelib.AliveHelper;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.utils.AliveLog;
import org.ancode.alivelib.utils.AliveStatsUtils;
import org.ancode.alivelib.utils.AliveStats;
import org.ancode.alivelib.utils.AliveTestUtils;

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


    public static final String KILL_ALIVE_HELPER_SERVICE = "org.ancode.alivelib.service.KILL_ALIVE_HELPER_SERVICE";

    private final int WARNING_TIME = 1000 * 60 * 30;

    //定时弹出使用指南
    //是否已经提示
    private boolean isNotify = false;
    private Timer warningTimer = null;

    //保活统计
    private AliveStats aliveStats = null;

    @Override
    public IBinder onBind(Intent intent) {
        AliveLog.v(TAG, "onBind");
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AliveLog.v(TAG, "onStartCommand");
        if (aliveStats == null) {
            aliveStats = new AliveStats(this);
            AliveTestUtils.LogStats("aliveStats类被新建");
        }
        String action = null;
        try {
            action = intent.getStringExtra(ACTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action != null) {
            switch (action) {
                case OPEN_ALIVE_STATS_SERVICE_ACTION:
                    aliveStats.openStatsLiveTimer();
                    break;
                case OPEN_ALIVE_WARNING_SERVICE_ACTION:
                    openWarningTimer();
                    break;
                case CLOSE_ALIVE_STATS_SERVICE_ACTION:
                    aliveStats.closeStatsLiveTimer();
                    break;
                case CLOSE_ALIVE_WARNING_SERVICE_ACTION:
                    closeWarningTimer();
                    break;
                case KILL_ALIVE_HELPER_SERVICE:
                    stopSelf();
                    break;
            }
        }

        return super.onStartCommand(intent, START_STICKY, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        AliveLog.v(TAG, "onCreate");
        AliveTestUtils.LogStats("AliveHelperService onCreate service被新建");
    }

    private void openWarningTimer() {

        if (warningTimer == null) {
            AliveLog.v(TAG, "---open notification timer---");
            warningTimer = new Timer();
            warningTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (isNotify) {
                            AliveLog.v(TAG, "allready notificaton,do return");
                            return;
                        }
                        float percent = AliveStatsUtils.getAlivePercent();
                        if (percent >= 0) {
                            if (percent <= HelperConfig.WARNING_POINT) {
                                handler.sendEmptyMessage(SHOW_NOTIFICATION);
                                isNotify = true;
                            }
                        } else {
                            isNotify = true;
                            AliveLog.e(TAG, "no Stats yesterday, no warning!");
                        }
                        AliveLog.v(TAG, "alive percent=" + percent + ",warning point=" + HelperConfig.WARNING_POINT);
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
            AliveLog.v(TAG, "---close notification alert---");
        }

    }

    private static final int SHOW_NOTIFICATION = 0x101;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SHOW_NOTIFICATION) {
                AliveHelper.getHelper().notifyAliveUseGuide(0);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        AliveLog.v(TAG, "AliveHelperService onDestroy");
        AliveTestUtils.LogStats("AliveHelperService onDestroy service被销毁");
        aliveStats.clearAliveStats();
        closeWarningTimer();

    }
}
