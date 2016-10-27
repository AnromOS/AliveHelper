package org.ancode.alivelib.http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.listener.StringCallBack;
import org.ancode.alivelib.service.AliveHelperService;
import org.ancode.alivelib.utils.AliveStatsUtils;
import org.ancode.alivelib.utils.Log;
import org.ancode.alivelib.utils.NetUtils;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static boolean GETING_URL = false;
    public static final String GET_DATA_KEY = "get_data_key";
    public static final int GET_DATA_SUCCESS = 1;
    public static final int GET_DATA_ERROR = 2;


    /**
     * 查询统计结果
     *
     * @param params
     * @param flag
     * @param stringCallBack
     */
    public static void getAliveStats(final Map<String, String> params, final String flag, StringCallBack stringCallBack) {
        final StrHandler handler = new StrHandler(stringCallBack);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url;
                    if (HelperConfig.USE_ANET) {
                        url = HttpUrlConfig.QUERY_ALIVE_STATS_V6_URL;
                        Log.v(TAG, "走IPV6");
                    } else {
                        url = HttpUrlConfig.QUERY_ALIVE_STATS_V4_URL;
                        Log.v(TAG, "走IPV4");
                    }
                    String data = HttpHelper.get(url, params, flag);
                    if (TextUtils.isEmpty(data)) {
                        sendHandler(handler, GET_DATA_ERROR, "response is null");
                        return;
                    }
                    sendHandler(handler, GET_DATA_SUCCESS, data);
                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                    sendHandler(handler, GET_DATA_ERROR, e.getLocalizedMessage());
                    return;
                }
            }
        }).start();
    }


    /***
     * 获取防杀指南链接
     *
     * @param params
     * @param flag
     * @param stringCallBack
     */
    public static void getUrl(final Map<String, String> params, final String flag, StringCallBack stringCallBack) {
        final StrHandler handler = new StrHandler(stringCallBack);
        if (GETING_URL == true) {
            return;
        }
        GETING_URL = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resultUrl = "";
                try {
                    String url;
                    if (HelperConfig.USE_ANET) {
                        url = HttpUrlConfig.GET_ALIVE_GUIDE_V6_URL;
                        Log.v(TAG, "走IPV6");
                    } else {
                        url = HttpUrlConfig.GET_ALIVE_GUIDE_V4_URL;
                        Log.v(TAG, "走IPV4");
                    }
                    String data = HttpHelper.get(url, params, flag);
                    GETING_URL = false;
                    if (TextUtils.isEmpty(data)) {
                        sendHandler(handler, GET_DATA_ERROR, "response is null");
                        return;
                    }
                    JSONObject jsonObj = new JSONObject(data);
                    if (jsonObj.has("result")) {
                        if (jsonObj.get("result").toString().equals("ok")) {
                            if (jsonObj.has("url")) {
                                resultUrl = jsonObj.getString("url");
                            } else {
                                sendHandler(handler, GET_DATA_ERROR, "return url is null");
                            }

                        } else if (jsonObj.get("result").toString().equals("failed")) {
                            sendHandler(handler, GET_DATA_ERROR, HttpClient.DATA_IS_NULL);
                            return;
                        } else {
                            sendHandler(handler, GET_DATA_ERROR, "result is failed");
                            return;
                        }
                    } else {
                        sendHandler(handler, GET_DATA_ERROR, "result is failed");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    GETING_URL = false;
                    sendHandler(handler, GET_DATA_ERROR, e.getLocalizedMessage());
                    return;
                }
                sendHandler(handler, GET_DATA_SUCCESS, resultUrl);
                GETING_URL = false;
            }
        }).start();

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
                if (HelperConfig.USE_ANET) {
                    if (NetUtils.ping6(HttpUrlConfig.HOST_V6)) {
                        Log.v(TAG, "网络可用开始上传服务器");
                        return AliveStatsUtils.uploadAliveStats();
                    } else {
                        Log.v(TAG, "网络不可用不能上传服务器");
                        return false;
                    }
                } else {
                    if (NetUtils.ping(HttpUrlConfig.HOST_V4)) {
                        Log.v(TAG, "网络可用开始上传服务器");
                        return AliveStatsUtils.uploadAliveStats();
                    } else {
                        Log.v(TAG, "网络不可用不能上传服务器");
                        return false;
                    }
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

        public StrHandler(StringCallBack callBack) {
            this.callBack = callBack;
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
                return;
            }
            String data = msg.getData().getString(GET_DATA_KEY);
            if (!TextUtils.isEmpty(data)) {
                if (msg.what == GET_DATA_ERROR) {
                    if (callBack != null) {
                        callBack.error(data);
                    } else {
                        Log.e(TAG, "StringCallBack is null_image");
                    }
                } else if (msg.what == GET_DATA_SUCCESS) {
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
        }
    }


    private static void sendHandler(Handler handler, int what, String data) {
        Bundle bundle = new Bundle();
        bundle.putString(GET_DATA_KEY, data);
        Message message = new Message();
        message.setData(bundle);
        message.what = what;
        handler.sendMessage(message);
    }
}
