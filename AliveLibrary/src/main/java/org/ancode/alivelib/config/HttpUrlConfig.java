package org.ancode.alivelib.config;

/**
 * Created by andyliu on 16-10-11.
 */
public class HttpUrlConfig {
    /**
     * SERVER IPV4
     */
    public static final String HOST_V4 = "xz.mixun.org";

    /***
     * SERVER IPV6
     */
    public static final String HOST_V6 = "xz.mixun.org.anet6.link";

    public static final String HTTP="https://";
    /***
     * 获取防杀指南IPV4
     */
    public static final String GET_ALIVE_GUIDE_V4_URL =HTTP + HOST_V4 + "/app/alivehelper/help/query";
    /***
     * 默认防杀指南IPV4
     */
    public static final String DEFAULT_ALIVE_GUIDE_V4_URL = HTTP + HOST_V4 + "/app/alivehelper/static/default/default/index.html";
    /**
     * 上传保活统计IPV4
     */
    public static final String POST_ALIVE_STATS_V4_URL = HTTP + HOST_V4 + "/app/alivehelper/stats/upload";
    /****
     * 获取保活统计IPV4
     */
    public static final String QUERY_ALIVE_STATS_V4_URL = HTTP + HOST_V4 + "/app/template/index.html";
    /***
     *上传crash接口IPV4
     */
    public static final String URL_POST_CRASH_V4_URL = "xxxxxx";

    /***
     * 是否显示notify
     */
    public static final String IS_ENABLE_SHOW_NOTIFY_V4_URL =  HTTP + HOST_V4 + "/app/template/alive_enable.html";
    /***
     * 获取防杀指南IPV6
     */
    public static final String GET_ALIVE_GUIDE_V6_URL = HTTP + HOST_V6 + "/app/alivehelper/help/query";
    /***
     * 默认防杀指南IPV6
     */
    public static final String DEFAULT_ALIVE_GUIDE_V6_URL = HTTP + HOST_V6 + "/app/alivehelper/static/default/default/index.html";
    /**
     * 上传保活统计IPV6
     */
    public static final String POST_ALIVE_STATS_V6_URL = HTTP + HOST_V6 + "/app/alivehelper/stats/upload";
    /****
     * 获取保活统计IPV6
     */
    public static final String QUERY_ALIVE_STATS_V6_URL = HTTP + HOST_V6 + "/app/template/index.html";


}
