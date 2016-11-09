package org.ancode.alivelib.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.ancode.alivelib.R;
import org.ancode.alivelib.config.Constants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AliveLog.v(TAG, "AliveStatsActivity onCreate");
    }

    @Override
    public void loadData() {
        Map<String, String> params = new HashMap<String, String>();
        Date date = new Date();
        String beginTime = String.valueOf(AliveDateUtils.getLastDayStartTime(date));
        String endTime = String.valueOf(AliveDateUtils.getToDayStartTime());
        params.put("type", Constants.TYPE_ALIVE);
        params.put("tag", AliveSPUtils.getInstance().getASTag());
        params.put("begin", beginTime);
        params.put("end", endTime);
        Log.v(TAG, "aliveStats params=" + params.toString());
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
        final ProgressBar finalProgressBar = progressBar;
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                finalProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    finalProgressBar.setVisibility(View.INVISIBLE);
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
    protected void onResume() {
        super.onResume();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.getSettings().setJavaScriptEnabled(false);
        webView.onPause();
    }

    @Override
    protected void onRefresh(String data) {
        String dataUrl = data + "&t=" + new Date().getTime();
        webView.loadUrl(dataUrl);
        Log.v(TAG, "请求到的保活统计界面为\n" + dataUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                }
                AliveLog.v("WebViewDialog", "start Web url is = " + url);
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
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
        }

        return super.onKeyDown(keyCode, event);
    }

}
