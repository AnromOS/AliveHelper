package org.ancode.alivelib;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.ancode.alivelib.activity.AliveHelperActivity;
import org.ancode.alivelib.activity.AliveStatsActivity;
import org.ancode.alivelib.bean.BaseStatsInfo;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.listener.StringCallBack;
import org.ancode.alivelib.notification.AliveNotification;
import org.ancode.alivelib.service.AliveHelperService;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.IntentUtils;
import org.ancode.alivelib.utils.Log;
import org.ancode.alivelib.utils.NotifyUtils;

/**
 * Created by andyliu on 16-8-25.
 */
public abstract class BaseAliveHelper {


    /***
     * 开启保活统计服务
     */
    protected void openAliveStats() {
        Intent intent = new Intent(HelperConfig.CONTEXT, AliveHelperService.class);
        intent.putExtra(AliveHelperService.ACTION, AliveHelperService.OPEN_ALIVE_STATS_SERVICE_ACTION);
        HelperConfig.CONTEXT.startService(intent);
    }


    /**
     * 设置TAG
     * <p>
     * 参数说明
     * <p>
     * tag like this:
     * "MX:104601"
     * "MH:13011021102"
     * </p>
     *
     * @param tag
     * @return
     */
    protected BaseAliveHelper setAliveTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag is null");
        }

        AliveSPUtils.getInstance().setASTag(tag);
        Log.v("AliveHelper", "接收到tag信息 tag = " + tag);
        return this;
    }



}
