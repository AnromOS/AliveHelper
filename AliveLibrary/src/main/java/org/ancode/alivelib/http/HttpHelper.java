package org.ancode.alivelib.http;

import android.text.TextUtils;
import android.util.Log;

import org.ancode.alivelib.utils.AliveLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by andyliu on 16-8-25.
 */
public class HttpHelper {
    private static final String TAG = HttpHelper.class.getSimpleName();
    //    public static Map<String, HttpURLConnection> urlgetConnections = null;
//    public static Map<String, HttpURLConnection> urlpostConnections = null;
    public static final String CHARSET = "UTF-8";
    public static final int CONNECT_TIMEOUT = 20 * 1000;
    public static final int READ_TIMEOUT = 20 * 1000;
    public static final String SSL_ERROR = "ssl_error";
    private static final String PROTOCOL = "";
    private static final String PROVIDER = "";
//    private SSLSocketFactory getSSLSocketFactory() throws Exception {
//        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
//        TrustManager[] tm = {new MyX509TrustManager()};
//        SSLContext sslContext = SSLContext.getInstance(PROTOCOL,PROVIDER);
//        sslContext.init(null, tm, new java.security.SecureRandom());
//        // 从上述SSLContext对象中得到SSLSocketFactory对象
//        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
//    }

    /**
     * 向指定URL发送GET方法的请求
     */
    public static String get(String urlStr, Map<String, String> map, String flag) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        String result = "";
        StringBuffer params = new StringBuffer();
        try {

            // 组织请求参数
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry element = (Map.Entry) it.next();
                params.append(element.getKey());
                params.append("=");
                params.append(URLEncoder.encode((String) element.getValue(), CHARSET).replace("+", "%20"));
                params.append("&");
            }
            if (params.length() > 0) {
                params.deleteCharAt(params.length() - 1);
            }
            URL url = new URL(urlStr + "?" + params.toString());
            AliveLog.v(TAG, "get url=" + url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            // 设置请求方法，默认是GET
            connection.setRequestMethod("GET");
            // 设置字符集
            connection.setRequestProperty("Charset", CHARSET);
            // 设置文件类型
            connection.setRequestProperty("Content-Type", "text/xml; charset=" + CHARSET);
            // 设置请求参数，可通过Servlet的getHeader()获取
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                // 定义BufferedReader输入流来读取URL的响应
                bufferedReader = new BufferedReader(
                        new InputStreamReader(is));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                AliveLog.v(TAG, "result =" + result);
                AliveLog.v(TAG, "请求成功!");
            } else {
                AliveLog.e(TAG, "错误 response=" + connection.getResponseCode());
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (e.toString().contains("was not verified")) {
                result = SSL_ERROR;
            } else if (e.toString().contains("SSLHandshakeException")) {
                result = SSL_ERROR;
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }

        }
        return result;
    }

    /**
     * post
     *
     * @param requestUrl
     * @param requestParamsMap
     * @return
     */
    public static String post(String requestUrl, Map<String, String> requestParamsMap, String flag) {
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        StringBuffer responseResult = new StringBuffer();
        StringBuffer params = new StringBuffer();
        HttpURLConnection HttpURLConnection = null;
        // 组织请求参数
        Iterator it = requestParamsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            params.append(element.getKey());
            params.append("=");
            params.append(element.getValue());
            params.append("&");
        }
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }

        try {
            URL realUrl = new URL(requestUrl);
            // 打开和URL之间的连接
            HttpURLConnection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            HttpURLConnection.setRequestProperty("accept", "*/*");
            HttpURLConnection.setRequestProperty("connection", "Keep-Alive");
            HttpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
            HttpURLConnection.setReadTimeout(READ_TIMEOUT);
            HttpURLConnection.setRequestProperty("Accept-Charset", CHARSET);
            HttpURLConnection.setRequestProperty("Content-Length", String
                    .valueOf(params.length()));
            // 发送POST请求必须设置如下两行
            HttpURLConnection.setDoOutput(true);
            HttpURLConnection.setDoInput(true);
//            if (urlpostConnections == null) {
//                urlpostConnections = new HashMap<String, HttpURLConnection>();
//            }
//            urlpostConnections.put(flag, HttpURLConnection);

            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(HttpURLConnection.getOutputStream());
            // 发送请求参数
            printWriter.write(params.toString());
            // flush输出流的缓冲
            printWriter.flush();
            // 根据ResponseCode判断连接是否成功
            int responseCode = HttpURLConnection.getResponseCode();
            if (responseCode != 200) {
                AliveLog.e(TAG, "错误 response=" + responseCode);
            } else {
                AliveLog.e(TAG, "请求成功!");
            }
            // 定义BufferedReader输入流来读取URL的ResponseData
            bufferedReader = new BufferedReader(new InputStreamReader(
                    HttpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult.append(line);
            }

        } catch (Exception e) {
            String error = e.toString();
            if (!TextUtils.isEmpty(error)) {
                if (error.contains("Permission denied")) {
                    AliveLog.e(TAG, "发送post请求错误!\n请配置'android.permission.INTERNET'权限");
                } else if (error.contains("was not verified")) {
                    return SSL_ERROR;
                } else if (error.contains("SSLHandshakeException")) {
                    return SSL_ERROR;
                } else {
                    AliveLog.e(TAG, "发送post请求错误!\n" + error);
                }
            }

        } finally {
            HttpURLConnection.disconnect();
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                AliveLog.e(TAG, "关闭http请求失败\n" + ex.getLocalizedMessage());
            }


        }
        AliveLog.v(TAG, "返回数据=" + responseResult.toString());
