package org.ancode.alivelib.utils;

import android.text.TextUtils;

import org.ancode.alivelib.config.Constants;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.http.HttpHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andyliu on 16-10-8.
 */
public class AliveStatsUtils {
    public static final String TAG = AliveStatsUtils.class.getSimpleName();
    public static final String STATS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FILE_NAME_FORMAT = "yyyy-MM-dd";


    /**
     * 检测两数据之间差值是否有效
     *
     * @param startTime
     * @param endTime
     * @param differ
     * @return
     */
    public static boolean check2time(long startTime, long endTime, Integer differ) {
        long result = getTimeDiffer(startTime, endTime);
        if (differ == null) {
            return result > 0;
        } else {
            return (0 < result && result <= differ);
        }

    }

    /**
     * 获取时间差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getTimeDiffer(String startTime, String endTime) {
        SimpleDateFormat format = new SimpleDateFormat(STATS_DATE_FORMAT);
        try {
            long start = format.parse(startTime).getTime();
            long end = format.parse(endTime).getTime();
            long result = end - start;
            return result;
        } catch (ParseException e) {
            Log.e(TAG, "getTimeDiffer error" + e.getLocalizedMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取时间差
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getTimeDiffer(long startTime, long endTime) {
        long result = endTime - startTime;
        return result;
    }

    /***
     * 获取文件存储数据
     *
     * @return
     */
    public static List<String> getAliveStatsResult() {
        List<String> result = new ArrayList<String>();
        File file = new File(HelperConfig.CONTEXT.getFilesDir(), HelperConfig.ALIVE_STATS_FILE_NAME);

        if (!file.exists()) {
            return result;
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            //当前遍历到的时间
            String nowLine = null;
            while ((nowLine = bufferedReader.readLine()) != null) {
                result.add(nowLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /***
     * 获取应用存活的时间段
     *
     * @return
     */
    public static List<Long[]> getTimeToLive() {
        List<Long[]> result = new ArrayList<Long[]>();
        File file = new File(HelperConfig.CONTEXT.getFilesDir(), HelperConfig.ALIVE_STATS_FILE_NAME);

        if (!file.exists()) {
            return result;
        }

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            //当前遍历到的时间
            String nowLineStr = null;
            long nowLine = -1;
            //上一次遍历的时间
            long beforeLine = -1;
//            //时间段开始时间
            long startTime = -1;
            //时间段结束时间
            long endTime = -1;
            Long[] timeArray = null;
//            Log.v(TAG, "read start");
            while ((nowLineStr = bufferedReader.readLine()) != null) {
                nowLine = Long.valueOf(nowLineStr.split(" ")[0]);
                if (beforeLine == -1) {
                    beforeLine = nowLine;
                    startTime = nowLine;
                    endTime = nowLine;
                }
//                Log.v(TAG,"read time="+nowLine);
                if (beforeLine == nowLine) {
                    continue;
                } else {
                    if (check2time(beforeLine, nowLine, HelperConfig.CHECK_STATS_DIFFER)) {
                        endTime = nowLine;
                    } else {
                        timeArray = new Long[2];
                        timeArray[0] = startTime;
                        timeArray[1] = endTime;
                        result.add(timeArray);
                        endTime = nowLine;
                        startTime = nowLine;
                    }
                    beforeLine = nowLine;
                }

            }
            if (check2time(startTime, endTime, null)) {
                timeArray = new Long[2];
                timeArray[0] = startTime;
                timeArray[1] = endTime;
                result.add(timeArray);
            }
//            Log.v(TAG, "read end");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 获取保活百分比
     * @return
     */
    public static float getAlivePercent() {
        //获取上一天当前时间
        float percent = 0;
        List<Long[]> result = AliveStatsUtils.getTimeToLive();
        long allTime = 0;
        long aliveTime = 0;
        if (result.size() > 0) {
            if (result.size() > 1) {
                long startTime = result.get(0)[0];
                long endTime = result.get(result.size() - 1)[1];
                allTime = getTimeDiffer(startTime, endTime);
                for (int i = 0; i < result.size(); i++) {
                    startTime = result.get(i)[0];
                    endTime = result.get(i)[1];
                    aliveTime = aliveTime + getTimeDiffer(startTime, endTime);
                    Log.v(TAG, "start/end" + i + " = " + result.get(i)[0] + "/" + result.get(i)[1]);
                }
                percent = ((float) aliveTime) / ((float) allTime);
            } else {
                percent = 1;
                Log.v(TAG, "start/end = " + result.get(0)[0] + "/" + result.get(0)[1]);
            }
        } else {
            return -1;
        }

        return percent;
    }


    /***
     * 提交aliveStats
     *
     * @return
     */
    public static boolean uploadAliveStats() {

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
            Log.v(TAG, "走IPV6");
        } else {
            url = HttpUrlConfig.POST_ALIVE_STATS_V4_URL;
            Log.v(TAG, "走IPV4");
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
}
