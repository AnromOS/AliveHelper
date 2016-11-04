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
     * 获取防杀指南IPV4
     */
    public static final String GET_ALIVE_GUIDE_V4_URL = "http://" + HOST_V4 + "/app/alivehelper/help/query";
    /***
     * 默认防杀指南IPV4
     */
    public static final String DEFAULT_ALIVE_GUIDE_V4_URL = "http://" + HOST_V4 + "/app/alivehelper/static/default/default/index.html";
    /**
     * 上传保活统计IPV4
     */
    public static final String POST_ALIVE_STATS_V4_URL = "http://" + HOST_V4 + "/app/alivehelper/stats/upload";
    /****
     * 获取保活统计IPV4
     */
    public static final String QUERY_ALIVE_STATS_V4_URL = "http://" + HOST_V4 + "/stats/query";
    /***
     *上传crash接口IPV4
     */
    public static final String URL_POST_CRASH_V4_URL = "xxxxxx";
    /***
     * 获取防杀指南IPV6
     */
    public static final String GET_ALIVE_GUIDE_V6_URL = "http://" + HOST_V6 + "/app/alivehelper/help/query";
    /***
     * 默认防杀指南IPV6
     */
    public static final String DEFAULT_ALIVE_GUIDE_V6_URL = "http://" + HOST_V6 + "/app/alivehelper/static/default/default/index.html";
    /**
     * 上传保活统计IPV6
     */
    public static final String POST_ALIVE_STATS_V6_URL = "http://" + HOST_V6 + "/app/alivehelper/stats/upload";
    /****
     * 获取保活统计IPV6
     */
    public static final String QUERY_ALIVE_STATS_V6_URL = "http://" + HOST_V6 + "/stats/query";


}
