package org.ancode.alivelib.utils;

import org.ancode.alivelib.config.HelperConfig;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by andyliu on 16-10-8.
 */
public class CountAliveUtils {
    public static final String TAG = CountAliveUtils.class.getSimpleName();
    public static final String COUNT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FILE_NAME_FORMAT = "yyyy-MM-dd";


    /**
     * 检测两数据之间差值是否有效
     *
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
        SimpleDateFormat format = new SimpleDateFormat(COUNT_DATE_FORMAT);
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
     *
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
    public static List<String> getAcResult() {
        List<String> result = new ArrayList<String>();
        File file = new File(HelperConfig.CONTEXT.getFilesDir(), HelperConfig.ALIVE_COUNT_FILE_NAME);

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
        File file = new File(HelperConfig.CONTEXT.getFilesDir(), HelperConfig.ALIVE_COUNT_FILE_NAME);

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
                    if (check2time(beforeLine, nowLine, HelperConfig.CHECK_COUNT_DIFFER)) {
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


    public static float getAlivePercent() {
        //获取上一天当前时间
        float percent = 0;
        List<Long[]> result = CountAliveUtils.getTimeToLive();
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
}
