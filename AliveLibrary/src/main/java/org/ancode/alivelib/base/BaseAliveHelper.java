package org.ancode.alivelib.base;

import android.content.Intent;
import android.text.TextUtils;

import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.service.AliveHelperService;
import org.ancode.alivelib.utils.AliveLog;
import org.ancode.alivelib.utils.AliveSPUtils;

/**
 * Created by andyliu on 16-8-25.
 */
public abstract class BaseAliveHelper {


    /***
     * {
     "app": "org.ancode.priv",
     "info": {
     "device": "Nexus 5",
     "os": "MMB29X",
     "phone": "18231176137"
     },
     "stat": {
     "data": [
     "1476409850149 wifi",
     "1476411231473 3g",
     "1476413935467 close"
     ],
     "tag": "MH:18231176137",
     "type": "alive"
     }
     }
     */
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
     */
    protected void setAliveTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag is null");
        }

        AliveSPUtils.getInstance().setASTag(tag);
        AliveLog.v("AliveHelper", "接收到tag信息 tag = " + tag);
    }


    /***
     * 设置
     *
     * @param info
     */
    protected void setAliveInfo(String info) {
        if (TextUtils.isEmpty(info)) {
            throw new IllegalArgumentException("info is null,you should set a json string");
        }

        AliveSPUtils.getInstance().setASUploadInfo(info);
        AliveLog.v("AliveHelper", "接收到info信息 info = " + info);
    }


}
