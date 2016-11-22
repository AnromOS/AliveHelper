package org.ancode.alivelib.utils;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by andyliu on 16-10-9.
 */
public class AliveDateUtils {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ALIVE_STATS_FILE_NAME_FORMAT = "yyyy-MM-dd_HH:mm:ss";

    public static final String DEFAULT_DAY_FORMAT = "yyyy-MM-dd";

    /**
     * 格式化指定日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String timeFormat(long date, String format) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date(date));
        java.text.SimpleDateFormat fm = null;
        if (format != null) {
            fm = new java.text.SimpleDateFormat(format);
        } else {
            fm = new java.text.SimpleDateFormat(DEFAULT_FORMAT);
        }
        return fm.format(gc.getTime());
    }


    /**
     * 格式化指定日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String timeFormat(Date date, String format) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        java.text.SimpleDateFormat fm = null;
        if (format != null) {
            fm = new java.text.SimpleDateFormat(format);
        } else {
            fm = new java.text.SimpleDateFormat(DEFAULT_FORMAT);
        }
        return fm.format(gc.getTime());
    }

    /***
     * 获取前指定天数前的日期
     *
     * @param date
     * @param differDay
     * @return
     */
    public static Date getBeforeDate(Date date, int differDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 当前年
        int year = cal.get(Calendar.YEAR);
        // 当前月
        int month = (cal.get(Calendar.MONTH))/* + 1*/;
        // 当前月的第几天：即当前日
        int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
        Calendar gregorianCalendar = new GregorianCalendar(year, month, day_of_month - differDay, 0, 0, 0);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取分钟差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static float getDifferMinute(long startTime, long endTime) {
        long differ = endTime - startTime;
        float result = ((float) differ) / 1000 / 60;
        return result;
    }

    /**
     * 获取时差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static float getDifferHours(long startTime, long endTime) {
        long differ = endTime - startTime;
        float result = ((float) differ) / 1000 / 60 / 60;

        return result;
    }

    /**
     * 获取时差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static int getDifferDayOnly(long startTime, long endTime) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date(startTime));
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(new Date(endTime));
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int result = endDay - startDay;
        AliveLog.v("AliveStats", "startDay=" + startDay + ",endDay=" + endDay + ",比较开始时间与结束时间相差天数=" + result);
        return result;
    }


    /**
     * 获取指定日期的9点
     *
     * @param nowTime
     * @return
     */
    public static long getTody9Point(long nowTime) {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date(nowTime));
        // 当前年
        int year = nowCalendar.get(Calendar.YEAR);
        // 当前月
        int month = (nowCalendar.get(Calendar.MONTH));
        // 当前月的第几天：即当前日
        int day_of_month = nowCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar compareCalendar = new GregorianCalendar(year, month, day_of_month, 9, 0, 0);
        return compareCalendar.getTimeInMillis();
    }

    /**
     * 获取下一天当前时间
     *
     * @param time
     * @return
     */
    public static long getNextDayThisTime(long time) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(new Date(time));
        // 当前年
        int year = timeCalendar.get(Calendar.YEAR);
        // 当前月
        int month = (timeCalendar.get(Calendar.MONTH));
        // 当前月的第几天：即当前日
        int day_of_month = timeCalendar.get(Calendar.DAY_OF_MONTH);
        int hours = timeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = timeCalendar.get(Calendar.MINUTE);
        int second = timeCalendar.get(Calendar.SECOND);
        Calendar compareCalendar = new GregorianCalendar(year, month, day_of_month + 1, hours, minute, second);
        return compareCalendar.getTimeInMillis();
    }


    /**
     * 返回昨天的0点0分
     *
     * @return
     */
    public static long getLastDayStartTime(Date nowDate) {
        long dateTime = 1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);

        // 当前年
        int year = cal.get(Calendar.YEAR);
        // 当前月
        int month = (cal.get(Calendar.MONTH))/* + 1*/;
        // 当前月的第几天：即当前日
        int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
        Calendar gregorianCalendar = new GregorianCalendar(year, month, day_of_month - 1, 0, 0, 0);
        return gregorianCalendar.getTime().getTime();
    }

    /**
     * 返回今天 0点0分
     *
     * @return
     */
    public static long getToDayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 当前年
        int year = cal.get(Calendar.YEAR);
        // 当前月
        int month = (cal.get(Calendar.MONTH))/* + 1*/;
        // 当前月的第几天：即当前日
        int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
        Calendar gregorianCalendar = new GregorianCalendar(year, month, day_of_month, 0, 0, 0);
        return gregorianCalendar.getTime().getTime();
    }
}
