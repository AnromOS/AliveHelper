package org.ancode.alivelib.http;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.listener.StringCallBack;
import org.ancode.alivelib.service.AliveHelperService;
import org.ancode.alivelib.utils.Log;
import org.ancode.alivelib.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andyliu on 16-10-17.
 */
public class HttpClient {
    public static final String TAG = HttpClient.class.getSimpleName();
    static boolean cancel = false;
    public static final String HTTP_CALL_FLAG = "http_call_flag";
    public static final String DATA_IS_NULL = "data is null";

    private static HttpImpl httpImpl = null;

    static {
        if (httpImpl == null) {
            httpImpl = new HttpImpl();
        }
    }

    /**
     * 查询统计结果
     *
     * @param params
     * @param flag
     * @param stringCallBack
     */
    public static void getAliveStats(final Map<String, String> params, final String flag, StringCallBack stringCallBack) {
        httpImpl.getAliveStats(params, new StrHandler(stringCallBack), flag);
    }


    /***
     * 获取防杀指南链接
     *
     * @param params
     * @param flag
     * @param stringCallBack
     */
    public static void getUrl(final Map<String, String> params, final String flag, StringCallBack stringCallBack) {
        Map<String, String> map = new HashMap<String, String>();
        httpImpl.getUrl(params, new StrHandler(stringCallBack), flag);
    }


    /***
     * 提交aliveStats
     *
     * @param flag
     * @param handler
     */
    public static void uploadAliveStats(final String flag, final Handler handler) {

        new AsyncTask<Object, Object, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                if (NetUtils.ping(HttpUrlConfig.ALIVE_STATS_POST_HOST)) {
                    Log.v(TAG, "网络可用开始上传服务器");
                    return httpImpl.uploadAliveStats();
                } else {
                    Log.v(TAG, "网络不可用不能上传服务器");
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean b) {
                super.onPostExecute(b);
                if (b) {
                    handler.sendEmptyMessage(AliveHelperService.RESET_ALIVE_STATS);
                } else {
                    handler.sendEmptyMessage(AliveHelperService.UPLOAD_ALIVE_STATS_FAILED);
                }
            }
        }.execute();

    }

    static class StrHandler extends Handler {
        protected StringCallBack callBack = null;
        boolean b = false;

        public StrHandler(StringCallBack callBack) {
            this.callBack = callBack;
        }

        public StrHandler setIsThread(boolean b) {
            this.b = b;
            return this;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (cancel) {
                if (callBack != null) {

                    callBack.error("this connect is cancel");
                } else {
                    Log.e(TAG, "StringCallBack is null_image");
                }
                if (b) {
                    getLooper().quit();
                }
                return;
            }
            String data = msg.getData().getString(httpImpl.GET_DATA_KEY);
            if (!TextUtils.isEmpty(data)) {
                if (msg.what == httpImpl.GET_DATA_ERROR) {
                    if (callBack != null) {
                        callBack.error(data);
                    } else {
                        Log.e(TAG, "StringCallBack is null_image");
                    }
                } else if (msg.what == httpImpl.GET_DATA_SUCCESS) {
                    if (callBack != null) {

                        callBack.onResponse(data);
                    } else {
                        Log.e(TAG, "StringCallBack is null_image");
                    }

                }
            } else {
                if (callBack != null) {
                    callBack.error(DATA_IS_NULL);
                } else {
                    Log.e(TAG, "StringCallBack is null_image");
                }
                Log.e(TAG, "获取数据失败");
            }
            if (b) {
                getLooper().quit();
            }
        }
    }


}
