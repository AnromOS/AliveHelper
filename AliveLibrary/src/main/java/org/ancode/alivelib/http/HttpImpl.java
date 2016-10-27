package org.ancode.alivelib.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.ancode.alivelib.config.Constants;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.AliveStatsUtils;
import org.ancode.alivelib.utils.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by andyliu on 16-9-1.
 */
public class HttpImpl {
    public final String TAG = HttpImpl.class.getSimpleName();
    public boolean GETING_URL = false;
    public final String GET_DATA_KEY = "get_data_key";
    public final int GET_DATA_SUCCESS = 1;
    public final int GET_DATA_ERROR = 2;

    /**
     * 获取警告 html页面
     *
     * @param params
     * @param handler
     * @param flag
     */
    public void getUrl(final Map<String, String> params, final Handler handler, final String flag) {

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
                        Log.v("HttpImpl", "走IPV6");
                    } else {
                        url = HttpUrlConfig.GET_ALIVE_GUIDE_V4_URL;
                        Log.v("HttpImpl", "走IPV4");
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
     * @return
     */
    public boolean uploadAliveStats() {

        String packageName = HelperConfig.CONTEXT.getPackageName().toString();
        JSONObject info = null;
        String tag = AliveSPUtils.getInstance().getASTag();
        try {
            info = new JSONObject(AliveSPUtils.getInstance().getASUploadInfo());
        } catch (JSONException e) {
            Log.e(TAG, "用户设置的info解析出错");
            throw new IllegalArgumentException("Your info is error ,Please set info");
        }
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("Your aliveTag is null ,Please set aliveTag");
        }

        JSONObject uploadJson = new JSONObject();
        JSONObject statObject = new JSONObject();
        //统计数据
        List<String> data = AliveStatsUtils.getAliveStatsResult();

        JSONArray dataArray = new JSONArray(data);
        try {
            statObject.put("type", Constants.TYPE_ALIVE);
            statObject.put("tag", tag);
            statObject.putOpt("data", dataArray);
        } catch (JSONException e) {
            Log.e(TAG, "上传统计数据,参数初始化错误 'statObject'错误");
            e.printStackTrace();
            return false;
        }
        try {
            uploadJson.put("app", packageName);
            uploadJson.put("info", info);
            uploadJson.putOpt("stat", statObject);
        } catch (JSONException e) {
            Log.e(TAG, "上传统计数据,参数初始化错误 'uploadJson'错误");
            e.printStackTrace();
            return false;
        }
        String url;
        if (HelperConfig.USE_ANET) {
            url = HttpUrlConfig.POST_ALIVE_STATS_V6_URL;
            Log.v("HttpImpl", "走IPV6");
        } else {
            url = HttpUrlConfig.POST_ALIVE_STATS_V4_URL;
            Log.v("HttpImpl", "走IPV4");
        }
        String response = HttpHelper.postJson(url, uploadJson.toString(), "uploadStatsTime");

        Log.v(TAG, "uploadStatsTime response= " + response);
        if (TextUtils.isEmpty(response)) {
            Log.e(TAG, "response is null");
            return false;
        } else {
            JSONObject jsonObject = null;
            String result = null;
            try {
                jsonObject = new JSONObject(response);
                result = jsonObject.get("result").toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result == null) {
                return false;
            } else {
                if (result.equals("ok")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * 查询aliveStats
     *
     * @param params
     * @param handler
     * @param flag
     */
    public void getAliveStats(final Map<String, String> params, final Handler handler, final String flag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url;
                    if (HelperConfig.USE_ANET) {
                        url = HttpUrlConfig.QUERY_ALIVE_STATS_V6_URL;
                        Log.v("HttpImpl", "走IPV6");
                    } else {
                        url = HttpUrlConfig.QUERY_ALIVE_STATS_V4_URL;
                        Log.v("HttpImpl", "走IPV4");
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


    private void sendHandler(Handler handler, int what, String data) {
        Bundle bundle = new Bundle();
        bundle.putString(GET_DATA_KEY, data);
        Message message = new Message();
        message.setData(bundle);
        message.what = what;
        handler.sendMessage(message);
    }
}
