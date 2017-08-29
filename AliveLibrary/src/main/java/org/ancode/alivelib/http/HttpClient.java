package org.ancode.alivelib.http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.ancode.alivelib.AliveHelper;
import org.ancode.alivelib.config.Constants;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.utils.AliveLog;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.AliveStats;
import org.ancode.alivelib.utils.AliveStatsUtils;
import org.ancode.alivelib.utils.AliveTestUtils;
import org.ancode.alivelib.utils.NetUtils;
import org.ancode.alivelib.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
     * 查询统计结果(伪请求)
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
                    if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_ANET) {
                        url = HttpUrlConfig.QUERY_ALIVE_STATS_V6_URL;
                        AliveLog.v(TAG, "走IPV6");
                    } else if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_SBU) {
                        url = HttpUrlConfig.QUERY_ALIVE_STATS_SBU_URL;
                        AliveLog.v(TAG, "走SBU");
                    } else {
                        url = HttpUrlConfig.QUERY_ALIVE_STATS_V4_URL;
                        AliveLog.v(TAG, "走IPV4");
                    }
//                    String data = HttpHelper.get(url, params, flag);
                    StringBuffer sb = new StringBuffer();

                    // 组织请求参数
                    Iterator it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry element = (Map.Entry) it.next();
                        sb.append(element.getKey());
                        sb.append("=");
                        sb.append(URLEncoder.encode((String) element.getValue(), HttpHelper.CHARSET).replace("+", "%20"));
                        sb.append("&");
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    String data = url + "?" + sb.toString();
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
     * @param stringCallBack
     */
    public static void getAliveGuideUrl(StringCallBack stringCallBack) {
        final StrHandler handler = new StrHandler(stringCallBack);
        final Map<String, String> params = Utils.getProp();
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
                    if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_ANET) {
                        url = HttpUrlConfig.GET_ALIVE_GUIDE_V6_URL;
                        AliveLog.v(TAG, "走IPV6");
                    } else if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_SBU) {
                        url = HttpUrlConfig.GET_ALIVE_GUIDE_SBU_URL;
                        AliveLog.v(TAG, "走SBU");
                    } else {
                        url = HttpUrlConfig.GET_ALIVE_GUIDE_V4_URL;
                        AliveLog.v(TAG, "走IPV4");
                    }
                    String data = HttpHelper.get(url, params, "http_call_flag");
                    GETING_URL = false;
                    if (TextUtils.isEmpty(data)) {
                        sendHandler(handler, GET_DATA_ERROR, DATA_IS_NULL);
                        return;
                    }
                    if (data.equals(HttpHelper.SSL_ERROR)) {
                        sendHandler(handler, GET_DATA_ERROR, HttpHelper.SSL_ERROR);
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
                if (resultUrl.contains(HttpUrlConfig.HOST_SBU_V6_DOMAIN)) {
                    resultUrl = resultUrl.replace(HttpUrlConfig.HOST_SBU_V6_DOMAIN, HttpUrlConfig.HOST_SBU_V6);
                }
                sendHandler(handler, GET_DATA_SUCCESS, resultUrl);
                GETING_URL = false;
            }
        }).start();

    }


    /***
     * 提交aliveStats
     *
     * @param tag
     * @param handler
     */
    public static void uploadAliveStats(final String tag, final Handler handler) {

        new AsyncTask<Object, Object, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Object... params) {
                String fileNameStrs = AliveSPUtils.getInstance().getAliveStatsUploadFiles();
                String[] fileNames = fileNameStrs.split(",");
                String delFiles = "";
                for (int i = 0; i < fileNames.length; i++) {
                    boolean uploaded = false;
                    String fileName = fileNames[i];
                    if (!TextUtils.isEmpty(fileName)) {
                        uploaded = uploadAliveStats(fileName);
                    }
                    if (uploaded) {
                        AliveLog.v(TAG, "----上传数据成功----");
                        if (!TextUtils.isEmpty(fileName)) {
                            try {
                                HelperConfig.CONTEXT.deleteFile(fileName);
                                AliveLog.v(TAG, "delete file " + fileName);
                                AliveLog.v(TAG, "----删除数据成功----");
                            } catch (Exception e) {
                                Log.e(TAG, "删除文件" + fileName + "失败");
                                e.printStackTrace();
                            }
                            if (TextUtils.isEmpty(delFiles)) {
                                delFiles = fileName;
                            } else {
                                delFiles = delFiles + "," + fileName;
                            }
                        }

                    } else {
                        AliveLog.v(TAG, "上传" + fileName + "文件失败");
                    }
                }
                return delFiles;
            }

            @Override
            protected void onPostExecute(String delFileStrs) {
                super.onPostExecute(delFileStrs);
                //****清除掉已经上传的文件名称****///

                String[] delFiles = delFileStrs.split(",");
                String spFileNames = AliveSPUtils.getInstance().getAliveStatsUploadFiles();
                //清除文件列表中文件
                AliveLog.v(TAG, "处理文件列表" + spFileNames);
                for (int i = 0; i < delFiles.length; i++) {
                    String delFileName = delFiles[i];
                    if (spFileNames.contains(delFileName)) {
                        spFileNames = spFileNames.replace(delFileName, "");
                    }
                }
                String[] afterFileNames = spFileNames.split(",");
                String afterFileNameStr = "";
                for (int i = 0; i < afterFileNames.length; i++) {
                    String fileName = afterFileNames[i];

                    if (!TextUtils.isEmpty(fileName)) {
                        if (TextUtils.isEmpty(afterFileNameStr)) {
                            afterFileNameStr = fileName;
                        } else {
                            afterFileNameStr = afterFileNameStr + "," + fileName;
                        }
                    }
                }
                AliveSPUtils.getInstance().setAliveStatsUploadFiles(afterFileNameStr);
                AliveLog.v(TAG, "处理文件完成，结果为（" + afterFileNameStr + "）");
                //****清除掉已经上传的文件名称****///
                handler.sendEmptyMessage(AliveStats.UPLOAD_FILE_SUCCESS);
            }
        }.execute();

    }


    /***
     * 提交aliveStats
     *
     * @return
     */
    private static boolean uploadAliveStats(String fileName) {
        JSONObject info = null;
        String tag = AliveSPUtils.getInstance().getASTag();
        try {
            info = new JSONObject(AliveSPUtils.getInstance().getASUploadInfo());
        } catch (JSONException e) {
            AliveLog.e(TAG, "用户设置的info解析出错");
            throw new IllegalArgumentException("Your info is error ,Please set info");
        }
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("Your aliveTag is null ,Please set aliveTag");
        }

        JSONObject uploadJson = new JSONObject();
        JSONObject statObject = new JSONObject();
        //统计数据拆天操作后
