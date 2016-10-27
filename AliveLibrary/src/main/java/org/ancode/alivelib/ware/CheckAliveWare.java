package org.ancode.alivelib.ware;

import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.listener.StringCallBack;
import org.ancode.alivelib.notification.AliveNotification;
import org.ancode.alivelib.http.HttpHelper;
import org.ancode.alivelib.utils.Log;
import org.ancode.alivelib.utils.Utils;

import java.util.Map;

/**
 * Created by andyliu on 16-8-30.
 */
public class CheckAliveWare extends BaseWare {

    private static final String TAG = CheckAliveWare.class.getSimpleName();
    boolean cancel = false;
    protected StringCallBack stringCallBack = null;
    public static final String HTTP_CALL_FLAG = "http_call_flag";

    public CheckAliveWare() {
    }


    public void cancel() {
        cancel = true;
        HttpHelper.cancelAll();
    }

    private void getData(final String flag) {
        Map<String, String> map = Utils.getProp();
        HttpClient.getUrl(map, flag, stringCallBack);
    }




    public CheckAliveWare setCheckCallBack(StringCallBack callBack) {
        this.stringCallBack = callBack;
        return this;
    }


    /****
     * <p>
     * 调用本方法之前必须调用setCheckCallBack(StringCallBack stringCallBack)
     * </p>
     */
    @Override
    public void check() {
        if (stringCallBack == null) {
            throw new RuntimeException("stringCallBack is null_image");
        } else {
            getData(HTTP_CALL_FLAG);
        }
    }

    public CheckAliveWare check(StringCallBack callBack) {
        this.stringCallBack = callBack;
        getData(HTTP_CALL_FLAG);
        return this;
    }

    @Override
    public void showNotification(AliveNotification baseNotification, long aftertime) {
        Log.v(TAG, "直接获取数据未提供notification");
    }

}
