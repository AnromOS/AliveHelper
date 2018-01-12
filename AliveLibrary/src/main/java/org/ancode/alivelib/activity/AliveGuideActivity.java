package org.ancode.alivelib.activity;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.ancode.alivelib.R;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.config.HttpUrlConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.callback.StringCallBack;
import org.ancode.alivelib.http.HttpHelper;
import org.ancode.alivelib.utils.AliveLog;

/**
 * Created by andyliu on 16-8-24.
 */
public class AliveGuideActivity extends BaseAliveActivity {
    private static final String TAG = AliveGuideActivity.class.getSimpleName();
    private WebView webView;
    ProgressBar progressBar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AliveLog.v(TAG, "AliveGuideActivity onCreate");

    }


    @Override
    public void loadData() {
        HttpClient.getAliveGuideUrl(new StringCallBack() {
            @Override
            public void onResponse(String response) {
                showLoading(false);
                showErrorView(false);
                showSSLError(false);
                webView.setVisibility(View.VISIBLE);
                onRefresh(response);
            }

            @Override
            public void error(String error) {
                if (error.equals(HttpClient.DATA_IS_NULL)) {
                    AliveLog.v(TAG, "show default html");
                    showLoading(false);

                    if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_ANET) {
                        onRefresh(HttpUrlConfig.DEFAULT_ALIVE_GUIDE_V6_URL);
                        AliveLog.v(TAG, "走IPV6");
                    } else if (HelperConfig.USE_ANET == HelperConfig.TYPE_USE_SBU) {
                        onRefresh(HttpUrlConfig.DEFAULT_ALIVE_GUIDE_SBU_URL);
                        AliveLog.v(TAG, "走SBU");
                    } else {
                        onRefresh(HttpUrlConfig.DEFAULT_ALIVE_GUIDE_V4_URL);
                        AliveLog.v(TAG, "走IPV4");
                    }

                } else if (error.contains(HttpHelper.SSL_ERROR)) {
                    showLoading(false);
                    showSSLError(true);
                    webView.setVisibility(View.GONE);
                } else {
                    showLoading(false);
                    showErrorView(true);
                    AliveLog.e(TAG, "获取数据失败:\n" + error);
                }

            }

        });
    }


    @Override
    protected int setLayout() {
        return R.layout.alive_helper_activity;
    }


    @Override
    protected void initView() {
        setTitle(String.format(getString(R.string.alive_activity_title), appName));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        //获取WebSettings对象,设置缩放
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);//允许DCOM

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


    @Override
    protected void onRefresh(String data) {
        if (isFinishing()) return;
        String loadUrl = data + "?t=" + System.currentTimeMillis();
        webView.loadUrl(loadUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                }
                AliveLog.v(TAG, "start Web url is = " + url);
                try {
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
                            || url.contains(HttpUrlConfig.HOST_SBU_V6_DOMAIN)
                            || url.contains(HttpUrlConfig.HOST_SBU_V6_STATIC_PAGE)) {
                        AliveLog.v(TAG, "自己的url=" + url);
                        return null;

                    } else if (url.contains("aliveguide")) {
                        AliveLog.v(TAG, "自己的url=" + url);
                        return null;
                    } else {
                        AliveLog.v(TAG, "其它url=" + url);
                        return new WebResourceResponse(null, null, null);
                    }
                }
//                AliveLog.v(APP_TAG, "加载的url是自己的url=" + url);
                return null;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
//                AliveLog.v(APP_TAG, "验证证书失败!!" + error.toString());
                AliveLog.v(TAG, "HTTPS 验证证书失败!!");
                AliveLog.v(TAG, error.toString());
                showSSLError(true);
                handler.cancel();

            }
        });
    }

    @Override
    protected void reload() {
        if (webView != null) {
//            webView.clearCache(true);
//            webView.reload();
            loadData();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        webView.getSettings().setJavaScriptEnabled(true);
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        webView.getSettings().setJavaScriptEnabled(false);
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
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
