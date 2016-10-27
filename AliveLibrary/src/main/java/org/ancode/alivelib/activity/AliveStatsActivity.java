package org.ancode.alivelib.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.ancode.alivelib.R;
import org.ancode.alivelib.config.Constants;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.http.HttpClient;
import org.ancode.alivelib.listener.StringCallBack;
import org.ancode.alivelib.utils.AliveSPUtils;
import org.ancode.alivelib.utils.DateTimeUtils;
import org.ancode.alivelib.utils.Log;
import org.ancode.alivelib.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andyliu on 16-8-24.
 */
public class AliveStatsActivity extends BaseActivity {
    private static final String TAG = AliveStatsActivity.class.getSimpleName();
    private WebView webView;
    ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "AliveStatsActivity onCreate");
    }

    @Override
    public void loadData() {
        Map<String, String> params = new HashMap<String, String>();
        String packageName = HelperConfig.CONTEXT.getPackageName().toString();
        Date date = new Date();
        String beginTime = String.valueOf(DateTimeUtils.getBeforeDate(date, 1).getTime());
        String endTime = String.valueOf(date.getTime());
        params.put("app", packageName);
        params.put("type", Constants.TYPE_ALIVE);
        params.put("tag", AliveSPUtils.getInstance().getASTag());
        params.put("begin", beginTime);
        params.put("end", endTime);
        HttpClient.getAliveStats(params, HttpClient.HTTP_CALL_FLAG, new StringCallBack() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response);
                    if (jsonObj.has("result")) {
                        String result = jsonObj.get("result").toString();
                        if (result.equals("ok")) {
                            onRefresh(result);
                        } else if (result.equals("failed")) {
                            showLoading(false);
                            showErrorView(true);
                        }
                    } else {
                        showLoading(false);
                        showErrorView(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
    }


    @Override
    protected void onRefresh(String data) {
        webView.loadUrl(data);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                }
                Log.v("WebViewDialog", "start Web url is = " + url);
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
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
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
