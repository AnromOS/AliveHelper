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

    /***
     * SERVER SBU
     */
//    public static final String HOST_SBU_V6 = "starport.sbu";
    public static final String HOST_SBU_V6 = "[fb5e:ecff:d561:5c7d:8cfd:771:d6f:4919]";
    public static final String HOST_SBU_V6_STATIC_PAGE ="[fb73:5b9:6c24:10b7:74be:2dc0:3565:8904]";
    public static final String HOST_SBU_V6_DOMAIN="starport.sbu";

    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";
    /***
     * 获取防杀指南IPV4
     */
    public static final String GET_ALIVE_GUIDE_V4_URL = HTTPS + HOST_V4 + "/app/alivehelper/help/query";
    /***
     * 默认防杀指南IPV4
     */
    public static final String DEFAULT_ALIVE_GUIDE_V4_URL = HTTPS + HOST_V4 + "/app/alivehelper/static/default/default/index.html";
    /**
     * 上传保活统计IPV4
     */
    public static final String POST_ALIVE_STATS_V4_URL = HTTPS + HOST_V4 + "/app/alivehelper/stats/upload";
    /****
     * 获取保活统计IPV4
     */
    public static final String QUERY_ALIVE_STATS_V4_URL = HTTPS + HOST_V4 + "/app/template/index.html";
    /***
     * 上传crash接口IPV4
     */
    public static final String URL_POST_CRASH_V4_URL = "xxxxxx";

    /***
     * 是否显示notify
     */
    public static final String IS_ENABLE_SHOW_NOTIFY_V4_URL = HTTPS + HOST_V4 + "/app/template/alive_enable.html";
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


    /***
     * 获取防杀指南IPV6
     */
    public static final String GET_ALIVE_GUIDE_SBU_URL = HTTP + HOST_SBU_V6 + "/app/alivehelper/help/query";
    /***
     * 默认防杀指南IPV6
     */
    public static final String DEFAULT_ALIVE_GUIDE_SBU_URL = HTTP + HOST_SBU_V6_STATIC_PAGE + "/alive/index.html";
    /**
     * 上传保活统计IPV6
     */
    public static final String POST_ALIVE_STATS_SBU_URL = HTTP + HOST_SBU_V6 + "/app/alivehelper/stats/upload";
    /****
     * 获取保活统计IPV6
     */
    public static final String QUERY_ALIVE_STATS_SBU_URL = HTTP + HOST_SBU_V6 + "/app/template/index.html";


}
