package org.ancode.alivelib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.ancode.alivelib.utils.AliveLog;

/**
 * Created by andyliu on 16-11-16.
 */
public class AlermTimerReceiver extends BroadcastReceiver {
    public static final String TAG = AlermTimerReceiver.class.getSimpleName();
    public static final String SHOW_NOTIFY_ALIVE_STATS = "org.ancode.alivelib.SHOW_NOTIFY_ALIVE_STATS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(SHOW_NOTIFY_ALIVE_STATS)) {
                //notify
                AliveLog.v(TAG, "收到alermTimer广播" + action);
            }
        }
    }
}