//        JSONObject data = AliveStatsUtils.getAliveStatsResult();
        List<String> data = AliveStatsUtils.getAliveStatsResult(fileName);
        JSONArray dataArray = new JSONArray(data);
        try {
            statObject.put("type", Constants.TYPE_ALIVE);
            statObject.putOpt("data", dataArray);
//            statObject.put("tag",tag);
        } catch (JSONException e) {
            AliveLog.e(TAG, "上传统计数据,参数初始化错误 'statObject'错误");
            e.printStackTrace();
            return false;
        }
        try {
            uploadJson.put("tag", tag);
            uploadJson.put("info", info);
            uploadJson.putOpt("stat", statObject);
        } catch (JSONException e) {
            AliveLog.e(TAG, "上传统计数据,参数初始化错误 'uploadJson'错误");
            e.printStackTrace();
            return false;
        }
        String url;

        if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_ANET) {
            url = HttpUrlConfig.POST_ALIVE_STATS_V6_URL;
            AliveLog.v(TAG, "走IPV6");
        } else if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_SBU) {
            url = HttpUrlConfig.POST_ALIVE_STATS_SBU_URL;
            AliveLog.v(TAG, "走SBU");
        } else {
            url = HttpUrlConfig.POST_ALIVE_STATS_V4_URL;
            AliveLog.v(TAG, "走IPV4");
        }
        AliveLog.v(TAG, "上传保活数据" + uploadJson.toString());
        String response = HttpHelper.postJson(url, uploadJson.toString(), "uploadStatsTime");

        AliveLog.v(TAG, "uploadStatsTime response= " + response);
        if (TextUtils.isEmpty(response)) {
            AliveLog.e(TAG, "response is null");
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
                    if (!AliveSPUtils.getInstance().getIsRelease()) {
                        String[] s = new String[2];
                        long startTime = 0;
                        try {
                            s = data.get(0).split(" ");
                            startTime = Long.valueOf(s[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        AliveTestUtils.backUpUploadData(fileName, startTime, uploadJson);
                    }

                    return true;
                } else {
                    return false;
                }
            }
        }
    }


    public static void postCrash(final Map<String, String> params, StringCallBack stringCallBack) {
        final StrHandler handler = new StrHandler(stringCallBack);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url;
                    if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_ANET) {
                        url = HttpUrlConfig.URL_POST_CRASH_V4_URL;
                        AliveLog.v(TAG, "走IPV6");
                    } else if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_SBU) {
                        url = HttpUrlConfig.URL_POST_CRASH_V4_URL;
                        AliveLog.v(TAG, "走SBU");
                    } else {
                        url = HttpUrlConfig.URL_POST_CRASH_V4_URL;
                        AliveLog.v(TAG, "走IPV4");
                    }
                    String data = HttpHelper.post(url, params, "http_call_flag");
                    if (TextUtils.isEmpty(data)) {
                        sendHandler(handler, GET_DATA_ERROR, "response is null");
                        return;
                    }
                    JSONObject jsonObj = new JSONObject(data);
                    if (jsonObj.has("result")) {
                        if (jsonObj.get("result").toString().equals("ok")) {
                            sendHandler(handler, GET_DATA_SUCCESS, jsonObj.get("result").toString());

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
                    sendHandler(handler, GET_DATA_ERROR, e.getLocalizedMessage());
                    return;
                }
            }
        }).start();

    }

    /**
     * 是否显示notification
     *
     * @param stringCallBack
     */
    public static void isEnableShowNotify(StringCallBack stringCallBack) {
        final StrHandler handler = new StrHandler(stringCallBack);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url;

                    if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_ANET) {
                        url = HttpUrlConfig.IS_ENABLE_SHOW_NOTIFY_V4_URL;
                        AliveLog.v(TAG, "走IPV6");
                    } else if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_SBU) {
                        url = HttpUrlConfig.IS_ENABLE_SHOW_NOTIFY_V4_URL;
                        AliveLog.v(TAG, "走SBU");
                    } else {
                        url = HttpUrlConfig.IS_ENABLE_SHOW_NOTIFY_V4_URL;
                        AliveLog.v(TAG, "走IPV4");
                    }
                    Map<String, String> params = new HashMap<String, String>();
                    String data = HttpHelper.get(url, params, "isEnableShowNotify");
                    if (TextUtils.isEmpty(data)) {
                        sendHandler(handler, GET_DATA_ERROR, "response is null");
                        return;
                    }
                    if (data.equals(HttpHelper.SSL_ERROR)) {
                        sendHandler(handler, GET_DATA_ERROR, HttpHelper.SSL_ERROR);
                        return;
                    }
                    sendHandler(handler, GET_DATA_SUCCESS, data);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendHandler(handler, GET_DATA_ERROR, e.getLocalizedMessage());
                    return;
                }
            }
        }).start();
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
                    AliveLog.e(TAG, "StringCallBack is null_image");
                }
                return;
            }
            String data = msg.getData().getString(GET_DATA_KEY);
            if (!TextUtils.isEmpty(data)) {
                if (msg.what == GET_DATA_ERROR) {
                    if (callBack != null) {
                        callBack.error(data);
                    } else {
                        AliveLog.e(TAG, "StringCallBack is null_image");
                    }
                } else if (msg.what == GET_DATA_SUCCESS) {
                    if (callBack != null) {

                        callBack.onResponse(data);
                    } else {
                        AliveLog.e(TAG, "StringCallBack is null_image");
                    }

                }
            } else {
                if (callBack != null) {
                    callBack.error(DATA_IS_NULL);
                } else {
                    AliveLog.e(TAG, "StringCallBack is null_image");
                }
                AliveLog.e(TAG, "获取数据失败");
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
