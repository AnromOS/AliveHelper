package org.ancode.alivelib.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.ancode.alivelib.R;
import org.ancode.alivelib.config.Constants;
import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.AliveDateUtils;
import org.ancode.alivelib.utils.AliveLog;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andyliu on 16-8-24.
 */
public class AliveStatsActivity extends BaseAliveActivity {
    private static final String TAG = AliveStatsActivity.class.getSimpleName();
    private WebView webView;
    ProgressBar progressBar = null;
    String beginTime = null;
    String endTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AliveLog.v(TAG, "AliveStatsActivity onCreate");
    }


    @Override
    public void loadData() {
        Map<String, String> params = new HashMap<String, String>();
        Date date = new Date();
        beginTime = String.valueOf(AliveDateUtils.getLastDayStartTime(date));
        endTime = String.valueOf(AliveDateUtils.getToDayStartTime());
        params.put("type", Constants.TYPE_ALIVE);
        params.put("tag", AliveSPUtils.getInstance().getASTag());
        params.put("begin", beginTime);
        params.put("end", endTime);
        String beginTimeStr = AliveDateUtils.timeFormat(new Date(AliveDateUtils.getLastDayStartTime(date)), AliveDateUtils.DEFAULT_FORMAT);
        String endTimeStr = AliveDateUtils.timeFormat(new Date(AliveDateUtils.getToDayStartTime()), AliveDateUtils.DEFAULT_FORMAT);
        AliveLog.v(TAG, "aliveStats begin=" + beginTimeStr + " ,end=" + endTimeStr + ",params=" + params.toString());
        HttpClient.getAliveStats(params, HttpClient.HTTP_CALL_FLAG, new StringCallBack() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    showLoading(false);
                    showErrorView(true);
                } else {
                    showLoading(false);
                    onRefresh(response);
                }

            }

            @Override
            public void error(String error) {
                showErrorView(true);
            }
        });
    }


    @Override
    protected int setLayout() {
        return R.layout.alive_stats_activity;
    }


    @Override
    protected void initView() {
        setTitle(String.format(getString(R.string.alive_stats_title), appName));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        //获取WebSettings对象,设置缩放
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);//允许DCOM
//        webView.addJavascriptInterface(this, "aliveStatsMethod");
        final ProgressBar finalProgressBar = progressBar;
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                finalProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    finalProgressBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finalProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }, 300);

                } else {
                    finalProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    /***
     * @param activityName
     */
//    @JavascriptInterface
    public void toActivity(String activityName) {
        try {
            //此处应该定义常量对应，同时提供给web页面编写者
            if (TextUtils.equals(activityName, "aliveguide")) {
                startActivity(new Intent(this, AliveGuideActivity.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        webView.getSettings().setJavaScriptEnabled(true);
        webView.onResume();
        String beginTimeR = String.valueOf(AliveDateUtils.getLastDayStartTime(new Date()));
        String endTimeR = String.valueOf(AliveDateUtils.getToDayStartTime());
        if (beginTime != null && endTime != null) {
            if (!beginTimeR.equals(beginTime) && !endTimeR.equals(endTime)) {
                reload();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        webView.getSettings().setJavaScriptEnabled(false);
        webView.onPause();
    }

    @Override
    protected void onRefresh(String data) {
        if(isFinishing())return;
        showSSLError(false);
        webView.clearCache(true);
        String loadUrl = data + "&t=" + System.currentTimeMillis();
        webView.loadUrl(loadUrl);
        AliveLog.i(TAG, "请求到的保活统计界面为\n" + data);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.v(APP_TAG, "加载的url是=" + url);
                if (URLUtil.isNetworkUrl(url)) {
                    if (url.equals("http://toaliveguide/param?activity=aliveguide")) {
                        AliveLog.i(TAG, "跳转activity,url=" + url);
                        toActivity("aliveguide");
                        return true;
                    }
                    return false;
                }
                AliveLog.v("WebViewDialog", "start Web url is = " + url);
                try {
                    //   跳转防杀指南 <a href="ancode://toaliveguide/param?activity=aliveguide">gotoActivity</a>
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!TextUtils.isEmpty(url)) {
                    if (url.contains(HttpUrlConfig.HOST_V4)
                            || url.contains(HttpUrlConfig.HOST_V6)
                            || url.contains(HttpUrlConfig.HOST_SBU_V6)
                            || url.contains(HttpUrlConfig.HOST_SBU_V6_DOMAIN)) {
                        AliveLog.i(TAG, "自己的url=" + url);
                        return null;

                    } else if (url.contains("aliveguide")) {

                        AliveLog.i(TAG, "自己的url=" + url);
                        return null;
                    } else {
                        AliveLog.i(TAG, "其它url=" + url);
                        return new WebResourceResponse(null, null, null);
                    }
                }
//                Log.v(APP_TAG, "加载的url是自己的url=" + url);
                return null;
            }

//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                String url = request.getUrl().toString();
//                if (!TextUtils.isEmpty(url)) {
//                    if (url.contains("xz.mixun.org")) {
//                        return null;
//                    } else if (url.contains("aliveguide")) {
//                        return null;
//                    } else {
//                        return null;
//                    }
//                }
//                return null;
//            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
//                AliveLog.v(APP_TAG, "验证证书失败!!" + error.toString());
                AliveLog.i(TAG, "HTTPS 验证证书失败!!");
                AliveLog.i(TAG, error.toString());
                showSSLError(true);
                handler.cancel();

            }
        });
    }

    @Override
    protected void reload() {
        if (webView != null) {
//            webView.clearCache(true);
            loadData();
//            webView.reload();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
//            webView.loadUrl("about:blank");
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

}
