package org.ancode.alivelib.utils;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by andyliu on 16-10-9.
 */
public class DateTimeUtils {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /***
     * 格式化指定日期
     *
     * @param date
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


    /***
     * 格式化指定日期
     *
     * @param date
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

    /**
     * 获取前指定天数前的日期
     *
     * @param date
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
}