//        urlpostConnections.remove(flag);
        return responseResult.toString();
    }


    /**
     * postJson
     *
     * @param requestUrl
     * @param params
     * @return
     */
    public static String postJson(String requestUrl, String params, String flag) {
        BufferedReader bufferedReader = null;

        DataOutputStream out = null;
        StringBuffer responseResult = new StringBuffer();
        HttpURLConnection HttpURLConnection = null;
        BufferedWriter writer = null;
        // 组织请求参数
        try {
            URL realUrl = new URL(requestUrl);
            // 打开和URL之间的连接
            HttpURLConnection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            HttpURLConnection.setDoOutput(true);
            HttpURLConnection.setDoInput(true);
            HttpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
            HttpURLConnection.setReadTimeout(READ_TIMEOUT);
            HttpURLConnection.setRequestMethod("POST");
            HttpURLConnection.setUseCaches(false);
            HttpURLConnection.setInstanceFollowRedirects(true);
            HttpURLConnection.setRequestProperty("Content-Type", "application/json");
            HttpURLConnection.setRequestProperty("Accept-Charset", CHARSET);
            HttpURLConnection.connect();
            // 获取URLConnection对象对应的输出流
            out = new DataOutputStream(
                    HttpURLConnection.getOutputStream());
//            AliveLog.v(TAG, "上传的数据为\n" + params);
            DataOutputStream wr = new DataOutputStream(HttpURLConnection.getOutputStream());
            writer = new BufferedWriter(new OutputStreamWriter(out, CHARSET));
            writer.write(params);
            writer.flush();

            // 根据ResponseCode判断连接是否成功
            int responseCode = HttpURLConnection.getResponseCode();
            if (responseCode != 200) {
                AliveLog.e(TAG, "错误 response=" + responseCode);
            } else {
                AliveLog.v(TAG, "请求成功!");
            }
            // 定义BufferedReader输入流来读取URL的ResponseData
            bufferedReader = new BufferedReader(new InputStreamReader(
                    HttpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult.append(line);
            }

        } catch (Exception e) {
            String error = e.toString();
            if (!TextUtils.isEmpty(error)) {
                if (error.contains("Permission denied")) {
                    AliveLog.e(TAG, "发送postJson请求错误!\n请配置'android.permission.INTERNET'权限");
                } else if (error.contains("was not verified")) {
                    return SSL_ERROR;
                } else if (error.contains("SSLHandshakeException")) {
                    return SSL_ERROR;
                } else {
                    AliveLog.e(TAG, "发送postJson请求错误!\n" + error);
                }
            }


        } finally {
            HttpURLConnection.disconnect();
            try {
                if (writer != null) {
                    writer.close();
                }
                if (out != null) {
                    out.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }

            } catch (IOException ex) {
                AliveLog.e(TAG, "关闭http请求失败\n" + ex.getLocalizedMessage());
            }


        }
        AliveLog.v(TAG, "返回数据=" + responseResult.toString());
//        urlpostConnections.remove(flag);
        return responseResult.toString();
    }


    /**
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeString(InputStream inputStream,
                                       String encode) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = null;
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
                result = new String(byteArrayOutputStream.toByteArray(), encode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void cancelAll() {
//        try {
//            if (urlgetConnections != null)
//                for (Map.Entry<String, HttpURLConnection> entry : urlgetConnections.entrySet()) {
//                    entry.getValue().disconnect();
//                }
//
//            if (urlpostConnections != null)
//                for (Map.Entry<String, HttpURLConnection> entry : urlpostConnections.entrySet()) {
//                    entry.getValue().disconnect();
//                }
//        } catch (Exception e) {
//            AliveLog.e(TAG, "urlConnection disConnect error\n" + e.getLocalizedMessage());
//        }

    }


}
